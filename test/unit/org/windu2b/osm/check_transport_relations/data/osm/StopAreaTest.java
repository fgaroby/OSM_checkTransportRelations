/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations.data.osm;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.windu2b.osm.check_transport_relations.io.OsmTransferException;

/**
 * @author windu
 * 
 */
public class StopAreaTest
{

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
	}




	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}




	@Test
	public final void testABadStopAreaRelation()
	{
		OsmPrimitive op = new Node( 1 );

		try
		{
			Relation stopArea = PublicTransport.getStopAreaRelation( op );

			assertNull( stopArea );
		}
		catch( OsmTransferException e )
		{
			e.printStackTrace();
			fail();
		}
	}




	@Test
	public final void testContentOfAGoodStopAreaRelationWithOneStopAndOnePlatform()
	{
		OsmPrimitive op = new Node( 1932394892 );

		try
		{
			Relation stopArea = PublicTransport.getStopAreaRelation( op );

			assertEquals( 2434878, stopArea.getId() );

			List<RelationMember> lMembers = stopArea.getMembers();
			assertEquals( 4, lMembers.size() );
			assertEquals( 1932394892, lMembers.get( 0 ).getUniqueId() );
			assertEquals( Node.class, lMembers.get( 0 ).getMember().getClass() );
			assertEquals( "stop", lMembers.get( 0 ).getRole() );

			assertEquals( 1844024381, lMembers.get( 1 ).getUniqueId() );
			assertEquals( Node.class, lMembers.get( 1 ).getMember().getClass() );
			assertEquals( "platform", lMembers.get( 1 ).getRole() );

			assertEquals( 1932394889, lMembers.get( 2 ).getUniqueId() );
			assertEquals( Node.class, lMembers.get( 2 ).getMember().getClass() );
			assertEquals( "stop", lMembers.get( 2 ).getRole() );

			assertEquals( 1735411785, lMembers.get( 3 ).getUniqueId() );
			assertEquals( Node.class, lMembers.get( 3 ).getMember().getClass() );
			assertEquals( "platform", lMembers.get( 3 ).getRole() );
		}
		catch( OsmTransferException e )
		{
			e.printStackTrace();
			fail();
		}
	}




	@Test
	public final void testContentOfAGoodStopAreaRelationWithOneStopAndSeveralPlatforms()
	{
		OsmPrimitive op = new Node( 1844116848 );

		try
		{
			Relation stopArea = PublicTransport.getStopAreaRelation( op );

			assertEquals( 2334565, stopArea.getId() );

			List<RelationMember> lMembers = stopArea.getMembers();
			assertEquals( 10, lMembers.size() );

			assertEquals( 1844116848, lMembers.get( 0 ).getUniqueId() );
			assertEquals( Node.class, lMembers.get( 0 ).getMember().getClass() );
			assertEquals( "stop", lMembers.get( 0 ).getRole() );

			assertEquals( 155668534, lMembers.get( 1 ).getUniqueId() );
			assertEquals( Way.class, lMembers.get( 1 ).getMember().getClass() );
			assertEquals( "platform", lMembers.get( 1 ).getRole() );

			assertEquals( 155656229, lMembers.get( 2 ).getUniqueId() );
			assertEquals( Way.class, lMembers.get( 2 ).getMember().getClass() );
			assertEquals( "platform", lMembers.get( 2 ).getRole() );

			assertEquals( 1844116813, lMembers.get( 3 ).getUniqueId() );
			assertEquals( Node.class, lMembers.get( 3 ).getMember().getClass() );
			assertEquals( "stop", lMembers.get( 3 ).getRole() );

			assertEquals( 155672125, lMembers.get( 4 ).getUniqueId() );
			assertEquals( Way.class, lMembers.get( 4 ).getMember().getClass() );
			assertEquals( "platform", lMembers.get( 4 ).getRole() );

			assertEquals( 155650086, lMembers.get( 5 ).getUniqueId() );
			assertEquals( Way.class, lMembers.get( 5 ).getMember().getClass() );
			assertEquals( "platform", lMembers.get( 5 ).getRole() );

			assertEquals( 153348921, lMembers.get( 6 ).getUniqueId() );
			assertEquals( Node.class, lMembers.get( 6 ).getMember().getClass() );
			assertEquals( "stop", lMembers.get( 6 ).getRole() );

			assertEquals( 155657865, lMembers.get( 7 ).getUniqueId() );
			assertEquals( Way.class, lMembers.get( 7 ).getMember().getClass() );
			assertEquals( "platform", lMembers.get( 7 ).getRole() );

			assertEquals( 1854249631, lMembers.get( 8 ).getUniqueId() );
			assertEquals( Node.class, lMembers.get( 8 ).getMember().getClass() );
			assertEquals( "stop", lMembers.get( 8 ).getRole() );

			assertEquals( 155652409, lMembers.get( 9 ).getUniqueId() );
			assertEquals( Way.class, lMembers.get( 9 ).getMember().getClass() );
			assertEquals( "platform", lMembers.get( 9 ).getRole() );
		}
		catch( OsmTransferException e )
		{
			e.printStackTrace();
			fail();
		}
	}
}
