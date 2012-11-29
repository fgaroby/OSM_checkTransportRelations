/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations.data.osm;

import static org.junit.Assert.*;

import java.util.Collection;

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




	/**
	 * Test method for
	 * {@link org.windu2b.osm.check_transport_relations.data.osm.Way#getLastNode()}
	 * .
	 */
	@Test
	public final void testGetLastNode()
	{
		fail( "Not yet implemented" );
	}




	/**
	 * Test method for
	 * {@link org.windu2b.osm.check_transport_relations.data.osm.Way#getFirstNode()}
	 * .
	 */
	@Test
	public final void testGetFirstNode()
	{
		fail( "Not yet implemented" );
	}




	@Test
	public final void testIsLastNode()
	{
		Way way = null;
		Node nStopPosition = new Node( 1844116848 );

		DataSet ds;

		try
		{
			OsmServerObjectReader reader = new OsmServerObjectReader( 49904814,
			        OsmPrimitiveType.WAY, true );
			ds = reader.parseOsm( null );
			Collection<Way> cWays = ds.getWays();
			for( Way w : cWays )
			{
				way = w;
			}

			assertNotNull( way );
			assertFalse( way.isIncomplete() );
			assertEquals( nStopPosition, way.getLastNode() );
			assertFalse( way.getLastNode().equals( new Node( 2019010331 ) ) );
			assertFalse( way.getFirstNode().equals( new Node( 2019010331 ) ) );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}




	@Test
	public final void testIsFirstNode()
	{
		Way way = null;
		Node nStopPosition = new Node( 1844116848 );

		DataSet ds;

		try
		{
			OsmServerObjectReader reader = new OsmServerObjectReader(
			        173638935, OsmPrimitiveType.WAY, true );
			ds = reader.parseOsm( null );
			Collection<Way> cWays = ds.getWays();
			for( Way w : cWays )
			{
				way = w;
			}

			assertNotNull( way );
			assertFalse( way.isIncomplete() );
			assertEquals( nStopPosition, way.getFirstNode() );
			assertFalse( way.getFirstNode().equals( new Node( 2019010335 ) ) );
			assertFalse( way.getLastNode().equals( new Node( 2019010335 ) ) );
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
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
		fail( "Not yet implemented" );
	}

}
