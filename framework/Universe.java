/*
 *  Universe.java
*/

import java.io.*;

/**
 *  Umbrella object for all game-related objects and data
 *
 *@created    5 november 2002
 */
class Universe extends Thread {

	/**
	 *  The "physical" part of the Universe
	 */
	protected GameMap map;

	/**
	 *  The manager of the resources available in this Universe
	 */
	protected ResourceManager rm;

	/**
	 *  Image resource Id for a red dot
	 */
	public final int IMG_DOT = 1;

	/**
	 *  The object that measures the passage of time
	 */
	public TimerTriggerPool heartBeat;
	// counts game ticks, not real-time


	/**
	 *  Constructor for the Universe object
	 */
	Universe() {
		heartBeat = new TimerTriggerPool();
		rm = new ResourceManager();
		rm.registerImage(IMG_DOT, "dot.gif");
		map = readMap("Terrain.map");
	}

	/**
	 *  Returns a reference to the map
	 *
	 *@return    The map
	 */
	GameMap getMap() {
		return map;
	}


	/**
	 *  Sets a new map for this Universe
	 *
	 *@param  _map  The new map
	 */
	void setMap(GameMap _map) {
		map = _map;
	}


	/**
	 *  Reads the map from a file
	 *
	 *@param  _mapFilename  The name of the Map to read (ignored for now)
	 *@return               The Map that was read
	 */
	GameMap readMap(String _mapFilename) {
		try {
			return new MapBuilder(rm).readGameMap("Terrain.map");
		} catch (IOException e) {
			Logger.log("Could not read map: " + e.getMessage());
			e.printStackTrace(System.err);
		}
		return null;
	}


	/**
	 *  Main processing method for the Universe object
	 */
	public void run() {
		while (true) {
			try {
				sleep(50);
				// 50 msec per tick = 20 frames per sec
				heartBeat.tick();
			} catch (InterruptedException e) {
				Logger.log("ServerUniverse: somebody woke me, " + e);
			}
		}
	}

}

/*
 *  Revision history, maintained by CVS.
 *  $Log: Universe.java,v $
 *  Revision 1.5  2003/06/05 15:09:02  puf
 *  Removed some unused imports.
 *  Fixed a comment.
 *
 *  Revision 1.4  2002/11/05 15:31:50  quintesse
 *  Using Logger.log() instead of System.out.writeln();
 *  Added Javadoc comments.
 *  Added CVS history section.
 *
 */

