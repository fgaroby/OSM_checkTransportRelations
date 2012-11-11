// License: GPL. For details, see LICENSE file.
package org.windu2b.osm.check_transport_relations.data.osm;

public class DataIntegrityProblemException extends RuntimeException
{

	private String	htmlMessage;




	public DataIntegrityProblemException( String message )
	{
		super( message );
	}




	public DataIntegrityProblemException( String message, String htmlMessage )
	{
		super( message );
		this.htmlMessage = htmlMessage;
	}




	public String getHtmlMessage()
	{
		return htmlMessage;
	}
}
