/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations.check;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.windu2b.osm.check_transport_relations.data.osm.RelationMember;
import org.windu2b.osm.check_transport_relations.io.OsmTransferException;

/**
 * @author windu
 * 
 */
public class AbstractCheckTest
{
	private Check	      check;


	private AbstractCheck	ac;




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
	 * {@link org.windu2b.osm.check_transport_relations.check.AbstractCheck#AbstractCheck(org.windu2b.osm.check_transport_relations.check.Check)}
	 * .
	 */
	@Test
	public final void testAbstractCheckWithNullCheck()
	{
		check = null;
		
		ac = new AbstractCheck( check )
		{

			@Override
			public boolean check( RelationMember rm ) throws OsmTransferException
			{
				return true;
			}
		};
		
		assertNotNull( ac );
		assertNull( ac.getCheck() );
	}




	/**
	 * Test method for
	 * {@link org.windu2b.osm.check_transport_relations.check.AbstractCheck#AbstractCheck(org.windu2b.osm.check_transport_relations.check.Check)}
	 * .
	 */
	@Test
	public final void testAbstractCheckWithNotNullCheck()
	{
		check = new Check();
		
		ac = new AbstractCheck( check )
		{

			@Override
			public boolean check( RelationMember rm ) throws OsmTransferException
			{
				return true;
			}
		};
		
		assertNotNull( ac );
		assertNotNull( ac.getCheck() );
	}




	/**
	 * Test method for
	 * {@link org.windu2b.osm.check_transport_relations.check.AbstractCheck#getCheck()}
	 * and
	 * {@link org.windu2b.osm.check_transport_relations.check.AbstractCheck#setCheck(org.windu2b.osm.check_transport_relations.check.Check)}
	 * .
	 */
	@Test
	public final void testGetAndSetCheck()
	{
		check = null;
		ac = new AbstractCheck( check )
		{

			@Override
			public boolean check( RelationMember rm ) throws OsmTransferException
			{
				return true;
			}
		};
		
		assertNull( ac.getCheck() );
		
		check = new Check();
		ac.setCheck( check );
		assertNotNull( ac.getCheck() );
		assertEquals( check, ac.getCheck() );
		
		ac.setCheck( null );
		assertNull( ac.getCheck() );
	}
}
