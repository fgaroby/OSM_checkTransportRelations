package org.windu2b.osm.check_transport_relations.check;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith( Suite.class )
@SuiteClasses( { AbstractCheckTest.class, CheckPlatformTest.class,
        CheckStopPositionTest.class, CheckTest.class, CheckWayTest.class } )
public class AllTests
{

}
