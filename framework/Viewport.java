
import java.awt.*;
import util.*;

/**
 *  Implemented by classes that want to visualize game data
 *
 *@author     Tako
 *@created    5 november 2002
 */
public abstract class Viewport {
	ViewportContainer mContainer;
	Dimension mActualSize;
	Scale mScale;

	Viewport() {
		setContainer(null);
		setActualSize(null);
	}
	
	public void terminate() {
		setContainer(null);
	}

	public void setContainer(ViewportContainer _container) {
		mContainer = _container;
	}
	
	public Dimension getActualSize() {
		return mActualSize;
	}
	
	public void setActualSize(Dimension _size) {
		if (mContainer != null) {
			mActualSize = new Dimension(_size);
		} else {
			mActualSize = null;
		}
		// Calculate the scale we use to draw everything
		Dimension prefsize = getPreferredSize();
		if ((mContainer != null) && (prefsize != null)) {
			// The scale is determined by the difference between the preferred size and the acual size of the view port
			mScale = new Scale((float)mActualSize.width / prefsize.width, (float)mActualSize.height / prefsize.height);
		} else {
			mScale = new Scale(1.0, 1.0);
		}
	}
	
	public Scale getScale() {
		return mScale;
	}
	
	public Dimension getPreferredSize() {
		// No preferred size
		return null;
	}
	
	public Point screenToGameVect(Point p) {
		Point tp = new Point(p);
		tp.x = (int)((double)tp.x / mScale.x);
		tp.y = (int)((double)tp.y / mScale.y);
		return tp;
	}
	
	public Point gameToScreenVect(Point p) {
		Point tp = new Point(p);
		tp.x = (int)((double)tp.x * mScale.x);
		tp.y = (int)((double)tp.y * mScale.y);
		return tp;
	}
	
	public Point screenToGameCoords(Point p) {
		Point tp = new Point(p);
		tp.x = (int)((double)tp.x / mScale.x);
		tp.y = (int)((double)tp.y / mScale.y);
		return tp;
	}
	
	public Point gameToScreenCoords(Point p) {
		Point tp = new Point(p);
		tp.x = (int)((double)tp.x * mScale.x);
		tp.y = (int)((double)tp.y * mScale.y);
		return tp;
	}
	
	/**
	 *  Paint the Viewport contents to the specified Graphics context
	 *
	 *@param  g  Reference to the Graphics context the back buffer should be drawn in
	 */
	abstract public void paint(Graphics g);
	
}

/*
 *  Revision history, maintained by CVS.
 *  $Log: Viewport.java,v $
 *  Revision 1.2  2002/11/07 01:22:13  quintesse
 *  Implemenated abstract base class to for Viewports. Is now usable for a more MVC architecture. Views like the Radar and Satellite must now use a Model (Radar/Satellite) which does NOT do ANY drawing. For every Model there will be a Viewport (RadarViewport/SatelliteViewport) that will do all the drawing (and nothing more). But how and when the Viewports get to do their stuff is handled by a ViewportContainer (for now only a Windowed version exists: FramedViewportContainer).
 *
 */
