/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations.check;

import static org.windu2b.osm.check_transport_relations.tools.I18n.tr;

import org.windu2b.osm.check_transport_relations.data.osm.OsmPrimitiveType;
import org.windu2b.osm.check_transport_relations.data.osm.RelationMember;
import org.windu2b.osm.check_transport_relations.io.Log;
import org.windu2b.osm.check_transport_relations.io.OsmTransferException;

/**
 * @author windu
 * 
 */
public abstract class AbstractCheck implements ICheck
{
	protected Check	check;




	protected AbstractCheck( Check check )
	{
		this.check = check;
	}




	/**
	 * @return the check
	 */
	@Override
	public Check getCheck()
	{
		return check;
	}




	/**
	 * @param check
	 *            the check to set
	 */
	@Override
	public void setCheck( Check check )
	{
		this.check = check;
	}




	@Override
	public boolean check( RelationMember rm ) throws OsmTransferException
	{
		if( rm.getType().equals( OsmPrimitiveType.NODE ) )
		{
			return this.check.setState( this.check.cStopPosition ).check( rm );
		}

		else if( rm.getType().equals( OsmPrimitiveType.WAY ) )
		{
			if( !rm.hasRole() )
			{
				return this.check.setState( this.check.cWay ).check( rm );
			}
			else if( !rm.getRole().equals( "platform" ) )
			{
				Log.log( tr(
				        "[{0}]RelationMember {1} has not the role 'platform' !",
				        CheckPlatform.class.getSimpleName(), rm.getMember()
				                .getId() ) );
				return false;
			}

			return check( rm.getMember() );
		}
		else
		{
			Log.log( tr(
			        "[{0}]The type {1} is not supported !",
			        CheckPlatform.class.getSimpleName(), rm.getType() ) );
			
			return false;
		}
	}

}