// License: GPL. For details, see LICENSE file.
package org.windu2b.osm.check_transport_relations.data.osm;

import java.util.Date;

/**
 * IPrimitive captures the common functions of OsmPrimitive and PrimitiveData.
 */
public interface IPrimitive extends Tagged, PrimitiveId
{

	boolean isIncomplete();




	long getId();




	PrimitiveId getPrimitiveId();




	int getVersion();




	void setOsmId( long id, int version );




	Date getTimestamp();




	void setTimestamp( Date timestamp );




	String getName();




	boolean isModified();

}
