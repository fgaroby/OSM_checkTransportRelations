// License: GPL. For details, see LICENSE file.
package org.windu2b.osm.check_transport_relations.tools;


import java.util.Collection;
/**
 * Basic utils, that can be useful in different parts of the program.
 */
public class Utils
{

	/**
	 * Joins a list of strings (or objects that can be converted to string via
	 * Object.toString()) into a single string with fields separated by sep.
	 * 
	 * @param sep
	 *            the separator
	 * @param values
	 *            collection of objects, null is converted to the empty string
	 * @return null if values is null. The joined string otherwise.
	 */
	public static String join( String sep, Collection<?> values )
	{
		if( sep == null ) throw new IllegalArgumentException();
		if( values == null ) return null;
		if( values.isEmpty() ) return "";
		StringBuilder s = null;
		for( Object a : values )
		{
			if( a == null )
			{
				a = "";
			}
			if( s != null )
			{
				s.append( sep ).append( a.toString() );
			}
			else
			{
				s = new StringBuilder( a.toString() );
			}
		}
		return s.toString();
	}
}
