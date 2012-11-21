// License: GPL. For details, see LICENSE file.
package org.windu2b.osm.check_transport_relations.data.osm;

import static org.windu2b.osm.check_transport_relations.tools.I18n.tr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.windu2b.osm.check_transport_relations.data.conflict.Conflict;
import org.windu2b.osm.check_transport_relations.data.conflict.ConflictCollection;
import org.windu2b.osm.check_transport_relations.gui.progress.ProgressMonitor;
import org.windu2b.osm.check_transport_relations.tools.CheckParameterUtil;

/**
 * A dataset merger which takes a target and a source dataset and merges the
 * source data set onto the target dataset.
 * 
 */
public class DataSetMerger
{

	/** the collection of conflicts created during merging */
	private final ConflictCollection	        conflicts;


	/** the target dataset for merging */
	private final DataSet	                    targetDataSet;


	/** the source dataset where primitives are merged from */
	private final DataSet	                    sourceDataSet;


	/**
	 * A map of all primitives that got replaced with other primitives. Key is
	 * the PrimitiveId in their dataset, the value is the PrimitiveId in my
	 * dataset
	 */
	private final Map<PrimitiveId, PrimitiveId>	mergedMap;


	/**
	 * a set of primitive ids for which we have to fix references (to nodes and
	 * to relation members) after the first phase of merging
	 */
	private final Set<PrimitiveId>	            objectsWithChildrenToMerge;




	/**
	 * constructor
	 * 
	 * The visitor will merge <code>theirDataSet</code> onto
	 * <code>myDataSet</code>
	 * 
	 * @param targetDataSet
	 *            dataset with my primitives. Must not be null.
	 * @param sourceDataSet
	 *            dataset with their primitives. Ignored, if null.
	 * @throws IllegalArgumentException
	 *             thrown if myDataSet is null
	 */
	public DataSetMerger( DataSet targetDataSet, DataSet sourceDataSet )
	        throws IllegalArgumentException
	{
		CheckParameterUtil.ensureParameterNotNull( targetDataSet,
		        "targetDataSet" );
		this.targetDataSet = targetDataSet;
		this.sourceDataSet = sourceDataSet;
		conflicts = new ConflictCollection();
		mergedMap = new HashMap<PrimitiveId, PrimitiveId>();
		objectsWithChildrenToMerge = new HashSet<PrimitiveId>();
	}




	/**
	 * Merges a primitive <code>other</code> of type
	 * <P>
	 * onto my primitives.
	 * 
	 * If other.id != 0 it tries to merge it with an corresponding primitive
	 * from my dataset with the same id. If this is not possible a conflict is
	 * remembered in {@link #conflicts}.
	 * 
	 * If other.id == 0 it tries to find a primitive in my dataset with id == 0
	 * which is semantically equal. If it finds one it merges its technical
	 * attributes onto my primitive.
	 * 
	 * @param <P>
	 *            the type of the other primitive
	 * @param source
	 *            the other primitive
	 */
	protected void mergePrimitive( OsmPrimitive source,
	        Collection<? extends OsmPrimitive> candidates )
	{
		// try to merge onto a matching primitive with the same
		// defined id
		//
		if( mergeById( source ) ) return;
	}




	protected OsmPrimitive getMergeTarget( OsmPrimitive mergeSource )
	        throws IllegalStateException
	{
		PrimitiveId targetId = mergedMap.get( mergeSource.getPrimitiveId() );
		if( targetId == null ) return null;
		return targetDataSet.getPrimitiveById( targetId );
	}




	protected void addConflict( Conflict<?> c )
	{
		c.setMergedMap( mergedMap );
		conflicts.add( c );
	}




	protected void addConflict( OsmPrimitive my, OsmPrimitive their )
	{
		addConflict( new Conflict<OsmPrimitive>( my, their ) );
	}




