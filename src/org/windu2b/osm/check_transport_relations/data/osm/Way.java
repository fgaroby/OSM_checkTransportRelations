package org.windu2b.osm.check_transport_relations.data.osm;

import java.util.Arrays;
import java.util.List;

import org.windu2b.osm.check_transport_relations.tools.CopyList;

public class Way extends OsmPrimitive implements IWay
{
	/**
	 * All way nodes in this way
	 * 
	 */
	private Node[]	nodes	= new Node[0];




	/**
	 * Contructs a new {@code Way} for the given id. If the id > 0, the way is
	 * marked as incomplete. If id == 0 then way is marked as new
	 * 
	 * @param id
	 *            the id. >= 0 required
	 * @throws IllegalArgumentException
	 *             if id < 0
	 * @since 343
	 */
	public Way( long id ) throws IllegalArgumentException
	{
		super( id );
	}




	/**
	 * Contructs a new {@code Way} with given id and version.
	 * 
	 * @param id
	 *            the id. >= 0 required
	 * @param version
	 *            the version
	 * @throws IllegalArgumentException
	 *             if id < 0
	 * @since 2620
	 */
	public Way( long id, int version ) throws IllegalArgumentException
	{
		super( id, version );
	}




	/**
	 * Contructs a new {@code Way} from an existing {@code Way}.
	 * 
	 * @param original
	 *            The original {@code Way} to be identically cloned. Must not be
	 *            null
	 * @since 2410
	 */
	public Way( Way original )
	{
		super( original.getUniqueId() );
		cloneFrom( original );
	}




	@Override
	public int compareTo( OsmPrimitive o )
	{
		if ( o instanceof Relation ) return 1;
		return o instanceof Way ? Long.valueOf( getUniqueId() ).compareTo(
		        o.getUniqueId() ) : -1;
	}




	@Override
	public OsmPrimitiveType getType()
	{
		return OsmPrimitiveType.WAY;
	}




	@Override
	public OsmPrimitiveType getDisplayType()
	{
		return isClosed() ? OsmPrimitiveType.CLOSEDWAY : OsmPrimitiveType.WAY;
	}




	@Override
	public boolean isClosed()
	{
		Node[] nodes = this.nodes;
		return nodes.length >= 3 && nodes[nodes.length - 1] == nodes[0];
	}




	/**
	 * Replies true if this way has incomplete nodes, false otherwise.
	 * 
	 * @return true if this way has incomplete nodes, false otherwise.
	 * @since 2587
	 */
	public boolean hasIncompleteNodes()
	{
		Node[] nodes = this.nodes;
		for ( Node node : nodes )
		{
			if ( node.isIncomplete() ) return true;
		}
		return false;
	}




	/**
	 * Replies the number of nodes in this ways.
	 * 
	 * @return the number of nodes in this ways.
	 * @since 1862
	 */
	@Override
	public int getNodesCount()
	{
		return nodes.length;
	}




	/**
	 * Replies the node at position <code>index</code>.
	 * 
	 * @param index
	 *            the position
	 * @return the node at position <code>index</code>
	 * @exception IndexOutOfBoundsException
	 *                thrown if <code>index</code> < 0 or <code>index</code> >=
	 *                {@link #getNodesCount()}
	 * @since 1862
	 */
	public Node getNode( int index )
	{
		return nodes[index];
	}




	/**
	 * Replies the last node.
	 * 
	 * @return the last node
	 */
	public Node getLastNode()
	{
		return getNode( nodes.length - 1 );
	}




	/**
	 * Replies the first node.
	 * 
	 * @return the first node
	 */
	public Node getFirstNode()
	{
		return getNode( 0 );
	}




	@Override
	public long getNodeId( int idx )
	{
		return nodes[idx].getUniqueId();
	}




	/**
	 * 
	 * You can modify returned list but changes will not be propagated back to
	 * the Way. Use {@link #setNodes(List)} to update this way
	 * 
	 * @return Nodes composing the way
	 * @since 1862
	 */
	public List<Node> getNodes()
	{
		return new CopyList<Node>( nodes );
	}




	/**
	 * Set new list of nodes to way. This method is preferred to multiple calls
	 * to addNode/removeNode and similar methods because nodes are internally
	 * saved as array which means lower memory overhead but also slower
	 * modifying operations.
	 * 
	 * @param nodes
	 *            New way nodes. Can be null, in that case all way nodes are
	 *            removed
	 * @since 1862
	 */
	public void setNodes( List<Node> nodes )
	{
		boolean locked = writeLock();
		try
		{
			if ( nodes == null )
			{
				this.nodes = new Node[0];
			}
			else
			{
				this.nodes = nodes.toArray( new Node[nodes.size()] );
			}
		}
		finally
		{
			writeUnlock( locked );
		}
	}




	@Override
	public String toString()
	{
		String nodesDesc = "nodes=" + Arrays.toString( nodes );
		return "{Way id=" + getUniqueId() + " version=" + getVersion() + " "
		        + nodesDesc + "}";
	}
}
