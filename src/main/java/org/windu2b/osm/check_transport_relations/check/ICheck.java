/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations.check;

import org.windu2b.osm.check_transport_relations.data.osm.OsmPrimitive;
import org.windu2b.osm.check_transport_relations.data.osm.RelationMember;
import org.windu2b.osm.check_transport_relations.io.OsmTransferException;

/**
 * @author windu
 * 
 */
public interface ICheck
{

	public boolean check( OsmPrimitive op ) throws OsmTransferException;




	public boolean check( RelationMember rm ) throws OsmTransferException;




	Check getCheck();




	void setCheck( Check check );
}