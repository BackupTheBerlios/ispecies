/*
 * PathFinderDirector.java
 *
 */

import java.awt.Point;
import util.*;

/**
 * This class finds the path for it's associated path finder.
 * <br>
 * @version $Revision: 1.5 $
 */
public class PathFinderDirector implements Director, ObjectListener {
	/** The version number of this file as determined by the RCS. */
	public static final String RCS_VERSION = "$Revision: 1.5 $";
	
	public static int[][] FLAGMAP = null;

	public static synchronized int[][] getFLAGMAP(GameMap _map) {
		// initialize flag map
		if (FLAGMAP == null) {
			FLAGMAP = new int[_map.mParcelMapWidth][_map.mParcelMapHeight];
		}
		return FLAGMAP;
	}
	
	public PathNode mStartNode = null;
	public PathNode mLatestNode = null;
	java.util.Map mNodePositionMap = new java.util.HashMap();
	PriorityQueue mNodesByCost = new PriorityQueue();

	public static final int SEGMENT_COUNT = 8;
	public class PathNode {
		// The position this node represents.
		FloatPoint mPosition;
		// The node from which we reached this node
		PathNode mPreviousNode;
		// The cost with which we can reach this node (from the start position via mPreviousNode)
		double mCost;
		// the lowest potential cost with which we can reach the target from this node
		double mPotentialCost;
		// The nodes we can reach from this node.
		//      3
		//   2     4
		// 1    o    5
		//   0     6
		PathNode mNextNodes[] = new PathNode[SEGMENT_COUNT-1];
		double mNextCosts[] = new double[SEGMENT_COUNT-1];
		// indicates if the node has been evaluated already
		boolean mIsEvaluated = false;
		// whether this node is on the cheapest path
		boolean mIsMarked = false;
	}
	
	// Returns the lowest possible cost to get from [_p] to [_target]
	double getLowestPotentialCost(FloatPoint _p, FloatPoint _target) {
		return 2 * _p.distance(_target);
	}
	
	// Returns the leaf node with the lowest potential cost for reaching [_target] from [_startNode]
	PathNode findNodeWithLowestPotentialCost(PathNode _startNode, FloatPoint _target) {
		PathNode result = _startNode;
		if (mNodesByCost.size() > 0) {
			result = (PathNode) mNodesByCost.get();
		}
		else if (_startNode != null) {			
			double minCost = Float.POSITIVE_INFINITY;
			if (_startNode.mIsEvaluated) {
				// we already evaluated this node, so it can be a result
				result = null;
			}
			else {
				minCost = _startNode.mCost + getLowestPotentialCost(_startNode.mPosition, _target);
			}
			// recursively scan PathNodes
			for (int direction = 0; direction < SEGMENT_COUNT-1; direction++) {
				PathNode directionNode = findNodeWithLowestPotentialCost(_startNode.mNextNodes[direction], _target);
				if (directionNode != null && directionNode.mCost + getLowestPotentialCost(directionNode.mPosition, _target) < minCost) {
					result = directionNode;
					minCost = directionNode.mCost  + getLowestPotentialCost(directionNode.mPosition, _target);
				}
			}
		}
		return result;
	}

	// Returns true if a node with [_nextPoint] is on the path back from [_node]
	boolean isPositionOnPath(Point _point, PathNode _node) {
		if (_node == null) {
			// arrived at start of path without a match
			return false;
		}
		else if (_point.equals(_node.mPosition.toPoint())) {
			// found a match
			return true;
		}
		else {
			// search backwards
			return isPositionOnPath(_point, _node.mPreviousNode);
		}
	}


	long mNodeCount = 0;
	long mRedundantNodesNotCreatedCount = 0;
	long mExpensiveNodesNotCreatedCount=0;
	long mNodesCutCount = 0;
	PathNode createNode(FloatPoint _pos, PathNode _previous, double _cost) {
		if (mNodeCount++ % 1000 == 0) {
			log("createNode   : "+mNodeCount+" created");
			log("\tnodes cut  : "+mNodesCutCount);
			log("\tnodes left : "+countNodes(mStartNode));
			log("\tnot created: "+mRedundantNodesNotCreatedCount+" already on path + "+mExpensiveNodesNotCreatedCount+" more expensive than existing path to point");
		}
			
		//log("createNode: pos="+StringHelper.toString(_pos)+" cost="+StringHelper.toString(_cost));
		PathNode result = new PathNode();
		result.mPosition = _pos;
		result.mPreviousNode = _previous;
		result.mCost = _cost;
		return result;
	}
	
	// counts the nodes that can be reached from [_node]
	long countNodes(PathNode _node) {
		long result = 0;
		if (_node != null) {
			result++;
			for (int d=0; d < SEGMENT_COUNT-1; d++) {
				result += countNodes(_node.mNextNodes[d]);
			}
		}
		return result;
	}
	
