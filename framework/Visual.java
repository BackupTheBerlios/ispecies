/*
 *  Visual.java
 */
 
import java.awt.*;

/**
 *  Represents something that is visible. Contains no game logic.
 *
 *@created    5 november 2002
 */
interface Visual {
	/**
	 *  Paint the Visual to the specified position in the specified Graphics context
	 *
	 *@param  g    The graphics context where this Visual show be drawn
	 *@param  org  The coordinate where this Visual should be drawn
	 */
	public void paint(Graphics g, Point org);
}

/**
 *  Description of the Class
 *
 *@author     Tako
 *@created    5 november 2002
 */
class IndexedVisual implements Visual {
	ImageStrip mTileset;
	int mnIndex;


	/**
	 *  Constructor for the IndexedVisual object
	 *
	 *@param  _rm      A reference to the ResourceManager that holds the resources for this Visual
	 *@param  _nId     The tile set resource Id for this Visual
	 *@param  _nIndex  The index within the tile set of the Image to use
	 */
	IndexedVisual(ResourceManager _rm, int _nId, int _nIndex) {
		mTileset = _rm.getTileSet(_nId);
		mnIndex = _nIndex;
	}


	/**
	 *  Constructor for the IndexedVisual object
	 *
	 *@param  _tileset  A reference to a tile set
	 *@param  _nIndex   The index within the tile set of the Image to use
	 */
	IndexedVisual(ImageStrip _tileset, int _nIndex) {
		mTileset = _tileset;
		mnIndex = _nIndex;
	}


	/**
	 *  Gets the index of the currently selected Image within the tile set
	 *
	 *@return    The index value
	 */
	public int getIndex() {
		return mnIndex;
	}


	/**
	 *  Sets the index of Image which we want to make the current selection
	 *
	 *@param  _nIndex  The new index value
	 */
	public void setIndex(int _nIndex) {
		mnIndex = _nIndex;
	}


	/**
	 *  Paint the Visual to the specified position in the specified Graphics context
	 *
	 *@param  g    The graphics context where this Visual show be drawn
	 *@param  org  The coordinate where this Visual should be drawn
	 */
	public void paint(Graphics g, Point org) {
		g.drawImage(mTileset.getFrame(mnIndex), org.x, org.y, null);
	}
}

/**
 *  Description of the Class
 *
 *@created    5 november 2002
 */
class StateVisual implements Visual {
	Visual mvVisuals[];
	int mnState;


	/**
	 *  Constructor for the StateVisual object
	 */
	StateVisual() {
	}


	/**
	 *  Constructor for the StateVisual object
	 *
	 *@param  _vVisuals   An array of Visual objects
	 */
	StateVisual(Visual _vVisuals[]) {
		mvVisuals = _vVisuals;
	}


	/**
	 *  Sets the Visual for the given state
	 *
	 *@param  _state   The state
	 *@param  _visual  The Visual to set for the given state
	 */
	public void setStateVisual(int _state, Visual _visual) {
		if (_state > mvVisuals.length) {
			Visual old[] = mvVisuals;
			mvVisuals = new Visual[_state];
			System.arraycopy(old, 0, mvVisuals, 0, old.length);
		}
		mvVisuals[_state] = _visual;
	}


	/**
	 *  Sets the current state
	 *
	 *@param  _state  The new state value
	 */
	public void setState(int _state) {
		mnState = _state;
	}


	/**
	 *  Paint the currently selected Visual to the specified position in the specified Graphics context
	 *
	 *@param  g    The graphics context where this Visual show be drawn
	 *@param  org  The coordinate where this Visual should be drawn
	 */
	public void paint(Graphics g, Point org) {
		mvVisuals[mnState].paint(g, org);
	}
}


/**
 *  Implements an animating Visual
 *
 *@created    5 november 2002
 */
class AnimatedVisual implements Visual {
	ImageStrip images;

	/**
	 *  Paint the Visual to the specified position in the specified Graphics context
	 *
	 *@param  g    The graphics context where this Visual show be drawn
	 *@param  org  The coordinate where this Visual should be drawn
	 */
	public void paint(Graphics g, Point org) { }
}

/*
 *  Revision history, maintained by CVS.
 *  $Log: Visual.java,v $
 *  Revision 1.4  2003/06/05 15:07:38  puf
 *  Removed an unneeded import.
 *
 *  Revision 1.3  2002/11/05 15:32:34  quintesse
 *  Added Javadoc comments.
 *  Added CVS history section.
 *
 */

