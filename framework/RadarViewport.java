import java.awt.*;
import java.util.*;
import util.*;

/**
 *  Visualizes the Radar model
 *
 *@author     Tako
 *@created    6 november 2002
 */
public class RadarViewport extends Viewport {
	Radar mRadar;
	int mnLastAngle = 0; // last angle received (= last line drawn)
	long mlLastTime;
	Dimension mPreferredSize;

	/**
	 *  The relation between game units and pixels
	 */
	public final static float PREFERRED_SCALE = 2.0f;

	/**
	 *  The color to draw with
	 */
	public final static Color COLOR = Color.green;
	/**
	 *  The background color
	 */
	public final static Color BG_COLOR = Color.black;


	/**
	 *  Constructor for the RadarViewport object
	 *
	 *@param  _radar      A reference to the radar model to be visualized
	 *@param  _container  A reference to the container this view port is part of
	 */
	RadarViewport(Radar _radar, ViewportContainer _container) {
		super();
		setRadar(_radar);
		setContainer(_container);
		mlLastTime = System.currentTimeMillis();
		Logger.info("RadarViewport created");
	}


	/**
	 *  Constructor for the RadarViewport object
	 *
	 *@param  _radar  A reference to the radar model to be visualized
	 */
	RadarViewport(Radar _radar) {
		this(_radar, null);
	}


	/**
	 *  Constructor for the RadarViewport object
	 */
	RadarViewport() {
		this(null, null);
	}


	public void terminate() {
		super.terminate();
		Radar radar = null;
		synchronized(this) {
			radar = mRadar;
			mRadar = null;
		}
		if (radar != null) {
			radar.terminate();
		}
	}


	/**
	 *  Sets the radar attribute of the RadarViewport object
	 *
	 *@param  _radar  The new radar value
	 */
	public void setRadar(Radar _radar) {
		synchronized(this) {
			mRadar = _radar;
		}
		if (_radar != null) {
			mPreferredSize = new Dimension(
					2 * (int)(PREFERRED_SCALE * _radar.getRadius()),
					2 * (int)(PREFERRED_SCALE * _radar.getRadius())
			);
		} else {
			mPreferredSize = null;
		}
	}


	/**
	 *  Returns the preferred size of the Radar Viewport
	 *
	 *@return    A Dimension with the preferred size
	 */
	public Dimension getPreferredSize() {
		return mPreferredSize;
	}


	/**
	 *  Draws the outline (box and circle) of the radar
	 *
	 *@param  _g  Graphics context to draw in
	 */
	void drawOutline(Graphics _g) { // draws the (constant) outline of the radar
		_g.setColor(COLOR);
		_g.drawRect(0, 0, _g.getClipBounds().width - 1, _g.getClipBounds().height - 1); // bounding box
		_g.drawOval(0, 0, _g.getClipBounds().width - 1, _g.getClipBounds().height - 1); // radar outline
	}


	/**
	 *  Draws the rotating line indicating where the radar is pointing to
	 *
	 *@param  _g      Graphics context to draw in
	 *@param  _start  The angle of the line
	 *@param  _arc    The amount the line has moved since the last time (pos = CCW, neg = CW)
	 */
	void drawSegment(Graphics _g, int _start, int _arc) { // draws a segment of the radar
		_g.setColor(COLOR);
		int x = _g.getClipBounds().width / 2;
		int y = _g.getClipBounds().height / 2;
		Scale s = getScale();
		_g.drawLine(
				x,
				y,
				x + (int)(PREFERRED_SCALE * s.x * mRadar.getRadius() * Math.cos(_start * Math.PI / 180)),
				y - (int)(PREFERRED_SCALE * s.y * mRadar.getRadius() * Math.sin(_start * Math.PI / 180))
		);
	}


	/**
	 *  Draw a game object as a "blip" on the radar
	 *
	 *@param  _g     Graphics context to draw in
	 *@param  _gp    Position of the GameObject to draw
	 *@param  _size  Size of the "blip"
	 */
	void drawObject(Graphics _g, Point _gp, int _size) {
		Point sp = gameToScreenCoords(_gp);
		_g.setColor(COLOR);
		_g.fillOval(sp.x-(_size/2), sp.y-(_size/2), _size, _size);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  _g  Description of the Parameter
	 */
	public void paint(Graphics _g) {
		_g.setColor(BG_COLOR);
		_g.fillRect(0, 0, _g.getClipBounds().width, _g.getClipBounds().height);
		drawOutline(_g);
		Radar radar = null;
		synchronized(this) {
			radar = mRadar;
		}
		if (radar != null) {
			// for every GameObject within the radar's bounding box
			for (Enumeration e = mRadar.getRadarMap().getObjectEnumeration(); e.hasMoreElements();) {
				Logger.info("############");
				GameObject object = (GameObject)e.nextElement();
				if (object.getPosition() != null) {
					FloatPoint p = new FloatPoint(object.getPosition());
					p.x -= mRadar.getPosition().x;
					p.y -= mRadar.getPosition().y;
					drawObject(_g, p.toPoint(), 2);
				}
			}
			drawSegment(_g, mRadar.getAngle(), mRadar.getRotationSpeed());
		}
	}
}

/*
 *  Revision history, maintained by CVS.
 *  $Log: RadarViewport.java,v $
 *  Revision 1.9  2003/06/05 15:30:01  puf
 *  Removed dependency on JDK1.4 logging mechanism.
 *
 *  Revision 1.8  2002/11/12 08:35:27  quintesse
 *  Now using official 1.4 JDK logging system.
 *  Paint method now using getRadarMap() instead of getMap().
 *
 *  Revision 1.7  2002/11/07 18:01:36  quintesse
 *  Added some synchronized sections around the references to the Radar model.
 *
 *  Revision 1.6  2002/11/07 01:07:22  quintesse
 *  Lots of changes because of the new Viewport/ViewportContainer system and because of a clearer separation into a MVC architecture.
 *  The radar model has moved to its own file.
 *
 *  Revision 1.5  2002/11/05 15:27:21  quintesse
 *  Using Logger.log() instead of System.out.writeln();
 *  Added CVS history section.
 *
 */

