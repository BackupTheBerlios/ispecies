/*
 *  Targettable.java
 */
import java.awt.Point;

/**
 *  Interface for objects that have a special interest in a specific point on the map.
 *  Used by moving GameObjects to get where they want to go for example.
 *
 *@created    5 november 2002
 */
interface Targettable {
	/**
	 *  The target coordiante set for this object
	 *
	 *@return    The target value
	 */
	Point getTarget();


	/**
	 *  Sets the target coordiante for this object
	 *
	 *@param  _target  The new target value
	 */
	void setTarget(Point _target);
}

/**
 *  A decorator that will make it possible for a GameObject to have a target
 *
 *@created    5 november 2002
 */
class TargettableGameObject extends GameObjectDecorator implements Targettable {


	Point mTarget = null;


	/**
	 *  Constructor for the TargettableGameObject object
	 *
	 *@param  _base  The object for which this object is a decorator
	 */
	TargettableGameObject(GameObject _base) {
		super(_base);
	}


	/**
	 *  Gets the target coordiante for this object
	 *
	 *@return    The target coordiante
	 */
	public Point getTarget() {
		return mTarget;
	}


	/**
	 *  Sets the target coordiante for this object
	 *
	 *@param  _target  The new target coordinate
	 */
	public void setTarget(Point _target) {
		mTarget = _target;
	}
}

/*
 *  Revision history, maintained by CVS.
 *  $Log: Targettable.java,v $
 *  Revision 1.3  2003/06/05 15:16:14  puf
 *  Layout, nothing else.
 *
 *  Revision 1.2  2002/11/05 15:29:45  quintesse
 *  Added Javadoc comments.
 *  Added CVS history section.
 *  Made sure method and member names adhere to our standards.
 *
 */

