
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FramedViewportContainer
		extends JFrame
		implements ViewportContainer, WindowListener, ComponentListener, MouseListener, MouseMotionListener, TimerReceiver {
	Image mBackBufferImg = null; // off screen buffer
	Graphics mBackBufferGfx = null;
	Graphics mDrawGfx = null;
	Viewport mViewport = null;
	Dimension mViewportSize = null;
	Universe mGame = null;
	TimerTrigger mTrigger;
	int mInterval = 20;
	
	// class constants
	public final static int INSET = 5; // for 'dungeon dressing'
	public final static Color BG_COLOR = Color.black;
	
	FramedViewportContainer(Universe _game, Viewport _viewport) {
		super("Foobar");
		setBackground(BG_COLOR);
		show();

		mTrigger = new TimerTrigger(this);
		mTrigger.setRepeat(true);

		setUniverse(_game);
		setViewport(_viewport);
		
		this.addWindowListener(this);
		this.addComponentListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		Logger.log("FramedViewportContainer created");
	}
	
	FramedViewportContainer() {
		this(null, null);
	}
	
	public void terminate() {
		dispose();
	}
	
	public void dispose() {
		super.dispose();
		if (mViewport != null) {
			mViewport.terminate();
			mViewport = null;
		}
		setUniverse(null);
		if (mBackBufferGfx != null) {
			mBackBufferGfx.dispose();
		}
		Logger.log("FramedViewportContainer terminated");
	}
	
	public void setUniverse(Universe _universe) {
		if (mGame != null) {
			mGame.heartBeat.remove(mTrigger);
		}
		mGame = _universe;
		if (mGame != null) {
			mGame.heartBeat.addRel(mTrigger, mInterval);
		}
	}

	public void setViewport(Viewport _viewport) {
		if (_viewport != null) {	// disconnect from old viewport
			_viewport.setContainer(null);
		}
		mViewport = _viewport;
		if (mViewport != null) {	// connect to new Satellite
			mViewport.setContainer(this);
			// resize to show entire view of satellite
			Dimension size = mViewport.getPreferredSize(); 
			setViewportSize(size);
			// resize the window to fit the image
			setSize(size.width + getInsets().left + getInsets().right + 2 * INSET, size.height + getInsets().top  + getInsets().bottom + 2 * INSET);
		}
	}
	
	public Dimension getViewportSize() {
		return mViewportSize;
	}
	
	public void setViewportSize(Dimension _size) {
		mViewportSize = new Dimension(_size);
		int w = mViewportSize.width + 2 * INSET;
		int h = mViewportSize.height + 2 * INSET;
		// create an off screen buffer for drawing
		if (mBackBufferGfx != null) {
			mBackBufferGfx.dispose(); // free old one
		}
		mBackBufferImg = createImage(w, h);
		Logger.log("Create off-screen buffer of " + w + "x" + h + " (" + mBackBufferImg + ")");
		mBackBufferGfx  = mBackBufferImg.getGraphics();
		// Determine the graphics context for the viewport
		mDrawGfx  = mBackBufferGfx.create(INSET, INSET, mViewportSize.width, mViewportSize.height);
		mViewport.setActualSize(mViewportSize);
		Logger.log("Viewport resized");
	}
	
	public void paint(Graphics g) {
		// simply copy the off screen buffer to the window
		if (mBackBufferImg != null) {
			g.drawImage(mBackBufferImg, getInsets().left+1, getInsets().top+1, null);
		}
	}
	
	public void update(Graphics  g) {
		paint(g);
	}

	public void doTimer(TimerTrigger tt) {
		mViewport.paint(mDrawGfx);
		repaint();
	}
	
	public Point mouseToScreenCoords(Point p) {
		Point tp = new Point(p);
		Insets i = getInsets();
		tp.translate(-i.left, -i.top);
		return tp;
	}
	
	public void windowActivated(WindowEvent e) {
	}
	
	public void windowClosed(WindowEvent e) {
	}
	
	public void windowClosing(WindowEvent e) {
		dispose();
	}
	
	public void windowDeactivated(WindowEvent e) {
	}
	
	public void windowDeiconified(WindowEvent e) {
	}
	
	public void windowIconified(WindowEvent e) {
	}
	
	public void windowOpened(WindowEvent e) {
	}

	public void componentHidden(ComponentEvent e)  {
	}

	public void componentMoved(ComponentEvent e)  {
	}

	public void componentResized(ComponentEvent e)  {
		Dimension newSize = new Dimension(getSize());
		newSize.width -= getInsets().left + getInsets().right + 2 * INSET;
		newSize.height -= getInsets().top  + getInsets().bottom + 2 * INSET;
		setViewportSize(newSize);
	}

	public void componentShown(ComponentEvent e)  {
	}

	public void mouseClicked(MouseEvent event) {
	}
	
	public void mousePressed(MouseEvent event) {
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

