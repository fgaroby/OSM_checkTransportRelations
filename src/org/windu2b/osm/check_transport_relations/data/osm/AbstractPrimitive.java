/**
 * 
 */
package org.windu2b.osm.check_transport_relations.data.osm;

import static org.windu2b.osm.check_transport_relations.tools.I18n.tr;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import org.windu2b.osm.check_transport_relations.data.osm.PrimitiveData;

/**
 * @author windu
 * 
 */
public abstract class AbstractPrimitive implements Tagged, IPrimitive
{
	private static final AtomicLong	idCounter	= new AtomicLong( 0 );




	static long generateUniqueId()
	{
		return idCounter.decrementAndGet();
	}



	/*-------------------
	 * OTHER PROPERTIES
	 *-------------------*/

	/**
	 * Unique identifier in OSM. This is used to identify objects on the server.
	 * An id of 0 means an unknown id. The object has not been uploaded yet to
	 * know what id it will get.
	 */
	protected long	           id	            = 0;


	/**
	 * Contains the version number as returned by the API. Needed to ensure
	 * update consistency
	 */
	protected int	           version	        = 0;


	/**
	 * The id of the changeset this primitive was last uploaded to. 0 if it
	 * wasn't uploaded to a changeset yet of if the changeset id isn't known.
	 */
	protected int	           changesetId;


	protected int	           timestamp;


	/**
	 * This flag shows, that the properties have been changed by the user and on
	 * upload the object will be send to the server.
	 */
	protected static final int	FLAG_MODIFIED	= 1 << 0;


	/**
	 * This flag is false, if the object is marked as deleted on the server.
	 */
	protected static final int	FLAG_VISIBLE	= 1 << 1;


	/**
	 * A primitive is incomplete if we know its id and type, but nothing more.
	 * Typically some members of a relation are incomplete until they are
	 * fetched from the server.
	 */
	protected static final int	FLAG_INCOMPLETE	= 1 << 3;


	/**
	 * Put several boolean flags to one short int field to save memory. Other
	 * bits of this field are used in subclasses.
	 */
	protected volatile short	flags	        = FLAG_VISIBLE; // visible per
	                                                            // default


	/*------------
	 * Keys handling
	 ------------*/

	// Note that all methods that read keys first make local copy of keys array
	// reference. This is to ensure thread safety - reading
	// doesn't have to be locked so it's possible that keys array will be
	// modified. But all write methods make copy of keys array so
	// the array itself will be never modified - only reference will be changed

	/**
	 * The key/value list for this primitive.
	 * 
	 */
	protected String[]	       keys;




	/**
	 * 
	 */
	public AbstractPrimitive()
	{
		// TODO Auto-generated constructor stub
	}




	/**
	 * Get and write all attributes from the parameter. Does not fire any
	 * listener, so use this only in the data initializing phase
	 * 
	 * @param other
	 *            the primitive to clone data from
	 */
	public void cloneFrom( AbstractPrimitive other )
	{
		setKeys( other.getKeys() );
		id = other.id;
		if( id <= 0 )
		{
			// reset version and changeset id
			version = 0;
			changesetId = 0;
		}
		timestamp = other.timestamp;
		if( id > 0 )
		{
			version = other.version;
		}
		flags = other.flags;
	}




	/**
	 * Replies the version number as returned by the API. The version is 0 if
	 * the id is 0 or if this primitive is incomplete.
	 * 
	 * @see PrimitiveData#setVersion(int)
	 */
	@Override
	public int getVersion()
	{
		return version;
	}




	/**
	 * Replies the id of this primitive.
	 * 
	 * @return the id of this primitive.
	 */
	@Override
	public long getId()
	{
		long id = this.id;
		return id >= 0 ? id : 0;
	}




	@Override
	public void setTimestamp( Date timestamp )
	{
		this.timestamp = ( int ) ( timestamp.getTime() / 1000 );
	}




	/**
	 * Time of last modification to this object. This is not set by JOSM but
	 * read from the server and delivered back to the server unmodified. It is
	 * used to check against edit conflicts.
	 * 
	 * @return date of last modification
	 */
	@Override
	public Date getTimestamp()
	{
		return new Date( timestamp * 1000l );
	}




