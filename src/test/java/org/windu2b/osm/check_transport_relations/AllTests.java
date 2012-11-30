package org.windu2b.osm.check_transport_relations;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith( Suite.class )
@SuiteClasses( {
        org.windu2b.osm.check_transport_relations.check.AllTests.class,
        org.windu2b.osm.check_transport_relations.data.osm.AllTests.class } )
public class AllTests
{

}