	protected void fixIncomplete( Way other )
	{
		Way myWay = ( Way ) getMergeTarget( other );
		if( myWay == null )
		    throw new RuntimeException( tr(
		            "Missing merge target for way with id {0}",
		            other.getUniqueId() ) );
	}




	/**
	 * Postprocess the dataset and fix all merged references to point to the
	 * actual data.
	 */
	public void fixReferences()
	{
		for( Way w : sourceDataSet.getWays() )
		{
			if( !conflicts.hasConflictForTheir( w )
			        && objectsWithChildrenToMerge.contains( w.getPrimitiveId() ) )
			{
				mergeNodeList( w );
				fixIncomplete( w );
			}
		}
		for( Relation r : sourceDataSet.getRelations() )
		{
			if( !conflicts.hasConflictForTheir( r )
			        && objectsWithChildrenToMerge.contains( r.getPrimitiveId() ) )
			{
				mergeRelationMembers( r );
			}
		}
	}




	/**
	 * Merges the node list of a source way onto its target way.
	 * 
	 * @param source
	 *            the source way
	 * @throws IllegalStateException
	 *             thrown if no target way can be found for the source way
	 * @throws IllegalStateException
	 *             thrown if there isn't a target node for one of the nodes in
	 *             the source way
	 * 
	 */
	private void mergeNodeList( Way source ) throws IllegalStateException
	{
		Way target = ( Way ) getMergeTarget( source );
		if( target == null )
		    throw new IllegalStateException( tr(
		            "Missing merge target for way with id {0}",
		            source.getUniqueId() ) );

		List<Node> newNodes = new ArrayList<Node>( source.getNodesCount() );
		for( Node sourceNode : source.getNodes() )
		{
			Node targetNode = ( Node ) getMergeTarget( sourceNode );
			if( targetNode != null )
			{
				newNodes.add( targetNode );
			}
			else throw new IllegalStateException( tr(
			        "Missing merge target for node with id {0}",
			        sourceNode.getUniqueId() ) );
		}
		target.setNodes( newNodes );
	}




	/**
	 * Merges the relation members of a source relation onto the corresponding
	 * target relation.
	 * 
	 * @param source
	 *            the source relation
	 * @throws IllegalStateException
	 *             thrown if there is no corresponding target relation
	 * @throws IllegalStateException
	 *             thrown if there isn't a corresponding target object for one
	 *             of the relation members in source
	 */
	private void mergeRelationMembers( Relation source )
	        throws IllegalStateException
	{
		Relation target = ( Relation ) getMergeTarget( source );
		if( target == null )
		    throw new IllegalStateException( tr(
		            "Missing merge target for relation with id {0}",
		            source.getUniqueId() ) );
		LinkedList<RelationMember> newMembers = new LinkedList<RelationMember>();
		for( RelationMember sourceMember : source.getMembers() )
		{
			OsmPrimitive targetMember = getMergeTarget( sourceMember
			        .getMember() );
			if( targetMember == null )
			    throw new IllegalStateException( tr(
			            "Missing merge target of type {0} with id {1}",
			            sourceMember.getType(), sourceMember.getUniqueId() ) );
			RelationMember newMember = new RelationMember(
			        sourceMember.getRole(), targetMember );
			newMembers.add( newMember );
		}
		target.setMembers( newMembers );
	}




