// License: GPL. For details, see LICENSE file.
package org.windu2b.osm.check_transport_relations.data;

public interface IRelation extends IPrimitive
{

	int getMembersCount();




	long getMemberId( int idx );




	String getRole( int idx );




	OsmPrimitiveType getMemberType( int idx );

}
