
import java.util.*;
import java.awt.*;

public class ResourceManager { 
	Dictionary dictTileNames;
	Dictionary dictTileMaps;
	Dictionary dictTiles;
	
	ResourceManager() {
		dictTileNames = new Hashtable();
		dictTileMaps = new Hashtable();
		dictTiles = new Hashtable();
	}
	
	public void addTileSet(int _nId, String _sName) {
		Image tileMap = Toolkit.getDefaultToolkit().getImage(_sName + "Tiles_65x65.gif");
		ImageStrip tiles = new ImageStrip(tileMap,65,65,null);
		
		Integer i = new Integer(_nId);
		dictTileNames.put(i, _sName);
		dictTileMaps.put(i, tileMap);
		dictTiles.put(i, tiles);
		
		System.out.println("Read tileset '" + _sName + "' (#" + _nId + ")");
	}
	
	public ImageStrip getTileSet(int _nId) {
		Integer i = new Integer(_nId);
		return (ImageStrip)dictTiles.get(i);
	}
	
	public String getTileSetName(int _nId) {
		Integer i = new Integer(_nId);
		return (String)dictTileNames.get(i);
	}
	
	public Dictionary getTileSets() {
		return dictTiles;
	}
	
	public int getTileWidth() {
		// of course this should come from somewhere!
		// but for now I'll just hard-wire it.
		return 65;
	}
	
	public int getTileHeight() {
		return 65;
	}
}