/*
 *  Scale.java
 */

package util;

/**
 *  More easily accessible alias for java.awt.geom.Point2D.Double.
 *
 *@author     quintesse
 *@created    5 november 2002
 */
public class Scale extends java.awt.geom.Point2D.Double {

	/**
	 *  Constructor for a new unitialized Scale
	 */
	public Scale() {
	}


	/**
	 *  Constructor for a new Scale object initialized with the values of another Scale
	 *
	 *@param  _p  The Scale to use to initialize the new object
	 */
	public Scale(Scale _p) {
		super(_p.x, _p.y);
	}


	/**
	 *  Constructor for a new Scale object initialized with the values of a Point
	 *
	 *@param  _p  The Point to use to initialize the new object
	 */
	public Scale(java.awt.Point _p) {
		super(_p.x, _p.y);
	}


	/**
	 *  Constructor for a new Scale object initialized with x and y values
	 *
	 *@param  _x  The x coordinate
	 *@param  _y  The y coordinate
	 */
	public Scale(double _x, double _y) {
		super(_x, _y);
	}

}

/*
 *  Revision history, maintained by CVS.
 *  $Log: Scale.java,v $
 *  Revision 1.1  2002/11/07 01:34:04  quintesse
 *  First check-in
 *
 */

