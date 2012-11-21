package org.windu2b.osm.check_transport_relations.data.osm;

import java.util.List;

import org.windu2b.osm.check_transport_relations.tools.CopyList;

public class Relation extends OsmPrimitive
{
	private RelationMember[]	members	= new RelationMember[0];




	/**
	 * Creates a new relation for the given id. If the id > 0, the way is marked
	 * as incomplete.
	 * 
	 * @param id
	 *            the id. > 0 required
	 * @throws IllegalArgumentException
	 *             thrown if id < 0
	 */
	public Relation( long id ) throws IllegalArgumentException
	{
		super( id );
	}




	/**
	 * Creates new relation
	 * 
	 * @param id
	 * @param version
	 */
	public Relation( long id, int version )
	{
		super( id, version );
	}




	public Relation( Relation clone )
	{
		super( clone.getUniqueId() );
		cloneFrom( clone );
	}




	public void addMember( RelationMember member )
	{
		boolean locked = writeLock();
		try
		{
			RelationMember[] newMembers = new RelationMember[members.length + 1];
			System.arraycopy( members, 0, newMembers, 0, members.length );
			newMembers[members.length] = member;
			members = newMembers;
		}
		finally
		{
			writeUnlock( locked );
		}
	}




	public void addMember( int index, RelationMember member )
	{
		boolean locked = writeLock();
		try
		{
			RelationMember[] newMembers = new RelationMember[members.length + 1];
			System.arraycopy( members, 0, newMembers, 0, index );
			System.arraycopy( members, index, newMembers, index + 1,
			        members.length - index );
			newMembers[index] = member;
			members = newMembers;
		}
		finally
		{
			writeUnlock( locked );
		}
	}




	/**
	 * @return Members of the relation. Changes made in returned list are not
	 *         mapped back to the primitive, use setMembers() to modify the
	 *         members
	 * @since 1925
	 */
	public List<RelationMember> getMembers()
	{
		return new CopyList<RelationMember>( members );
	}




	/**
	 * 
	 * @param members
	 *            Can be null, in that case all members are removed
	 * @since 1925
	 */
	public void setMembers( List<RelationMember> members )
	{
		if( members != null )
		{
			this.members = members.toArray( new RelationMember[members.size()] );
		}
		else
		{
			this.members = new RelationMember[0];
		}
	}




	/**
	 * Replies true if at least one child primitive is incomplete
	 * 
	 * @return true if at least one child primitive is incomplete
	 */
	public boolean hasIncompleteMembers()
	{
		RelationMember[] members = this.members;
		for( RelationMember rm : members )
		{
			if( rm.getMember().isIncomplete() ) return true;
		}
		return false;
	}




	@Override
	public int compareTo( OsmPrimitive o )
	{
		return o instanceof Relation ? Long.valueOf( getUniqueId() ).compareTo(
		        o.getUniqueId() ) : -1;
	}




	@Override
	public OsmPrimitiveType getType()
	{
		// TODO Auto-generated method stub
		return OsmPrimitiveType.RELATION;
	}




	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append( "{Relation id=" + getUniqueId() + " version=" + getVersion() + " [" );
		for( RelationMember rm : this.getMembers() )
		{
			OsmPrimitive member = rm.getMember();
			sb.append( member.getDisplayType() ).append( "(" ).append( member.getId() ).append( ")" );
		}
		
		return sb.toString();
	}

}
