
import java.io.*;
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
			Point tsp = gameToScreenCoords(((Targettable)obj).getTarget());
			g.drawLine(sp.x, sp.y, tsp.x, tsp.y);
		}
		
		g.setColor(Color.red);
		g.fillOval(sp.x-2, sp.y-2, 4, 4); // putPixel
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
 *  Revision 1.1  2002/11/07 01:39:13  quintesse
 *  Put Model and View in separate files
 *
 */

