import java.awt.*;

/**
 *  This class models a radar on the map
 *
 *@author     Tako
 *@created    6 november 2002
 */
public class Radar extends BaseGameObject implements TimerReceiver {
	Universe mGame = null;
	RadarViewport mViewport; // should be a vector, to handle multiple views on this radar
	TimerTrigger mTrigger;
	int mnInterval = 4; // number of heartbeats between updates
	int mnRadius = 50; // radius of the radar
	int mnAngle = 0; // start angle of next segment
	int mnRotationSpeed = 5; // speed of rotation in degrees per tick
	MapView mMap; // map range of radar


	/**
	 *  Constructor for the Radar object
	 *
	 *@param  _game  A reference to the Universe this object exists in
	 */
	Radar(Universe _game) {
		super(_game.getMap(),
			new Point(
				(int)(Math.random() * _game.getMap().getWidth()),
				(int)(Math.random() * _game.getMap().getHeight())
			)
		);
		mTrigger = new TimerTrigger(this);
		mTrigger.setRepeat(true);
		setUniverse(_game);
		Logger.info("Radar created");
	}


	/**
	 *  Constructor for the Radar object
	 */
	Radar() {
		// no universe yet
		this(null);
	}


	public void terminate() {
		super.terminate();
		setUniverse(null);
		mTrigger = null;
	}


	/**
	 *  Makes this Radar object part of the specified Universe
	 *
	 *@param  _universe  The universe value
	 */
	public void setUniverse(Universe _universe) {
		if (mGame != null) {
			mGame.heartBeat.remove(mTrigger);
			mMap = null;
		}
		mGame = _universe;
		if (mGame != null) {
			mGame.heartBeat.addRel(mTrigger, mnInterval);
			mMap = mGame.getMap().getRange(getPosition().toPoint().x, getPosition().toPoint().y, 2 * getRadius(), 2 * getRadius());
		}
	}


	/**
	 *  Gets a reference to the part of the map this radar covers
	 *
	 *@return    A reference to a map
	 */
	public MapView getRadarMap() {
		return mMap;
	}


	/**
	 *  Gets the radius of the Radar
	 *
	 *@return    The radius
	 */
	public int getRadius() {
		return mnRadius;
	}


	/**
	 *  Sets the radius of the Radar
	 *
	 *@param  _radius  The new radius
	 */
	public void setRadius(int _radius) {
		mnRadius = _radius;
	}


	/**
	 *  Gets the rotation speed of the Radar
	 *
	 *@return    The rotation speed in degrees per tick
	 */
	public int getRotationSpeed() {
		return mnRotationSpeed;
	}


	/**
	 *  Sets the rotation speed of the Radar
	 *
	 *@param  _rotationSpeed  The new rotation speed in degrees per tick
	 */
	public void setRotationSpeed(int _rotationSpeed) {
		mnRotationSpeed = _rotationSpeed;
	}


	/**
	 *  Gets the angle to which the radar is currently pointing
	 *
	 *@return    The angle in degrees
	 */
	public int getAngle() {
		return mnAngle;
	}


	/**
	 *  Handles the heartbeat of this object
	 *
	 *@param  _tt  Description of the Parameter
	 */
	public void doTimer(TimerTrigger _tt) {
		// increment angle
		mnAngle = mnAngle - mnRotationSpeed;
		if (mnAngle < 0) {
			mnAngle = 360 - mnAngle;
		}
	}
}

/*
 *  Revision history, maintained by CVS.
 *  $Log: Radar.java,v $
 *  Revision 1.4  2003/06/05 15:29:47  puf
 *  Removed dependency on JDK1.4 logging mechanism.
 *
 *  Revision 1.3  2002/11/12 08:32:54  quintesse
 *  Now using official 1.4 JDK logging system.
 *
 *  Revision 1.2  2002/11/11 10:50:56  quintesse
 *  Changed getMap() to getRadarMap() to avoid conflict with a method with the same name in BaseGameObject (and it is more logical there so decided to change this one).
 *
 *  Revision 1.1  2002/11/07 01:39:13  quintesse
 *  Put Model and View in separate files
 *
 */

