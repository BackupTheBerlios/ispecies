/*
 * PathFinderDirector.java
 *
 */

import java.awt.Point;
import java.util.logging.*;

import util.*;

/**
 *
 * <br>
 * @version $Revision: 1.4 $
 */
public class PathFinderDirector implements Director, ObjectListener {
	/** The version number of this file as determined by the RCS. */
	public static final String RCS_VERSION = "$Revision: 1.4 $";
	
	static Logger mLogger = Logger.getLogger(PathFinderDirector.class.getName());

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

	public class PathNode {
		// The position this node represents.
		FloatPoint mPosition;
		// The node from which we can most cheaply reach this node
		PathNode mCheapestPreviousNode;
		// The lowest cost with which we can reach this node (from mCheapestPreviousNode)
		double mLowestCost;
		// The nodes we can reach from this node.
		//    1
		// 0  o  2
		PathNode mNextNodes[] = new PathNode[3];
		double mNextCosts[] = new double[3];
		// indicates if the node has been evaluated already
		boolean mIsEvaluated = false;
		// whether this node is on the cheapest path
		boolean mIsMarked = false;
	}
	
	// Returns the lowest possible cost to get from [_p] to [_target]
	double getLowestPotentialCost(FloatPoint _p, FloatPoint _target) {
		return 2 * _p.distance(_target);
	}
	
	String mIndent = "";
	// Returns the leaf node with the lowest potential cost for reaching [_target] from [_startNode]
	PathNode findNodeWithLowestPotentialCost(PathNode _startNode, FloatPoint _target) {
		PathNode result = _startNode;
		if (_startNode != null) {
			//System.out.println(mIndent+"findNodeWithLowestPotentialCost"+StringHelper.toString(_startNode.mPosition));
			mIndent += "\t";
			double minCost = Float.POSITIVE_INFINITY;
			if (_startNode.mIsEvaluated) {
				// we already evaluated this node, so it can be a result
				result = null;
			}
			else {
				minCost = _startNode.mLowestCost + getLowestPotentialCost(_startNode.mPosition, _target);
			}
			// recursively scan PathNodes
			for (int direction = 0; direction < 3; direction++) {
				PathNode directionNode = findNodeWithLowestPotentialCost(_startNode.mNextNodes[direction], _target);
				if (directionNode != null && directionNode.mLowestCost + getLowestPotentialCost(directionNode.mPosition, _target) < minCost) {
					result = directionNode;
					minCost = directionNode.mLowestCost  + getLowestPotentialCost(directionNode.mPosition, _target);
				}
			}
			mIndent = mIndent.substring(0, mIndent.length()-1);
			//System.out.println(mIndent+"findNodeWithLowestPotentialCost"+StringHelper.toString(_startNode.mPosition)+" = "+StringHelper.toString(result.mPosition)+" for "+minCost);
			if (mIndent == null || mIndent.length() == 0) {
				System.out.println("findNodeWithLowestPotentialCost="+StringHelper.toString(result.mPosition)+" cost="+StringHelper.toString(minCost)+" distance="+StringHelper.toString(result.mPosition.distance(_target)));
			}
		}
		return result;
	}

	long mNodeCount = 0;
	PathNode createNode(FloatPoint _pos, PathNode _previous, double _cost) {
		if (mNodeCount++ % 1000 == 0) log("createNode: "+mNodeCount+" created");
		//log("createNode: pos="+StringHelper.toString(_pos)+" cost="+StringHelper.toString(_cost));
		PathNode result = new PathNode();
		result.mPosition = _pos;
		result.mCheapestPreviousNode = _previous;
		result.mLowestCost = _cost;
		return result;
	}
	
	// determines and creates the nodes we can reach from _node to get to [_target]
	void calculateDirections(PathNode _node, Point _target, GameMap _map, double _maxSpeed) {
		// determine distance and direction to _target
		FloatPoint direction = new FloatPoint(_target.x - _node.mPosition.x, _target.x - _node.mPosition.y);
		double distance = _node.mPosition.distance(_target.getX(), _target.getY());
		// if distance > speed, scale vector down to speed
		if (distance > _maxSpeed) {
			direction.x = (_maxSpeed * direction.x / distance);
			direction.y = (_maxSpeed * direction.y / distance);
		}
		// rotate left to get to point 0
		direction = MathHelper.rotate(direction, -90);
		for (int d = 0; d < 3; d++) {
			// determine next position in this direction
			FloatPoint nextPosition = new FloatPoint(_node.mPosition.x + direction.x, _node.mPosition.y + direction.y);
			// determine cost to get to next position in this direction
			double nextCost = _node.mLowestCost + getCostForMove(_node.mPosition, nextPosition, _map);
			// put the values into a new node and store it
			_node.mNextNodes[d] = createNode(nextPosition, _node, nextCost);
			// rotate right for nedt step
			direction = MathHelper.rotate(direction, 90);
		}
		// mark the node as having been evaluated
		_node.mIsEvaluated = true;
	}

	// Determines the direction to move in to get fastest from [_pos] to [_target] on [_map] at [_maxSpeed]
	public FloatPoint determineDirection(FloatPoint _pos, Point _target, GameMap _map, double _maxSpeed) {
		FloatPoint result = new FloatPoint(0, 0);
		FloatPoint floatTarget = new FloatPoint(_target);
		// create a node for _pos
		PathNode currentNode = null;
		if (mStartNode == null) {
			mStartNode = createNode(_pos, null, 0);
		}
		currentNode = findNodeWithLowestPotentialCost(mStartNode, floatTarget);
		if (currentNode.mPosition.toPoint().equals(_target)) {
			System.out.println("Found target");
			// track back from current node to start node
			while (currentNode.mCheapestPreviousNode != mStartNode) {
				currentNode.mIsMarked = true;
				currentNode = currentNode.mCheapestPreviousNode;
			}
			result = new FloatPoint(currentNode.mPosition.x - mStartNode.mPosition.x, currentNode.mPosition.y - mStartNode.mPosition.y);
			mStartNode = currentNode;
		}
		else {
			mLatestNode = currentNode;
			// determine where we can go from the cheapest node so far
			calculateDirections(currentNode, _target, _map, _maxSpeed);
		}
		// return the direction from the current _pos to the first next node on the path
		return result;
		/*
		FloatPoint floatTarget = new FloatPoint(_target);
		// create a node for _pos
		PathNode startNode = createNode(_pos, null, 0);
		PathNode currentNode = startNode;
		// while the current node is not at the target
		while ( ! currentNode.mPosition.toPoint().equals(_target) ) {
			// determine where we can go from the current node
			calculateDirections(currentNode, _target, _map, _maxSpeed);
			// set the new current node to the cheapest node
			currentNode = findNodeWithLowestPotentialCost(startNode, floatTarget);
		}
		// track back from current node to start node
		while (currentNode.mCheapestPreviousNode != startNode) {
			currentNode = currentNode.mCheapestPreviousNode;
		}
		// return the direction from the current _pos to the first next node on the path
		return new FloatPoint(currentNode.mPosition.x - _pos.x, currentNode.mPosition.y - _pos.y);
		*/
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
		cost += getFLAGMAP(_map)[parcelindex.x][parcelindex.y] * 1000;
		// return accumulated cost
		return cost;
	}

	public void log(String _line) {
		mLogger.info("PathFinderDirector: "+_line);
	}
	
}


/* Revision history, maintained by VSS.
 * $Log: PathFinderDirector.java,v $
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
