// License: GPL. Copyright 2007 by Immanuel Scholz and others
package org.windu2b.osm.check_transport_relations.data.osm;

import static org.windu2b.osm.check_transport_relations.tools.I18n.tr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.windu2b.osm.check_transport_relations.tools.Predicate;
import org.windu2b.osm.check_transport_relations.tools.SubclassFilteredCollection;

/**
 * DataSet is the data behind the application. It can consists of only a few
 * points up to the whole osm database. DataSet's can be merged together, saved,
 * (up/down/disk)loaded etc.
 * 
 * Note that DataSet is not an osm-primitive and so has no key association but a
 * few members to store some information.
 * 
 * Dataset is threadsafe - accessing Dataset simultaneously from different
 * threads should never lead to data corruption or
 * ConccurentModificationException. However when for example one thread removes
 * primitive and other thread try to add another primitive reffering to the
 * removed primitive, DataIntegrityException will occur.
 * 
 * To prevent such situations, read/write lock is provided. While read lock is
 * used, it's guaranteed that Dataset will not change. Sample usage: <code>
 *   ds.getReadLock().lock();
 *   try {
 *     // .. do something with dataset
 *   } finally {
 *     ds.getReadLock().unlock();
 *   }
 * </code>
 * 
 * Write lock should be used in case of bulk operations. In addition to ensuring
 * that other threads can't use dataset in the middle of modifications it also
 * stops sending of dataset events. That's good for performance reasons - GUI
 * can be updated after all changes are done. Sample usage: <code>
 * ds.beginUpdate()
 * try {
 *   // .. do modifications
 * } finally {
 *  ds.endUpdate();
 * }
 * </code>
 * 
 * Note that it is not necessary to call beginUpdate/endUpdate for every dataset
 * modification - dataset will get locked automatically.
 * 
 * Note that locks cannot be upgraded - if one threads use read lock and and
 * then write lock, dead lock will occur - see #5814 for sample ticket
 * 
 * @author imi
 */
public class DataSet implements Cloneable
{

	private static class IdHash implements Hash<PrimitiveId, OsmPrimitive>
	{

		public int getHashCode( PrimitiveId k )
		{
			return ( int ) k.getUniqueId() ^ k.getType().hashCode();
		}




		public boolean equals( PrimitiveId key, OsmPrimitive value )
		{
			if( key == null || value == null ) return false;
			return key.getUniqueId() == value.getUniqueId()
			        && key.getType() == value.getType();
		}
	}



	private Storage<OsmPrimitive>	       allPrimitives	 = new Storage<OsmPrimitive>(
	                                                                 new IdHash(),
	                                                                 true );


	private Map<PrimitiveId, OsmPrimitive>	primitivesMap	 = allPrimitives
	                                                                 .foreignKey( new IdHash() );


	// Number of open calls to beginUpdate
	private int	                           updateCount;


	private int	                           highlightUpdateCount;


	private boolean	                       uploadDiscouraged	= false;


	private final ReadWriteLock	           lock	             = new ReentrantReadWriteLock();




	public DataSet()
	{
	}




	public Lock getReadLock()
	{
		return lock.readLock();
	}




	/**
	 * This method can be used to detect changes in highlight state of
	 * primitives. If highlighting was changed then the method will return
	 * different number.
	 * 
	 * @return
	 */
	public int getHighlightUpdateCount()
	{
		return highlightUpdateCount;
	}



	/**
	 * History of selections - shared by plugins and SelectionListDialog
	 */
	private final LinkedList<Collection<? extends OsmPrimitive>>	selectionHistory	= new LinkedList<Collection<? extends OsmPrimitive>>();




	/**
	 * Replies the history of JOSM selections
	 * 
	 * @return
	 */
	public LinkedList<Collection<? extends OsmPrimitive>> getSelectionHistory()
	{
		return selectionHistory;
	}




	/**
	 * Clears selection history list
	 */
	public void clearSelectionHistory()
	{
		selectionHistory.clear();
	}



	/**
	 * The API version that created this data set, if any.
	 */
	private String	version;




