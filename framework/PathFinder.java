/*
 * PathFinder.java
 *
 */
import java.awt.*;
import java.awt.geom.*;
import java.util.logging.*;

import util.*;

/**
 * An object that tries to find it's way to a target. PathFinders die when they get into water.
 * <br>
 * @author  puf
 */
public class PathFinder extends BaseGameObject implements Targettable, TimerReceiver {
	static Logger mLogger = Logger.getLogger(PathFinder.class.getName());

	private Point mTarget = null;
	private Universe mGame = null;
	private TimerTrigger mTrigger;
	private int mSpeed = 5;
	//Director mDirector = new OneStepLookAheadDirector();
	public Director mDirector = new PathFinderDirector();

	/** Creates a new instance of PathFinder */
	public PathFinder(GameMap _map, Universe _game, Point _position) {
		super(_map, _position);
		mGame = _game;
		// add to heartbeat
		mTrigger = new TimerTrigger(this);
		mTrigger.setRepeat(true);
		_game.heartBeat.addRel ( mTrigger, 1 );
		// give this object a director
		//OneStepLookAheadDirector dir = new OneStepLookAheadDirector();
		PathFinderDirector dir = new PathFinderDirector();
		mDirector = dir;
		// notify the director of object events
		addObjectListener(dir);
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
	
	public void setPosition(FloatPoint _pos) {
		super.setPosition(_pos);
		FloatPoint newpos = getPosition();
		if ((mGame != null) && (newpos != null)) {
			Point point = newpos.toPoint();
			Parcel aNewParcel = mGame.getMap().getParcel(point.x, point.y);
			if (aNewParcel.getTerrain() instanceof WaterTerrain) {
				// can't live in water, handle object 'death'
				log("died in water");
				// remove object from universe
				mGame.heartBeat.remove(mTrigger);
				// remove object from map (call super to avoid NPE)
				kill();
			}
		}
	}
	
	public void log(String _line) {
		mLogger.info(getName()+": "+_line);
	}
	
}

class OneStepLookAheadDirector implements Director, ObjectListener {
	static Logger mLogger = Logger.getLogger(OneStepLookAheadDirector.class.getName());

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
	
	// Called to indicate that the game object has died
	public void died(ObjectEvent _e) {
		BaseGameObject src = (BaseGameObject)_e.getSource();
		Point point = src.getPosition().toPoint();
		GameMap map = src.getMap();
		Point parcelposition = map.gameXYToParcelXY(point.x, point.y);
		getFLAGMAP(map)[parcelposition.x][parcelposition.y]++;
	}

	// determines the cost for an object at [_pos] to move in [_dir]
	protected double getCostForMove(Point2D.Double _pos, Point2D.Double _dir, GameMap _map) {
		Point2D.Double dst = new Point2D.Double(_pos.x + _dir.x, _pos.y + _dir.y);
		Point parcelindex = _map.gameXYToParcelXY((int)dst.x, (int)dst.y);
		return getFLAGMAP(_map)[parcelindex.x][parcelindex.y] * 1000 + _pos.distance(dst);
	}
	
	public void log(String _line) {
		mLogger.info("OneStepLookAheadDirector: "+_line);
	}
}

/*
 *  Revision history, maintained by CVS.
 *  $Log: PathFinder.java,v $
 *  Revision 1.10  2002/11/12 08:34:16  quintesse
 *  Now using official 1.4 JDK logging system.
 *
 *  Revision 1.9  2002/11/11 10:48:45  quintesse
 *  PathFinder now uses the kill() method of the GameObject to die.
 *  PathFinder adds the Director being used to its list of event listeners.
 *  The OneStepLookAheadDirector now listens for the object's died() event.
 *
 *  Revision 1.8  2002/11/10 08:18:33  puf
 *  The Director interface and PathFinderDirector implementation are now in separate files.
 *  The PathFinder now uses the PathFinderDirector for directing it's movement.
 *
 *  Revision 1.7  2002/11/07 01:04:12  quintesse
 *  Added import statement because some helper classes have been moved to util.*
 *
 *  Revision 1.6  2002/11/05 20:33:35  puf
 *  Now makes use of the functions in StringHelper and MathHelper.
 *  Started working on PathFinderDirector, which will try to find a path in a more intelligent way.
 *
 *  Revision 1.5  2002/11/05 15:27:48  quintesse
 *  Using Logger.log() instead of System.out.writeln();
 *  Added CVS history section.
 *
 */
