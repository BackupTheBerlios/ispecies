/*
 *  IsometricViewport.java
 */
 
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 *  This class represents a view of the map as it is seen by the IsometricViewport
 *
 *@created    5 november 2002
 */
class IsometricDataSource implements TimerReceiver {
	Universe mGame;
	IsometricViewport mViewport;
	// should be a vector, to handle multiple views on this Isometric
	TimerTrigger mTrigger;
	int mInterval = 25;
	// number of heartbeats between updates
	GameMap mMap;
	// Isometric can see entire map


	/**
	 *  Constructor for the IsometricDataSource object
	 *
	 *@param  _game  A reference to the Universe this view exists in
	 */
	IsometricDataSource(Universe _game) {
		mTrigger = new TimerTrigger(this);
		mTrigger.setRepeat(true);
		OnUniverse(_game);
		Logger.log("IsometricDatasource created");
	}


	/**
	 *  Constructor for the IsometricDataSource object
	 */
	IsometricDataSource() {
		// no universe yet
		this(null);
	}


	/**
	 *  Callback that gets called whenever the Universe changes
	 *
	 *@param  _universe  A reference to the Universe we move to
	 */
	public void OnUniverse(Universe _universe) {
		if (mGame != null) {
			mGame.heartBeat.remove(mTrigger);
		}
		mGame = _universe;
		if (mGame != null) {
			mMap = mGame.getMap();
			// can see the entire map
			mGame.heartBeat.addRel(mTrigger, mInterval);
		}
	}


	/**
	 *  Callback for this object's "heartbeat"
	 *
	 *@param  tt  Description of the Parameter
	 */
	public void doTimer(TimerTrigger tt) {
		mViewport.updateMap();
	}
}

/**
 *  Description of the Class
 *
 *@created    5 november 2002
 */
class IsometricViewport extends Frame {

	// member variables

	Image mBackBufferImg = null;
	Graphics mBackBufferGfx = null;
	Universe mGame;
	IsometricDataSource mSource;

	/**
	 *  Size of a tile relative to world size
	 */
	public final static float SCALE = 1f;

	/**
	 *  Background color
	 */
	public final static Color BG_COLOR = Color.black;


	/**
	 *  Constructor for the IsometricViewport object
	 *
	 *@param  _game    A reference to the Universe this view exists in
	 *@param  _source  A reference to the model this view looks upon
	 */
	IsometricViewport(Universe _game, IsometricDataSource _source) {
		super("Isometric");
		setBackground(BG_COLOR);
		show();
		OnUniverse(_game);
		OnDataSource(_source);
		Logger.log("IsometricViewport created");
	}


	/**
	 *  Constructor for the IsometricViewport object
	 *
	 *@param  _game    A reference to the Universe this view exists in
	 */
	IsometricViewport(Universe _game) {
		this(_game, null);
	}


	/**
	 *  Constructor for the IsometricViewport object
	 */
	IsometricViewport() {
		this(null, null);
	}


	/**
	 *  Performs clean-up
	 */
	protected void finalize() {
		mBackBufferGfx.dispose();
		// dispose of the Graphics
	}


	/**
	 *  Callback that gets called whenever the Universe changes
	 *
	 *@param  _universe  A reference to the Universe we move to
	 */
	public void OnUniverse(Universe _universe) {
		mGame = _universe;
	}


	/**
	 *  Callback that gets called whenever the model changes
	 *
	 *@param  _source  A reference to the model we're going to view
	 */
	public void OnDataSource(IsometricDataSource _source) {
		if (mSource != null) {
			// disconnect from old Isometric
			mSource.mViewport = null;
		}
		mSource = _source;
		if (mSource != null) {
			// connect to new IsometricDataSource
			mSource.mViewport = this;
			// resize to show entire view of source
			setViewportSize(
					Math.round(mSource.mMap.getWidth() * SCALE),
					Math.round(mSource.mMap.getHeight() * SCALE / 2) // isometric is half height
			);
		}
	}


