/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations.data.osm;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.windu2b.osm.check_transport_relations.check.LastElements;
import org.windu2b.osm.check_transport_relations.data.osm.Way.Direction;
import org.windu2b.osm.check_transport_relations.io.OsmServerObjectReader;

/**
 * @author windu
 * 
 */
public class WayTest
{
	Node	n1, n2, n3, n4;


	Way	 w1, w2;




	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		n1 = new Node( 1 );
		n2 = new Node( 2 );
		n3 = new Node( 3 );
		n4 = new Node( 4 );
		
		List<Node> lNodes1 = new ArrayList<>();
		lNodes1.add( n1 );
		lNodes1.add( n2 );
		lNodes1.add( n3 );
		
		List<Node> lNodes2 = new ArrayList<>();
		lNodes2.add( n3 );
		lNodes2.add( n4 );

		w1 = new Way( 100 );
		w1.setNodes( lNodes1 );

		w2 = new Way( 200 );
		w2.setNodes( lNodes2 );
	}




	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
		n1 = null;
		n2 = null;
		n3 = null;
		n4 = null;
	}




	/**
	 * Test method for
	 * {@link org.windu2b.osm.check_transport_relations.data.osm.Way#getFirstNode()}
	 * .
	 */
	@Test
	public final void testGetFirstNode()
	{
		assertNotNull( w1.getFirstNode() );
		assertEquals( n1, w1.getFirstNode() );
		
		assertNotNull( w2.getFirstNode() );
		assertEquals( n3, w2.getFirstNode() );
	}




	/**
	 * Test method for
	 * {@link org.windu2b.osm.check_transport_relations.data.osm.Way#getLastNode()}
	 * .
	 */
	@Test
	public final void testGetLastNode()
	{
		assertNotNull( w1.getLastNode() );
		assertEquals( n3, w1.getLastNode() );
		
		assertNotNull( w2.getLastNode() );
		assertEquals( n4, w2.getLastNode() );
	}




	@Test
	public final void testContiguousWays()
	{
		Way w1 = null, w2 = null;

		DataSet ds;
		Collection<Way> cWays;

		try
		{
			OsmServerObjectReader reader = new OsmServerObjectReader(
			        179989360, OsmPrimitiveType.WAY, true );
			ds = reader.parseOsm( null );
			cWays = ds.getWays();
			for( Way w : cWays )
			{
				w1 = w;
			}

			assertNotNull( w1 );
			assertFalse( w1.isIncomplete() );
			assertEquals( new Node( 1904351306 ), w1.getFirstNode() );
			assertEquals( new Node( 1904349277 ), w1.getLastNode() );

			reader = new OsmServerObjectReader( 179989358,
			        OsmPrimitiveType.WAY, true );
			ds = reader.parseOsm( null );
			cWays = ds.getWays();
			for( Way w : cWays )
			{
				w2 = w;
			}

			assertNotNull( w2 );
			assertFalse( w2.isIncomplete() );
			assertEquals( new Node( 1228985822 ), w2.getFirstNode() );
			assertEquals( new Node( 1904351306 ), w2.getLastNode() );

			w1.setDirection( Direction.BACKWARD );
			LastElements.setLastWay( w1 );
			w2.setDirection();
			assertEquals( Direction.BACKWARD, w2.getDirection() );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}




	/**
	 * Test method for
	 * {@link org.windu2b.osm.check_transport_relations.data.osm.Way#setDirection(org.windu2b.osm.check_transport_relations.data.osm.Way.Direction)}
	 * .
	 */
	@Test
	public final void testSetAndGetDirection()
	{
		w1.setDirection( Direction.FORWARD );
		LastElements.setLastWay( w1 );
		w2.setDirection();
		
		assertNotNull( w2.getDirection() );
		assertEquals( Direction.FORWARD, w2.getDirection() );
	}

}