	/**
	 * Replies the API version this dataset was created from. May be null.
	 * 
	 * @return the API version this dataset was created from. May be null.
	 */
	public String getVersion()
	{
		return version;
	}




	/**
	 * Sets the API version this dataset was created from.
	 * 
	 * @param version
	 *            the API version, i.e. "0.5" or "0.6"
	 */
	public void setVersion( String version )
	{
		this.version = version;
	}




	public final boolean isUploadDiscouraged()
	{
		return uploadDiscouraged;
	}




	public final void setUploadDiscouraged( boolean uploadDiscouraged )
	{
		this.uploadDiscouraged = uploadDiscouraged;
	}



	/*
	 * Holding bin for changeset tag information, to be applied when or if this
	 * is ever uploaded.
	 */
	private Map<String, String>	changeSetTags	= new HashMap<String, String>();




	public Map<String, String> getChangeSetTags()
	{
		return changeSetTags;
	}




	public void addChangeSetTag( String k, String v )
	{
		this.changeSetTags.put( k, v );
	}



	/**
	 * All nodes goes here, even when included in other data (ways etc). This
	 * enables the instant conversion of the whole DataSet by iterating over
	 * this data structure.
	 */
	private Collection<Node>	nodes	= new ArrayList<Node>();




	private <T extends OsmPrimitive> Collection<T> getPrimitives(
	        Predicate<OsmPrimitive> predicate )
	{
		return new SubclassFilteredCollection<OsmPrimitive, T>( allPrimitives,
		        predicate );
	}




	/**
	 * Replies an unmodifiable collection of nodes in this dataset
	 * 
	 * @return an unmodifiable collection of nodes in this dataset
	 */
	public Collection<Node> getNodes()
	{
		return getPrimitives( OsmPrimitive.nodePredicate );
	}



	/**
	 * All ways (Streets etc.) in the DataSet.
	 * 
	 * The way nodes are stored only in the way list.
	 */
	private Collection<Way>	ways	= new ArrayList<Way>();




	/**
	 * Replies an unmodifiable collection of ways in this dataset
	 * 
	 * @return an unmodifiable collection of ways in this dataset
	 */
	public Collection<Way> getWays()
	{
		return getPrimitives( OsmPrimitive.wayPredicate );
	}



	/**
	 * All relations/relationships
	 */
	private Collection<Relation>	relations	= new ArrayList<Relation>();




	/**
	 * Replies an unmodifiable collection of relations in this dataset
	 * 
	 * @return an unmodifiable collection of relations in this dataset
	 */
	public Collection<Relation> getRelations()
	{
		return getPrimitives( OsmPrimitive.relationPredicate );
	}




	/**
	 * @return A collection containing all primitives of the dataset. Data are
	 *         not ordered
	 */
	public Collection<OsmPrimitive> allPrimitives()
	{
		return getPrimitives( OsmPrimitive.allPredicate );
	}




	/**
	 * Adds a primitive to the dataset
	 * 
	 * @param primitive
	 *            the primitive.
	 */
	public void addPrimitive( OsmPrimitive primitive )
	{
		beginUpdate();
		try
		{
			if( getPrimitiveById( primitive ) != null )
			    throw new DataIntegrityProblemException(
			            tr( "Unable to add primitive {0} to the dataset because it is already included",
			                    primitive.toString() ) );
			boolean success = false;
			if( primitive instanceof Node )
			{
				success = nodes.add( ( Node ) primitive );
			}
			else if( primitive instanceof Way )
			{
				success = ways.add( ( Way ) primitive );
			}
			else if( primitive instanceof Relation )
			{
				success = relations.add( ( Relation ) primitive );
			}
			if( !success )
			    throw new RuntimeException( "failed to add primitive: "
			            + primitive );
			allPrimitives.add( primitive );
			primitive.setDataset( this );
		}
		finally
		{
			endUpdate();
		}
	}




