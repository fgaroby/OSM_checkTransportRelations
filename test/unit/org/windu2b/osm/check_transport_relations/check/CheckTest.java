/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations.check;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author windu
 * 
 */
public class CheckTest
{
	private Check	check;




	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}




	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}




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
	}




	@Test
	public final void Checktest()
	{
		assertNotNull( check );
	}




	@Test
	public void setAndGetStateTest()
	{
		assertNotNull( check.getState() );
		assertEquals( check.getState(), check.way );
		
		check.setState( check.platform );
		assertNotNull( check.getState() );
		assertEquals( check.getState(), check.platform );
		
		
		check.setState( check.start );
		assertNotNull( check.getState() );
		assertEquals( check.getState(), check.start );
		
		
		check.setState( check.stop_position );
		assertNotNull( check.getState() );
		assertEquals( check.getState(), check.stop_position );
		
		
		check.setState( check.way );
		assertNotNull( check.getState() );
		assertEquals( check.getState(), check.way );
	}

}
