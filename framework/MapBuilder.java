import java.io.*;
import java.awt.*;

public class MapBuilder
{
	Map m_map;
		
	GameMap readGameMap (String _filename)
		throws FileNotFoundException
	{
		return readGameMap(new DataInputStream(new FileInputStream(_filename)));
	}
	GameMap readGameMap (DataInputStream _in)
	{
		long width, height;
		int  pwidth, pheight;
		try 
		{
			StreamTokenizer st = new StreamTokenizer(new BufferedReader(new InputStreamReader(_in)));
			st.eolIsSignificant(false);
			st.commentChar('#');
			// read width and height
			st.nextToken();
			width  = (long)st.nval;
			st.nextToken();
			height = (long)st.nval;
			st.nextToken();
			pwidth = (int)st.nval;
			st.nextToken();
			pheight = (int)st.nval;
			// create GameMap
			m_map = new GameMap(width,height,pwidth,pheight);
			// read parcel map
			readParcelMap(st);
			// read height map
			// read persisted game objects
		}
		catch(IOException e)
		{
			System.out.println("Invalid map file format");
		}
		return (GameMap)m_map;
	}
	public void readParcelMap (StreamTokenizer _st) 
		throws IOException
	{
		Terrain grass = new GrassTerrain();
		grass.setVisual(new StaticVisual(0));
		Terrain water = new WaterTerrain();
		water.setVisual(new StaticVisual(1));
		Terrain desert= new DesertTerrain();
		desert.setVisual(new StaticVisual(2));
		
		_st.eolIsSignificant(false);
		_st.commentChar('#');
		for (int x=0; x < m_map.getParcelMap().getWidth(); x++)
		{
			for (int y=0; y < m_map.getParcelMap().getHeight(); y++)
			{
				if (_st.nextToken() == StreamTokenizer.TT_NUMBER) {
					int n = (int)_st.nval; 
					switch (n) {
						case 0: {
							m_map.getParcelMap().getParcel(x,y).setTerrain( grass );
							break;
						}
						case 1: {
							m_map.getParcelMap().getParcel(x,y).setTerrain ( water );
							break;
						}
						case 2: {
							m_map.getParcelMap().getParcel(x,y).setTerrain ( desert );
							break;
						}
					} // switch
				} // for x
			} // for y
		} 
	}
	
}
