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
		// Is it a node ?
		if( rm.getType().equals( OsmPrimitiveType.NODE ) )
		{
			// Is it a 'stop position' node ?
			if( rm.getRole().equals( "stop" ) )
				this.check.setState( this.check.cStopPosition );
			// Or is it a 'platform' node ?
			else if( rm.getRole().equals( "platform" ) )
				this.check.setState( this.check.cPlatform );

			return this.check.cState.check( rm.getMember() );

		}

		// Or is it a way ?
		else if( rm.getType().equals( OsmPrimitiveType.WAY ) )
		{
			// No role ? It's a 'way'
			if( !rm.hasRole() )
			{
				return this.check.setState( this.check.cWay ).check(
				        rm.getMember() );
			}
			// Has a role ? It's a 'platform' or a 'station'
			else if( !rm.getRole().equals( "platform" ) )
			{
				Log.log( tr(
				        "[{0}]RelationMember {1} has not the role 'platform' !",
				        AbstractCheck.class.getSimpleName(), rm.getMember()
				                .getId() ) );
				return false;
			}

			return check( rm.getMember() );
		}

		// Or is it a relation ?
		else if( rm.getType().equals( OsmPrimitiveType.RELATION ) )
		{
			// Is it a 'route' ?
			if( rm.getMember().isThisKind( "type", "route" ) )
			{
				return this.check.setState( this.check.cRoute ).check(
				        rm.getMember() );
			}
			// Or a 'route_master' ?
			else if( rm.getMember().isThisKind( "type", "route_master" ) )
			{
				return this.check.setState( this.check.cRoute_master ).check(
				        rm.getMember() );
			}
			// Anything else ? Error
			else
			{
				Log.log( tr(
				        "[{0}]RelationMember {1} is neither a 'route' nor a 'route_master' relation !",
				        AbstractCheck.class.getSimpleName(), rm.getMember()
				                .getId() ) );
				return false;
			}
		}

		// Anything else ? Not supported...
		else
		{
			Log.log( tr( "[{0}]The type {1} is not supported !",
			        AbstractCheck.class.getSimpleName(), rm.getType() ) );

			return false;
		}
	}

}