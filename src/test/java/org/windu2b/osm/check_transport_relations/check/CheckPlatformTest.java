/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations.check;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.windu2b.osm.check_transport_relations.data.osm.Node;
import org.windu2b.osm.check_transport_relations.data.osm.OsmPrimitive;
import org.windu2b.osm.check_transport_relations.data.osm.Relation;
import org.windu2b.osm.check_transport_relations.data.osm.RelationMember;
import org.windu2b.osm.check_transport_relations.data.osm.Way;
import org.windu2b.osm.check_transport_relations.io.OsmTransferException;

/**
 * @author windu
 * 
 */
public class CheckPlatformTest
{

	private Check	check;


	private Node	nStopPosition, nPlatform;


	private Way	  way, wPlatform1, wPlatform2;
	
	
	private Relation rStopArea;




	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		check = new Check();

		nStopPosition = new Node( 497581093 );
		Map<String, String> keysStopPosition = new HashMap<>();
		keysStopPosition.put( "public_transport", "stop_position" );
		nStopPosition.setKeys( keysStopPosition );
		
		nPlatform = new Node( 1849501768 );
		Map<String, String> keysPlatform = new HashMap<>();
		keysPlatform.put( "public_transport", "stop_position" );
		nPlatform.setKeys( keysPlatform );
		

		wPlatform1 = new Way( 155666119 );
		Map<String, String> keysPlatform1 = new HashMap<>();
		keysPlatform1.put( "public_transport", "platform" );
		wPlatform1.setKeys( keysPlatform1 );

		wPlatform2 = new Way( 155659693 );
		Map<String, String> keysPlatform2 = new HashMap<>();
		keysPlatform2.put( "public_transport", "platform" );
		wPlatform2.setKeys( keysPlatform2 );
		
		rStopArea = new Relation( 2334571 );
		
		way = new Way( 170761104 );
		
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
	 * {@link org.windu2b.osm.check_transport_relations.check.CheckPlatform#CheckPlatform(org.windu2b.osm.check_transport_relations.check.Check)}
	 * .
	 */
	@Test
	public final void testCheckPlatform()
	{
		ICheck cw = new CheckPlatform( check );

		assertNotNull( cw );
		assertEquals( check, cw.getCheck() );
	}




	/**
	 * Test method for
	 * {@link org.windu2b.osm.check_transport_relations.check.CheckStopPosition#check(OsmPrimitive)}
	 * .
	 */
	@Test
	public final void testCheckWithOnlyOneNotPlatformNode()
	{
		RelationMember rm1 = new RelationMember( "", new Node( 1 ) );
		ICheck csp = new CheckPlatform( check );

		assertNull( LastElements.getLastWay() );
		assertNull( LastElements.getLastStopArea() );
		assertNull( LastElements.getLastStopPosition() );
		try
		{
			assertFalse( csp.check( rm1 ) );
			assertNull( LastElements.getLastWay() );
			assertNull( LastElements.getLastStopArea() );
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
	public final void testCheckWithAWayBeforeANotPlatformNode()
	{
		Node n1 = new Node( 1 );
		Way w1 = new Way( 3 );
		RelationMember rm1 = new RelationMember( "platform", n1 );

		ICheck cp = new CheckPlatform( check );

		assertNull( LastElements.getLastWay() );
		assertNull( LastElements.getLastStopArea() );
		assertNull( LastElements.getLastStopPosition() );
		try
		{
			// On simule la vérification d'une 'way'
			LastElements.lastWay = w1;
			check.cState = check.cWay;
			
			assertFalse( cp.check( rm1 ) );
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
	public final void testCheckWithAWayBeforeAPlatformNode()
	{
		Way w1 = new Way( 49904814 );

		RelationMember rm1 = new RelationMember( "platform", wPlatform1 );

		ICheck cp = new CheckPlatform( check );

		assertNull( LastElements.getLastWay() );
		assertNull( LastElements.getLastStopArea() );
		assertNull( LastElements.getLastStopPosition() );
		try
		{
			LastElements.setLastWay( w1 );
			assertTrue( cp.check( rm1 ) );
			assertNotNull( LastElements.getLastWay() );
			assertEquals( w1, LastElements.getLastWay() );
			assertNull( LastElements.getLastStopPosition() );
		}
		catch( OsmTransferException e )
		{
			fail();
			e.printStackTrace();
		}
	}




	/**
	 * Test method for
	 * {@link org.windu2b.osm.check_transport_relations.check.CheckStopPosition#check(OsmPrimitive)}
	 * .
	 */
	@Test
	public final void testCheckWithAWayAndAStopPositionBeforeAPlatformNode()
	{
		Way w1 = new Way( 49904814 );

		RelationMember rm = new RelationMember( "platform", wPlatform1 );

		ICheck cp = new CheckPlatform( check );

		assertNull( LastElements.getLastWay() );
		assertNull( LastElements.getLastStopPosition() );
		assertNull( LastElements.getLastStopArea() );
		try
		{
			// On simule la vérification d'une 'way'
			LastElements.lastWay = w1;
			check.cState = check.cWay;

			// On simule la vréification d'un 'stop_position'
			LastElements.lastStopPosition = nStopPosition;
			check.cState = check.cPlatform;

			assertTrue( cp.check( rm ) );
			assertEquals( check.cPlatform, check.cState );
			assertNotNull( LastElements.getLastWay() );
			assertEquals( w1, LastElements.getLastWay() );
			assertNull( LastElements.getLastStopPosition() );
			assertNull( LastElements.getLastStopArea() );
		}
		catch( OsmTransferException e )
		{
			fail();
			e.printStackTrace();
		}
	}




	/**
	 * Test method for
	 * {@link org.windu2b.osm.check_transport_relations.check.CheckStopPosition#check(OsmPrimitive)}
	 * .
	 */
	@Test
	public final void testCheckWithAWayAndAStopPositionNodeBeforeSeveralPlatformNodes()
	{
		Way w1 = new Way( 49904814 );

		RelationMember rm1 = new RelationMember( "platform", wPlatform1 );
		RelationMember rm2 = new RelationMember( "platform", wPlatform2 );

		ICheck cp = new CheckPlatform( check );

		assertNull( LastElements.getLastWay() );
		assertNull( LastElements.getLastStopPosition() );
		assertNull( LastElements.getLastStopArea() );
		try
		{
			// On simule la vérification d'une 'way'
			LastElements.lastWay = w1;
			check.cState = check.cWay;

			// On simule la vréification d'un 'stop_position'
			LastElements.lastStopPosition = nStopPosition;
			LastElements.lastStopArea = rStopArea;
			check.cState = check.cPlatform;

			/*
			 * Platform node 1
			 */
			assertTrue( cp.check( rm1 ) );
			assertEquals( check.cPlatform, check.cState );
			assertNotNull( LastElements.getLastWay() );
			assertEquals( w1, LastElements.getLastWay() );
			assertNull( LastElements.getLastStopPosition() );
			assertNotNull( LastElements.getLastStopArea() );
			assertEquals( rStopArea, LastElements.lastStopArea );

			/*
			 * Platform node 2
			 */
			assertTrue( cp.check( rm2 ) );
			assertEquals( check.cPlatform, check.cState );
			assertNotNull( LastElements.getLastWay() );
			assertEquals( w1, LastElements.getLastWay() );
			assertNull( LastElements.getLastStopPosition() );
			assertNotNull( LastElements.getLastStopArea() );
			assertEquals( rStopArea, LastElements.lastStopArea );
		}
		catch( OsmTransferException e )
		{
			fail();
			e.printStackTrace();
		}
	}
}
