/*
 * PathFinder.java
 *
 * Created on June 3, 2002, 7:55 PM
 */

import java.awt.*;
import java.awt.geom.*;

/**
 * An object that tries to find it's way to a target. PathFinders die when they get into water.
 * <br>
 * @author  puf
 */
public class PathFinder extends BaseGameObject implements Targettable, TimerReceiver {
	private Point mTarget = null;
	private Universe mGame = null;
	private TimerTrigger mTrigger;
	private int mSpeed = 1;
	Director mDirector = new OneStepLookAheadDirector();

	/** Creates a new instance of PathFinder */
	public PathFinder(GameMap _map, Universe _game, Point _position) {
		super(_map, _position);
		mGame = _game;
		// add to heartbeat
		mTrigger = new TimerTrigger(this);
		mTrigger.setRepeat(true);
		_game.heartBeat.addRel ( mTrigger, 1 );
	}

	/** Creates a new instance of PathFinder */
	public PathFinder(String _name, GameMap _map, Universe _game, Point _position) {
		this(_map, _game, _position);
		super.setName(_name);
	}
	
	public Point getTarget() {
		return mTarget;
	}
	
	public void setTarget(Point _target) {
		mTarget = _target;
	}
	
	public void doTimer(TimerTrigger timerTrigger) {
		//log("received timer trigger");
		if (mPosition != null && mTarget != null) {
			// determine direction and distance of movement
			FloatPoint pos = new FloatPoint(getPosition());
			FloatPoint direction = mDirector.determineDirection(pos, mTarget, mGame.getMap(), mSpeed);
			// update position according to direction and clip to map size
			pos.x += direction.x;
			if (pos.x < 0) {
				pos.x = 0;
			} else if (pos.x >= mMap.mMapWidth) {
				pos.x = (int)mMap.mMapWidth-1;
			}
			pos.y += direction.y;
			if (pos.y < 0) {
				pos.y = 0;
			} else if (pos.y >= mMap.mMapHeight) {
				pos.y = (int)mMap.mMapHeight-1;
			}
			// update the position field
			setPosition(pos);
		}
	}
	
	// rotates the vector [_v] by [_a] degrees
	protected Point rotate(Point _v, int _a) {
		double a = Math.toRadians(_a);
		Point result = new Point(
			(int)Math.round(_v.x * Math.cos(a) + _v.y * Math.sin(a)),
			(int)Math.round(_v.y * Math.cos(a) - _v.x * Math.sin(a))
		);
		//log("rotate("+_v+", "+_a+") = "+result);
		return result;
	}

	// rotates the vector [_v] by [_a] degrees
	protected FloatPoint rotate(FloatPoint _v, int _a) {
		double a = Math.toRadians(_a);
		FloatPoint result = new FloatPoint(
			_v.x * Math.cos(a) + _v.y * Math.sin(a),
			_v.y * Math.cos(a) - _v.x * Math.sin(a)
		);
		//log("rotate("+_v+", "+_a+") = "+result);
		return result;
	}

	public void setPosition(FloatPoint _pos) {
		super.setPosition(_pos);
		if (mGame != null) {
			Point point = getPosition().toPoint();
			Parcel aNewParcel = mGame.getMap().getParcel(point.x, point.y);
			if (aNewParcel.getTerrain() instanceof WaterTerrain) {
				// can't live in water, handle object 'death'
				log("died in water");
				// tell of the death to the director so it can update it's cost map
				mDirector.onObjectDied(_pos, mGame.getMap());
				// remove object from universe
				mGame.heartBeat.remove(mTrigger);
				// remove object from map (call super to avoid NPE)
				super.setPosition(null);
			}
		}
	}
	
	public void log(String _line) {
		System.out.println(getName()+": "+_line);
	}
	
	public String toString(Point _p) {
		return "(" + _p.x + ", " + _p.y + ")";
	}
	
	public String toString(FloatPoint _p) {
		//return "(" + _p.x + ", " + _p.y + ")";
		return "(" + toString(_p.x) + ", " + toString(_p.y) + ")";
	}
	
	public String toString(double _f) {
		return Integer.toString((int)_f)+"."+Integer.toString((int)(_f * 100 % 100));
	}
	
}

interface Director {
	// Determines the direction to move in to get fastest from [_pos] to [_target] on [_map] at [_maxSpeed]
	FloatPoint determineDirection(FloatPoint _pos, Point _target, GameMap _map, double _maxSpeed);
	// Called to indicate that the game object has died at [_pos] on [_map]
	public void onObjectDied(FloatPoint _pos, GameMap _map);
}

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
			//log("Direction=("+direction.x+","+direction.y+")");
		}
		// determine the cost to move ahead, left or right
		double costAhead = getCostForMove(_pos, direction, _map);
		double costLeft = getCostForMove(_pos, rotate(direction, -90), _map);
		double costRight = getCostForMove(_pos, rotate(direction, 90), _map);
		if (costAhead > costLeft || costAhead > costRight) {
			Point point = _map.gameXYToParcelXY(_pos.x, _pos.y);
			log("Cost for moving from "+toString(_pos)+": ahead"+toString(direction)+"="+toString(costAhead)+" left="+toString(costLeft)+" right="+toString(costRight));
			if (costLeft < costRight) {
				log("Stepping left");
				direction = rotate(direction, -90);
			}
			else if (costRight < costLeft) {
				log("Stepping right");
				direction = rotate(direction, 90);
			}
			else {
				log("Stepping left/right");
				direction = ( (Math.random() < 0.5) ? rotate(direction, -90) : rotate(direction, 90) );
			}
		}
		return direction;
	}
	
	// rotates the vector [_v] by [_a] degrees
	protected FloatPoint rotate(FloatPoint _v, int _a) {
		double a = Math.toRadians(_a);
		FloatPoint result = new FloatPoint(
			_v.x * Math.cos(a) + _v.y * Math.sin(a),
			_v.y * Math.cos(a) - _v.x * Math.sin(a)
		);
		//log("rotate("+_v+", "+_a+") = "+result);
		return result;
	}
	
	// Called to indicate that the game object has died at [_pos] on [_map]
	public void onObjectDied(FloatPoint _pos, GameMap _map) {
		Point point = _pos.toPoint();
		Point parcelposition = _map.gameXYToParcelXY(point.x, point.y);
		getFLAGMAP(_map)[parcelposition.x][parcelposition.y]++;
	}

	// determines the cost for an object at [_pos] to move in [_dir]
	protected double getCostForMove(Point2D.Double _pos, Point2D.Double _dir, GameMap _map) {
		Point2D.Double dst = new Point2D.Double(_pos.x + _dir.x, _pos.y + _dir.y);
		Point parcelindex = _map.gameXYToParcelXY((int)dst.x, (int)dst.y);
		return getFLAGMAP(_map)[parcelindex.x][parcelindex.y] * 1000 + _pos.distance(dst);
	}
	public void log(String _line) {
		System.out.println("OneStepLookAheadDirector: "+_line);
	}
	
	public String toString(Point _p) {
		return "(" + _p.x + ", " + _p.y + ")";
	}
	
	public String toString(FloatPoint _p) {
		//return "(" + _p.x + ", " + _p.y + ")";
		return "(" + toString(_p.x) + ", " + toString(_p.y) + ")";
	}
	
	public String toString(double _f) {
		return Integer.toString((int)_f)+"."+Integer.toString((int)(_f * 100 % 100));
	}
}