	// determines and creates the nodes we can reach from _node to get to [_target]
	void calculateDirections(PathNode _node, Point _target, GameMap _map, double _maxSpeed) {
		// determine distance and direction
		FloatPoint direction = null;
		double distance = 0; // distance of the move
		double distanceToTarget = _node.mPosition.distance(_target);
		if (_node.mPreviousNode == null) {
			// determine distance and direction to target
			direction = new FloatPoint(_target.x - _node.mPosition.x, _target.y - _node.mPosition.y);
			distance = distanceToTarget;
		}
		else {
			// go straight ahead from previous move
			direction = new FloatPoint(_node.mPosition.x - _node.mPreviousNode.mPosition.x, _node.mPosition.y - _node.mPreviousNode.mPosition.y);
			distance = direction.distance(0.0, 0.0);
			if (distance < _maxSpeed) {
				direction = new FloatPoint(direction.x * _maxSpeed/distance, direction.y * _maxSpeed/distance);
				distance = direction.distance(0.0, 0.0);
			}
		}
		// make sure we stick to the speed limit
		if (distance > _maxSpeed) {
			direction.x = (_maxSpeed * direction.x / distance);
			direction.y = (_maxSpeed * direction.y / distance);
			distance = direction.distance(0.0, 0.0);
		}
		// make sure we don't "overshoot" our target
		if (distance > distanceToTarget) {
			direction.x = (distanceToTarget * direction.x) / distance;
			direction.y = (distanceToTarget * direction.y) / distance;
			distance = direction.distance(0.0, 0.0);
		}
		// rotate left to get to point 0
		int angle = 360 / SEGMENT_COUNT;
		
		direction = MathHelper.rotate(direction,  -angle * (int)Math.floor((SEGMENT_COUNT-1) / 2));
		for (int d = 0; d < SEGMENT_COUNT-1; d++) {
			// determine next position in this direction
			FloatPoint nextPosition = new FloatPoint(_node.mPosition.x + direction.x, _node.mPosition.y + direction.y);
			if ( ! isPositionOnPath(nextPosition.toPoint(), _node) ) {
				// determine cost to get to next position in this direction
				double nextCost = _node.mCost + getCostForMove(_node.mPosition, nextPosition, _map);
				// if a node already exists on the next position
				if (mNodePositionMap.get(nextPosition.toPoint()) != null) {
					PathNode existingNode = (PathNode)mNodePositionMap.get(nextPosition.toPoint());
					// if this route is cheaper than the current route to nextPosition
					if (existingNode.mCost > nextCost) {
						// clear the 'next' reference of the old previous node
						for (int d2=0; d2 < SEGMENT_COUNT-1; d2++) {
							if (existingNode.mPreviousNode.mNextNodes[d2] == existingNode) {
								existingNode.mPreviousNode.mNextNodes[d2] = null;
								mNodesCutCount += countNodes(existingNode);
								//log("Cutting "+countNodes(existingNode)+" more expensive nodes at "+StringHelper.toString(nextPosition.toPoint()));
							}
						}
					}
					else {
						mExpensiveNodesNotCreatedCount++;
						//log("Ignoring new route to "+StringHelper.toString(nextPosition.toPoint())+" since a cheaper alternative already exists");
					}
				}
				else {
					// put the values into a new node and store it
					_node.mNextNodes[d] = createNode(nextPosition, _node, nextCost);
					_node.mNextNodes[d].mPotentialCost = nextCost + getLowestPotentialCost(nextPosition, new FloatPoint(_target));
					// put the new node in the map
					mNodePositionMap.put(nextPosition.toPoint(), _node.mNextNodes[d]);
					// put the new node in the cost map
					mNodesByCost.add(_node.mNextNodes[d], (long)(1000*_node.mNextNodes[d].mPotentialCost));
					//System.out.println("mNodesByCost now contains "+mNodesByCost.size()+" nodes, the cheapest of which has potential cost of "+mNodesByCost.getPriority());
				}
			}
			else {
				mRedundantNodesNotCreatedCount++;
				//log("Encountered point "+StringHelper.toString(nextPosition.toPoint())+" that's already on this path. Ignoring the new instance...");
			}
			// rotate right for nedt step
			direction = MathHelper.rotate(direction, angle);
		}
		// mark the node as having been evaluated
		_node.mIsEvaluated = true;
		// TODO: remote the evaluated node from the cost map
		mNodesByCost.remove(_node);
	}

