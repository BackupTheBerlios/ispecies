
import java.io.*;
import java.awt.*;
import java.awt.event.*;
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
			map = game.getMap(); // can see the entire map
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
		img = createImage(_width, _height);
		bg  = img.getGraphics();
		System.out.println("IsometricViewport.setViewportSize: OSB("+_width+", "+_height+") "+((bg!=null)?"":"NOT ")+"created");
	}
		
	void DrawParcelTerrain(Graphics g, int x, int y, int h, Terrain terrain)
		// draws the terrain of a parcel
	{
		float hw = game.rm.getTileWidth() / 2; // half width
		float hh = game.rm.getTileHeight() / 2; // half height
		int   left   = Math.round((x*hw) + (y*hw));
		int   top    = Math.round(
						(img.getHeight(null) / 2) - hh - 
						(x * hh / 2) + 
						((y - h) * hh / 2)
		               );
		//g.drawImage(game.m_greenTiles.getTile(0),left,top,this);
		terrain.getVisual().paint(g, new Point(left,top));
	}
	
	void DrawObject(Graphics g, float x, float y, int size)
	{
		int tw = game.rm.getTileWidth();
		int th = game.rm.getTileHeight();
		float hw = tw / 2; // half width
		float hh = th / 2; // half height
		int   left = Math.round(x*hw) + 
					 Math.round(y*hw);
		int   top  = (img.getHeight(null) / 2) - 
					 Math.round(x*hh/2) + 
					 Math.round(y*hh/2);
		g.setColor(Color.red);
		//g.drawImage(dot,LEFT+x*SCALE,TOP+y*SCALE,null);
		g.fillOval(Math.round(left*SCALE), Math.round(top*SCALE), size, size);
	}
	
	public void paint(Graphics g) 
	{
		// simply copy the off screen buffer to the window
		g.drawImage(img, insets().left, insets().top, null);
		//g.drawImage(img,0,0,null);
	}

	public void updateMap()
	{
		Rectangle r = bg.getClipBounds();
		if (r != null) {
			bg.clearRect(r.x, r.y, r.width, r.height);
		}
		// TODO: Clear entire image of we're not clipping

		for (int x=0; x < source.map.getParcelMap().getWidth(); x++) {
			for (int y=0; y < source.map.getParcelMap().getWidth(); y++) {
				Parcel p = source.map.getParcelMap().getParcel(x,y);
				DrawParcelTerrain(
					bg,
					x,
					y,
					p.getBaseHeight(),
					p.getTerrain()
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


class IsometricEditViewport extends IsometricViewport implements KeyListener, MouseListener { 
	int FLASH_INTERVAL = 500;
	boolean m_bFlashOn = false;
	long m_lFlashSwitchTime = 0;
	int m_nCursorX = 0, m_nCursorY = 0;
	
	IsometricEditViewport( Universe _game, IsometricDataSource _source ) {
		super(_game, _source);
		this.addKeyListener(this);
		this.addMouseListener(this);
	}
	
	IsometricEditViewport( Universe _game ) {
		this(_game, null);
	}
	
	IsometricEditViewport() {
		this(null, null);
	}
	
	public void updateMap() { 
		Rectangle r = bg.getClipBounds();
		if (r != null) {
			bg.clearRect(r.x, r.y, r.width, r.height);
		}
		// TODO: Clear entire bg if r == null
		
		for (int x=0; x < source.map.getParcelMap().getWidth(); x++) {
			for (int y=0; y < source.map.getParcelMap().getWidth(); y++) {
				if (m_bFlashOn || x != m_nCursorX || y != m_nCursorY) {
					Parcel p = source.map.getParcelMap().getParcel(x,y);
					DrawParcelTerrain(
						bg,
						x,
						y,
						p.getBaseHeight(),
						p.getTerrain()
					);
					float hw = game.rm.getTileWidth() / 2; // half width
					float hh = game.rm.getTileHeight() / 2; // half height
					int   left   = Math.round((x*hw) + (y*hw));
					int   top    = Math.round(
									(img.getHeight(null) / 2) - hh - 
									(x * hh / 2) + 
									((y - p.getBaseHeight()) * hh / 2)
								   );
					if (x == m_nCursorX && y == m_nCursorY) {
						bg.setColor(Color.white);
						bg.drawOval(
							left + game.rm.getTileWidth()/2 - 1, 
							top + game.rm.getTileHeight()/2 - 1,
							3, 
							3
						);
					}
				}
			} // for y
		} // for x
		if (System.currentTimeMillis() - m_lFlashSwitchTime > FLASH_INTERVAL) {
			m_bFlashOn = !m_bFlashOn;
			m_lFlashSwitchTime = System.currentTimeMillis();
		}
		
		// update screen
		repaint();
	}

	public void keyPressed(KeyEvent e) {
		Parcel p;
		
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if (m_nCursorX > 0) {
					m_nCursorX--;
				}
				break;
			case KeyEvent.VK_RIGHT:
				if (m_nCursorX < (source.map.getParcelMap().getWidth() - 1)) {
					m_nCursorX++;
				}
				break;
			case KeyEvent.VK_UP:
				if (m_nCursorY > 0) {
					m_nCursorY--;
				}
				break;
			case KeyEvent.VK_DOWN:
				if (m_nCursorY < (source.map.getParcelMap().getHeight() - 1)) {
					m_nCursorY++;
				}
				break;
			case KeyEvent.VK_PAGE_UP:
				p = source.map.getParcelMap().getParcel(m_nCursorX, m_nCursorY);
				p.setBaseHeight(p.getBaseHeight() + 1);
				break;
			case KeyEvent.VK_PAGE_DOWN:
				p = source.map.getParcelMap().getParcel(m_nCursorX, m_nCursorY);
				p.setBaseHeight(p.getBaseHeight() - 1);
				break;
			case KeyEvent.VK_HOME:
				p = source.map.getParcelMap().getParcel(m_nCursorX, m_nCursorY);
				p.setBaseHeight(0);
				break;
			case KeyEvent.VK_S:
				p = source.map.getParcelMap().getParcel(m_nCursorX, m_nCursorY);
				Terrain t = p.getTerrain();
				p.setTerrain(Terrain.getShapedTerrain(game.rm, t.getTileSet(), t.getShape() + 1 )); // TODO: Wrap if t.getShape()+1 >= shapeCount
				break;
			default:
				if ((e.getModifiers() & e.CTRL_MASK) != 0) {
					if (e.getKeyCode() == KeyEvent.VK_F) {
						for (int x=0; x < source.map.getParcelMap().getWidth(); x++) {
							for (int y=0; y < source.map.getParcelMap().getWidth(); y++) {
								p = source.map.getParcelMap().getParcel(x, y);
								p.setBaseHeight(0);
								p.getTerrain().setShape(0);
							}
						}
					} else if (e.getKeyCode() == KeyEvent.VK_W) {
						MapBuilder mb = new MapBuilder(source.game.rm);
						try {
							mb.writeGameMap("test.map", source.map);
						} catch (IOException err) {
							System.out.println("Could not write map");
						};
					}
				} else {
					p = source.map.getParcelMap().getParcel(m_nCursorX, m_nCursorY);
					int nShape = (int)e.getKeyChar() - (int)'a';
					if ((nShape >= 0) && (nShape <= 24)) {
						p.getTerrain().setShape(nShape);
					}
					int nTileSet = (int)e.getKeyChar() - (int)'0';
					if ((nTileSet >= 0) && (nTileSet < source.game.rm.getTileSets().size())) {
						p.getTerrain().setTileSet(nTileSet);
					}
				}
				break;
		}
		System.out.println(e.getKeyText(e.getKeyCode()));
	}
	
	public void keyReleased(KeyEvent e) {
	}
	
	public void keyTyped(KeyEvent e) {
	}
	
	public void mouseExited(java.awt.event.MouseEvent mouseEvent) {
	}
	
	public void mouseReleased(java.awt.event.MouseEvent mouseEvent) {
	}
	
	public void mousePressed(java.awt.event.MouseEvent mouseEvent) {
		float hw = game.rm.getTileWidth() / 2; // half width
		float hh = game.rm.getTileHeight() / 2; // half height
		/*
		int   left   = Math.round((x*hw) + (y*hw));
		int   top    = Math.round(
						(img.getHeight(null) / 2) - hh - 
						(x * hh / 2) + 
						((y - h) * hh / 2)
		               );
		*/
		/*
		Point parcelPoint = source.map.gameXYToParcelXY(mouseEvent.getX(), mouseEvent.getY());
		m_nCursorX = (int)Math.round(parcelPoint.getX());
		m_nCursorY = (int)Math.round(parcelPoint.getY());
		*/
		m_nCursorX = (int)Math.round(mouseEvent.getX() / hw - mouseEvent.getY() / hh);
		m_nCursorY = (int)Math.round(mouseEvent.getY() / hh);
		System.out.println("cursor = ("+m_nCursorX+", "+m_nCursorY+")");
	}
	
	public void mouseClicked(java.awt.event.MouseEvent mouseEvent) {
	}
	
	public void mouseEntered(java.awt.event.MouseEvent mouseEvent) {
	}
	
}

