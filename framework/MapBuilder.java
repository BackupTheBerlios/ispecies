
import java.io.*;
import java.util.*;

public class MapBuilder {
	ResourceManager m_rm;
	
	MapBuilder(ResourceManager _rm) {
		m_rm = _rm;
	}
	
	GameMap readGameMap(String _sFilename) throws FileNotFoundException {		
		return readGameMap(new DataInputStream(ResourceManager.readFile("res/maps/" + _sFilename)));
	}
	
	GameMap readGameMap(DataInputStream _in) {
		long width, height;
		int  pwidth, pheight;
		Map map = null;
		
		try {
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
			map = new GameMap(width, height, pwidth, pheight);
			
			// read resource table
			readResources(st, m_rm);
			
			// read parcel map
			readParcelMap(st, map.getParcelMap());
			
			// read persisted game objects
		}
		catch(IOException e) {
			Logger.info("Invalid map file format");
		}
		return (GameMap)map;
	}
	
	void writeGameMap(String _sFilename, GameMap _game) throws IOException {
		PrintWriter fl = new PrintWriter(new BufferedWriter(new FileWriter(_sFilename)));
		writeGameMap(fl, _game);
		fl.flush();
		fl.close();
	}
	
	public void writeGameMap(PrintWriter _fl, GameMap _game) throws IOException {
		
		_fl.println("# width and height of the map in game units");
		_fl.println(_game.getWidth() + " " + _game.getHeight());
		_fl.println("# width and height of a parcel");
		_fl.println(_game.mParcelWidth + " " + _game.mParcelHeight);
		
		writeResources(_fl, m_rm);
		
		writeParcelMap(_fl, _game.getParcelMap());
	}
	
	public void readResources(StreamTokenizer _st, ResourceManager _rm) throws IOException {
		_st.eolIsSignificant(false);
		_st.commentChar('#');
		while (_st.nextToken() != StreamTokenizer.TT_EOF) {
			if ((_st.ttype == StreamTokenizer.TT_WORD) && !_st.sval.equals("end")) {
				if (_st.sval.equals("Tileset")) {
					if (_st.nextToken() == StreamTokenizer.TT_NUMBER) {
						int nId = (int)_st.nval;
						if (_st.nextToken() == StreamTokenizer.TT_WORD) {
							String sName = _st.sval;
							_rm.registerTileSet(nId, sName);
						}
					}
				}
			} else {
				break;
			}
		}
	}
	
	public void writeResources(PrintWriter _fl, ResourceManager _rm) {
		_fl.println("#");
		_fl.println("# Resources");
		_fl.println("#");
		for (Enumeration e = _rm.getTileSetIds(); e.hasMoreElements(); ) {
			int nId = ((Integer)e.nextElement()).intValue();
			_fl.println("Tileset " + nId + " " + _rm.getTileSetName(nId));
		}
		_fl.println("end");
	}
	
	public void readParcelMap(StreamTokenizer _st, ParcelMap _map) throws IOException {
		_st.eolIsSignificant(false);
		_st.commentChar('#');
		for (int y=0; y < _map.getHeight(); y++) {
			for (int x=0; x < _map.getWidth(); x++) {
				if (_st.nextToken() == StreamTokenizer.TT_NUMBER) {
					int nType = (int)_st.nval;
					if (_st.nextToken() == StreamTokenizer.TT_WORD) {
						int nShape = (int)_st.sval.charAt(0) - (int)'a';
						int nHeight = Integer.valueOf(_st.sval.substring(1)).intValue();
						Parcel p = _map.getParcel(x,y);
						p.setBaseHeight(nHeight);
						p.setTerrain( Terrain.getShapedTerrain(m_rm, nType, nShape) );
					}
				}
			} // for x
		} // for y
	}
	
	public void writeParcelMap(PrintWriter _fl, ParcelMap _map) throws IOException {
		_fl.println("#");
		_fl.println("# ParcelMap (Type/Shape/Height)");
		_fl.println("#");
		for (int y=0; y < _map.getHeight(); y++) {
			for (int x=0; x < _map.getWidth(); x++) {
				Parcel p = _map.getParcel(x,y);
				Terrain t = p.getTerrain();
				String s = String.valueOf(t.getTileSet()) + (char)(t.getShape() + (int)'a') + String.valueOf(p.getBaseHeight());
				if (x == 0) {
					_fl.print(s);
				} else {
					_fl.print(" " + s);
				}
			} // for x
			_fl.println("");
		} // for y
	}
}

/*
 *  Revision history, maintained by CVS.
 *  $Log: MapBuilder.java,v $
 *  Revision 1.8  2003/06/05 15:22:25  puf
 *  Removed dependency on JDK1.4 logging mechanism.
 *  Removed unused import.
 *
 *  Revision 1.7  2002/11/12 08:33:48  quintesse
 *  Now using official 1.4 JDK logging system.
 *
 *  Revision 1.6  2002/11/05 15:37:14  quintesse
 *  Now using Logger.log() instead of System.out.println();
 *  Added CVS history section.
 *  Now using ResourceManager to read the map file.
 *  Some code changes because of changes to the interface of the ResourceManager.
 *
 */