	/**
	 *  Sets the size the IsometricViewport
	 *
	 *@param  _width   The width of the view
	 *@param  _height  The height of the view
	 */
	public void setViewportSize(int _width, int _height) {
		setSize(
				_width + getInsets().left + getInsets().right,
				_height + getInsets().top + getInsets().bottom
				);
		// create an off screen buffer for drawing
		if (mBackBufferGfx != null) {
			mBackBufferGfx.dispose();
		}
		// free old one
		mBackBufferImg = createImage(_width, _height);
		mBackBufferGfx = mBackBufferImg.getGraphics();
		Logger.log("IsometricViewport.setViewportSize: OSB(" + _width + ", " + _height + ") " + ((mBackBufferGfx != null) ? "" : "NOT ") + "created");
	}


	/**
	 *  Draws the terrain at the specified position in the graphics context
	 *
	 *@param  g        Reference to the Graphics context the terrain should be drawn in
	 *@param  x        The x position to start drawing
	 *@param  y        The y position to start drawing
	 *@param  h        The base-height to start drawing
	 *@param  terrain  Description of the Parameter
	 */
	void DrawParcelTerrain(Graphics g, int x, int y, int h, Terrain terrain) {
		// draws the terrain of a parcel

		float hw = mGame.rm.getTileWidth() / 2;
		// half width
		float hh = mGame.rm.getTileHeight() / 2;
		// half height
		int left = Math.round((x * hw) + (y * hw));
		int top = Math.round(
				(mBackBufferImg.getHeight(null) / 2) - hh -
				(x * hh / 2) +
				((y - h) * hh / 2)
		);
		//g.drawImage(game.mgreenTiles.getTile(0),left,top,this);
		terrain.getVisual().paint(g, new Point(left, top));
	}


	/**
	 *  Draws the object at the specified position in the graphics context
	 *
	 *@param  g     Reference to the Graphics context the object should be drawn in
	 *@param  x     The x position to start drawing
	 *@param  y     The y position to start drawing
	 *@param  size  The size of the object
	 */
	void DrawObject(Graphics g, float x, float y, int size) {
		int tw = mGame.rm.getTileWidth();
		int th = mGame.rm.getTileHeight();
		float hw = tw / 2;
		// half width
		float hh = th / 2;
		// half height
		int left = Math.round(x * hw) +
				Math.round(y * hw);
		int top = (mBackBufferImg.getHeight(null) / 2) -
				Math.round(x * hh / 2) +
				Math.round(y * hh / 2);
		g.setColor(Color.red);
		//g.drawImage(dot,LEFT+x*SCALE,TOP+y*SCALE,null);
		g.fillOval(Math.round(left * SCALE), Math.round(top * SCALE), size, size);
	}


	/**
	 *  Paint the back buffer image to the specified Graphics context
	 *
	 *@param  g     Reference to the Graphics context the back buffer should be drawn in
	 */
	public void paint(Graphics g) {
		// simply copy the off screen buffer to the window
		g.drawImage(mBackBufferImg, getInsets().left, getInsets().top, null);
		//g.drawImage(img,0,0,null);
	}


	/**
	 *  Draws the current map to the back buffer
	 */
	public void updateMap() {
		Rectangle r = mBackBufferGfx.getClipBounds();
		if (r != null) {
			mBackBufferGfx.clearRect(r.x, r.y, r.width, r.height);
		}
		// TODO: Clear entire image of we're not clipping

		for (int x = 0; x < mSource.mMap.getParcelMap().getWidth(); x++) {
			for (int y = 0; y < mSource.mMap.getParcelMap().getWidth(); y++) {
				Parcel p = mSource.mMap.getParcelMap().getParcel(x, y);
				DrawParcelTerrain(
						mBackBufferGfx,
						x,
						y,
						p.getBaseHeight(),
						p.getTerrain()
				);
			}
			// for y
		}
		// for x
		for (int x = 0; x < mSource.mMap.getParcelMap().getWidth(); x++) {
			for (int y = 0; y < mSource.mMap.getParcelMap().getWidth(); y++) {
				for (Enumeration e = mSource.mMap.getParcelMap().getParcel(x, y).objects(); e.hasMoreElements(); ) {
					GameObject obj = (GameObject)e.nextElement();
					DrawObject(
							mBackBufferGfx,
							(float)obj.getPosition().x / mSource.mMap.mParcelWidth,
							(float)obj.getPosition().y / mSource.mMap.mParcelHeight,
							5
					);
				}
				// for objects
			}
			// for y
		}
		// for x
		// update screen
		repaint();
	}


	/**
	 *  Paint the back buffer image to the specified Graphics context
	 *
	 *@param  g     Reference to the Graphics context the back buffer should be drawn in
	 */
	public void update(Graphics g) {
		paint(g);
	}
}

