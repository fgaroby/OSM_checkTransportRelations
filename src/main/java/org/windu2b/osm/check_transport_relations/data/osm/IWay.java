// License: GPL. For details, see LICENSE file.
package org.windu2b.osm.check_transport_relations.data.osm;

public interface IWay extends IPrimitive
{

	int getNodesCount();




	long getNodeId( int idx );




	boolean isClosed();

}
