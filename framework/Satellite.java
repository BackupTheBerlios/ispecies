import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


// this class represents a Satellite above the map
class Satellite implements TimerReceiver {
	Universe			game;
	SatelliteViewport	vp;				// should be a vector, to handle multiple views on this Satellite
	TimerTrigger		trigger;
	int					interval = 25;	// number of heartbeats between updates
	GameMap				map;			// satellite can see entire map
	
	Satellite( Universe _game ) {
		trigger = new TimerTrigger( this );
		trigger.setRepeat(true);
		OnUniverse(_game);
		Logger.log("Satellite created");
	}
	
	Satellite() {
		// no universe yet
		this(null);
	}
	
	public void OnUniverse(Universe _universe) {
		if (game != null)
			game.heartBeat.remove(trigger);
		game = _universe;
		if (game != null) {
			map = game.getMap(); // can see the entire map
			game.heartBeat.addRel( trigger, interval );
		}
	}
	
	public void doTimer(TimerTrigger tt) {
		vp.updateMap();
	}
}

//class SatelliteViewport extends Viewport
class SatelliteViewport extends Frame implements MouseListener, MouseMotionListener {
	// member variables
	Image			img = null; // off screen buffer
	Graphics		bg = null;
	Universe		game;
	Satellite		satellite;
	double			fScaleX, fScaleY;
	
	// class constants
	public final static int SCALE = 32; // size of a parcel in pixels
	public final static int INSET = 5; // for 'dungeon dressing'
	public final static Color BG_COLOR = Color.black;
	
	SatelliteViewport( Universe _game, Satellite _Satellite ) {
		super("Satellite");
		setBackground(BG_COLOR);
		show();
		OnUniverse  (_game);
		OnSatellite(_Satellite);
		
		// determine the scale of things for mapping screen coordinates back to map coordinates later
		fScaleX = (satellite.map.getWidth() / (SCALE * satellite.map.getParcelMap().getWidth()));
		fScaleY = (satellite.map.getHeight() / (SCALE * satellite.map.getParcelMap().getHeight()));
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		Logger.log("SatelliteViewport created");
	}
	SatelliteViewport( Universe _game ) {
		this(_game,null);
	}
	SatelliteViewport() {
		this(null,null);
	}
	protected void finalize() {
		bg.dispose(); // dispose of the Graphics
	}
	public void OnUniverse(Universe _universe) {
		game = _universe;
	}
	public void OnSatellite(Satellite _satellite) {
		if (satellite != null) {	// disconnect from old Satellite
			satellite.vp = null;
		}
		satellite = _satellite;
		if (satellite != null) {	// connect to new Satellite
			satellite.vp = this;
			// resize to show entire view of satellite
			setViewportSize(
				SCALE*satellite.map.getParcelMap().getWidth(),
				SCALE*satellite.map.getParcelMap().getHeight()
			);
		}
	}
	
	public void setViewportSize(int _width, int _height) {
		setSize(
			_width  + insets().left + insets().right,
			_height + insets().top  + insets().bottom
		);
		// create an off screen buffer for drawing
		if (bg != null) {
			bg.dispose(); // free old one
		}
		img = createImage(_width,_height);
		System.err.println("Create OSB of "+_width+"x"+_height);
		bg  = img.getGraphics();
		Logger.log("SatelliteViewport created");
	}
	
	// draws the terrain of a parcel
	void DrawParcelTerrain(Graphics g, int x, int y, int h, Terrain terrain) {
		// determine color
		//g.setColor(terrain.color);
		Color aColor = new java.awt.Color(terrain.color.getRGB());
		for (int i=0; i < h; i++) {
			aColor = aColor.darker();
		}
		g.setColor(aColor);
		// draw rectangle
		g.fillRect(
			INSET+x*SCALE,
			INSET+y*SCALE,
			INSET+(x+1)*SCALE-1,
			INSET+(y+1)*SCALE-1
		);
	}
	
