/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations.check;

import static org.windu2b.osm.check_transport_relations.tools.I18n.tr;

import org.windu2b.osm.check_transport_relations.data.osm.OsmPrimitive;
import org.windu2b.osm.check_transport_relations.data.osm.OsmPrimitiveType;
import org.windu2b.osm.check_transport_relations.data.osm.Relation;
import org.windu2b.osm.check_transport_relations.data.osm.RelationMember;
import org.windu2b.osm.check_transport_relations.data.osm.Tag;
import org.windu2b.osm.check_transport_relations.io.Log;
import org.windu2b.osm.check_transport_relations.io.OsmTransferException;

/**
 * @author windu
 * 
 */
public class CheckPublicTransport extends AbstractCheck
{

	/**
	 * @param check
	 */
	public CheckPublicTransport( Check check )
	{
		super( check );
	}




	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.windu2b.osm.check_transport_relations.check.ICheck#check(org.windu2b
	 * .osm.check_transport_relations.data.osm.RelationMember)
	 */
	@Override
	public boolean check( OsmPrimitive op ) throws OsmTransferException
	{
		// Is it a relation ?
		if( !op.getType().equals( OsmPrimitiveType.RELATION ) )
		{
			Log.log( tr( "[{0}]{1} {2} is not a Relation !",
			        CheckPublicTransport.class.getSimpleName(), op.getType(),
			        op.getId() ) );

			return false;
		}

		Relation r = ( Relation ) op;

		// Is this relation have the good tags ?
		if( !r.isThisKind( "type", "route" ) )
		{
			Log.log( tr( "[{0}]Relation {1} is not a 'type=route' relation !",
			        CheckPublicTransport.class.getSimpleName(), r.getId() ) );

			return false;
		}

		Tag tagType = new Tag( "route", r.get( "route" ) );
		System.out.println( tagType );

		this.check.setState( this.check.cWay );
		
		// On boucle sur le contenu de la relation
		boolean checkIsOK = true;
		for( RelationMember rm : r.getMembers() )
		{
			if( this.check.cState.check( rm ) == false )
					checkIsOK = false;
		}
		return checkIsOK;
	}




	@Override
	public boolean check( RelationMember rm ) throws OsmTransferException
	{
		throw new IllegalArgumentException( tr(
		        "[{0}]This method should'nt be called !",
		        CheckPublicTransport.class.getSimpleName() ) );
	}
}