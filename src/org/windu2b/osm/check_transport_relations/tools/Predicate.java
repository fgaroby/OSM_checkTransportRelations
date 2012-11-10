// License: GPL. For details, see LICENSE file.
package org.windu2b.osm.check_transport_relations.tools;

// Used to identify objects that fulfill a certain condition, e.g. when
// filtering a collection
public interface Predicate<T>
{
	// @return whether the object passes the test or not
	public boolean evaluate( T object );
}
