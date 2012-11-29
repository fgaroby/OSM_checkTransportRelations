/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations;

import static org.windu2b.osm.check_transport_relations.tools.I18n.tr;
import java.io.IOException;
import java.util.Collection;

import org.windu2b.osm.check_transport_relations.check.Check;
import org.windu2b.osm.check_transport_relations.data.osm.DataSet;
import org.windu2b.osm.check_transport_relations.data.osm.OsmPrimitiveType;
import org.windu2b.osm.check_transport_relations.data.osm.Relation;
import org.windu2b.osm.check_transport_relations.io.Log;
import org.windu2b.osm.check_transport_relations.io.OsmServerObjectReader;

/**
 * @author windu
 * 
 */
public class Main
{

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main( String[] args )
	{
		if( args.length == 0 )
		{
			Log.log( tr( "No relation ID found ! Usage : org.windu2b.osm.check_transport_relations.Main <Relation ID>" ) );

			return;
		}

		int relationId = Integer.parseInt( args[0] );
		DataSet ds;

		try
		{
			OsmServerObjectReader reader = new OsmServerObjectReader(
			        relationId, OsmPrimitiveType.RELATION, true );
			ds = reader.parseOsm( null );
			Collection<Relation> cRelations = ds.getRelations();
			for( Relation r : cRelations )
			{
				Check c = new Check( r );
				c.check();
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}

}