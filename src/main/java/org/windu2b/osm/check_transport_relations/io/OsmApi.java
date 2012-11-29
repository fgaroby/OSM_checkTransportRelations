// License: GPL. See README for details.
package org.windu2b.osm.check_transport_relations.io;

import static org.windu2b.osm.check_transport_relations.tools.I18n.tr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.windu2b.osm.check_transport_relations.tools.CheckParameterUtil;

/**
 * Class that encapsulates the communications with the <a
 * href="http://wiki.openstreetmap.org/wiki/API_v0.6">OSM API</a>.<br/>
 * <br/>
 * 
 * All interaction with the server-side OSM API should go through this class.<br/>
 * <br/>
 * 
 * It is conceivable to extract this into an interface later and create various
 * classes implementing the interface, to be able to talk to various kinds of
 * servers.
 * 
 */
public class OsmApi extends OsmConnection
{

	/**
	 * Maximum number of retries to send a request in case of HTTP 500 errors or
	 * timeouts
	 */
	static public final int	               DEFAULT_MAX_NUM_RETRIES	= 5;


	/**
	 * Maximum number of concurrent download threads, imposed by <a href=
	 * "http://wiki.openstreetmap.org/wiki/API_usage_policy#Technical_Usage_Requirements"
	 * > OSM API usage policy.</a>
	 * 
	 * @since 5386
	 */
	static public final int	               MAX_DOWNLOAD_THREADS	   = 2;


	/**
	 * Default URL of the standard OSM API.
	 * 
	 * @since 5422
	 */
	static public final String	           DEFAULT_API_URL	       = "http://api.openstreetmap.org/api";


	// The collection of instantiated OSM APIs
	private static HashMap<String, OsmApi>	instances	           = new HashMap<String, OsmApi>();


	/**
	 * API version used for server communications
	 */
	private String	                       version	               = "0.6";


	/** the server URL */
	private String	                       serverUrl;




	/**
	 * Replies the {@link OsmApi} for a given server URL
	 * 
	 * @param serverUrl
	 *            the server URL
	 * @return the OsmApi
	 * @throws IllegalArgumentException
	 *             thrown, if serverUrl is null
	 * 
	 */
	static public OsmApi getOsmApi( String serverUrl )
	{
		OsmApi api = instances.get( serverUrl );
		if ( api == null )
		{
			api = new OsmApi( serverUrl );
			instances.put( serverUrl, api );
		}
		return api;
	}




	/**
	 * Replies the {@link OsmApi} for the URL given by the
	 * <code>DEFAULT_API_URL</code>
	 * 
	 * @return the OsmApi
	 * 
	 */
	static public OsmApi getOsmApi()
	{
		return getOsmApi( DEFAULT_API_URL );
	}




	/**
	 * creates an OSM api for a specific server URL
	 * 
	 * @param serverUrl
	 *            the server URL. Must not be null
	 * @throws IllegalArgumentException
	 *             thrown, if serverUrl is null
	 */
	protected OsmApi( String serverUrl )
	{
		CheckParameterUtil.ensureParameterNotNull( serverUrl, "serverUrl" );
		this.serverUrl = serverUrl;
	}




	/**
	 * Replies the OSM protocol version we use to talk to the server.
	 * 
	 * @return protocol version, or null if not yet negotiated.
	 */
	public String getVersion()
	{
		return version;
	}




	/**
	 * Replies the host name of the server URL.
	 * 
	 * @return the host name of the server URL, or null if the server URL is
	 *         malformed.
	 */
	public String getHost()
	{
		String host = null;
		try
		{
			host = ( new URL( serverUrl ) ).getHost();
		}
		catch ( MalformedURLException e )
		{
		}
		return host;
	}




	/**
	 * Returns the base URL for API requests, including the negotiated version
	 * number.
	 * 
	 * @return base URL string
	 */
	public String getBaseUrl()
	{
		StringBuffer rv = new StringBuffer( serverUrl );
		if ( version != null )
		{
			rv.append( "/" );
			rv.append( version );
		}
		rv.append( "/" );
		// this works around a ruby (or lighttpd) bug where two consecutive
		// slashes in
		// an URL will cause a "404 not found" response.
		int p;
		while ( ( p = rv.indexOf( "//", 6 ) ) > -1 )
		{
			rv.delete( p, p + 1 );
		}
		return rv.toString();
	}




	private void sleepAndListen( int retry )
	        throws OsmTransferCanceledException
	{
		System.out.print( tr( "Waiting 10 seconds ... " ) );
		for ( int i = 0; i < 10; i++ )
		{
			if ( cancel ) throw new OsmTransferCanceledException();
			try
			{
				Thread.sleep( 1000 );
			}
			catch ( InterruptedException ex )
			{
			}
		}
		System.out.println( tr( "OK - trying again." ) );
	}




	/**
	 * Replies the max. number of retries in case of 5XX errors on the server
	 * 
	 * @return the max number of retries
	 */
	protected int getMaxRetries()
	{
		return Math.max( DEFAULT_MAX_NUM_RETRIES, 0 );
	}




	protected boolean isUsingOAuth()
	{
		return false;
	}




