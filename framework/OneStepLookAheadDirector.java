import java.awt.Point;
import util.*;

/**
 * A simple Director that looks exactly one step ahead to determine the best direction.
 * Note that this director is likely to get it's actor stuck in local minima.
 * <br>
 * @author puf
 * @version $Revision: 1.1 $
 */
class OneStepLookAheadDirector implements Director {
	public static int[][] FLAGMAP = null;

	public synchronized int[][] getFLAGMAP(GameMap _map) {
		// initialize flag map
		if (FLAGMAP == null) {
			FLAGMAP = new int[_map.mParcelMapWidth][_map.mParcelMapHeight];
		}
		return FLAGMAP;
	}

	// Determines the direction to move in to get fastest from [_pos] to [_target] on [_map] at [_maxSpeed]
	public FloatPoint determineDirection(FloatPoint _pos, Point _target, GameMap _map, double _maxSpeed) {
		FloatPoint direction = new FloatPoint(_target.x - _pos.x, _target.x - _pos.y);
		double distance = _pos.distance(_target.getX(), _target.getY());
		// if distance > speed, scale vector down to speed
		if (distance > _maxSpeed) {
			direction.x = (_maxSpeed * direction.x / distance);
			direction.y = (_maxSpeed * direction.y / distance);
		}
		// determine the cost to move ahead, left or right
		double costAhead = getCostForMove(_pos, direction, _map);
		double costLeft = getCostForMove(_pos, MathHelper.rotate(direction, -90), _map);
		double costRight = getCostForMove(_pos, MathHelper.rotate(direction, 90), _map);
		if (costAhead > costLeft || costAhead > costRight) {
			Point point = _map.gameXYToParcelXY(_pos.x, _pos.y);
			log("Cost for moving from "+StringHelper.toString(_pos)+": ahead"+StringHelper.toString(direction)+"="+StringHelper.toString(costAhead)+" left="+StringHelper.toString(costLeft)+" right="+StringHelper.toString(costRight));
			if (costLeft < costRight) {
				log("Stepping left");
				direction = MathHelper.rotate(direction, -90);
			}
			else if (costRight < costLeft) {
				log("Stepping right");
				direction = MathHelper.rotate(direction, 90);
			}
			else {
				log("Stepping left/right");
				direction = ( (Math.random() < 0.5) ? MathHelper.rotate(direction, -90) : MathHelper.rotate(direction, 90) );
			}
		}
		return direction;
	}
	
	// Called to indicate that the game object has died at [_pos] on [_map]
	public void onObjectDied(FloatPoint _pos, GameMap _map) {
		Point point = _pos.toPoint();
		Point parcelposition = _map.gameXYToParcelXY(point.x, point.y);
		getFLAGMAP(_map)[parcelposition.x][parcelposition.y]++;
	}

	// determines the cost for an object at [_pos] to move in [_dir]
	protected double getCostForMove(FloatPoint _pos, FloatPoint _dir, GameMap _map) {
		FloatPoint dst = new FloatPoint(_pos.x + _dir.x, _pos.y + _dir.y);
		Point parcelindex = _map.gameXYToParcelXY((int)dst.x, (int)dst.y);
		return getFLAGMAP(_map)[parcelindex.x][parcelindex.y] * 1000 + _pos.distance(dst);
	}
	public void log(String _line) {
		Logger.log("OneStepLookAheadDirector: "+_line);
	}
}