	/**
	 * Tries to merge a primitive <code>source</code> into an existing primitive
	 * with the same id.
	 * 
	 * @param source
	 *            the source primitive which is to be merged into a target
	 *            primitive
	 * @return true, if this method was able to merge <code>source</code> into a
	 *         target object; false, otherwise
	 */
	private boolean mergeById( OsmPrimitive source )
	{
		OsmPrimitive target = targetDataSet.getPrimitiveById( source.getId(),
		        source.getType() );
		// merge other into an existing primitive with the same id, if possible
		//
		if( target == null ) return false;
		// found a corresponding target, remember it
		mergedMap.put( source.getPrimitiveId(), target.getPrimitiveId() );

		if( target.getVersion() > source.getVersion() )
		// target.version > source.version => keep target version
		    return true;

		if( target.isIncomplete() && !source.isIncomplete() )
		{
			// target is incomplete, source completes it
			// => merge source into target
			//
			target.mergeFrom( source );
			objectsWithChildrenToMerge.add( source.getPrimitiveId() );
		}
		else if( !target.isIncomplete() && source.isIncomplete() )
		{
			// target is complete and source is incomplete
			// => keep target, it has more information already
			//
		}
		else if( target.isIncomplete() && source.isIncomplete() )
		{
			// target and source are incomplete. Doesn't matter which one to
			// take. We take target.
			//
		}
		else if( !target.isModified() && !source.isModified()
		        && target.getVersion() == source.getVersion() )
		{
			// both not modified. Merge nevertheless.
			// This helps when updating "empty" relations, see #4295
			target.mergeFrom( source );
			objectsWithChildrenToMerge.add( source.getPrimitiveId() );
		}
		else if( !target.isModified() && !source.isModified()
		        && target.getVersion() < source.getVersion() )
		{
			// my not modified but other is newer. clone other onto mine.
			//
			target.mergeFrom( source );
			objectsWithChildrenToMerge.add( source.getPrimitiveId() );
		}
		else
		{
			// clone from other. mergeFrom will mainly copy
			// technical attributes like timestamp or user information. Semantic
			// attributes should already be equal if we get here.
			//
			target.mergeFrom( source );
			objectsWithChildrenToMerge.add( source.getPrimitiveId() );
		}
		return true;
	}




	/**
	 * Runs the merge operation. Successfully merged {@link OsmPrimitive}s are
	 * in {@link #getMyDataSet()}.
	 * 
	 * See {@link #getConflicts()} for a map of conflicts after the merge
	 * operation.
	 */
	public void merge()
	{
		merge( null );
	}




	/**
	 * Runs the merge operation. Successfully merged {@link OsmPrimitive}s are
	 * in {@link #getMyDataSet()}.
	 * 
	 * See {@link #getConflicts()} for a map of conflicts after the merge
	 * operation.
	 */
	public void merge( ProgressMonitor progressMonitor )
	{
		if( sourceDataSet == null ) return;
		if( progressMonitor != null )
		{
			progressMonitor.beginTask( tr( "Merging data..." ), sourceDataSet
			        .allPrimitives().size() );
		}
		targetDataSet.beginUpdate();
		try
		{
			ArrayList<? extends OsmPrimitive> candidates = new ArrayList<Node>(
			        targetDataSet.getNodes() );
			for( Node node : sourceDataSet.getNodes() )
			{
				mergePrimitive( node, candidates );
				if( progressMonitor != null )
				{
					progressMonitor.worked( 1 );
				}
			}
			candidates.clear();
			candidates = new ArrayList<Way>( targetDataSet.getWays() );
			for( Way way : sourceDataSet.getWays() )
			{
				mergePrimitive( way, candidates );
				if( progressMonitor != null )
				{
					progressMonitor.worked( 1 );
				}
			}
			candidates.clear();
			candidates = new ArrayList<Relation>( targetDataSet.getRelations() );
			for( Relation relation : sourceDataSet.getRelations() )
			{
				mergePrimitive( relation, candidates );
				if( progressMonitor != null )
				{
					progressMonitor.worked( 1 );
				}
			}
			candidates.clear();
			fixReferences();
		}
		finally
		{
			targetDataSet.endUpdate();
		}
		if( progressMonitor != null )
		{
			progressMonitor.finishTask();
		}
	}




	/**
	 * replies my dataset
	 * 
	 * @return
	 */
	public DataSet getTargetDataSet()
	{
		return targetDataSet;
	}




	/**
	 * replies the map of conflicts
	 * 
	 * @return the map of conflicts
	 */
	public ConflictCollection getConflicts()
	{
		return conflicts;
	}
}
