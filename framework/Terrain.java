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
	
	public Color color = Color.black;
	
	public Visual getVisual() { return m_visual; }
	public void setVisual(Visual _visual) { m_visual = _visual; }
}

class GrassTerrain extends Terrain {
	GrassTerrain()
	{
		super();
		color = Color.green;
	}
}

class WaterTerrain extends Terrain {
	WaterTerrain()
	{
		super();
		color = Color.blue;
	}
}

class DesertTerrain extends Terrain {
	DesertTerrain()
	{
		super();
		color = Color.yellow;
	}
}
