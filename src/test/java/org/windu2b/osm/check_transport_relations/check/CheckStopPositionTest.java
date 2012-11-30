/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations.check;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.windu2b.osm.check_transport_relations.data.osm.Node;
import org.windu2b.osm.check_transport_relations.data.osm.OsmPrimitive;
import org.windu2b.osm.check_transport_relations.data.osm.PublicTransport;
import org.windu2b.osm.check_transport_relations.data.osm.RelationMember;
import org.windu2b.osm.check_transport_relations.data.osm.Way;
import org.windu2b.osm.check_transport_relations.io.OsmTransferException;

/**
 * @author windu
 * 
 */
public class CheckStopPositionTest
{
	private Check	check;


	private Node	nStopPosition;




	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		check = new Check();

		nStopPosition = new Node( 1 );
		Map<String, String> keys = new HashMap<>();
		keys.put( "public_transport", "stop_position" );
		nStopPosition.setKeys( keys );
	}




	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
		check = null;
		LastElements.reset();
	}




	/**
	 * Test method for
	 * {@link org.windu2b.osm.check_transport_relations.check.CheckStopPosition#CheckStopPosition(org.windu2b.osm.check_transport_relations.check.Check)}
	 * .
	 */
	@Test
	public final void testCheckStopPosition()
	{
		ICheck csp = new CheckStopPosition( check );

		assertNotNull( csp );
		assertEquals( check, csp.getCheck() );
	}




	/**
	 * Test method for
	 * {@link org.windu2b.osm.check_transport_relations.data.osm.PublicTransport#isStopPosition(Node)}
	 * 
	 */
	@Test
	public final void testIsStopPosition()
	{
		Node n1 = new Node( 1 );
		assertFalse( PublicTransport.isStopPosition( n1 ) );

		assertTrue( PublicTransport.isStopPosition( nStopPosition ) );
		assertNotNull( nStopPosition.getKeys() );
		assertEquals( "stop_position", nStopPosition.get( "public_transport" ) );
	}




	/**
	 * Test method for
	 * {@link org.windu2b.osm.check_transport_relations.check.CheckStopPosition#check(OsmPrimitive)}
	 * .
	 */
	@Test
	public final void testCheckWithOnlyOneNotStopPositionNode()
	{
		RelationMember rm1 = new RelationMember( "", new Node( 1 ) );
		ICheck csp = new CheckStopPosition( check );

		assertNull( LastElements.getLastWay() );
		assertNull( LastElements.getLastStopArea() );
		assertNull( LastElements.getLastStopPosition() );
		try
		{
			csp.check( rm1 );
			assertNull( LastElements.getLastWay() );
			assertNull( LastElements.getLastStopPosition() );
		}
		catch( OsmTransferException e )
		{
			e.printStackTrace();
			fail();
		}
	}




	/**
	 * Test method for
	 * {@link org.windu2b.osm.check_transport_relations.check.CheckStopPosition#check(OsmPrimitive)}
	 * .
	 */
	@Test
	public final void testCheckWithAWayBeforeANotStopPositionNode()
	{
		Node n1 = new Node( 1 );
		Node n2 = new Node( 2 );

		List<Node> lNodes = new ArrayList<>();
		lNodes.add( n1 );
		lNodes.add( n2 );

		Way w1 = new Way( 3 );
		w1.setNodes( lNodes );

		RelationMember rm1 = new RelationMember( "", n1 );

		ICheck csp = new CheckStopPosition( check );

		assertNull( LastElements.getLastWay() );
		assertNull( LastElements.getLastStopArea() );
		assertNull( LastElements.getLastStopPosition() );
		try
		{
			LastElements.setLastWay( w1 );
			csp.check( rm1 );
			assertNotNull( LastElements.getLastWay() );
			assertEquals( w1, LastElements.getLastWay() );
			assertNull( LastElements.getLastStopPosition() );
		}
		catch( OsmTransferException e )
		{
			e.printStackTrace();
			fail();
		}
	}




	/**
	 * Test method for
	 * {@link org.windu2b.osm.check_transport_relations.check.CheckStopPosition#check(OsmPrimitive)}
	 * .
	 */
	@Test
	public final void testCheckWithAWayBeforeAStopPositionNode()
	{
		Node n2 = new Node( 2 );

		List<Node> lNodes = new ArrayList<>();
		lNodes.add( nStopPosition );
		lNodes.add( n2 );

		Way w1 = new Way( 3 );
		w1.setNodes( lNodes );

		RelationMember rm1 = new RelationMember( "stop", nStopPosition );

		ICheck csp = new CheckStopPosition( check );

		assertNull( LastElements.getLastWay() );
		assertNull( LastElements.getLastStopArea() );
		assertNull( LastElements.getLastStopPosition() );
		try
		{
			LastElements.setLastWay( w1 );
			csp.check( rm1 );
			assertNotNull( LastElements.getLastWay() );
			assertEquals( w1, LastElements.getLastWay() );
			assertNotNull( LastElements.getLastStopPosition() );
			assertEquals( nStopPosition, LastElements.getLastStopPosition() );
			
			assertEquals( check.cPlatform, check.cState );
		}
		catch( OsmTransferException e )
		{
			fail();
			e.printStackTrace();
		}
	}

}
