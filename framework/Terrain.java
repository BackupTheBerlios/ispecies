import java.awt.Color;

public class Terrain {

	// Support constants
	public static final int VERY_SLIPPERY = 1;		// Oily
	public static final int SLIPPERY = 2;			// Ice
	public static final int VERY_SOFT = 3;			// Swamp
	public static final int SOFT = 4;				// Sand
	public static final int VERY_HARD = 5;			// Rocks or road
	public static final int HARD = 6;				// Packed dirt

	// Obstacle constants
	public static final int SMOOTH = 1;				// No obstacles
	public static final int BUMPY = 2;				// Minor obstacles
	public static final int ROUGH = 3;				// Large obstacles
	public static final int VERY_ROUGH = 4;			// Very large obstacles
	public static final int IMPOSSIBLE = 5;			// No movement possible

	protected int m_nSupport;

	protected int m_nObstacles;
	
	protected Visual m_visual;
	protected ResourceManager m_rm;
	protected ImageStrip m_tiles;
	protected int m_nShape;
	protected int m_nTileSet;
	
	public Color color = Color.black;
	
	public Visual getVisual() { return m_visual; }
	public void setVisual(Visual _visual) { m_visual = _visual; }

	public int getShape() { return m_nShape; }
	public void setShape(int _nShape) {
		m_nShape = _nShape;
		((IndexedVisual)m_visual).setIndex(_nShape);
	}

	public int getTileSet() { return m_nTileSet; }
	public void setTileSet(int _nTileSet) {
		m_nTileSet = _nTileSet;
		m_tiles = m_rm.getTileSet(_nTileSet);
		m_visual = new IndexedVisual(m_tiles, m_nShape);
	}
	
	Terrain(ResourceManager _rm, int _nTileSet, int _nShape) {
		m_rm = _rm;
		m_nShape = _nShape;
		setTileSet(_nTileSet);
	}

	static public Terrain getShapedTerrain(ResourceManager rm, int nTileSet, int nShape) {
		Terrain t = null;
		
		switch (nTileSet) {
			case 0:
				t = new GrassTerrain(rm, 0, nShape);
				break;
			case 1:
				t = new WaterTerrain(rm, 1, nShape);
				break;
			case 2:
				t = new DesertTerrain(rm, 2, nShape);
				break;
		}
		
		return t;
	}
}

class GrassTerrain extends Terrain {

	GrassTerrain(ResourceManager _rm, int _nTileSet, int _nShape) {
		super(_rm, _nTileSet, _nShape);
		color = Color.green;
	}
}

class WaterTerrain extends Terrain {

	WaterTerrain(ResourceManager _rm, int _nTileSet, int _nShape) {
		super(_rm, _nTileSet, _nShape);
		color = Color.blue;
	}
}

class DesertTerrain extends Terrain {

	DesertTerrain(ResourceManager _rm, int _nTileSet, int _nShape) {
		super(_rm, _nTileSet, _nShape);
		color = Color.yellow;
	}
}

/*
 *  Revision history, maintained by CVS.
 *  $Log: Terrain.java,v $
 *  Revision 1.3  2002/11/05 15:30:13  quintesse
 *  Added CVS history section.
 *
 */

