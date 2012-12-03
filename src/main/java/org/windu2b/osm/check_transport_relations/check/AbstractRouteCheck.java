/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations.check;

import static org.windu2b.osm.check_transport_relations.tools.I18n.tr;

import org.windu2b.osm.check_transport_relations.data.osm.Relation;
import org.windu2b.osm.check_transport_relations.io.Log;

/**
 * @author windu
 * 
 */
public abstract class AbstractRouteCheck extends AbstractCheck
{

	/**
	 * @param check
	 */
	public AbstractRouteCheck( Check check )
	{
		super( check );
	}




	protected void traceRouteRelation( Relation r )
	{
		Log.log( "*******************************************" );
		Log.log( tr( "Relation {0} : {1}", r.getId(), r.get( "type" ) ) );
		Log.log( tr( "Name : {0}", r.getName() ) );
		Log.log( "*******************************************" );
	}
}