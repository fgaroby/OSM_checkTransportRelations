// License: GPL. Copyright 2007 by Immanuel Scholz and others
package org.windu2b.osm.check_transport_relations.io;


import java.net.HttpURLConnection;


/**
 * Base class that handles common things like authentication for the reader and
 * writer to the osm server.
 * 
 * @author imi
 */
public class OsmConnection
{
	protected boolean	        cancel	= false;


	protected HttpURLConnection	activeConnection;

	/**
	 * Initialize the http defaults and the authenticator.
	 */
	static
	{
		try
		{
			HttpURLConnection.setFollowRedirects( true );
		}
		catch ( SecurityException e )
		{
			e.printStackTrace();
		}
	}




	public void cancel()
	{
		cancel = true;
		synchronized ( this )
		{
			if ( activeConnection != null )
			{
				activeConnection.setConnectTimeout( 100 );
				activeConnection.setReadTimeout( 100 );
			}
		}
		try
		{
			Thread.sleep( 100 );
		}
		catch ( InterruptedException ex )
		{
		}

		synchronized ( this )
		{
			if ( activeConnection != null )
			{
				activeConnection.disconnect();
			}
		}
	}




	/**
	 * Replies true if this connection is canceled
	 * 
	 * @return true if this connection is canceled
	 * @return
	 */
	public boolean isCanceled()
	{
		return cancel;
	}
}
