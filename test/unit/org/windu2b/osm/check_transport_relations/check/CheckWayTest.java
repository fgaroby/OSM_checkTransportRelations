/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations.check;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.windu2b.osm.check_transport_relations.data.osm.Node;
import org.windu2b.osm.check_transport_relations.data.osm.RelationMember;
import org.windu2b.osm.check_transport_relations.data.osm.Way;
import org.windu2b.osm.check_transport_relations.io.OsmTransferException;

/**
 * @author windu
 * 
 */
public class CheckWayTest
{
	private Check	check;




	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		check = new Check();
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
	 * {@link org.windu2b.osm.check_transport_relations.check.CheckWay#CheckWay(org.windu2b.osm.check_transport_relations.check.Check)}
	 * .
	 */
	@Test
	public final void testCheckWay()
	{
		ICheck cw = new CheckWay( check );

		assertNotNull( cw );
		assertEquals( check, cw.getCheck() );
	}




	/**
	 * Test method for
	 * {@link org.windu2b.osm.check_transport_relations.check.CheckWay#check(org.windu2b.osm.check_transport_relations.data.osm.RelationMember)}
	 * .
	 */
	@Test
	public final void testCheckWithOneWay()
	{
		RelationMember rm = new RelationMember( "", new Way( 1 ) );
		ICheck cw = new CheckWay( check );

		assertNull( LastElements.getLastWay() );
		try
		{
			cw.check( rm );
			assertNotNull( LastElements.getLastWay() );
			assertEquals( rm.getWay(), LastElements.getLastWay() );
		}
		catch( OsmTransferException e )
		{
			fail();
			e.printStackTrace();
		}
	}




	/**
	 * Test method for
	 * {@link org.windu2b.osm.check_transport_relations.check.CheckWay#areContiguousWays(Way, Way)}
	 * .
	 */
	@Test
	public final void testAreContiguousWaysWithSeveralNonContiguousWays()
	{
		Node n1 = new Node( 10 );
		Node n2 = new Node( 11 );
		Node n3 = new Node( 12 );
		Node n4 = new Node( 13 );
		Node n5 = new Node( 14 );

		List<Node> lNode1 = new ArrayList<Node>();
		lNode1.add( n1 );
		lNode1.add( n2 );

		List<Node> lNode2 = new ArrayList<Node>();
		lNode2.add( n2 );
		lNode2.add( n3 );

		List<Node> lNode3 = new ArrayList<Node>();
		lNode3.add( n4 );
		lNode3.add( n5 );

		Way w1 = new Way( 1 );
		w1.setNodes( lNode1 );

		Way w2 = new Way( 2 );
		w2.setNodes( lNode2 );

		Way w3 = new Way( 3 );
		w3.setNodes( lNode3 );

		assertTrue( CheckWay.areContiguousWays( w1, w2 ) );
		assertFalse( CheckWay.areContiguousWays( w2, w3 ) );
	}




	/**
	 * Test method for
	 * {@link org.windu2b.osm.check_transport_relations.check.CheckWay#check(org.windu2b.osm.check_transport_relations.data.osm.RelationMember)}
	 * .
	 */
	@Test
	public final void testCheckWithSeveralContiguousWays()
	{
		Node n1 = new Node( 10 );
		Node n2 = new Node( 11 );
		Node n3 = new Node( 12 );
		Node n4 = new Node( 13 );

		List<Node> lNode1 = new ArrayList<Node>();
		lNode1.add( n1 );
		lNode1.add( n2 );

		List<Node> lNode2 = new ArrayList<Node>();
		lNode2.add( n2 );
		lNode2.add( n3 );

		List<Node> lNode3 = new ArrayList<Node>();
		lNode3.add( n4 );
		lNode3.add( n1 );

		Way w1 = new Way( 1 );
		w1.setNodes( lNode1 );

		Way w2 = new Way( 2 );
		w2.setNodes( lNode2 );

		Way w3 = new Way( 3 );
		w3.setNodes( lNode3 );

		RelationMember rm1 = new RelationMember( "", w1 );
		RelationMember rm2 = new RelationMember( "", w2 );
		RelationMember rm3 = new RelationMember( "", w3 );
		ICheck cw = new CheckWay( check );

		assertNull( LastElements.getLastWay() );
		try
		{
			cw.check( rm1 );
			assertNotNull( LastElements.getLastWay() );
			assertEquals( rm1.getWay(), LastElements.getLastWay() );

			cw.check( rm2 );
			assertNotNull( LastElements.getLastWay() );
			assertEquals( rm2.getWay(), LastElements.getLastWay() );

			cw.check( rm3 );
			assertNotNull( LastElements.getLastWay() );
			assertEquals( rm3.getWay(), LastElements.getLastWay() );
		}
		catch( OsmTransferException e )
		{
			fail();
			e.printStackTrace();
		}
	}

}
