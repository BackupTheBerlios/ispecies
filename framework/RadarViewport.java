import java.io.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;


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
		super (_game.Map(),0,0);
		System.out.println("Radar created");
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
			setPosition(
				new Point(
					(int)(Math.random()*game.map.getWidth()),
					(int)(Math.random()*game.map.getHeight())
				)
			);
			map = game.Map().getRange(getPosition().x,getPosition().y,2*RADIUS,2*RADIUS);
		}
	}

	public void doTimer(TimerTrigger tt) 
	{
		// sends a segment of radar data on a single line in the following format:
		// start_angle arc_angle x1 y1 x2 y2 ... xn yn
		int			tmpAngle, 
					distance, 
					dx, dy;
		GameObject	object;
		Polygon		pol;

		vp.drawSegment(angle,segment);
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
			if (pol.contains(object.getPosition().x,object.getPosition().y))
				// Is it metal?
				//if (object instanceof RadarVisible)
					// tell vp to draw
					vp.drawObject (object.getPosition().x, object.getPosition().y, 1);
		}
		// increment angle
		angle = (angle + segment) % 360;
		//if (angle >= 360)
		//	angle = angle - 360;
	}
}

//class RadarViewport extends Viewport
class RadarViewport extends Frame
	implements MouseListener
{
	// member variables
	Image			img = null; // off screen buffer
	Graphics		bg = null;
	int				angle = 0; // last angle received (= last line drawn)
	Image			dot = null;
	Universe		game;
	Radar			radar;
	// "buttons" to move the radar
	Rectangle		rcUp    = new Rectangle( 5, 5,10,10);
	Rectangle		rcLeft  = new Rectangle( 0,15,10,10);
	Rectangle		rcRight = new Rectangle(10,15,10,10);
	Rectangle		rcDown  = new Rectangle( 5,25,10,10);

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

	RadarViewport( Universe _game, Radar _radar ) 
	{
		super("Radar");
		OnUniverse(_game);
		OnRadar   (_radar);
		// show and resize our window
		setBackground(BG_COLOR);
		show();
		setSize(VIEWPORT_SIZE+insets().left+insets().right,VIEWPORT_SIZE+insets().top+insets().bottom);
		// create an off screen buffer for drawing
		img = createImage(VIEWPORT_SIZE,VIEWPORT_SIZE);
		bg = img.getGraphics();
		drawOutline(bg);
		// start listening to mouse events
		addMouseListener(this);
		// start loading of the 'dot'
		dot = Toolkit.getDefaultToolkit().getImage("dot.gif");
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

	void drawOutline(Graphics g)
		// draws the (constant) outline of the radar
	{
		g.setColor(COLOR);
		g.drawRect(0,0,VIEWPORT_SIZE-1,VIEWPORT_SIZE-1); // bounding box
		g.drawOval(0,0,VIEWPORT_SIZE-1,VIEWPORT_SIZE-1); // radar outline
		// draw pseudo-buttons to move te radar
		g.draw3DRect(rcUp.x,rcUp.y,rcUp.width,rcUp.height, true);
		g.draw3DRect(rcLeft.x,rcLeft.y,rcLeft.width,rcLeft.height, true);
		g.draw3DRect(rcRight.x,rcRight.y,rcRight.width,rcRight.height, true);
		g.draw3DRect(rcDown.x,rcDown.y,rcDown.width,rcDown.height, true);
	}
	void drawSegment(int start, int arc)
		// draws a segment of the radar
	{
		// delete old segment marker
		bg.setColor(BG_COLOR);
		bg.drawLine(CENTER,CENTER, CENTER+(int)(2*RADIUS*Math.cos(angle*Math.PI/180)), CENTER-(int)(2*RADIUS*Math.sin(angle*Math.PI/180)));
		// delete previous contents
		bg.fillArc(LEFT,TOP,WIDTH,HEIGHT,start,arc+1);
		// draw new segment marker
		bg.setColor(COLOR);
		angle = start+arc+1;
		bg.drawLine(
			CENTER,
			CENTER,
			CENTER+(int)(2*RADIUS*Math.cos(angle*Math.PI/180)),
			CENTER-(int)(2*RADIUS*Math.sin(angle*Math.PI/180)));
		repaint(); // TODO: Maybe use timeout?
	}
	void drawObject(int x, int y, int size)
	{
		bg.drawImage(dot,LEFT+x*SCALE,TOP+y*SCALE,null);
		System.out.println("object drawn");
		//g.drawOval(LEFT+x*SCALE,TOP+y*SCALE,size,size); // putPixel
		//g.drawLine(LEFT+x*SCALE,TOP+y*SCALE,LEFT+x*SCALE,TOP+y*SCALE);
		repaint(); // TODO: Maybe use timeout?
	}
	public void paint(Graphics g) 
	{
		// simply copy the off screen buffer to the window
		g.drawImage(img,insets().left+1,insets().top+1,null);
	}

	public void update(Graphics  g)
	{
		// don't clear the background
		paint(g);
	}
	public void moveRadar(int dx, int dy)
	{
		radar.setPosition(
			new Point(
				radar.getPosition().x + dx,
				radar.getPosition().y + dy
			)
		);
	}
	public void mousePressed( MouseEvent e ) 
	{
		Point pt = e.getPoint();
		pt.translate(-getInsets().left,-getInsets().top);
		// up
		if (rcUp.contains(pt))
		{
			moveRadar(0,-5);
		}
		else if (rcLeft.contains(pt))
		{
			moveRadar(-5,0);
		}
		else if (rcRight.contains(pt))
		{
			moveRadar(5,0);
		}
		else if (rcDown.contains(pt))
		{
			moveRadar(0,5);
		}
	}
	public void mouseReleased( MouseEvent e ) {}
	public void mouseClicked( MouseEvent e ) {}
	public void mouseEntered( MouseEvent e ) {}
	public void mouseExited( MouseEvent e ) {}
}


