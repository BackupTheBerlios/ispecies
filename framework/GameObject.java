/*
 *  GameObject.java
 */

import java.util.*;

import util.*;

/**
 * Game object. Has behaviour. Does not know how to draw
 * itself, but has a reference to a Visual;
 *
 *@created    5 november 2002
 */
interface GameObject {
	/**
	 *  Gets the position of the GameObject object
	 *
	 *@return    The position value
	 */
	FloatPoint getPosition();


	/**
	 *  Sets the position of the GameObject object
	 *
	 *@param  _position  The new position value
	 */
	void setPosition(FloatPoint _position);


	/**
	 *  Sets the name of the GameObject object
	 *
	 *@param  _name  The new name value
	 */
	void setName(String _name);


	/**
	 *  Gets the name of the GameObject object
	 *
	 *@return    The name value
	 */
	String getName();


	/**
	 *  Adds a new listener to the queue of listeners intereseted in object events.
	 *
	 *@param  _listener  The listener that wants to be notified of events related to this object
	 */
	void addObjectListener(ObjectListener _listener);
	
	
	/**
	 *  Object will be terminated due to influences from within the Universe.
	 *  This could be anything from being killed by a bullet, to walking into lava, to dying of thirst.
	 */
	void kill();
	
	
	/**
	 *  Object will be terminated due to influences from within the Universe.
	 *  This could be anything from being killed by a bullet, to walking into lava, to dying of thirst.
	 *
	 *@param  _killer  A reference to the GameObject responsible for this object's death, null if it
	 * was due to other circumstances (like environment).
	 */
	void kill(GameObject _killer);


	/**
	 *  Object will be silently removed from the Universe.
	 */
	public void terminate();
}


/**
 *  Base implementation of a GameObject
 *
 *@created    5 november 2002
 */
class BaseGameObject implements GameObject {
	GameMap mMap;
	FloatPoint mPosition;
	String mName;
	List mListeners;

	/**
	 *  Constructor for the BaseGameObject object
	 *
	 *@param  _name  Description of the Parameter
	 *@param  _map   Description of the Parameter
	 *@param  _x     Description of the Parameter
	 *@param  _y     Description of the Parameter
	 */
	BaseGameObject(String _name, GameMap _map, int _x, int _y) {
		mName = _name;
		mMap = _map;
		mPosition = null;
		setPosition(new FloatPoint(_x, _y));
		mListeners = new ArrayList();
		Logger.info("Created new GameObject: " + this);
	}


	/**
	 *  Constructor for the BaseGameObject object
	 *
	 *@param  _map       Description of the Parameter
	 *@param  _position  Description of the Parameter
	 */
	BaseGameObject(GameMap _map, java.awt.Point _position) {
		this(null, _map, _position.x, _position.y);
	}


	/**
	 *  Constructor for the BaseGameObject object
	 *
	 *@param  _name      Description of the Parameter
	 *@param  _map       Description of the Parameter
	 *@param  _position  Description of the Parameter
	 */
	BaseGameObject(String _name, GameMap _map, java.awt.Point _position) {
		this(_name, _map, _position.x, _position.y);
	}


	/**
	 *  Constructor for the BaseGameObject object
	 *
	 *@param  _map  Description of the Parameter
	 *@param  _x    Description of the Parameter
	 *@param  _y    Description of the Parameter
	 */
	BaseGameObject(GameMap _map, int _x, int _y) {
		this(null, _map, _x, _y);
	}


	/**
	 *  Adds a new listener to the queue of listeners intereseted in object events.
	 *
	 *@param  _listener  The listener that wants to be notified of events related to this object
	 */
	public void addObjectListener(ObjectListener _listener) {
		mListeners.add(_listener);
	}
	

