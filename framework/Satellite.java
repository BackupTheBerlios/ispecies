import java.io.*;
import java.awt.*;
import java.util.*;


class Satellite
	implements TimerReceiver
// this class represents a Satellite above the map
{
	Universe			game;
	SatelliteViewport	vp;				// should be a vector, to handle multiple views on this Satellite
	TimerTrigger		trigger;
	int					interval = 25;	// number of heartbeats between updates
	GameMap				map;			// satellite can see entire map

	Satellite( Universe _game ) 
	{
		trigger = new TimerTrigger ( this );
		trigger.setRepeat(true);
		OnUniverse(_game);
		System.out.println("Satellite created");
	}

	Satellite ()
	{
		// no universe yet
		this(null);
	}
	
	public void OnUniverse(Universe _universe)
	{
		if (game != null)
			game.heartBeat.remove(trigger);
		game = _universe;
		if (game != null)
		{
			map = game.Map(); // can see the entire map
			game.heartBeat.addRel ( trigger, interval );
		}
	}

	public void doTimer(TimerTrigger tt) 
	{
		vp.updateMap();
	}
}

//class SatelliteViewport extends Viewport
class SatelliteViewport extends Frame
{
	// member variables
	Image			img = null; // off screen buffer
	Graphics		bg = null;
	Universe		game;
	Satellite		satellite;

	// class constants
	public final static int SCALE = 32; // size of a parcel
	public final static int INSET = 5; // for 'dungeon dressing'
	public final static int MAP_HEIGHT = 10; // num parcels, should be read from Satellite
	public final static int MAP_WIDTH  = 10; // num parcels, should be read from Satellite
	public final static int VIEWPORT_WIDTH = SCALE*MAP_WIDTH + 2*INSET;
	public final static int VIEWPORT_HEIGHT = SCALE*MAP_HEIGHT + 2*INSET;
	public final static int CENTER= INSET + (SCALE * MAP_WIDTH / 2);
	public final static Color BG_COLOR = Color.black;

	SatelliteViewport( Universe _game, Satellite _Satellite ) 
	{
		super("Satellite");
		setBackground(BG_COLOR);
		show();
		OnUniverse  (_game);
		OnSatellite (_Satellite);
		// show and resize our window
		setSize(
			VIEWPORT_WIDTH  + insets().left + insets().right,
			VIEWPORT_HEIGHT + insets().top  + insets().bottom
		);
		// create an off screen buffer for drawing
		img = createImage(VIEWPORT_WIDTH,VIEWPORT_HEIGHT);
		bg = img.getGraphics();
		System.out.println("SatelliteViewport created");
	}
	SatelliteViewport( Universe _game )
	{
		this (_game,null);
	}
	SatelliteViewport ()
	{
		this(null,null);
	}
	protected void finalize()
	{
		bg.dispose(); // dispose of the Graphics
	}
	public void OnUniverse(Universe _universe)
	{
		game = _universe;
	}
	public void OnSatellite(Satellite _satellite)
	{
		if (satellite != null)
		{	// disconnect from old Satellite
			satellite.vp = null;
		}
		satellite = _satellite;
		if (satellite != null)
		{	// connect to new Satellite
			satellite.vp = this;
			// resize to show entire view of satellite
			setViewportSize(
				SCALE*satellite.map.getParcelMap().getWidth(),
				SCALE*satellite.map.getParcelMap().getHeight()
			);
		}
	}
	
	public void setViewportSize(int _width, int _height)
	{
		setSize(
			_width  + insets().left + insets().right,
			_height + insets().top  + insets().bottom
		);
		// create an off screen buffer for drawing
		if (bg != null) bg.dispose(); // free old one
		img = createImage(_width,_height);
		bg  = img.getGraphics();
		System.out.println("SatelliteViewport created");
	}
		

	void drawParcelTerrain(Graphics g, int x, int y, Terrain terrain)
		// draws the terrain of a parcel
	{
		// draw new segment marker
		g.setColor(terrain.color);
		g.fillRect(
			INSET+x*SCALE,
			INSET+y*SCALE, 
			INSET+(x+1)*SCALE-1,
			INSET+(y+1)*SCALE-1
		);
	}
	void drawObject(Graphics g, float x, float y, GameObject obj)
	{
		if (obj instanceof Radar) {
			g.setColor(Color.black);
			g.drawOval(INSET+Math.round(x*SCALE)-25,INSET+Math.round(y*SCALE)-25,50,50);
		}
			
		g.setColor(Color.red);
		//g.drawImage(dot,LEFT+x*SCALE,TOP+y*SCALE,null);
		g.fillOval(INSET+Math.round(x*SCALE),INSET+Math.round(y*SCALE),2,2); // putPixel
		//g.drawLine(LEFT+x*SCALE,TOP+y*SCALE,LEFT+x*SCALE,TOP+y*SCALE);
	}
	public void paint(Graphics g) 
	{
		// simply copy the off screen buffer to the window
		g.drawImage(img,insets().left+1,insets().top+1,null);
	}

	public void updateMap() 
	{
		int x, y;
		
		for ( x=0; x < MAP_WIDTH; x++) {
			for ( y=0; y < MAP_HEIGHT; y++) {
				drawParcelTerrain(bg,x,y,satellite.map.getParcelMap().getParcel(x,y).getTerrain());
			} // for y
		} // for x
		for (x=0; x < MAP_WIDTH; x++) {
			for (y=0; y < MAP_HEIGHT; y++) {
				bg.setColor(Color.cyan);
				bg.drawString( 
					Integer.toString(satellite.map.getParcelMap().getParcel(x,y).mObjectStack.size()),
					INSET+Math.round(x*SCALE + SCALE/2),
					INSET+Math.round(y*SCALE + SCALE/2)
				);
				for (Enumeration e = satellite.map.getParcelMap().getParcel(x,y).objects(); e.hasMoreElements(); ) {
					GameObject obj = (GameObject)e.nextElement();
					drawObject(
						bg,
						(float)obj.getPosition().x / satellite.map.mParcelWidth,
						(float)obj.getPosition().y / satellite.map.mParcelHeight,
						obj
					);
				} // for objects
			} // for y
		} // for x
		// update screen
		repaint();
	}
	public void update(Graphics  g)
	{
		paint(g);
	}
}


