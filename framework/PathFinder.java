/*
 * PathFinder.java
 *
 */
import java.awt.*;

import util.*;

/**
 * An object that tries to find it's way to a target. PathFinders die when they get into water.
 * <br>
 * @author  puf
 */
public class PathFinder extends BaseGameObject implements Targettable, TimerReceiver {
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
		if (mPosition != null && mTarget != null) {
			// determine direction and distance of movement
			FloatPoint pos = new FloatPoint(getPosition());
			FloatPoint direction = mDirector.determineDirection(pos, mTarget, mGame.getMap(), mSpeed);
			if (direction != null) {
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
				// determine if we've reached the target
				if (mTarget.equals(pos.toPoint())) {
					log("Reached target");
					mTarget = null;
					mDirector = null;
				}
			}
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
		Logger.log(getName()+": "+_line);

	}
	

}

/*
 *  Revision history, maintained by CVS.
 *  $Log: PathFinder.java,v $
 *  Revision 1.11  2003/06/05 15:24:51  puf
 *  Removed the OneStepLookAheadDirector class, which is now in a file of it's own.
 *  Removed dependency on JDK1.4 logging mechanism.
 *
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