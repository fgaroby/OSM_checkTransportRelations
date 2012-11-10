/**
 * License: GPL. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations.data;


/**
 * @author windu
 * 
 */
public interface PrimitiveId
{
	/**
	 * Gets a unique id representing this object (the OSM server id for OSM
	 * objects)
	 * 
	 * @return the id number
	 */
	long getUniqueId();




	/**
	 * Gets the type of object represented by this object.
	 * 
	 * @see Node
	 * @see Way
	 * @see Relation
	 * @return the object type
	 */
	OsmPrimitiveType getType();
}
