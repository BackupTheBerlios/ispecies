
import java.awt.*;
import java.util.*;

import util.*;

public class SatelliteViewport extends Viewport {
	Satellite mSatellite;
	Dimension mPreferredSize;
	
	// class constants
	public final static int PREFERRED_SCALE = 32; // size of a parcel in pixels
	public final static Color BG_COLOR = Color.black;
	
	SatelliteViewport(Satellite _satellite, ViewportContainer _container) {
		super();
		setSatellite(_satellite);
		setContainer(_container);
		
//# Temporarily disabled while I change the Viewport implementations - Tako
//		this.addMouseListener(this);
//		this.addMouseMotionListener(this);
		
		Logger.log("SatelliteViewport created");
	}

	SatelliteViewport(Satellite _satellite) {
		this(_satellite, null);
	}

	SatelliteViewport() {
		this(null, null);
	}

	public void terminate() {
		super.terminate();
		mSatellite.terminate();
	}

	public void setSatellite(Satellite _satellite) {
		mSatellite = _satellite;
		if (mSatellite != null) {
			mPreferredSize = new Dimension(
				PREFERRED_SCALE * mSatellite.getMap().getParcelMap().getWidth(),
				PREFERRED_SCALE * mSatellite.getMap().getParcelMap().getHeight()
			);
		} else {
			mPreferredSize = null;
		}
	}
	
	public Dimension getPreferredSize() {
		return mPreferredSize;
	}
	
	// draws the terrain of a parcel
	void drawParcelTerrain(Graphics g, int x, int y, int h, Terrain terrain) {
		// determine color
		Color aColor = new java.awt.Color(terrain.color.getRGB());
		for (int i=0; i < h; i++) {
			aColor = aColor.darker();
		}
		g.setColor(aColor);
		// draw rectangle
		float fScaleX = g.getClipBounds().width / mSatellite.getMap().getParcelMap().getWidth();
		float fScaleY = g.getClipBounds().height / mSatellite.getMap().getParcelMap().getHeight();
		g.fillRect(
			(int)(x * fScaleX),
			(int)(y * fScaleY),
			(int)((x+1) * fScaleX) - 1,
			(int)((y+1) * fScaleY) - 1
		);
	}
	
	void drawObject(Graphics g, Point gp, GameObject obj) {
		Point sp = gameToScreenCoords(gp);
		
		if (obj instanceof Radar) {
			g.setColor(Color.black);
			// REMARK: Why the 3 * parcel width/height?
			// Because it's easy to check in the window.
			// It shouldn't use parcel info in final version.
			Point sw = gameToScreenVect(new Point(3 * mSatellite.getMap().mParcelWidth, 3 * mSatellite.getMap().mParcelHeight));
			g.drawOval(sp.x - sw.x/2, sp.y - sw.y/2, sw.x, sw.y);
		}
		if (obj instanceof Targettable) {
			g.setColor(Color.white);
			Point target = ((Targettable)obj).getTarget();
			if (target != null) {
				Point tsp = gameToScreenCoords(target);
				g.drawLine(sp.x, sp.y, tsp.x, tsp.y);
			}
		}
		if (obj instanceof PathFinder) {
			PathFinder pf = (PathFinder)obj;
			if (pf.mDirector instanceof PathFinderDirector) {
				PathFinderDirector director = (PathFinderDirector)pf.mDirector;
				if (director.mLatestNode != null) {
					g.setColor(Color.magenta);
					Point current = gameToScreenCoords(director.mLatestNode.mPosition.toPoint());
					g.drawOval(current.x-1, current.y-1, 2, 2);
				}
				// recursively draw all path nodes
				drawPathNode(g, director.mStartNode);
				if (director.mLatestNode != null) {
					// draw the point that was evaluated last
					g.setColor(Color.magenta);
					Point current = gameToScreenCoords(director.mLatestNode.mPosition.toPoint());
					g.drawOval(current.x-1, current.y-1, 2, 2);
					// draw the min cost for the node evaluated
					g.drawString(
						StringHelper.toString(director.mLatestNode.mCost),
						obj.getPosition().toPoint().x+10, 
						obj.getPosition().toPoint().y
					);
					g.drawString(
						StringHelper.toString(director.mLatestNode.mCost),
						current.x+10, 
						current.y
					);
					// draw the potential cost to reach the target
					g.drawString(
						StringHelper.toString(director.mLatestNode.mPotentialCost), 
						gameToScreenCoords(((Targettable)obj).getTarget()).x+10, 
						gameToScreenCoords(((Targettable)obj).getTarget()).y+10
					);
					// draw the path from the start to the node that was evaluated last time
					PathFinderDirector.PathNode currentBestNode = director.mLatestNode;
					while (currentBestNode.mPreviousNode != null) {
						Point from = gameToScreenCoords(currentBestNode.mPosition.toPoint());
						Point to = gameToScreenCoords(currentBestNode.mPreviousNode.mPosition.toPoint());
						g.drawLine(from.x, from.y, to.x, to.y);
						currentBestNode = currentBestNode.mPreviousNode;
					}
				}
			}
		}
		// draw the object itself in read
		g.setColor(Color.red);
		g.fillOval(sp.x-2, sp.y-2, 4, 4); // putPixel
	}

