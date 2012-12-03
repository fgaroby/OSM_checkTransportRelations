/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations.check;

import org.windu2b.osm.check_transport_relations.data.osm.Relation;
import org.windu2b.osm.check_transport_relations.io.OsmTransferException;

/**
 * @author windu
 * 
 */
public class Check
{
	ICheck	 cWay;


	ICheck	 cStopPosition;


	ICheck	 cPlatform;


	ICheck	 cState;


	ICheck	 cRoute;


	ICheck	 cRoute_master;


	Relation	relation;




	public Check()
	{
		this.cWay = new CheckWay( this );
		this.cStopPosition = new CheckStopPosition( this );
		this.cPlatform = new CheckPlatform( this );
		this.cRoute = new CheckRoute( this );
		this.cRoute_master = new CheckRouteMaster( this );

		this.cState = this.cRoute;

	}




	public Check( Relation r )
	{
		this();

		// Cas où on traite une 'route_master'
		if( r.isThisKind( "type", "route_master" ) )
			this.setState( this.cRoute_master );

		this.relation = r;
	}




	public Check( long id )
	{
		this( new Relation( id ) );
	}




	public void setRelation( Relation r )
	{
		this.relation = r;
	}




	public Relation getRelation()
	{
		return this.relation;
	}




	public ICheck setState( ICheck state )
	{
		this.cState = state;

		return this.cState;
	}




	public ICheck getState()
	{
		return this.cState;
	}




	public boolean check() throws OsmTransferException
	{
		return this.cState.check( this.relation );

	}
}