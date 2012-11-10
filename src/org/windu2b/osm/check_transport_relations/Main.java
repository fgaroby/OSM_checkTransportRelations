/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations;

import java.io.IOException;
import java.util.Collection;

import org.windu2b.osm.check_transport_relations.check.Check;
import org.windu2b.osm.check_transport_relations.data.DataSet;
import org.windu2b.osm.check_transport_relations.data.OsmPrimitiveType;
import org.windu2b.osm.check_transport_relations.data.Relation;
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
		int relationId = 2323110;
		DataSet ds;

		try
		{
			OsmServerObjectReader reader = new OsmServerObjectReader(
			        relationId, OsmPrimitiveType.RELATION, true );
			ds = reader.parseOsm( null );
			Collection<Relation> cRelations = ds.getRelations();
			for ( Relation r : cRelations )
			{
				Check.checkRelation( r );
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

}