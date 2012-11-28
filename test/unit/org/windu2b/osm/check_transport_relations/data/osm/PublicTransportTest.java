/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations.data.osm;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.windu2b.osm.check_transport_relations.io.OsmTransferException;

/**
 * @author windu
 *
 */
public class PublicTransportTest
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
	 * Test method for {@link org.windu2b.osm.check_transport_relations.data.osm.PublicTransport#getStopAreaRelation(org.windu2b.osm.check_transport_relations.data.osm.OsmPrimitive)}.
	 */
	@Test
	public final void testGetStopPositionWithABadStopPositionRelation()
	{
		OsmPrimitive op = new Node( 1 );
		
		try
        {
	        Relation r = PublicTransport.getStopAreaRelation( op );
	        assertNull( r );
        }
        catch( OsmTransferException e )
        {
	        e.printStackTrace();
	        fail();
        }
	}




	/**
	 * Test method for {@link org.windu2b.osm.check_transport_relations.data.osm.PublicTransport#getStopAreaRelation(org.windu2b.osm.check_transport_relations.data.osm.OsmPrimitive)}.
	 */
	@Test
	public final void testGetStopPositionWithAGoodStopPositionRelation()
	{
		OsmPrimitive op = new Node( 27324070 );
		
		try
        {
	        Relation r = PublicTransport.getStopAreaRelation( op );
	        assertNotNull( r );
	        assertEquals( new Relation( 2521639 ), r );
        }
        catch( OsmTransferException e )
        {
	        e.printStackTrace();
	        fail();
        }
	}

	
	@Test
	public final void testIsPlatform()
	{
		Map<String, String> keysPlatform = new HashMap<>();
		keysPlatform.put( "public_transport", "platform" );
		Map<String, String> keysStation = new HashMap<>();
		keysStation.put( "public_transport", "station" );
		
		// It's a platform (node)
		Node nPlatform = new Node( 1849501768 );
		nPlatform.setKeys( keysPlatform );
		assertTrue( PublicTransport.isPlatform( nPlatform ) );
		
		// It's a platform (way)
		Way wPlatform = new Way( 155668534 );
		wPlatform.setKeys( keysPlatform );
		assertTrue( PublicTransport.isPlatform( wPlatform ) );
		
		// It's a station (way)
		Way wStation = new Way( 163198297 );
		wStation.setKeys( keysStation );
		assertTrue( PublicTransport.isPlatform( wStation ) );
		
		// It's NOT a platform (node)
		Node nNotPlatform = new Node( 1844116848 );
		assertFalse( PublicTransport.isPlatform( nNotPlatform ) );
		
		// It's NOT a platform (way)
		Way wNotPlatform = new Way( 26419103 );
		assertFalse( PublicTransport.isPlatform( wNotPlatform ) );
		
		// It's NOT a station (way)
		Way wNotStation = new Way( 171982706 );
		assertFalse( PublicTransport.isPlatform( wNotStation ) );
		
	}
}
