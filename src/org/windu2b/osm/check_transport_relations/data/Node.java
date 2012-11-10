/**
 * 
 */
package org.windu2b.osm.check_transport_relations.data;

import java.util.Collection;

import org.windu2b.osm.check_transport_relations.io.OsmServerObjectReader;
import org.windu2b.osm.check_transport_relations.io.OsmTransferException;

/**
 * @author windu
 * 
 */
public class Node extends OsmPrimitive
{

	/**
	 * Constructs an incomplete {@code Node} object with the given id.
	 * 
	 * @param id
	 *            The id. Must be >= 0
	 * @throws IllegalArgumentException
	 *             if id < 0
	 */
	public Node( long id ) throws IllegalArgumentException
	{
		super( id );
	}




	/**
	 * Constructs a new {@code Node} with the given id and version.
	 * 
	 * @param id
	 *            The id. Must be >= 0
	 * @param version
	 *            The version
	 * @throws IllegalArgumentException
	 *             if id < 0
	 */
	public Node( long id, int version ) throws IllegalArgumentException
	{
		super( id, version );
	}




	/**
	 * Constructs an identical clone of the argument (including the id).
	 * 
	 * @param clone
	 *            The node to clone
	 */
	public Node( Node clone )
	{
		super( clone.getUniqueId() );
		cloneFrom( clone );
	}




	@Override
	public int compareTo( OsmPrimitive o )
	{
		return o instanceof Node ? Long.valueOf( getUniqueId() ).compareTo(
		        o.getUniqueId() ) : 1;
	}




	@Override
	public OsmPrimitiveType getType()
	{
		return OsmPrimitiveType.NODE;
	}




	public Collection<Relation> getRelations() throws OsmTransferException
	{
		OsmServerObjectReader reader = new OsmServerObjectReader( getId(),
		        OsmPrimitiveType.NODE, true );
		DataSet ds = reader.parseOsm( null );
		Collection<Relation> c = ds.getRelations();

		return c;
	}




	@Override
	public void load( PrimitiveData data )
	{
		boolean locked = writeLock();
		try
		{
			super.load( data );
		}
		finally
		{
			writeUnlock( locked );
		}
	}




	@Override
	public String toString()
	{
		return "{Node id=" + getUniqueId() + " version=" + getVersion() + " "
		        + "}";
	}




	public boolean isThisKindOfNode( String key, String value )
	{
		return this.getKeys().containsKey( key ) == true
		        && this.getKeys().get( key ).equals( value ) == true;
	}
}
