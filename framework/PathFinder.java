/*
 * PathFinder.java
 *
 * Created on June 3, 2002, 7:55 PM
 */

import java.awt.Point;

/**
 *
 * @author  puf
 */
public class PathFinder extends BaseGameObject implements Targettable, TimerReceiver {
	private Point mTarget = null;
	private Universe mGame = null;
	private TimerTrigger mTrigger;
	public static int[][] FLAGMAP = null;

	/** Creates a new instance of PathFinder */
	public PathFinder(GameMap _map, Universe _game, Point _position) {
		super(_map, _position);
		mGame = _game;
		// initialize flag map
		if (FLAGMAP == null) {
			FLAGMAP = new int[mMap.mParcelMapWidth][mMap.mParcelMapHeight];
		}
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
		log("received timer trigger");
		Point pos = getPosition();
		Point direction = new Point(mTarget.x - pos.x, mTarget.x - pos.y);
		double distance = pos.distance(mTarget.getX(), mTarget.getY());
		if (distance > 1.0) {
			direction.x = Math.max((int)(direction.x / distance), 1);
			direction.y = Math.max((int)(direction.y / distance), 1);
		}
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
		setPosition(pos);
	}

	public void setParcel(Parcel _parcel) {
		log("setParcel("+_parcel+")");
		// let superclass handle normal setting of parcel
		super.setParcel(_parcel);
		if (mParcel != null) {
			// handle allowed parcels for pathfinder
			if (mParcel.getTerrain() instanceof WaterTerrain) {
				// can't live in water, handle object 'death'
				log("died in water");
				// update flagmap
				Point parcelposition = mMap.getParcelPosition(_parcel);
				FLAGMAP[parcelposition.x][parcelposition.y]++;
				// remove object from universe
				if (mTrigger != null) {
					mGame.heartBeat.remove(mTrigger);
				}
				setParcel(null);
			}
		}
	} // setParcel

	public void log(String _line) {
		System.out.println(getName()+": "+_line);
	}

}