	void drawPathNode(Graphics g, PathFinderDirector.PathNode node) {
		if (node != null) {
			Point point = gameToScreenCoords(node.mPosition.toPoint());
			// draw the point
			g.setColor(Color.black);
			g.drawOval(point.x, point.y, 1, 1);
			// for each of the next nodes
			for (int i=0; i < 3; i++) {
				if (node.mNextNodes[i] != null) {
					//g.setColor(new Color((int)(node.mNextNodes[i].mLowestCost / 2)));
					// draw a vector to the next node
					//Point dst = gameToScreenCoords(node.mNextNodes[i].mPosition.toPoint());
					//g.drawLine(point.x, point.y, dst.x, dst.y);
					// let the next node draw itself
					drawPathNode(g, node.mNextNodes[i]);
				}
			}
		}
	}

	public void paint(Graphics g) {
		int x, y;
		
		for ( x=0; x < mSatellite.getMap().getParcelMap().getWidth(); x++) {
			for ( y=0; y < mSatellite.getMap().getParcelMap().getHeight(); y++) {
				Parcel p = mSatellite.getMap().getParcelMap().getParcel(x,y);
				drawParcelTerrain(g, x, y, p.getBaseHeight(), p.getTerrain());
			} // for y
		} // for x
		
		for (Enumeration e = mSatellite.getMap().getRange().getObjectEnumeration(); e.hasMoreElements(); ) {
			GameObject obj = (GameObject)e.nextElement();
			drawObject(g, obj.getPosition().toPoint(), obj);
		} // for objects
	}
	
/*
//# Temporarily disabled while I change the Viewport implementations - Tako
	public void mousePressed(MouseEvent event) {
		Point p = screenToGameCoords(mouseToScreenCoords(event.getPoint()));
		Point pp = mSatellite.getMap().gameXYToParcelXY(p.x, p.y);
		Logger.log("MousePressed " + p + pp);
		MapView v = mSatellite.getMap().getRange(p, 32, 32);
		for (Enumeration e = v.getObjectEnumeration(); e.hasMoreElements(); ) {
			GameObject obj = (GameObject)e.nextElement();
			Logger.log(obj.getName());
			if (obj instanceof Targettable) {
				Targettable to = (Targettable)obj;
				Logger.log("target = "+to.getTarget());
			}
		}
	}
*/
}

/*
 *  Revision history, maintained by CVS.
 *  $Log: SatelliteViewport.java,v $
 *  Revision 1.3  2003/06/05 15:31:54  puf
 *  Added some code to draw the network of nodes of the current PathFinder. It's not very pretty, but it helps immensely in debugging the path finding.
 *
 *  Revision 1.2  2002/11/10 08:20:12  puf
 *  Draws the path of the PathFinderDirector (for debugging purposes).
 *
 *  Revision 1.1  2002/11/07 01:39:13  quintesse
 *  Put Model and View in separate files
 *
 */

