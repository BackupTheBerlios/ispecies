/*
 * FloatPoint.java
 *
 */


/**
 * More easily accessible alias for java.awt.geom.Point2D.Double.
 */
public class FloatPoint extends java.awt.geom.Point2D.Double {
	/** Creates a new FloatPoint */
	public FloatPoint() {
	}
	
	public FloatPoint(FloatPoint _p) {
		super(_p.x, _p.y);
	}
	
	public FloatPoint(java.awt.Point _p) {
		super(_p.x, _p.y);
	}
	
	public FloatPoint(double _x, double _y) {
		super(_x, _y);
	}
	
	public java.awt.Point toPoint() {
		return new java.awt.Point((int)x, (int)y);
	}
	
}

/* Revision history, maintained by VSS.
 * $Log: FloatPoint.java,v $
 * Revision 1.1  2002/11/05 07:48:24  puf
 * Descendant of java.awt.geom.Point2D.Double with a simpler name/package.
 *
 *
 */
