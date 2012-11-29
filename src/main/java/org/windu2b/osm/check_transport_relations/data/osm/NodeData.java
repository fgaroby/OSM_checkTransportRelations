// License: GPL. For details, see LICENSE file.
package org.windu2b.osm.check_transport_relations.data.osm;

public class NodeData extends PrimitiveData implements INode
{
	public NodeData()
	{
	}




	public NodeData( NodeData data )
	{
		super( data );
	}




	@Override
	public NodeData makeCopy()
	{
		return new NodeData( this );
	}




	@Override
	public String toString()
	{
		return super.toString() + " NODE " + getId();
	}




	@Override
	public OsmPrimitiveType getType()
	{
		return OsmPrimitiveType.NODE;
	}
}
