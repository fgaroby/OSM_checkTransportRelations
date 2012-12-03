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

		/*
		 * On découpe le paramètre d'entrée en autant d'id de relations que
		 * possible
		 */
		String[] ids = args[0].split( ";" );
		DataSet ds;

		try
		{
			// On boucle sur les id de relation passés en paramètre
			for( String s : ids )
			{
				String[] relationId;
				if( s.contains( "-" ) )
					relationId = s.split( "-" );
				else
				{
					relationId = new String[1];
					relationId[0] = s;
				}

				for( String idx : relationId )
				{
					OsmServerObjectReader reader = new OsmServerObjectReader(
					        Integer.parseInt( idx ), OsmPrimitiveType.RELATION,
					        true );
					ds = reader.parseOsm( null );
					Collection<Relation> cRelations = ds.getRelations();
					for( Relation r : cRelations )
					{
						Check c = new Check( r );
						c.check();
					}
				}
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
}