package org.windu2b.osm.check_transport_relations;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.windu2b.osm.check_transport_relations.data.osm.PublicTransportTest;

@RunWith( Suite.class )
@SuiteClasses( {
        org.windu2b.osm.check_transport_relations.check.AllTests.class,
        PublicTransportTest.class } )
public class AllTests
{

}
