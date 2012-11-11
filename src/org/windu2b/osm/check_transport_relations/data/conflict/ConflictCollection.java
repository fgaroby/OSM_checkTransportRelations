// License: GPL. For details, see LICENSE file.
package org.windu2b.osm.check_transport_relations.data.conflict;

import static org.windu2b.osm.check_transport_relations.tools.I18n.tr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.windu2b.osm.check_transport_relations.data.osm.OsmPrimitive;
import org.windu2b.osm.check_transport_relations.tools.CheckParameterUtil;

/**
 * This is a collection of {@link Conflict}s. This collection is
 * {@link Iterable}, i.e. it can be used in <code>for</code>-loops as follows:
 * 
 * <pre>
 *    ConflictCollection conflictCollection = ....
 * 
 *    for(Conflict c : conflictCollection) {
 *      // do something
 *    }
 * </pre>
 * 
 * This collection emits an event when the content of the collection changes.
 * You can register and unregister for these events using:
 * <ul>
 * <li>{@link #addConflictListener(IConflictListener)}</li>
 * <li>{@link #removeConflictListener(IConflictListener)}</li>
 * </ul>
 */
public class ConflictCollection implements
        Iterable<Conflict<? extends OsmPrimitive>>
{
	private final List<Conflict<? extends OsmPrimitive>>	conflicts;




	public ConflictCollection()
	{
		conflicts = new ArrayList<Conflict<?>>();
	}




	/**
	 * Adds a conflict to the collection
	 * 
	 * @param conflict
	 *            the conflict
	 * @exception IllegalStateException
	 *                thrown, if this collection already includes a conflict for
	 *                conflict.getMy()
	 */
	protected void addConflict( Conflict<?> conflict )
	        throws IllegalStateException
	{
		if( hasConflictForMy( conflict.getMy() ) )
		    throw new IllegalStateException( tr(
		            "Already registered a conflict for primitive ''{0}''.",
		            conflict.getMy().toString() ) );
		if( !conflicts.contains( conflict ) )
		{
			conflicts.add( conflict );
		}
	}




	/**
	 * Adds a conflict to the collection of conflicts.
	 * 
	 * @param conflict
	 *            the conflict to add. Must not be null.
	 * @throws IllegalArgumentException
	 *             thrown, if conflict is null
	 * @throws IllegalStateException
	 *             thrown if this collection already includes a conflict for
	 *             conflict.getMy()
	 * 
	 */
	public void add( Conflict<?> conflict ) throws IllegalStateException
	{
		CheckParameterUtil.ensureParameterNotNull( conflict, "conflict" );
		addConflict( conflict );
	}




	/**
	 * Add the conflicts in <code>otherConflicts</code> to this collection of
	 * conflicts
	 * 
	 * @param otherConflicts
	 *            the collection of conflicts. Does nothing is conflicts is
	 *            null.
	 */
	public void add( Collection<Conflict<?>> otherConflicts )
	{
		if( otherConflicts == null ) return;
		for( Conflict<?> c : otherConflicts )
		{
			addConflict( c );
		}
	}




	/**
	 * Adds a conflict for the pair of {@link OsmPrimitive}s given by
	 * <code>my</code> and <code>their</code>.
	 * 
	 * @param my
	 *            my primitive
	 * @param their
	 *            their primitive
	 */
	public void add( OsmPrimitive my, OsmPrimitive their )
	{
		addConflict( new Conflict<OsmPrimitive>( my, their ) );
	}




	/**
	 * removes a conflict from this collection
	 * 
	 * @param conflict
	 *            the conflict
	 */
	public void remove( Conflict<?> conflict )
	{
		conflicts.remove( conflict );
	}




	/**
	 * removes the conflict registered for {@link OsmPrimitive} <code>my</code>
	 * if any
	 * 
	 * @param my
	 *            the primitive
	 */
	public void remove( OsmPrimitive my )
	{
		Iterator<Conflict<?>> it = iterator();
		while( it.hasNext() )
		{
			if( it.next().isMatchingMy( my ) )
			{
				it.remove();
			}
		}
	}




	/**
	 * Replies the conflict for the {@link OsmPrimitive} <code>my</code>, null
	 * if no such conflict exists.
	 * 
	 * @param my
	 *            my primitive
	 * @return the conflict for the {@link OsmPrimitive} <code>my</code>, null
	 *         if no such conflict exists.
	 */
	public Conflict<?> getConflictForMy( OsmPrimitive my )
	{
		for( Conflict<?> c : conflicts )
		{
			if( c.isMatchingMy( my ) ) return c;
		}
		return null;
	}




	/**
	 * Replies the conflict for the {@link OsmPrimitive} <code>their</code>,
	 * null if no such conflict exists.
	 * 
	 * @param my
	 *            my primitive
	 * @return the conflict for the {@link OsmPrimitive} <code>their</code>,
	 *         null if no such conflict exists.
	 */
	public Conflict<?> getConflictForTheir( OsmPrimitive their )
	{
		for( Conflict<?> c : conflicts )
		{
			if( c.isMatchingTheir( their ) ) return c;
		}
		return null;
	}




	/**
	 * Replies true, if this collection includes a conflict for <code>my</code>.
	 * 
	 * @param my
	 *            my primitive
	 * @return true, if this collection includes a conflict for <code>my</code>;
	 *         false, otherwise
	 */
	public boolean hasConflictForMy( OsmPrimitive my )
	{
		return getConflictForMy( my ) != null;
	}




	/**
	 * Replies true, if this collection includes a given conflict
	 * 
	 * @param c
	 *            the conflict
	 * @return true, if this collection includes the conflict; false, otherwise
	 */
	public boolean hasConflict( Conflict<?> c )
	{
		return hasConflictForMy( c.getMy() );
	}




	/**
	 * Replies true, if this collection includes a conflict for
	 * <code>their</code>.
	 * 
	 * @param their
	 *            their primitive
	 * @return true, if this collection includes a conflict for
	 *         <code>their</code>; false, otherwise
	 */
	public boolean hasConflictForTheir( OsmPrimitive their )
	{
		return getConflictForTheir( their ) != null;
	}




	/**
	 * Removes any conflicts for the {@link OsmPrimitive} <code>my</code>.
	 * 
	 * @param my
	 *            the primitive
	 */
	public void removeForMy( OsmPrimitive my )
	{
		Iterator<Conflict<?>> it = iterator();
		while( it.hasNext() )
		{
			if( it.next().isMatchingMy( my ) )
			{
				it.remove();
			}
		}
	}




	/**
	 * Removes any conflicts for the {@link OsmPrimitive} <code>their</code>.
	 * 
	 * @param their
	 *            the primitive
	 */
	public void removeForTheir( OsmPrimitive their )
	{
		Iterator<Conflict<?>> it = iterator();
		while( it.hasNext() )
		{
			if( it.next().isMatchingTheir( their ) )
			{
				it.remove();
			}
		}
	}




	/**
	 * Replies the conflicts as list.
	 * 
	 * @return the list of conflicts
	 */
	public List<Conflict<?>> get()
	{
		return conflicts;
	}




	/**
	 * Replies the size of the collection
	 * 
	 * @return the size of the collection
	 */
	public int size()
	{
		return conflicts.size();
	}




	/**
	 * Replies the conflict at position <code>idx</code>
	 * 
	 * @param idx
	 *            the index
	 * @return the conflict at position <code>idx</code>
	 */
	public Conflict<?> get( int idx )
	{
		return conflicts.get( idx );
	}




	/**
	 * Replies the iterator for this collection.
	 * 
	 * @return the iterator
	 */
	public Iterator<Conflict<?>> iterator()
	{
		return conflicts.iterator();
	}




	public void add( ConflictCollection other )
	{
		for( Conflict<?> c : other )
		{
			add( c );
		}
	}




	/**
	 * Replies the set of {@link OsmPrimitive} which participate in the role of
	 * "my" in the conflicts managed by this collection.
	 * 
	 * @return the set of {@link OsmPrimitive} which participate in the role of
	 *         "my" in the conflicts managed by this collection.
	 */
	public Set<OsmPrimitive> getMyConflictParties()
	{
		HashSet<OsmPrimitive> ret = new HashSet<OsmPrimitive>();
		for( Conflict<?> c : conflicts )
		{
			ret.add( c.getMy() );
		}
		return ret;
	}




	/**
	 * Replies the set of {@link OsmPrimitive} which participate in the role of
	 * "their" in the conflicts managed by this collection.
	 * 
	 * @return the set of {@link OsmPrimitive} which participate in the role of
	 *         "their" in the conflicts managed by this collection.
	 */
	public Set<OsmPrimitive> getTheirConflictParties()
	{
		HashSet<OsmPrimitive> ret = new HashSet<OsmPrimitive>();
		for( Conflict<?> c : conflicts )
		{
			ret.add( c.getTheir() );
		}
		return ret;
	}




	/**
	 * Replies true if this collection is empty
	 * 
	 * @return true, if this collection is empty; false, otherwise
	 */
	public boolean isEmpty()
	{
		return size() == 0;
	}




	@Override
	public String toString()
	{
		return conflicts.toString();
	}
}