	void DrawObject(Graphics g, Point gp, GameObject obj) {
		Point sp = gameToScreenCoords(gp);
		
		if (obj instanceof Radar) {
			g.setColor(Color.black);
			// REMARK: Why the 3 * parcel width/height?
			// Because it's easy to check in the window.
			// It shouldn't use parcel info in final version.
			Point sw = gameToScreenVect(new Point(3 * satellite.map.mParcelWidth, 3 * satellite.map.mParcelHeight));
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
		// simply copy the off screen buffer to the window
		if (img != null) {
			g.drawImage(img, insets().left+1, insets().top+1, null);
		}
	}
	
	public void updateMap() {
		int x, y;
		
		for ( x=0; x < satellite.map.getParcelMap().getWidth(); x++) {
			for ( y=0; y < satellite.map.getParcelMap().getHeight(); y++) {
				Parcel p = satellite.map.getParcelMap().getParcel(x,y);
				DrawParcelTerrain( bg, x, y, p.getBaseHeight(), p.getTerrain() );
			} // for y
		} // for x
		
		for (Enumeration e = satellite.map.getRange().getObjectEnumeration(); e.hasMoreElements(); ) {
			GameObject obj = (GameObject)e.nextElement();
			DrawObject(
				bg,
				obj.getPosition().toPoint(),
				obj
			);
		} // for objects
		// update screen
		repaint();
	}
	
	public void update(Graphics  g) {
		paint(g);
	}
	
	public Point screenToGameVect(Point p) {
		Point tp = new Point(p);
		tp.x = (int)(tp.x * fScaleX);
		tp.y = (int)(tp.y * fScaleY);
		return tp;
	}
	
	public Point gameToScreenVect(Point p) {
		Point tp = new Point(p);
		tp.x = (int)(tp.x / fScaleX);
		tp.y = (int)(tp.y / fScaleY);
		return tp;
	}
	
	public Point mouseToScreenCoords(Point p) {
		Point tp = new Point(p);
		Insets i = getInsets();
		tp.translate(-i.left, -i.top);
		return tp;
	}
	
	public Point screenToGameCoords(Point p) {
		Point tp = new Point(p);
		tp.translate(-INSET, -INSET);
		tp.x = (int)(tp.x * fScaleX);
		tp.y = (int)(tp.y * fScaleY);
		return tp;
	}
	
	public Point gameToScreenCoords(Point p) {
		Point tp = new Point(p);
		tp.x = (int)(tp.x / fScaleX);
		tp.y = (int)(tp.y / fScaleY);
		tp.translate(INSET, INSET);
		return tp;
	}
	
	public void mouseClicked(MouseEvent event) {
	}
	
	public void mousePressed(MouseEvent event) {
		Point p = screenToGameCoords(mouseToScreenCoords(event.getPoint()));
		Point pp = satellite.map.gameXYToParcelXY(p.x, p.y);
		Logger.log("MousePressed " + p + pp);
		MapView v = satellite.map.getRange(p, 32, 32);
		for (Enumeration e = v.getObjectEnumeration(); e.hasMoreElements(); ) {
			GameObject obj = (GameObject)e.nextElement();
			Logger.log(obj.getName());
			if (obj instanceof Targettable) {
				Targettable to = (Targettable)obj;
				Logger.log("target = "+to.getTarget());
			}
		}
	}
	
	public void mouseReleased(MouseEvent event) {
	}
	
	public void mouseEntered(MouseEvent event) {
	}
	
	public void mouseExited(MouseEvent event) {
	}
	
	public void mouseMoved(MouseEvent event) {
	}
	
	public void mouseDragged(MouseEvent event) {
	}
}

/*
 *  Revision history, maintained by CVS.
 *  $Log: Satellite.java,v $
 *  Revision 1.6  2002/11/05 15:29:16  quintesse
 *  Using Logger.log() instead of System.out.writeln();
 *  Added CVS history section.
 *
 */

