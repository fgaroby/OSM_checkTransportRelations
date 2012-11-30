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


	ICheck	 cPublicTransport;


	Relation	relation;




	public Check()
	{
		this.cWay = new CheckWay( this );
		this.cStopPosition = new CheckStopPosition( this );
		this.cPlatform = new CheckPlatform( this );
		this.cPublicTransport = new CheckPublicTransport( this );

		this.cState = this.cPublicTransport;

	}




	public Check( Relation r )
	{
		this();
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