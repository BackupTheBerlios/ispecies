/*
 * Director.java
 *
 */
import util.*;

/** A <code>Director</code> is an object that determines where the <code>GameObject</code> moves.
 * <br>
 * @version $Revision: 1.2 $
 */
interface Director {
	/** The version number of this file as determined by the RCS. */
	static final String RCS_VERSION = "$Revision: 1.2 $";
	
	/** Determines the direction to move in to get fastest from [_pos] to [_target] on [_map] at [_maxSpeed].
	 * @param _pos the position that the game object is currently at.
	 * @param _target the position the game object is trying to reach.
	 * @param _map the map over which the game object if moving.
	 * @param _maxSpeed the maximum speed at which the object can move.
	 * @return the direction in which the game object should move to reach it's destination according to this <code>Director</code>
	 */
	FloatPoint determineDirection(FloatPoint _pos, java.awt.Point _target, GameMap _map, double _maxSpeed);
}

/* Revision history, maintained by VSS.
 * $Log: Director.java,v $
 * Revision 1.2  2002/11/11 10:40:52  quintesse
 * Removed onObjectDied() because it shouldn't be part of the Director interface. Implementations must now subscribe Directors to the died() event of the ObjectListener interface.
 *
 * Revision 1.1  2002/11/10 08:09:18  puf
 * A Director is an object that determines where the GameObject moves.
 *
 *
 */