	boolean bFoundTarget = false;
	// Determines the direction to move in to get fastest from [_pos] to [_target] on [_map] at [_maxSpeed]
	public FloatPoint determineDirection(FloatPoint _pos, Point _target, GameMap _map, double _maxSpeed) {
		// if the start position is the same as the target, do nothing
		if (_pos.toPoint().equals(_target)) return null;
		// determine an arbitrary result to avoid NPE
		FloatPoint result = new FloatPoint(0, 0);
		Profiler.startProfiling("PathFinderDirector.determineDirection()"); 
		try {
			FloatPoint floatTarget = new FloatPoint(_target);
			// if this is the first time we're called, create a start node
			if (mStartNode == null) {
				mStartNode = createNode(_pos, null, 0);
			}
			// find the node that currently has the lowest potential cost to reach _target
			PathNode currentNode = findNodeWithLowestPotentialCost(mStartNode, floatTarget);
			if (currentNode.mPosition.toPoint().equals(_target)) {
				if ( ! bFoundTarget ) {
					bFoundTarget = true;
					log("Found target!");
					log("Nodes created: "+mNodeCount);
					log("  not created: "+mRedundantNodesNotCreatedCount);
					log("Nodes in use : "+countNodes(mStartNode));
				}
				// track back from current node to start node
				while (currentNode.mPreviousNode != null && currentNode.mPreviousNode != mStartNode) {
					currentNode.mIsMarked = true;
					currentNode = currentNode.mPreviousNode;
				}
				result = new FloatPoint(currentNode.mPosition.x - mStartNode.mPosition.x, currentNode.mPosition.y - mStartNode.mPosition.y);
				mStartNode = currentNode;
			}
			else {
				mLatestNode = currentNode;
				// determine where we can go from the cheapest node so far
				calculateDirections(currentNode, _target, _map, _maxSpeed);
				// <TEMP> Speed up calculation
				for (int i=0; i < 500; i++) {
					// find the node that currently has the lowest potential cost to reach _target
					currentNode = findNodeWithLowestPotentialCost(mStartNode, floatTarget);
					if ( ! currentNode.mPosition.toPoint().equals(_target)) {
						mLatestNode = currentNode;
						// determine where we can go from the cheapest node so far
						calculateDirections(currentNode, _target, _map, _maxSpeed);
					}
					else {
						break;
					}
				}
				// </TEMP>
			} // else
		} finally { Profiler.endProfiling(); }
		// return the direction from the current _pos to the first next node on the path
		return result;
	}
	
	// Called to indicate that the game object has died
	public void died(ObjectEvent _e) {
		BaseGameObject src = (BaseGameObject)_e.getSource();
		Point point = src.getPosition().toPoint();
		GameMap map = src.getMap();
		Point parcelposition = map.gameXYToParcelXY(point.x, point.y);
		getFLAGMAP(map)[parcelposition.x][parcelposition.y]++;
	}

	// determines the cost for an object at [_pos] to move in [_dst]
	protected double getCostForMove(FloatPoint _pos, FloatPoint _dst, GameMap _map) {
		double cost = 0.0;
		// determine the cost for actually moving from _pos in _dir
		cost += _pos.distance(_dst);
		// determine the cost for the new position
		Point parcelindex = _map.gameXYToParcelXY((int)_dst.x, (int)_dst.y);
		if (
			parcelindex.x >= 0 && 
			parcelindex.x < getFLAGMAP(_map).length &&
			parcelindex.y >= 0 &&
			parcelindex.y < getFLAGMAP(_map)[parcelindex.x].length
		) {
			cost += getFLAGMAP(_map)[parcelindex.x][parcelindex.y] * 1000;
			// <TEMP>
			Parcel aNewParcel = _map.getParcel(_dst);
			if (aNewParcel.getTerrain() instanceof WaterTerrain) {
				cost += 1000;
			}
			// </TEMP>
		}
		else {
			// hit boundary of map
			cost += 100000;
		}
		// return accumulated cost
		return cost;
	}

	public void log(String _line) {
		Logger.info("PathFinderDirector: "+_line);
	}
	
}


/* Revision history, maintained by VSS.
 * $Log: PathFinderDirector.java,v $
 * Revision 1.5  2003/06/05 15:29:28  puf
 * Now uses a priority queue to find the cheapest node. This has speeded up the path finding by a factor (10x).
 *
 * Revision 1.4  2002/11/12 08:34:38  quintesse
 * Now using official 1.4 JDK logging system.
 *
 * Revision 1.3  2002/11/11 10:49:39  quintesse
 * The PathFinderDirector now listens for the GameObject's died() event.
 *
 * Revision 1.2  2002/11/11 09:06:25  quintesse
 * Missing import for FloatPoint.
 *
 * Revision 1.1  2002/11/10 08:14:59  puf
 * The PathFinderDirector implements the Director interface using my own path finding algorithm with an "ant based" cost function.
 *
 *
 */
