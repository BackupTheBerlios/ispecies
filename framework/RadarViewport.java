import java.io.*;
import java.awt.*;
import java.util.*;


class Radar extends BaseGameObject
	implements TimerReceiver
// this class represents a radar on the map
{
	Universe		game;
	RadarViewport	vp; // should be a vector, to handle multiple views on this radar
	TimerTrigger	trigger;
	int interval = 4;		// number of heartbeats between updates
	int	angle = 0;			// start angle of next segment
	int segment = 5;		// size of segment
	MapView map;			// map range of radar

	public final static int RADIUS = 50; // 100x100 game units

	Radar( Universe _game ) 
	{
		super (_game.getMap(),
			new Point(
				(int)(Math.random()*_game.map.getWidth()),
				(int)(Math.random()*_game.map.getHeight())
			)
		);
		trigger = new TimerTrigger ( this );
		trigger.setRepeat(true);
		OnUniverse(_game);
	}

	Radar ()
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
			game.heartBeat.addRel ( trigger, interval );
			map = game.getMap().getRange(getPosition().x,getPosition().y,2*RADIUS,2*RADIUS);
		}
	}

	public void doTimer(TimerTrigger tt) 
	{
		int			tmpAngle, 
					distance, 
					dx, dy;
		GameObject	object;
		Polygon		pol;

		// alternative ObjectInsideSegment algorithm
		// create a polygon approximation of the segment
		pol = new Polygon();
		pol.addPoint(getPosition().x,getPosition().y);
		pol.addPoint(
			getPosition().x + (int)(RADIUS * Math.cos( angle * Math.PI / 180 )),
			getPosition().y - (int)(RADIUS * Math.sin( angle * Math.PI / 180 )) );
		tmpAngle = angle+segment; // may be >360 but that's ok
		pol.addPoint(
			getPosition().x + (int)(RADIUS * Math.cos( tmpAngle * Math.PI / 180 )),
			getPosition().y - (int)(RADIUS * Math.sin( tmpAngle * Math.PI / 180 )) );
		//pol.addPoint(center.x,center.y); // closing point
		// the above is an approximation of a pie slice. the circular
		// part of the slice is not included in the polygon.

		// for every GObject
		for (Enumeration e = map.getObjectEnumeration(); e.hasMoreElements();)
		{
			object = (GameObject)e.nextElement();
			// is it inside the segment?
			//if (pol.inside(object.getPosition().x, object.getPosition().y))
			//if (pol.contains(object.getPosition().x, object.getPosition().y))
				// Is it metal?
				//if (object instanceof RadarVisible)
					vp.doDrawObject(this, object.getPosition().x - getPosition().x, object.getPosition().y - getPosition().y, 1);
					// vp.doDrawObject(this, object.getPosition().x, object.getPosition().y, ((RadarVisible)object).getReflectivity())
		}

		vp.doDrawSegment(this, angle, segment, pol);

		// increment angle
		angle = angle + segment;
		if (angle >= 360)
			angle = angle - 360;
	}
}

//class RadarViewport extends Viewport
class RadarViewport extends Frame
{
	// member variables
	Image			img = null; // off screen buffer
	Graphics		bg = null;
	int				angle = 0; // last angle received (= last line drawn)
	Image			dot = null;
	Universe		game;
	Radar			radar;

	// class constants
	public final static int RADIUS = 50; // 100x100 game units
	public final static int SCALE = 2; // 200x200 pixels
	public final static int INSET = 5; // for 'dungeon dressing'
	public final static int VIEWPORT_SIZE = SCALE*RADIUS*2 + 2*INSET;
	public final static int LEFT  = INSET;
	public final static int TOP   = INSET;
	public final static int WIDTH = RADIUS*2*SCALE;
	public final static int HEIGHT= RADIUS*2*SCALE;
	public final static int CENTER= INSET + RADIUS*SCALE;

	public final static Color COLOR = Color.green;
	public final static Color BG_COLOR = Color.black;

	long lastTime;

	RadarViewport( Universe _game, Radar _radar ) 
	{
		super("Radar");
		OnUniverse(_game);
		OnRadar   (_radar);
		// show and resize our window
		setBackground(BG_COLOR);
		show();
		//resize(VIEWPORT_SIZE+insets().left+insets().right,VIEWPORT_SIZE+insets().top+insets().bottom);
		setSize(VIEWPORT_SIZE+insets().left+insets().right,VIEWPORT_SIZE+insets().top+insets().bottom);
		// create an off screen buffer for drawing
		img = createImage(VIEWPORT_SIZE,VIEWPORT_SIZE);
		bg = img.getGraphics();
		DrawOutline(bg);
		// start loading of the 'dot'
		dot = Toolkit.getDefaultToolkit().getImage("dot.gif");
		lastTime = System.currentTimeMillis();
	}
	
	RadarViewport( Universe _game )
	{
		this (_game,null);
	}
	
	RadarViewport ()
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
	
	public void OnRadar(Radar _radar)
	{
		if (radar != null)
		{	// disconnect from old radar
			radar.vp = null;
		}
		radar = _radar;
		if (radar != null)
		{	// connect to new radar
			radar.vp = this;
		}
	}

	void DrawOutline(Graphics g)
		// draws the (constant) outline of the radar
	{
		//Graphics bg = img.getGraphics();
		g.setColor(COLOR);
		g.drawRect(0,0,VIEWPORT_SIZE-1,VIEWPORT_SIZE-1); // bounding box
		g.drawOval(0,0,VIEWPORT_SIZE-1,VIEWPORT_SIZE-1); // radar outline
		//bg.fillOval(CENTER-2,CENTER-2,4,4); // center
		//bg.dispose();
	}
	
	void DrawSegment(Graphics g, int start, int arc)
		// draws a segment of the radar
	{
		//Graphics bg = img.getGraphics();
		// delete old segment marker
		g.setColor(BG_COLOR);
		g.drawLine(CENTER,CENTER, CENTER+(int)(2*RADIUS*Math.cos(angle*Math.PI/180)), CENTER-(int)(2*RADIUS*Math.sin(angle*Math.PI/180)));
		// delete previous contents
		g.fillArc(LEFT,TOP,WIDTH,HEIGHT,start,arc+1);
		// draw new segment marker
		g.setColor(COLOR);
		angle = start+arc+1;
		g.drawLine(
			CENTER,
			CENTER,
			CENTER+(int)(2*RADIUS*Math.cos(angle*Math.PI/180)),
			CENTER-(int)(2*RADIUS*Math.sin(angle*Math.PI/180)));
	}
	
	void DrawObject(Graphics g, int x, int y, int size)
	{
		//g.drawImage(dot,LEFT+x*SCALE,TOP+y*SCALE,null);
		//System.out.println("object drawn");
		//g.drawOval(LEFT+x*SCALE,TOP+y*SCALE,size,size); // putPixel
		g.drawLine(LEFT+x*SCALE,TOP+y*SCALE,LEFT+x*SCALE,TOP+y*SCALE);
	}
	
	public void paint(Graphics g) 
	{
		// simply copy the off screen buffer to the window
		g.drawImage(img,insets().left+1,insets().top+1,null);
	}

	public void doDrawSegment(Radar _radar, int _start, int _arc, Polygon _pol) {
		DrawSegment(bg, _start, _arc);
		
		bg.drawPolygon(_pol);
		
		repaint();
	}
	
	public void doDrawObject(Radar _radar, int _x, int _y, int _size) { 
		DrawObject(bg, _x, _y, _size);
	}
	
	public void update(Graphics  g)
	{
		paint(g);
	}
}


