/*
 * StringHelper.java
 */

package util;

/**
 * Helper class with functions for performing math.
 * <br>
 * @created    5 november 2002
 */
public final class MathHelper {
	/** Avoids instantiating a new MathHelper */
	private MathHelper() {
	}
	
	/** rotates the vector [_v] by [_a] degrees.
	 * @param _v the vector to rotate.
	 * @param _a the angle in degrees at which to rotate [_v]. Positive angles indicate clockwise rotation, negative angles indicate counter-clockwise rotation.
	 * @return the vector determine by rotating [_v] over [_a] degrees.
	 */
	public static final FloatPoint rotate(FloatPoint _v, int _a) {
		double a = Math.toRadians(_a);
		FloatPoint result = new FloatPoint(
			_v.x * Math.cos(a) + _v.y * Math.sin(a),
			_v.y * Math.cos(a) - _v.x * Math.sin(a)
		);
		return result;
	}
	
}

/*
 * Revision history, maintained by CVS.
 * $Log: MathHelper.java,v $
 * Revision 1.1  2002/11/07 01:31:49  quintesse
 * Moved from main package to util.*
 *
 * Revision 1.1  2002/11/05 20:27:31  puf
 * Initial version with a method to rotate a vector about an angle.
 *
 */
