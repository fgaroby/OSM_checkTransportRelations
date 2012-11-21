/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations.check;

import org.windu2b.osm.check_transport_relations.data.osm.Relation;
import org.windu2b.osm.check_transport_relations.data.osm.RelationMember;
import org.windu2b.osm.check_transport_relations.io.IllegalDataException;
import org.windu2b.osm.check_transport_relations.io.OsmTransferException;

/**
 * @author windu
 * 
 */
public class Check
{
	ICheck	 start;


	ICheck	 way;


	ICheck	 stop_position;


	ICheck	 platform;


	ICheck	 state;


	Relation	relation;




	public Check()
	{
		this.way = new CheckWay( this );
		this.stop_position = new CheckStopPosition( this );
		this.platform = new CheckPlatform( this );

		this.state = this.way;

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
		this.state = state;
		
		return this.state;
	}




	public ICheck getState()
	{
		return this.state;
	}




	public void check( Relation r ) throws IllegalDataException,
	        OsmTransferException
	{
		this.setRelation( r );
		check();
	}




	public void check() throws IllegalDataException, OsmTransferException
	{
		for( RelationMember rm : this.relation.getMembers() )
		{
			this.state.check( rm );

		}
	}
}