/**
 *  Description of the Class
 *
 *@created    5 november 2002
 */
class IsometricEditViewport extends IsometricViewport implements KeyListener, MouseListener {
	boolean mbFlashOn = false;
	long mlFlashSwitchTime = 0;
	int mnCursorX = 0, mnCursorY = 0;

	public final static int FLASH_INTERVAL = 500;

	/**
	 *  Constructor for the IsometricEditViewport object
	 *
	 *@param  _game    A reference to the Universe this view exists in
	 *@param  _source  A reference to the model this view looks upon
	 */
	IsometricEditViewport(Universe _game, IsometricDataSource _source) {
		super(_game, _source);
		this.addKeyListener(this);
		this.addMouseListener(this);
	}


	/**
	 *  Constructor for the IsometricEditViewport object
	 *
	 *@param  _game  Description of the Parameter
	 */
	IsometricEditViewport(Universe _game) {
		this(_game, null);
	}


	/**
	 *  Constructor for the IsometricEditViewport object
	 */
	IsometricEditViewport() {
		this(null, null);
	}


	/**
	 *  Draws the current map to the back buffer
	 */
	public void updateMap() {
		Rectangle r = mBackBufferGfx.getClipBounds();
		if (r != null) {
			mBackBufferGfx.clearRect(r.x, r.y, r.width, r.height);
		}
		// TODO: Clear entire bg if r == null

		for (int x = 0; x < mSource.mMap.getParcelMap().getWidth(); x++) {
			for (int y = 0; y < mSource.mMap.getParcelMap().getWidth(); y++) {
				if (mbFlashOn || x != mnCursorX || y != mnCursorY) {
					Parcel p = mSource.mMap.getParcelMap().getParcel(x, y);
					DrawParcelTerrain(
							mBackBufferGfx,
							x,
							y,
							p.getBaseHeight(),
							p.getTerrain()
					);
					float hw = mGame.rm.getTileWidth() / 2;
					// half width
					float hh = mGame.rm.getTileHeight() / 2;
					// half height
					int left = Math.round((x * hw) + (y * hw));
					int top = Math.round(
							(mBackBufferImg.getHeight(null) / 2) - hh -
							(x * hh / 2) +
							((y - p.getBaseHeight()) * hh / 2)
					);
					if (x == mnCursorX && y == mnCursorY) {
						mBackBufferGfx.setColor(Color.white);
						mBackBufferGfx.drawOval(
								left + mGame.rm.getTileWidth() / 2 - 1,
								top + mGame.rm.getTileHeight() / 2 - 1,
								3,
								3
						);
					}
				}
			}
			// for y
		}
		// for x
		if (System.currentTimeMillis() - mlFlashSwitchTime > FLASH_INTERVAL) {
			mbFlashOn = !mbFlashOn;
			mlFlashSwitchTime = System.currentTimeMillis();
		}

		// update screen
		repaint();
	}