	/**
	 *  Object will be terminated due to influences from within the Universe.
	 *  This could be anything from being killed by a bullet, to walking into lava, to dying of thirst.
	 */
	public void kill() {
		kill(null);
	}

	
	/**
	 *  Object will be terminated due to influences from within the Universe.
	 *  This could be anything from being killed by a bullet, to walking into lava, to dying of thirst.
	 *
	 *@param  _killer  A reference to the GameObject responsible for this object's death, null if it
	 * was due to other circumstances (like environment).
	 */
	public void kill(GameObject _killer) {
		ObjectEvent event = new ObjectEvent(this, _killer);
		Iterator i = mListeners.listIterator();
		while (i.hasNext()) {
			ObjectListener listener = (ObjectListener)(i.next());
			listener.died(event);
		}
		terminate();
	}


	/**
	 *  Object will be silently removed from the Universe.
	 */
	public void terminate() {
		mListeners = null;
		setPosition(null);
		mMap = null;
	}


	/**
	 *  Gets the position attribute of the BaseGameObject object
	 *
	 *@return    The position value
	 */
	public FloatPoint getPosition() {
		return mPosition;
	}


	/**
	 *  Sets the position attribute of the BaseGameObject object
	 *
	 *@param  _position  The new position value
	 */
	public void setPosition(FloatPoint _position) {
		mMap.moveObject(this, getPosition(), _position);
		//mPosition.move(_position.x, _position.y);
		mPosition = _position;
	}


	/**
	 *  Sets the name attribute of the BaseGameObject object
	 *
	 *@param  _name  The new name value
	 */
	public void setName(String _name) {
		mName = _name;
	}


	/**
	 *  Gets the name attribute of the BaseGameObject object
	 *
	 *@return    The name value
	 */
	public String getName() {
		return mName;
	}


	public GameMap getMap() {
		return mMap;
	}


	public String toString() {
		String name = getName();
		if (name == null) {
			name = super.toString();
		}
		String pos;
		if (getPosition() != null) {
			pos = getPosition().x + ", " + getPosition().y;
		} else {
			pos = "<nowhere>";
		}
		return "'" + name + "' (" + getClass().getName() + ") @ (" + pos + ")";

	}
}


/**
 *  A GameObjectDecorator extends the behavior of a game
 *  object. This base decorator just forwards all behavior
 *  to it's base, you should subclass this class and override
 *  specific methods to make it useful.
 *
 *  if you just got a hammer, everything looks like a nail.
 *  kortom: ik moet zo nodig alles met patterns doen....
 *
 *@created    5 november 2002
 */
class GameObjectDecorator implements GameObject {
	GameObject base;


	/**
	 *  Constructor for the GameObjectDecorator object
	 *
	 *@param  _base  Description of the Parameter
	 */
	GameObjectDecorator(GameObject _base) {
		base = _base;
	}


	/**
	 *  Adds a new listener to the queue of listeners intereseted in object events.
	 *
	 *@param  _listener  The listener that wants to be notified of events related to this object
	 */
	public void addObjectListener(ObjectListener _listener) {
		base.addObjectListener(_listener);
	}
	

	/**
	 *  Object will be terminated due to influences from within the Universe.
	 *  This could be anything from being killed by a bullet, to walking into lava, to dying of thirst.
	 */
	public void kill() {
		base.kill();
	}

	
	/**
	 *  Object will be terminated due to influences from within the Universe.
	 *  This could be anything from being killed by a bullet, to walking into lava, to dying of thirst.
	 *
	 *@param  _killer  A reference to the GameObject responsible for this object's death, null if it
	 * was due to other circumstances (like environment).
	 */
	public void kill(GameObject _killer) {
		base.kill(_killer);
	}


	public void terminate() {
		base.terminate();
	}


	/**
	 *  Gets the position attribute of the GameObjectDecorator object
	 *
	 *@return    The position value
	 */
	public FloatPoint getPosition() {
		return base.getPosition();
	}


