/*
 *  FloatPoint.java
 */

package util;

/**
 *  More easily accessible alias for java.awt.geom.Point2D.Double.
 *
 *@author     puf
 *@created    5 november 2002
 */
public class FloatPoint extends java.awt.geom.Point2D.Double {

	/**
	 *  Constructor for a new unitialized FloatPoint
	 */
	public FloatPoint() {
	}


	/**
	 *  Constructor for a new FloatPoint object initialized with the values of another FloatPoint
	 *
	 *@param  _p  The FloatPoint to use to initialize the new object
	 */
	public FloatPoint(FloatPoint _p) {
		super(_p.x, _p.y);
	}


	/**
	 *  Constructor for a new FloatPoint object initialized with the values of a Point
	 *
	 *@param  _p  The Point to use to initialize the new object
	 */
	public FloatPoint(java.awt.Point _p) {
		super(_p.x, _p.y);
	}


	/**
	 *  Constructor for a new FloatPoint object initialized with x and y values
	 *
	 *@param  _x  The x coordinate
	 *@param  _y  The y coordinate
	 */
	public FloatPoint(double _x, double _y) {
		super(_x, _y);
	}


	/**
	 *  Returns a new Point object holding the "floored" coordinates of this object
	 *
	 *@return    A new Point object
	 */
	public java.awt.Point toPoint() {
		return new java.awt.Point((int)x, (int)y);
	}

}

/*
 *  Revision history, maintained by CVS.
 *  $Log: FloatPoint.java,v $
 *  Revision 1.1  2002/11/07 01:31:49  quintesse
 *  Moved from main package to util.*
 *
 *  Revision 1.2  2002/11/05 12:49:42  quintesse
 *  Added Javadoc comments.
 *
 *  Revision 1.1  2002/11/05 07:48:24  puf
 *  Descendant of java.awt.geom.Point2D.Double with a simpler name/package.
 *
 *
 */

