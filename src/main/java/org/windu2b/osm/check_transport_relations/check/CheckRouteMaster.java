/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations.check;

import static org.windu2b.osm.check_transport_relations.tools.I18n.tr;

import java.util.Collection;

import org.windu2b.osm.check_transport_relations.data.osm.DataSet;
import org.windu2b.osm.check_transport_relations.data.osm.OsmPrimitive;
import org.windu2b.osm.check_transport_relations.data.osm.OsmPrimitiveType;
import org.windu2b.osm.check_transport_relations.data.osm.Relation;
import org.windu2b.osm.check_transport_relations.data.osm.RelationMember;
import org.windu2b.osm.check_transport_relations.io.Log;
import org.windu2b.osm.check_transport_relations.io.OsmServerObjectReader;
import org.windu2b.osm.check_transport_relations.io.OsmTransferException;

/**
 * @author windu
 * 
 */
public class CheckRouteMaster extends AbstractRouteCheck
{

	/**
	 * @param check
	 */
	public CheckRouteMaster( Check check )
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
			        CheckRouteMaster.class.getSimpleName(), op.getType(),
			        op.getId() ) );

			return false;
		}

		Relation r = ( Relation ) op;

		// Is this relation have the good tags ?
		if( !r.isThisKind( "type", "route_master" ) )
		{
			Log.log( tr(
			        "[{0}]Relation {1} is not a 'type=route_master' relation !",
			        CheckRouteMaster.class.getSimpleName(), r.getId() ) );

			return false;
		}

		// On affiche les infos principales de la relation
		traceRouteRelation( r );

		this.check.setState( this.check.cRoute );

		// On boucle sur le contenu de la relation
		boolean checkIsOK = true;
		DataSet ds;
		
		for( RelationMember rm : r.getMembers() )
		{
			OsmPrimitive opRoute = rm.getMember();
			
			OsmServerObjectReader reader = new OsmServerObjectReader(
			        opRoute.getId(), OsmPrimitiveType.RELATION, true );
			ds = reader.parseOsm( null );
			Collection<Relation> cRelations = ds.getRelations();
			for( Relation rel : cRelations )
			{
				if( this.check.cState.check( rel ) == false )
					checkIsOK = false;
			}
		}
		return checkIsOK;
	}




	@Override
	public boolean check( RelationMember rm ) throws OsmTransferException
	{
		throw new IllegalArgumentException( tr(
		        "[{0}]This method should'nt be called !",

		        CheckRouteMaster.class.getSimpleName() ) );
	}
}