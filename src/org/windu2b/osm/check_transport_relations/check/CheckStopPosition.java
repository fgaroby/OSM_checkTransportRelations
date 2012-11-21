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
import org.windu2b.osm.check_transport_relations.data.osm.RelationMember;
import org.windu2b.osm.check_transport_relations.data.osm.Way;
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
	public boolean check( RelationMember rm ) throws OsmTransferException
	{
		OsmPrimitive op = rm.getMember();

		// Si le membre en cours est un 'node'
		if( op.getDisplayType() == OsmPrimitiveType.NODE )
		{
			Node node = ( Node ) op;
			Node lastStopPosition = LastElements.getLastStopPosition();
			Way lastWay = LastElements.getLastWay();

			// S'il s'agit du premier node après un 'way'
			if( lastStopPosition == null )
			{
				// Est-il bien de type 'public_transport=stop_position' ?
				if( !PublicTransport.isStopPosition( node ) )
				{
					Log.log( tr(
					        "The node {0} is not a 'public_transport=stop_position'",
					        node.getId() ) );

					return false;
				}
			}
			/*
			 * Si c'est un nouveau 'stop_position' => on vérifie qu'il n'y a pas
			 * 2 'stop_position' qui se suivent immédiatement
			 */
			else if( lastStopPosition.isThisKind( "public_transport",
			        "stop_position" )
			        && node.isThisKind( "public_transport", "stop_position" ) )
			{
				Log.log( tr(
				        "No consecutive 'public_transport=stop_position' nodes allowed ! Node {0} is after node {1}",
				        node.getId(), lastStopPosition.getId() ) );

				return false;
			}
			// C'est autre chose => erreur
			else
			{
				Log.log( tr(
				        "The node {0} is not a 'public_transport='stop_position",
				        node.getId() ) );

				return false;
			}

			/*
			 * On vérifie qu'au moins un 'way' a bien été rencontré AVANT le
			 * 'stop_position'
			 */
			if( lastWay == null )
			{
				Log.log( tr( "No way was found before the node {0} !",
				        node.getId() ) );

				return false;
			}

			/*
			 * On vérifie que le 'stop_position' se trouve sur le dernier 'way'
			 * parcouru
			 */
			else if( !lastWay.contains( node ) )
			{
				Log.log( tr( "The way {0} doesn't contain the node {1} !",
				        lastWay.getId(), node.getId() ) );

				return false;
			}
			else if( lastWay.getLastNode().equals( node ) )
			{
				Log.log( tr(
				        "The node {0} hasn't to be the last node of the way {1} !",
				        node.getId(), lastWay.getId() ) );

				return false;
			}

			// On récupère la relation 'stop_area' à laquelle appartient ce
			// 'stop_position'
			Relation stopArea = PublicTransport.loadStopArea( op );

			// Si aucune 'stop_area' n'a été trouvée
			if( stopArea == null )
			{
				Log.log( tr(
				        "No 'stop_area' relation found for the 'stop_position node {0}!",
				        op.getId() ) );
			}
			LastElements.setLastStopArea( stopArea );

			// On pointe sur le 'stop_position' en cours
			LastElements.setLastStopPosition( node );

			this.check.setState( this.check.platform );
		}
		// Error cases
		else
		{
			Log.log( tr(
			        "The {0} {1} is not a 'public_transport='stop_position",
			        op.getDisplayType(), op.getId() ) );

			return false;
		}

		this.check.setState( this.check.platform );

		return true;
	}

}
