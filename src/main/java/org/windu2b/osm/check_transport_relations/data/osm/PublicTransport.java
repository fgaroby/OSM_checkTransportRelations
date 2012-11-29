/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations.data.osm;

import static org.windu2b.osm.check_transport_relations.tools.I18n.tr;

import org.windu2b.osm.check_transport_relations.io.Log;
import org.windu2b.osm.check_transport_relations.io.OsmTransferException;

/**
 * @author windu
 * 
 */
public class PublicTransport
{
	public String	PUBLIC_TRANSPORT	= "public_transport";


	public String	STOP_AREA	     = "stop_area";


	public String	STOP_POSITION	 = "stop_position";


	public String	PLATFORM	     = "platform";




	public static Relation getStopAreaRelation( OsmPrimitive primitive )
	        throws OsmTransferException
	{
		Relation stopArea = null;

		/*
		 * Si tout est OK, on récupère la relation, qui doit être
		 * 'type=public_transport & public_transport=stop_area', à laquelle le
		 * node appartient
		 */
		Tag[] tags = { new Tag( "type", "public_transport" ),
		        new Tag( "public_transport", "stop_area" ) };
		TagCollection tagCollection = new TagCollection( tags );
		stopArea = primitive.getRelation( tagCollection );

		if( stopArea == null )
		{
			Log.log( tr(
			        "[{0}]The {1} {2} is not in a 'public_transport=stop_area' relation !",
			        PublicTransport.class.getSimpleName(), primitive.getDisplayType(), primitive.getId() ) );
		}

		return stopArea;
	}




	public static boolean isStopPosition( Node node )
	{
		return node.isThisKind( "public_transport", "stop_position" );
	}




	public static boolean isPlatform( OsmPrimitive op )
	{
		return op.isThisKind( "public_transport", "platform" )
		        || op.isThisKind( "public_transport", "station" );
	}
}