/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations.check;

import static org.windu2b.osm.check_transport_relations.tools.I18n.tr;

import org.windu2b.osm.check_transport_relations.data.osm.OsmPrimitive;
import org.windu2b.osm.check_transport_relations.data.osm.PublicTransport;
import org.windu2b.osm.check_transport_relations.data.osm.Relation;
import org.windu2b.osm.check_transport_relations.io.Log;
import org.windu2b.osm.check_transport_relations.io.OsmTransferException;

/**
 * @author windu
 * 
 */
public class CheckPlatform extends AbstractCheck
{

	public CheckPlatform( Check check )
	{
		super( check );
	}




	/*
	 * (non-Javadoc)
	 * 
	 * @see org.windu2b.osm.check_transport_relations.check.ICheck#check()
	 */
	@Override
	public boolean check( OsmPrimitive op ) throws OsmTransferException
	{
		/*
		 * S'il s'agit d'un 'public_transport=platform' ou d'un
		 * 'public_transport=station'
		 */
		if( PublicTransport.isPlatform( op ) )
		{
			Relation lastStopArea = LastElements.lastStopArea;
			Relation stopArea = PublicTransport.getStopAreaRelation( op );

			if( lastStopArea == null )
			{
				/*
				 * Si on n'a pas au préalable rencontré de 'stop_position', il
				 * ne peut y avoir de loastStopArea
				 */
				Log.log( tr(
				        "[{0}]No 'stop_position' found before this 'platform' ?",
				        CheckPlatform.class.getSimpleName() ) );
			}
			else if( stopArea != null )
			{
				/*
				 * On vérifie que le node appartient à la même relation
				 * 'public_transport=stop_area' que le dernier node
				 * 'stop_position' rencontré
				 */
				if( !PublicTransport.getStopAreaRelation( op ).equals(
				        lastStopArea ) )
				{
					Log.log( tr(
					        "[{0}]The {1} {2} is not in the same 'public_transport=stop_area' ({3}) than the previous 'public_transport='stop_position' !",
					        CheckPlatform.class.getSimpleName(),
					        op.getDisplayType(), op.getId(),
					        lastStopArea.getId() ) );

					return false;
				}
			}
			else
			{
				Log.log( tr(
				        "[{0}]The {1} {2} is not associated to a 'stop_area' relation",
				        CheckPlatform.class.getSimpleName(),
				        op.getDisplayType(), op.getId() ) );

				return false;
			}
		}
		// Error cases
		else
		{
			Log.log( tr(
			        "[{0}]{1} {2} is neither a 'public_transport=station' nor a 'public_transport=platform'",
			        CheckPlatform.class.getSimpleName(), op.getType(),
			        op.getId() ) );

			return false;
		}

		LastElements.setLastStopPosition( null );

		return true;
	}
}
