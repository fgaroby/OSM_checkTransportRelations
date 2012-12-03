/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations.check;

import static org.windu2b.osm.check_transport_relations.tools.I18n.tr;

import org.windu2b.osm.check_transport_relations.data.osm.Node;
import org.windu2b.osm.check_transport_relations.data.osm.OsmPrimitive;
import org.windu2b.osm.check_transport_relations.data.osm.OsmPrimitiveType;
import org.windu2b.osm.check_transport_relations.data.osm.PublicTransport;
import org.windu2b.osm.check_transport_relations.data.osm.Relation;
import org.windu2b.osm.check_transport_relations.data.osm.Way;
import org.windu2b.osm.check_transport_relations.data.osm.Way.Direction;
import org.windu2b.osm.check_transport_relations.io.Log;
import org.windu2b.osm.check_transport_relations.io.OsmTransferException;

/**
 * @author windu
 * 
 */
public class CheckStopPosition extends AbstractCheck
{

	public CheckStopPosition( Check check )
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
		boolean validation = true;

		/*
		 * Is this a 'node' ?
		 */
		if( op.getDisplayType() == OsmPrimitiveType.NODE )
		{
			Node node = ( Node ) op;
			Node lastStopPosition = LastElements.getLastStopPosition();
			Way lastWay = LastElements.getLastWay();

			/*
			 * Is it a 'public_transport=stop_position' type ?
			 */
			if( PublicTransport.isStopPosition( node ) )
			{
				// Is it the first node after 'way' (or after a previous
				// 'platform') ?
				if( lastStopPosition != null )
				{
					Log.log( tr(
					        "[{0}]No consecutive 'public_transport=stop_position' nodes allowed ! Node {1} is after node {2}",
					        CheckStopPosition.class.getSimpleName(),
					        node.getId(), lastStopPosition.getId() ) );

					validation = false;
				}
			}
			/*
			 * Anything else ? Error
			 */
			else
			{
				Log.log( tr(
				        "[{0}]}The node {1} is not a public_transport=stop_position",
				        CheckStopPosition.class.getSimpleName(), node.getId() ) );

				validation = false;
			}

			/*
			 * Is there at least one 'way' BEFORE this 'stop_position' ?
			 */
			if( lastWay == null )
			{
				Log.log( tr( "[{0}]No way was found before the node {1} !",
				        CheckStopPosition.class.getSimpleName(), node.getId() ) );

				validation = false;
			}

			/*
			 * Is this 'stop_position' node on the last 'way' ?
			 */
			else if( !lastWay.contains( node ) )
			{
				Log.log( tr( "[{0}]The way {1} doesn''t contain the node {2} !",
				        CheckStopPosition.class.getSimpleName(),
				        lastWay.getId(), node.getId() ) );

				validation = false;
			}
			/*
			 * Is this node not the last/first node of the last way ?
			 */
			if( lastWay.getDirection() != null )
			{
				switch( lastWay.getDirection() )
				{
					case FORWARD :
						if( lastWay.getLastNode().equals( node ) )
						{
							Log.log( tr(
							        "[{0}]The node {1} has not to be the last node of the forward-direction way {2} !",
							        CheckStopPosition.class.getSimpleName(),
							        node.getId(), lastWay.getId() ) );
						}
					break;

					case BACKWARD :
						if( lastWay.getFirstNode().equals( node ) )
						{
							Log.log( tr(
							        "[{0}]The node {1} has not to be the first node of the backward-direction way {2} !",
							        CheckStopPosition.class.getSimpleName(),
							        node.getId(), lastWay.getId() ) );
						}
					break;

				}
			}
			// Cas où la direction de 'lastWay' n'avait pas été
			// préalablement définie (premier way, ou coupure dans la
			// continuité)
			else
			{
				if( lastWay.getLastNode().equals( node ) )
					lastWay.setDirection( Direction.BACKWARD );
				else
					lastWay.setDirection( Direction.FORWARD );
			}
			// On récupère la relation 'stop_area' à laquelle appartient ce
			// 'stop_position'
			Relation stopArea = PublicTransport.getStopAreaRelation( op );

			// Si aucune 'stop_area' n'a été trouvée
			if( stopArea == null )
			{
				Log.log( tr(
				        "[{0}]No stop_area relation found for the stop_position node {1}!",
				        CheckStopPosition.class.getSimpleName(), op.getId() ) );
			}
			LastElements.setLastStopArea( stopArea );

			// On pointe sur le 'stop_position' en cours
			LastElements.setLastStopPosition( node );

			this.check.setState( this.check.cPlatform );
		}
		/*
		 * Not a node ? Error !
		 */
		else
		{
			Log.log( tr(
			        "[{0}]The {1} {2} is not a public_transport=stop_position relation",
			        CheckStopPosition.class.getSimpleName(),
			        op.getDisplayType(), op.getId() ) );

			validation = false;
		}

		this.check.setState( this.check.cPlatform );

		return validation;
	}
}