	/**
	 * Generic method for sending requests to the OSM API.
	 * 
	 * This method will automatically re-try any requests that are answered with
	 * a 5xx error code, or that resulted in a timeout exception from the TCP
	 * layer.
	 * 
	 * @param requestMethod
	 *            The http method used when talking with the server.
	 * @param urlSuffix
	 *            The suffix to add at the server url, not including the version
	 *            number, but including any object ids (e.g.
	 *            "/way/1234/history").
	 * @param requestBody
	 *            the body of the HTTP request, if any.
	 * @param fastFail
	 *            true to request a short timeout
	 * 
	 * @return the body of the HTTP response, if and only if the response code
	 *         was "200 OK".
	 * @throws OsmTransferException
	 *             if the HTTP return code was not 200 (and retries have been
	 *             exhausted), or rewrapping a Java exception.
	 */
	private String sendRequest( String requestMethod, String urlSuffix,
	        String requestBody, boolean fastFail ) throws OsmTransferException
	{
		StringBuffer responseBody = new StringBuffer();
		int retries = fastFail ? 0 : getMaxRetries();

		while ( true )
		{ // the retry loop
			try
			{
				URL url = new URL( new URL( getBaseUrl() ), urlSuffix );
				System.out.print( requestMethod + " " + url + "... " );
				activeConnection = ( HttpURLConnection ) url.openConnection();
				// fix #5369, see
				// http://www.tikalk.com/java/forums/httpurlconnection-disable-keep-alive
				activeConnection.setRequestProperty( "Connection", "close" );
				activeConnection.setConnectTimeout( fastFail ? 1000 : 15000 );
				if ( fastFail )
				{
					activeConnection.setReadTimeout( 1000 );
				}
				activeConnection.setRequestMethod( requestMethod );

				if ( requestMethod.equals( "PUT" )
				        || requestMethod.equals( "POST" )
				        || requestMethod.equals( "DELETE" ) )
				{
					activeConnection.setDoOutput( true );
					activeConnection.setRequestProperty( "Content-type",
					        "text/xml" );
					OutputStream out = activeConnection.getOutputStream();

					// It seems that certain bits of the Ruby API are very
					// unhappy upon
					// receipt of a PUT/POST message without a Content-length
					// header,
					// even if the request has no payload.
					// Since Java will not generate a Content-length header
					// unless
					// we use the output stream, we create an output stream for
					// PUT/POST
					// even if there is no payload.
					if ( requestBody != null )
					{
						BufferedWriter bwr = new BufferedWriter(
						        new OutputStreamWriter( out, "UTF-8" ) );
						bwr.write( requestBody );
						bwr.flush();
					}
					out.close();
				}

				activeConnection.connect();
				System.out.println( activeConnection.getResponseMessage() );
				int retCode = activeConnection.getResponseCode();

				if ( retCode >= 500 )
				{
					if ( retries-- > 0 )
					{
						sleepAndListen( retries );
						System.out.println( tr( "Starting retry {0} of {1}.",
						        getMaxRetries() - retries, getMaxRetries() ) );
						continue;
					}
				}

				// populate return fields.
				responseBody.setLength( 0 );

				// If the API returned an error code like 403 forbidden,
				// getInputStream
				// will fail with an IOException.
				InputStream i = null;
				try
				{
					i = activeConnection.getInputStream();
				}
				catch ( IOException ioe )
				{
					i = activeConnection.getErrorStream();
				}
				if ( i != null )
				{
					// the input stream can be null if both the input and the
					// error stream
					// are null. Seems to be the case if the OSM server replies
					// a 401
					// Unauthorized, see #3887.
					//
					BufferedReader in = new BufferedReader(
					        new InputStreamReader( i ) );
					String s;
					while ( ( s = in.readLine() ) != null )
					{
						responseBody.append( s );
						responseBody.append( "\n" );
					}
				}
				String errorHeader = null;
				// Look for a detailed error message from the server
				if ( activeConnection.getHeaderField( "Error" ) != null )
				{
					errorHeader = activeConnection.getHeaderField( "Error" );
					System.err.println( "Error header: " + errorHeader );
				}
				else if ( retCode != 200 && responseBody.length() > 0 )
				{
					System.err.println( "Error body: " + responseBody );
				}
				activeConnection.disconnect();

				errorHeader = errorHeader == null ? null : errorHeader.trim();
				String errorBody = responseBody.length() == 0 ? null
				        : responseBody.toString().trim();
				switch ( retCode )
				{
					case HttpURLConnection.HTTP_OK :
						return responseBody.toString();
					case HttpURLConnection.HTTP_GONE :
						throw new OsmApiPrimitiveGoneException( errorHeader,
						        errorBody );
					case HttpURLConnection.HTTP_FORBIDDEN :
						OsmApiException e = new OsmApiException( retCode,
						        errorHeader, errorBody );
						e.setAccessedUrl( activeConnection.getURL().toString() );
						throw e;
					default :
						throw new OsmApiException( retCode, errorHeader,
						        errorBody );
				}
			}
			catch ( UnknownHostException e )
			{
				throw new OsmTransferException( e );
			}
			catch ( SocketTimeoutException e )
			{
				if ( retries-- > 0 )
				{
					continue;
				}
				throw new OsmTransferException( e );
			}
			catch ( ConnectException e )
			{
				if ( retries-- > 0 )
				{
					continue;
				}
				throw new OsmTransferException( e );
			}
			catch ( IOException e )
			{
				throw new OsmTransferException( e );
			}
			catch ( OsmTransferCanceledException e )
			{
				throw e;
			}
			catch ( OsmTransferException e )
			{
				throw e;
			}
		}
	}
}
