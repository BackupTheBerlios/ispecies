/*
 * PathFinder.java
 *
 * Created on June 3, 2002, 7:55 PM
 */

import java.awt.Point;

/**
 * An object that tries to find it's way to a target. PathFinders die when they get into water.
 * <br>
 * @author  puf
 */
public class PathFinder extends BaseGameObject implements Targettable, TimerReceiver {
	public static int[][] FLAGMAP = null;

	private Point mTarget = null;
	private Universe mGame = null;
	private TimerTrigger mTrigger;
	private int mSpeed = 1;

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
	
	public synchronized int[][] getFLAGMAP() {
		// initialize flag map
		if (FLAGMAP == null) {
			FLAGMAP = new int[mMap.mParcelMapWidth][mMap.mParcelMapHeight];
		}
		return FLAGMAP;
	}

	public Point getTarget() {
		return mTarget;
	}
	
	public void setTarget(Point _target) {
		mTarget = _target;
	}
	
	public void doTimer(TimerTrigger timerTrigger) {
		//log("received timer trigger");
		// determine direction and distance of movement
		Point pos = getPosition();
		Point direction = new Point(mTarget.x - getPosition().x, mTarget.x - getPosition().y);
		double distance = pos.distance(mTarget.getX(), mTarget.getY());
		// if distance > speed, scale vector down to speed
		if (distance > mSpeed) {
			direction.x = Math.max((int)(mSpeed * direction.x / distance), 1);
			direction.y = Math.max((int)(mSpeed * direction.y / distance), 1);
		}
		// determine the cost to move ahead, left or right
		int costAhead = getCostForMove(pos, direction);
		int costLeft = getCostForMove(pos, rotate(direction, -90));
		int costRight = getCostForMove(pos, rotate(direction, 90));
		if (costAhead > 0) {
			log("Cost for moving from "+mGame.getMap().gameXYToParcelXY(pos.x, pos.y)+": ahead="+costAhead+" left="+costLeft+" right="+costRight);
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

	// determines the cost for an object at [_pos] to move in [_dir]
	protected int getCostForMove(Point _pos, Point _dir) {
		Point dst = new Point(_pos.x + _dir.x, _pos.y + _dir.y);
		Point parcelindex = mGame.getMap().gameXYToParcelXY(dst.x, dst.y);
		return getFLAGMAP()[parcelindex.x][parcelindex.y];
	}
	
	protected Point rotate(Point _v, int _a) {
		double a = Math.toRadians(_a);
		Point result = new Point(
			(int)Math.round(_v.x * Math.cos(a) + _v.y * Math.sin(a)),
			(int)Math.round(_v.y * Math.cos(a) - _v.x * Math.sin(a))
		);
		//log("rotate("+_v+", "+_a+") = "+result);
		return result;
	}

	
	public void setParcel(Parcel _parcel) {
		//log("setParcel("+_parcel+")");
		// let superclass handle normal setting of parcel
		super.setParcel(_parcel);
		if (mParcel != null) {
			// handle allowed parcels for pathfinder
			if (mParcel.getTerrain() instanceof WaterTerrain) {
				// can't live in water, handle object 'death'
				log("died in water");
				// update flagmap
				Point parcelposition = mMap.getParcelPosition(_parcel);
				getFLAGMAP()[parcelposition.x][parcelposition.y]++;
				// remove object from universe
				mGame.heartBeat.remove(mTrigger);
				setParcel(null);
			}	
		}
	} // setParcel
	
	public void log(String _line) {
		System.out.println(getName()+": "+_line);
	}
	
}
