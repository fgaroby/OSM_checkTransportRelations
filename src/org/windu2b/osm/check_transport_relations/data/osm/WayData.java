// License: GPL. For details, see LICENSE file.
package org.windu2b.osm.check_transport_relations.data.osm;

import java.util.ArrayList;
import java.util.List;

public class WayData extends PrimitiveData implements IWay
{

	private List<Long>	nodes	= new ArrayList<Long>();




	public WayData()
	{

	}




	public WayData( WayData data )
	{
		super( data );
		nodes.addAll( data.getNodes() );
	}




	public List<Long> getNodes()
	{
		return nodes;
	}




	@Override
	public int getNodesCount()
	{
		return nodes.size();
	}




	@Override
	public long getNodeId( int idx )
	{
		return nodes.get( idx );
	}




	@Override
	public boolean isClosed()
	{
		if ( isIncomplete() ) return false;
		return nodes.get( 0 ).equals( nodes.get( nodes.size() - 1 ) );
	}




	public void setNodes( List<Long> nodes )
	{
		this.nodes = new ArrayList<Long>( nodes );
	}




	@Override
	public WayData makeCopy()
	{
		return new WayData( this );
	}




	@Override
	public String toString()
	{
		return super.toString() + " WAY" + nodes.toString();
	}




	@Override
	public OsmPrimitiveType getType()
	{
		return OsmPrimitiveType.WAY;
	}

}
