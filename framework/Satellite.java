
import java.io.*;
import java.awt.*;
import java.util.*;

import util.*;

// this class represents a Satellite above the map
public class Satellite {
	Universe mGame;
	
	Satellite(Universe _game) {
		setUniverse(_game);
		Logger.log("Satellite created");
	}
	
	Satellite() {
		// no universe yet
		this(null);
	}
	
	public void terminate() {
		setUniverse(null);
	}

	public void setUniverse(Universe _universe) {
		mGame = _universe;
	}
	
	public GameMap getMap() {
		return mGame.getMap();
	}
}

/*
 *  Revision history, maintained by CVS.
 *  $Log: Satellite.java,v $
 *  Revision 1.7  2002/11/07 01:07:22  quintesse
 *  Lots of changes because of the new Viewport/ViewportContainer system and because of a clearer separation into a MVC architecture.
 *  The viewport has moved to its own file.
 *
 *  Revision 1.6  2002/11/05 15:29:16  quintesse
 *  Using Logger.log() instead of System.out.writeln();
 *  Added CVS history section.
 *
 */