	/**
	 * Gets a unique id representing this object.
	 * 
	 * @return Osm id if primitive already exists on the server. Unique negative
	 *         value if primitive is new
	 */
	@Override
	public long getUniqueId()
	{
		return id;
	}




	/**
	 * Replies the unique primitive id for this primitive
	 * 
	 * @return the unique primitive id for this primitive
	 */
	@Override
	public PrimitiveId getPrimitiveId()
	{
		return new SimplePrimitiveId( getUniqueId(), getType() );
	}




	public OsmPrimitiveType getDisplayType()
	{
		return getType();
	}




	/**
	 * Sets the id and the version of this primitive if it is known to the OSM
	 * API.
	 * 
	 * Since we know the id and its version it can't be incomplete anymore.
	 * incomplete is set to false.
	 * 
	 * @param id
	 *            the id. > 0 required
	 * @param version
	 *            the version > 0 required
	 * @throws IllegalArgumentException
	 *             thrown if id <= 0
	 * @throws IllegalArgumentException
	 *             thrown if version <= 0
	 * @throws DataIntegrityProblemException
	 *             If id is changed and primitive was already added to the
	 *             dataset
	 */
	@Override
	public void setOsmId( long id, int version )
	{
		if( id <= 0 )
		    throw new IllegalArgumentException( tr(
		            "ID > 0 expected. Got {0}.", id ) );
		if( version <= 0 )
		    throw new IllegalArgumentException( tr(
		            "Version > 0 expected. Got {0}.", version ) );
		this.id = id;
		this.version = version;
		this.setIncomplete( false );
	}




	/**
	 * Replies the name of this primitive. The default implementation replies
	 * the value of the tag <tt>name</tt> or null, if this tag is not present.
	 * 
	 * @return the name of this primitive
	 */
	@Override
	public String getName()
	{
		return get( "name" );
	}




	/**
	 * Replies the value for key <code>key</code>. Replies null, if
	 * <code>key</code> is null. Replies null, if there is no value for the
	 * given key.
	 * 
	 * @param key
	 *            the key. Can be null, replies null in this case.
	 * @return the value for key <code>key</code>.
	 */
	@Override
	public final String get( String key )
	{
		String[] keys = this.keys;
		if( key == null ) return null;
		if( keys == null ) return null;
		for( int i = 0; i < keys.length; i += 2 )
		{
			if( keys[i].equals( key ) ) return keys[i + 1];
		}
		return null;
	}




	/**
	 * Set the given value to the given key. If key is null, does nothing. If
	 * value is null, removes the key and behaves like {@link #remove(String)}.
	 * 
	 * @param key
	 *            The key, for which the value is to be set. Can be null, does
	 *            nothing in this case.
	 * @param value
	 *            The value for the key. If null, removes the respective
	 *            key/value pair.
	 * 
	 * @see #remove(String)
	 */
	@Override
	public void put( String key, String value )
	{
		Map<String, String> originalKeys = getKeys();
		if( key == null )
			return;
		else if( value == null )
		{
			remove( key );
		}
		else if( keys == null )
		{
			keys = new String[] { key, value };
		}
		else
		{
			for( int i = 0; i < keys.length; i += 2 )
			{
				if( keys[i].equals( key ) )
				{
					keys[i + 1] = value; // This modifies the keys array but it
					                     // doesn't make it invalidate for any
					                     // time so its ok (see note no top)
					return;
				}
			}
			String[] newKeys = new String[keys.length + 2];
			for( int i = 0; i < keys.length; i += 2 )
			{
				newKeys[i] = keys[i];
				newKeys[i + 1] = keys[i + 1];
			}
			newKeys[keys.length] = key;
			newKeys[keys.length + 1] = value;
			keys = newKeys;
		}
	}




	/**
	 * Remove the given key from the list
	 * 
	 * @param key
	 *            the key to be removed. Ignored, if key is null.
	 */
	@Override
	public void remove( String key )
	{
		if( key == null || keys == null ) return;
		if( !hasKey( key ) ) return;
		Map<String, String> originalKeys = getKeys();
		if( keys.length == 2 )
		{
			keys = null;
			return;
		}
		String[] newKeys = new String[keys.length - 2];
		int j = 0;
		for( int i = 0; i < keys.length; i += 2 )
		{
			if( !keys[i].equals( key ) )
			{
				newKeys[j++] = keys[i];
				newKeys[j++] = keys[i + 1];
			}
		}
		keys = newKeys;
	}




