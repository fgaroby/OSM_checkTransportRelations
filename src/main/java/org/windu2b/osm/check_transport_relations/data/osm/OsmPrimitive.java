/**
 * 
 */
package org.windu2b.osm.check_transport_relations.data.osm;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.windu2b.osm.check_transport_relations.data.osm.DataIntegrityProblemException;
import org.windu2b.osm.check_transport_relations.data.osm.OsmPrimitive;
import org.windu2b.osm.check_transport_relations.io.OsmServerBackreferenceReader;
import org.windu2b.osm.check_transport_relations.io.OsmServerReader;
import org.windu2b.osm.check_transport_relations.io.OsmTransferException;
import org.windu2b.osm.check_transport_relations.tools.CheckParameterUtil;
import org.windu2b.osm.check_transport_relations.tools.Predicate;

/**
 * @author windu
 * 
 */
public abstract class OsmPrimitive extends AbstractPrimitive implements
        Comparable<OsmPrimitive>
{
	/*---------
	 * DATASET
	 *---------*/

	/** the parent dataset */
	private DataSet	                            dataSet;


	/**
	 * Some predicates, that describe conditions on primitives.
	 */
	public static final Predicate<OsmPrimitive>	nodePredicate	  = new Predicate<OsmPrimitive>()
	                                                              {
		                                                              @Override
		                                                              public boolean evaluate(
		                                                                      OsmPrimitive primitive )
		                                                              {
			                                                              return primitive
			                                                                      .getClass() == Node.class;
		                                                              }
	                                                              };


	public static final Predicate<OsmPrimitive>	wayPredicate	  = new Predicate<OsmPrimitive>()
	                                                              {
		                                                              @Override
		                                                              public boolean evaluate(
		                                                                      OsmPrimitive primitive )
		                                                              {
			                                                              return primitive
			                                                                      .getClass() == Way.class;
		                                                              }
	                                                              };


	public static final Predicate<OsmPrimitive>	relationPredicate	= new Predicate<OsmPrimitive>()
	                                                              {
		                                                              @Override
		                                                              public boolean evaluate(
		                                                                      OsmPrimitive primitive )
		                                                              {
			                                                              return primitive
			                                                                      .getClass() == Relation.class;
		                                                              }
	                                                              };


	public static final Predicate<OsmPrimitive>	allPredicate	  = new Predicate<OsmPrimitive>()
	                                                              {
		                                                              @Override
		                                                              public boolean evaluate(
		                                                                      OsmPrimitive primitive )
		                                                              {
			                                                              return true;
		                                                              }
	                                                              };




	/**
	 * Creates a new primitive for the given id.
	 * 
	 * If allowNegativeId is set, provided id can be < 0 and will be set to
	 * primitive without any processing. If allowNegativeId is not set, then id
	 * will have to be 0 (in that case new unique id will be generated) or
	 * positive number.
	 * 
	 * @param id
	 *            the id
	 * @throws IllegalArgumentException
	 *             thrown if id < 0 and allowNegativeId is false
	 */
	protected OsmPrimitive( long id ) throws IllegalArgumentException
	{

		if( id < 0 )
			throw new IllegalArgumentException( MessageFormat.format(
			        "Expected ID >= 0. Got {0}.", id ) );
		else
		{
			this.id = id;
		}
		this.version = 0;
		this.setIncomplete( true );
	}




	/**
	 * Creates a new primitive for the given id and version.
	 * 
	 * If allowNegativeId is set, provided id can be < 0 and will be set to
	 * primitive without any processing. If allowNegativeId is not set, then id
	 * will have to be 0 (in that case new unique id will be generated) or
	 * positive number.
	 * 
	 * If id is not > 0 version is ignored and set to 0.
	 * 
	 * @param id
	 * @param version
	 * @throws IllegalArgumentException
	 *             thrown if id < 0 and allowNegativeId is false
	 */
	protected OsmPrimitive( long id, int version )
	        throws IllegalArgumentException
	{
		this( id );
		this.version = version;
		setIncomplete( true );
	}




	/**
	 * This method should never ever by called from somewhere else than
	 * Dataset.addPrimitive or removePrimitive methods
	 * 
	 * @param dataSet
	 */
	void setDataset( DataSet dataSet )
	{
		if( this.dataSet != null && dataSet != null && this.dataSet != dataSet )
		    throw new DataIntegrityProblemException(
		            "Primitive cannot be included in more than one Dataset" );
		this.dataSet = dataSet;
	}




	/**
	 * 
	 * @return DataSet this primitive is part of.
	 */
	public DataSet getDataSet()
	{
		return dataSet;
	}




	/**
	 * Throws exception if primitive is not part of the dataset
	 */
	public void checkDataset()
	{
		if( dataSet == null )
		    throw new DataIntegrityProblemException(
		            "Primitive must be part of the dataset: " + toString() );
	}




	protected boolean writeLock()
	{
		if( dataSet != null )
		{
			dataSet.beginUpdate();
			return true;
		}
		else return false;
	}




	protected void writeUnlock( boolean locked )
	{
		if( locked )
		{
			// It shouldn't be possible for dataset to become null because
			// method calling setDataset would need write lock which is owned by
			// this thread
			dataSet.endUpdate();
		}
	}




	@Override
	protected void setIncomplete( boolean incomplete )
	{
		boolean locked = writeLock();
		try
		{
			super.setIncomplete( incomplete );
		}
		finally
		{
			writeUnlock( locked );
		}
	}




	/**
	 * Merges the technical and semantical attributes from <code>other</code>
	 * onto this.
	 * 
	 * Both this and other must be new, or both must be assigned an OSM ID. If
	 * both this and <code>other</code> have an assigend OSM id, the IDs have to
	 * be the same.
	 * 
	 * @param other
	 *            the other primitive. Must not be null.
	 * @throws IllegalArgumentException
	 *             thrown if other is null.
	 * @throws DataIntegrityProblemException
	 *             thrown if either this is new and other is not, or other is
	 *             new and this is not
	 * @throws DataIntegrityProblemException
	 *             thrown if other isn't new and other.getId() != this.getId()
	 */
	public void mergeFrom( OsmPrimitive other )
	{
		boolean locked = writeLock();
		try
		{
			CheckParameterUtil.ensureParameterNotNull( other, "other" );

			setKeys( other.getKeys() );
			timestamp = other.timestamp;
			version = other.version;
			setIncomplete( other.isIncomplete() );
			flags = other.flags;
			changesetId = other.changesetId;
		}
		finally
		{
			writeUnlock( locked );
		}
	}




	/**
	 * Loads (clone) this primitive from provided PrimitiveData
	 * 
	 * @param data
	 */
	public void load( PrimitiveData data )
	{
		// Write lock is provided by subclasses
		setKeys( data.getKeys() );
		setTimestamp( data.getTimestamp() );
		setIncomplete( data.isIncomplete() );
		version = data.getVersion();
	}




	protected DataSet loadRelations() throws OsmTransferException
	{
		/*
		 * if( isIncomplete() ) {
		 */
		OsmServerReader reader = new OsmServerBackreferenceReader( getId(),
		        this.getType(), true );
		DataSet ds = reader.parseOsm( null );

		if( getDataSet() != null )
		{
			DataSetMerger dm = new DataSetMerger( getDataSet(), ds );
			dm.merge();
		}
		this.setIncomplete( false );

		return ds;
		/*
		 * }
		 * 
		 * return getDataSet();
		 */
	}




	public OsmPrimitive addRelation( Relation relation )
	{
		boolean locked = writeLock();
		try
		{
			dataSet.addPrimitive( relation );
		}
		finally
		{
			writeUnlock( locked );
		}

		return this;
	}




	public Collection<Relation> getRelations( String key )
	        throws OsmTransferException
	{
		loadRelations();

		Collection<Relation> relations = new ArrayList<Relation>();

		for( Relation r : getDataSet().getRelations() )
		{
			if( r.isThisKind( key ) ) relations.add( r );
		}

		return relations;
	}




	public Collection<Relation> getRelations( String key, String value )
	        throws OsmTransferException
	{
		loadRelations();

		Collection<Relation> relations = new ArrayList<Relation>();

		for( Relation r : getDataSet().getRelations() )
		{
			if( r.isThisKind( key, value ) ) relations.add( r );
		}

		return relations;
	}




	public Relation getRelation( String key, String value )
	        throws OsmTransferException
	{
		loadRelations();

		for( Relation r : getDataSet().getRelations() )
		{
			if( r.isThisKind( key, value ) ) return r;
		}

		return null;
	}




	public Relation getRelation( TagCollection tagCollection )
	        throws OsmTransferException
	{
		DataSet ds = loadRelations();
		Relation relation = null;
		for( Relation r : ds.getRelations() )
		{
			for( Tag t : tagCollection )
			{
				if( r.hasKeyValue( t.getKey(), t.getValue() ) )
				{
					relation = r;
				}
			}
		}

		return relation;
	}




	@Override
	public final void put( String key, String value )
	{
		super.put( key, value );
	}




	/*----------------
	 * OBJECT METHODS
	 *---------------*/

	/**
	 * Equal, if the id (and class) is equal.
	 * 
	 * An primitive is equal to its incomplete counter part.
	 */
	@Override
	public boolean equals( Object obj )
	{
		if( obj instanceof OsmPrimitive )
		    return ( ( OsmPrimitive ) obj ).id == id
		            && obj.getClass() == getClass();
		return false;
	}




	/**
	 * Return the id plus the class type encoded as hashcode or super's hashcode
	 * if id is 0.
	 * 
	 * An primitive has the same hashcode as its incomplete counterpart.
	 */
	@Override
	public final int hashCode()
	{
		return ( int ) id;
	}

}
