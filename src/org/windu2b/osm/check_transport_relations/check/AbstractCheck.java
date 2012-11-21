/**
 * License: GPL v3 or later. For details, see LICENSE file.
 */
package org.windu2b.osm.check_transport_relations.check;

/**
 * @author windu
 * 
 */
public abstract class AbstractCheck implements ICheck
{
	protected Check	check;




	protected AbstractCheck( Check check )
	{
		this.check = check;
	}




	/**
	 * @return the check
	 */
	@Override
	public Check getCheck()
	{
		return check;
	}




	/**
	 * @param check
	 *            the check to set
	 */
	@Override
	public void setCheck( Check check )
	{
		this.check = check;
	}

}