	/**
	 * Removes all keys from this primitive.
	 */
	@Override
	public void removeAll()
	{
		if( keys != null )
		{
			Map<String, String> originalKeys = getKeys();
			keys = null;
		}
	}




	/**
	 * Replies the map of key/value pairs. Never replies null. The map can be
	 * empty, though.
	 * 
	 * @return tags of this primitive. Changes made in returned map are not
	 *         mapped back to the primitive, use setKeys() to modify the keys
	 */
	@Override
	public Map<String, String> getKeys()
	{
		Map<String, String> result = new HashMap<String, String>();
		String[] keys = this.keys;
		if( keys != null )
		{
			for( int i = 0; i < keys.length; i += 2 )
			{
				result.put( keys[i], keys[i + 1] );
			}
		}
		return result;
	}




	/**
	 * Sets the keys of this primitives to the key/value pairs in
	 * <code>keys</code>. Old key/value pairs are removed. If <code>keys</code>
	 * is null, clears existing key/value pairs.
	 * 
	 * @param keys
	 *            the key/value pairs to set. If null, removes all existing
	 *            key/value pairs.
	 */
	@Override
	public void setKeys( Map<String, String> keys )
	{
		Map<String, String> originalKeys = getKeys();
		if( keys == null || keys.isEmpty() )
		{
			this.keys = null;
			return;
		}
		String[] newKeys = new String[keys.size() * 2];
		int index = 0;
		for( Entry<String, String> entry : keys.entrySet() )
		{
			newKeys[index++] = entry.getKey();
			newKeys[index++] = entry.getValue();
		}
		this.keys = newKeys;
	}




	@Override
	public final Collection<String> keySet()
	{
		String[] keys = this.keys;
		if( keys == null ) return Collections.emptySet();
		Set<String> result = new HashSet<String>( keys.length / 2 );
		for( int i = 0; i < keys.length; i += 2 )
		{
			result.add( keys[i] );
		}
		return result;
	}




	/**
	 * Replies true, if the map of key/value pairs of this primitive is not
	 * empty.
	 * 
	 * @return true, if the map of key/value pairs of this primitive is not
	 *         empty; false otherwise
	 */
	@Override
	public final boolean hasKeys()
	{
		return keys != null;
	}




	/**
	 * Replies true if this primitive has a tag with key <code>key</code>.
	 * 
	 * @param key
	 *            the key
	 * @return true, if his primitive has a tag with key <code>key</code>
	 */
	public boolean hasKey( String key )
	{
		String[] keys = this.keys;
		if( key == null ) return false;
		if( keys == null ) return false;
		for( int i = 0; i < keys.length; i += 2 )
		{
			if( keys[i].equals( key ) ) return true;
		}
		return false;
	}




	/**
	 * If set to true, this object is incomplete, which means only the id and
	 * type is known (type is the objects instance class)
	 */
	protected void setIncomplete( boolean incomplete )
	{
		updateFlags( FLAG_INCOMPLETE, incomplete );
	}




	@Override
	public boolean isIncomplete()
	{
		return ( flags & FLAG_INCOMPLETE ) != 0;
	}




	/**
	 * Replies <code>true</code> if the object has been modified since it was
	 * loaded from the server. In this case, on next upload, this object will be
	 * updated.
	 * 
	 * Deleted objects are deleted from the server. If the objects are added
	 * (id=0), the modified is ignored and the object is added to the server.
	 * 
	 * @return <code>true</code> if the object has been modified since it was
	 *         loaded from the server
	 */
	@Override
	public boolean isModified()
	{
		return ( flags & FLAG_MODIFIED ) != 0;
	}




	/*
	 * ------- /* FLAGS /* ------
	 */

	protected void updateFlags( int flag, boolean value )
	{
		if( value )
		{
			flags |= flag;
		}
		else
		{
			flags &= ~flag;
		}
	}




	public boolean isThisKind( String key )
	{
		return this.getKeys().containsKey( key ) == true;
	}





	public boolean isThisKind( String key, String value )
	{
		return this.getKeys().containsKey( key ) == true
		        && this.getKeys().get( key ).equals( value ) == true;
	}

}