	/**
	 *  Handles key press events
	 *
	 *@param  e  The event to process
	 */
	public void keyPressed(KeyEvent e) {
		Parcel p;

		switch (e.getKeyCode()) {
						case KeyEvent.VK_LEFT:
							if (mnCursorX > 0) {
								mnCursorX--;
							}
							break;
						case KeyEvent.VK_RIGHT:
							if (mnCursorX < (mSource.mMap.getParcelMap().getWidth() - 1)) {
								mnCursorX++;
							}
							break;
						case KeyEvent.VK_UP:
							if (mnCursorY > 0) {
								mnCursorY--;
							}
							break;
						case KeyEvent.VK_DOWN:
							if (mnCursorY < (mSource.mMap.getParcelMap().getHeight() - 1)) {
								mnCursorY++;
							}
							break;
						case KeyEvent.VK_PAGE_UP:
							p = mSource.mMap.getParcelMap().getParcel(mnCursorX, mnCursorY);
							p.setBaseHeight(p.getBaseHeight() + 1);
							break;
						case KeyEvent.VK_PAGE_DOWN:
							p = mSource.mMap.getParcelMap().getParcel(mnCursorX, mnCursorY);
							p.setBaseHeight(p.getBaseHeight() - 1);
							break;
						case KeyEvent.VK_HOME:
							p = mSource.mMap.getParcelMap().getParcel(mnCursorX, mnCursorY);
							p.setBaseHeight(0);
							break;
						case KeyEvent.VK_S:
							p = mSource.mMap.getParcelMap().getParcel(mnCursorX, mnCursorY);
							Terrain t = p.getTerrain();
							p.setTerrain(Terrain.getShapedTerrain(mGame.rm, t.getTileSet(), t.getShape() + 1));
							// TODO: Wrap if t.getShape()+1 >= shapeCount
							break;
						default:
							if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
								if (e.getKeyCode() == KeyEvent.VK_F) {
									for (int x = 0; x < mSource.mMap.getParcelMap().getWidth(); x++) {
										for (int y = 0; y < mSource.mMap.getParcelMap().getWidth(); y++) {
											p = mSource.mMap.getParcelMap().getParcel(x, y);
											p.setBaseHeight(0);
											p.getTerrain().setShape(0);
										}
									}
								} else if (e.getKeyCode() == KeyEvent.VK_W) {
									MapBuilder mb = new MapBuilder(mGame.rm);
									try {
										mb.writeGameMap("test.map", mSource.mMap);
									} catch (IOException err) {
										Logger.log("Could not write map");
									}
									;
								}
							} else {
								p = mSource.mMap.getParcelMap().getParcel(mnCursorX, mnCursorY);
								int nShape = (int)e.getKeyChar() - (int)'a';
								if ((nShape >= 0) && (nShape <= 24)) {
									p.getTerrain().setShape(nShape);
								}
								int nTileSet = (int)e.getKeyChar() - (int)'0';
								p.getTerrain().setTileSet(nTileSet);
							}
							break;
		}
		Logger.log(KeyEvent.getKeyText(e.getKeyCode()));
	}


	/**
	 *  Handles key release events
	 *
	 *@param  e  The event to process
	 */
	public void keyReleased(KeyEvent e) {
	}


	/**
	 *  Handles key typed events
	 *
	 *@param  e  The event to process
	 */
	public void keyTyped(KeyEvent e) {
	}


	/**
	 *  Handles mouse exit events
	 *
	 *@param  e  The event to process
	 */
	public void mouseExited(MouseEvent e) {
	}


	/**
	 *  Handles mouse release events
	 *
	 *@param  e  The event to process
	 */
	public void mouseReleased(MouseEvent e) {
	}


	/**
	 *  Handles mouse pressed events
	 *
	 *@param  e  The event to process
	 */
	public void mousePressed(MouseEvent e) {
		float hw = mGame.rm.getTileWidth() / 2;
		// half width
		float hh = mGame.rm.getTileHeight() / 2;
		// half height
		/*
		 *  int   left   = Math.round((x*hw) + (y*hw));
		 *  int   top    = Math.round(
		 *  (img.getHeight(null) / 2) - hh -
		 *  (x * hh / 2) +
		 *  ((y - h) * hh / 2)
		 *  );
		 */
		/*
		 *  Point parcelPoint = mSource.mMap.gameXYToParcelXY(mouseEvent.getX(), mouseEvent.getY());
		 *  mnCursorX = (int)Math.round(parcelPoint.getX());
		 *  mnCursorY = (int)Math.round(parcelPoint.getY());
		 */
		mnCursorX = (int)Math.round(e.getX() / hw - e.getY() / hh);
		mnCursorY = (int)Math.round(e.getY() / hh);
		Logger.log("cursor = (" + mnCursorX + ", " + mnCursorY + ")");
	}


	/**
	 *  Handles mouse clicked events
	 *
	 *@param  e  The event to process
	 */
	public void mouseClicked(MouseEvent e) {
	}


	/**
	 *  Handles mouse enter events
	 *
	 *@param  e  The event to process
	 */
	public void mouseEntered(MouseEvent e) {
	}

}

/*
 *  Revision history, maintained by CVS.
 *  $Log: IsometricViewport.java,v $
 *  Revision 1.5  2003/06/05 15:02:07  puf
 *  Fixed some references.
 *
 *  Revision 1.4  2002/11/07 00:56:53  quintesse
 *  Changed deprecated calls to method insets() with getInsets().
 *
 *  Revision 1.3  2002/11/05 15:18:56  quintesse
 *  Using Logger.log() instead of System.out.writeln();
 *  Added Javadoc comments.
 *  Added CVS history section.
 *  Made sure method and member names adhere to our standards.
 *
 */