	/**
	 *  Sets the position attribute of the GameObjectDecorator object
	 *
	 *@param  _position  The new position value
	 */
	public void setPosition(FloatPoint _position) {
		base.setPosition(_position);
	}


	/**
	 *  Sets the name attribute of the GameObjectDecorator object
	 *
	 *@param  _name  The new name value
	 */
	public void setName(String _name) {
		base.setName(_name);
	}


	/**
	 *  Gets the name attribute of the GameObjectDecorator object
	 *
	 *@return    The name value
	 */
	public String getName() {
		return base.getName();
	}
}


/**
 *  Description of the Class
 *
 *@created    5 november 2002
 */
class RandomGameObjectMover extends GameObjectDecorator
		 implements TimerReceiver {


	int interval = 4;
	int speed = 1;
	TimerTrigger trigger;
	java.awt.Point direction;
	Universe game;


	/**
	 *  Constructor for the RandomGameObjectMover object
	 *
	 *@param  _base      Description of the Parameter
	 *@param  _game      Description of the Parameter
	 *@param  _interval  Description of the Parameter
	 */
	RandomGameObjectMover(GameObject _base, Universe _game, int _interval) {
		super(_base);
		game = _game;
		interval = _interval;
		speed = (int)(Math.random() * 3) + 1;
		direction = new java.awt.Point(speed, speed);
		// add to heartbeat
		trigger = new TimerTrigger(this);
		trigger.setRepeat(true);
		game.heartBeat.addRel(trigger, interval);
	}


	/**
	 *  Description of the Method
	 */
	protected void finalize() {
		game.heartBeat.remove(trigger);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  tt  Description of the Parameter
	 */
	public void doTimer(TimerTrigger tt) {
		// move the object
		// determine new direction
		if (Math.random() < 0.05) {
			if (Math.random() < 0.50) {
				double r = Math.random();
				if (r < 0.33) {
					direction.x = -speed;
				} else if (r > 0.66) {
					direction.x = 0;
				} else {
					direction.x = speed;
				}
			}
			if (Math.random() < 0.50) {
				double r = Math.random();
				if (r < 0.33) {
					direction.y = -speed;
				} else if (r > 0.66) {
					direction.y = 0;
				} else {
					direction.y = speed;
				}
			}
		}
		if (Math.random() < 0.50) {
			FloatPoint pos = new FloatPoint(base.getPosition());
			pos.x += direction.x;
			if (pos.x < 0) {
				pos.x = 0;
			} else if (pos.x >= game.map.mMapWidth) {
				pos.x = (int)game.map.mMapWidth - 1;
			}
			pos.y += direction.y;
			if (pos.y < 0) {
				pos.y = 0;
			} else if (pos.y >= game.map.mMapHeight) {
				pos.y = (int)game.map.mMapHeight - 1;
			}
			base.setPosition(pos);
		}
	}

}

/*
 *  Revision history, maintained by CVS.
 *  $Log: GameObject.java,v $
 *  Revision 1.10  2003/06/05 14:43:34  puf
 *  Removed dependency on JDK1.4 logging mechanism.
 *
 *  Revision 1.9  2002/11/12 08:33:26  quintesse
 *  Now using official 1.4 JDK logging system.
 *
 *  Revision 1.8  2002/11/11 10:46:36  quintesse
 *  Added addObjectListener(), kill() and kill(_killer) methods to the GameObject interface.
 *  BaseGameObject implements the new methods by allowing ObjectListeners to register themselves with the object and by notifying them when the object's kill() method has been called.
 *  BaseGameObject now has a getMap() which is a bit of a hack.
 *
 *  Revision 1.7  2002/11/07 00:51:39  quintesse
 *  Added terminate() to GameObject interface to handle clean-up.
 *  Implemented basic clean-up for existing GameObject classes.
 *  Adjusted naming to be more according to our style.
 *
 *  Revision 1.6  2002/11/05 12:50:39  quintesse
 *  Added Javadoc comments.
 *
 */

