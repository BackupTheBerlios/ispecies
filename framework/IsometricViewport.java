import java.io.*;
import java.awt.*;
import java.util.*;


class IsometricDataSource
	implements TimerReceiver
// this class represents a view of the map as it is seen by the IsometricViewport
{
	Universe			game;
	IsometricViewport	vp;				// should be a vector, to handle multiple views on this Isometric
	TimerTrigger		trigger;
	int					interval = 25;	// number of heartbeats between updates
	GameMap				map;			// Isometric can see entire map

	IsometricDataSource( Universe _game ) 
	{
		trigger = new TimerTrigger ( this );
		trigger.setRepeat(true);
		OnUniverse(_game);
		System.out.println("IsometricDatasource created");
	}

	IsometricDataSource ()
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
} // IsometricDataSource

//class IsometricViewport extends Viewport
class IsometricViewport extends Frame
{
	// member variables
	Image				img = null; // off screen buffer
	Graphics			bg = null;
	Universe			game;
	IsometricDataSource	source;
		
	// class constants
	public final static float SCALE = 1f; // size of a tile relative to world scale
	public final static Color BG_COLOR = Color.black;

	IsometricViewport( Universe _game, IsometricDataSource _source ) 
	{
		super("Isometric");
		setBackground(BG_COLOR);
		show();
		OnUniverse   (_game);
		OnDataSource (_source);
		System.out.println("IsometricViewport created");
	}
	IsometricViewport( Universe _game )
	{
		this (_game,null);
	}
	IsometricViewport ()
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
	public void OnDataSource(IsometricDataSource _source)
	{
		if (source != null)
		{	// disconnect from old Isometric
			source.vp = null;
		}
		source = _source;
		if (source != null)
		{	// connect to new IsometricDataSource
			source.vp = this;
			// resize to show entire view of source
			setViewportSize(
				Math.round(source.map.getWidth()  * SCALE ),
				Math.round(source.map.getHeight() * SCALE / 2) // isometric is half height
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
		System.out.println("IsometricViewport.setViewportSize: OSB("+_width+", "+_height+") created");
	}
		

	void DrawParcelTerrain(Graphics g, int x, int y, Terrain terrain)
		// draws the terrain of a parcel
	{
		float hw = game.m_greenTiles.getTileWidth() / 2; // half width
		float hh = game.m_greenTiles.getTileHeight() / 2; // half height
		int   left   = Math.round((x*hw) + (y*hw));
		int   top    = Math.round(
						(img.getHeight(null) / 2) - hh - 
						(x * hh / 2) + 
						(y * hh / 2)
		               );
		//g.drawImage(game.m_greenTiles.getTile(0),left,top,this);
		terrain.getVisual().paint(g,game.m_greenTiles,new Point(left,top));
	}
	void DrawObject(Graphics g, float x, float y, int size)
	{
		int   tw = game.m_greenTiles.getTileWidth();
		int   th = game.m_greenTiles.getTileHeight();
		float hw = tw / 2; // half width
		float hh = th / 2; // half height
		int   left = Math.round(x*hw) + 
					 Math.round(y*hw);
		int   top  = (img.getHeight(null) / 2) - 
					 Math.round(x*hh/2) + 
					 Math.round(y*hh/2);
		g.setColor(Color.red);
		//g.drawImage(dot,LEFT+x*SCALE,TOP+y*SCALE,null);
		g.fillOval(Math.round(left*SCALE),Math.round(top*SCALE),size,size);
	}
	public void paint(Graphics g) 
	{
		// simply copy the off screen buffer to the window
		g.drawImage(img,insets().left,insets().top,null);
		//g.drawImage(img,0,0,null);
	}

	public void updateMap() 
	{
		for (int x=0; x < source.map.getParcelMap().getWidth(); x++) {
			for (int y=0; y < source.map.getParcelMap().getWidth(); y++) {
				DrawParcelTerrain(
					bg,
					x,
					y,
					source.map.getParcelMap().getParcel(x,y).getTerrain()
				);
			} // for y
		} // for x
		for (int x=0; x < source.map.getParcelMap().getWidth(); x++) {
			for (int y=0; y < source.map.getParcelMap().getWidth(); y++) {
				for (Enumeration e = source.map.getParcelMap().getParcel(x,y).objects(); e.hasMoreElements(); ) {
					GameObject obj = (GameObject)e.nextElement();
					DrawObject(
						bg,
						(float)obj.getPosition().x / source.map.mParcelWidth,
						(float)obj.getPosition().y / source.map.mParcelHeight,
						5
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