	@Override
	public DataSet clone()
	{
		getReadLock().lock();
		try
		{
			DataSet ds = new DataSet();
			HashMap<OsmPrimitive, OsmPrimitive> primMap = new HashMap<OsmPrimitive, OsmPrimitive>();
			for( Node n : nodes )
			{
				Node newNode = new Node( n );
				primMap.put( n, newNode );
				ds.addPrimitive( newNode );
			}
			for( Way w : ways )
			{
				Way newWay = new Way( w );
				primMap.put( w, newWay );
				List<Node> newNodes = new ArrayList<Node>();
				for( Node n : w.getNodes() )
				{
					newNodes.add( ( Node ) primMap.get( n ) );
				}
				newWay.setNodes( newNodes );
				ds.addPrimitive( newWay );
			}
			// Because relations can have other relations as members we first
			// clone all relations
			// and then get the cloned members
			for( Relation r : relations )
			{
				Relation newRelation = new Relation( r );
				newRelation.setMembers( null );
				primMap.put( r, newRelation );
				ds.addPrimitive( newRelation );
			}
			for( Relation r : relations )
			{
				Relation newRelation = ( Relation ) primMap.get( r );
				List<RelationMember> newMembers = new ArrayList<RelationMember>();
				for( RelationMember rm : r.getMembers() )
				{
					newMembers.add( new RelationMember( rm.getRole(), primMap
					        .get( rm.getMember() ) ) );
				}
				newRelation.setMembers( newMembers );
			}
			ds.version = version;
			return ds;
		}
		finally
		{
			getReadLock().unlock();
		}
	}




	/**
	 * returns a primitive with a given id from the data set. null, if no such
	 * primitive exists
	 * 
	 * @param id
	 *            uniqueId of the primitive. Might be < 0 for newly created
	 *            primitives
	 * @param type
	 *            the type of the primitive. Must not be null.
	 * @return the primitive
	 * @exception NullPointerException
	 *                thrown, if type is null
	 */
	public OsmPrimitive getPrimitiveById( long id, OsmPrimitiveType type )
	{
		return getPrimitiveById( new SimplePrimitiveId( id, type ) );
	}




	public OsmPrimitive getPrimitiveById( PrimitiveId primitiveId )
	{
		return primitivesMap.get( primitiveId );
	}




	/**
	 * Show message and stack trace in log in case primitive is not found
	 * 
	 * @param primitiveId
	 * @return Primitive by id.
	 */
	private OsmPrimitive getPrimitiveByIdChecked( PrimitiveId primitiveId )
	{
		OsmPrimitive result = getPrimitiveById( primitiveId );
		if( result == null )
		{
			System.out
			        .println( tr(
			                "JOSM expected to find primitive [{0} {1}] in dataset but it is not there. Please report this "
			                        + "at http://josm.openstreetmap.de/. This is not a critical error, it should be safe to continue in your work.",
			                primitiveId.getType(),
			                Long.toString( primitiveId.getUniqueId() ) ) );
			new Exception().printStackTrace();
		}

		return result;
	}




	/**
	 * Can be called before bigger changes on dataset. Events are disabled until
	 * {@link #endUpdate()}. {@link DataSetListener#dataChanged()} event is
	 * triggered after end of changes <br>
	 * Typical usecase should look like this:
	 * 
	 * <pre>
	 * ds.beginUpdate();
	 * try {
	 *   ...
	 * } finally {
	 *   ds.endUpdate();
	 * }
	 * </pre>
	 */
	public void beginUpdate()
	{
		lock.writeLock().lock();
		updateCount++;
	}




	/**
	 * @see DataSet#beginUpdate()
	 */
	public void endUpdate()
	{
		if( updateCount > 0 )
		{
			updateCount--;
			lock.writeLock().unlock();

		}
		else throw new AssertionError( "endUpdate called without beginUpdate" );
	}




	/**
	 * Removes all primitives from the dataset and resets the currently selected
	 * primitives to the empty collection. Also notifies selection change
	 * listeners if necessary.
	 * 
	 */
	public void clear()
	{
		beginUpdate();
		try
		{
			for( OsmPrimitive primitive : allPrimitives )
			{
				primitive.setDataset( null );
			}
			nodes.clear();
			ways.clear();
			relations.clear();
			allPrimitives.clear();
		}
		finally
		{
			endUpdate();
		}
	}
}
