/*
 * StringHelper.java
 */

package util;

/**
 * Helper class with functions for manipulating Strings.
 * <br>
 * @created    5 november 2002
 */
public final class StringHelper {
	/** Avoids instantiating a new StringHelper */
	private StringHelper() {
	}
	
	/** Returns a compact <code>String</code> representation of the coordinates in [_p].
	 * @param _p the point for which to return a String representation.
	 * @return the String representation of [_p] or "<null>" if [_p] is equal to <code>null</code>.
	 */
	public static final String toString(java.awt.Point _p) {
		return _p != null ? "(" + _p.x + ", " + _p.y + ")" : "<null>";
	}
	
	/** Returns a compact <code>String</code> representation of the coordinates in [_p].
	 * @param _p the point for which to return a String representation.
	 * @return the String representation of [_p] or "<null>" if [_p] is equal to <code>null</code>.
	 */
	public static final String toString(FloatPoint _p) {
		return _p != null ? "(" + toString(_p.x) + ", " + toString(_p.y) + ")" :  "<null>";
	}
	
	/** Returns a compact <code>String</code> representation of [_f].
	 * @param _f the double value for which to return a String representation.
	 * @return the String representation of [_f].
	 */
	public static final String toString(double _f) {
		return Integer.toString((int)_f)+"."+Integer.toString((int)Math.abs(_f * 100 % 100));
	}	
}

/*
 * Revision history, maintained by CVS.
 * $Log: StringHelper.java,v $
 * Revision 1.2  2003/06/05 14:17:15  puf
 * Now uses abs for the fractional part in toString(double).
 *
 * Revision 1.1  2002/11/07 01:31:49  quintesse
 * Moved from main package to util.*
 *
 * Revision 1.1  2002/11/05 20:26:27  puf
 * Initial version with some toString method that deliver more readable version of their parameters that the standard toString implementation of their class.
 *
 */
