
import java.util.*;
import java.awt.*;

public class ResourceManager {
	Dictionary dictImageNames;
	Dictionary dictImages;
	Dictionary dictTileNames;
	Dictionary dictTileMaps;
	Dictionary dictTiles;

	ResourceManager() {
		dictImageNames = new Hashtable();
		dictImages = new Hashtable();
		dictTileNames = new Hashtable();
		dictTileMaps = new Hashtable();
		dictTiles = new Hashtable();
	}

	protected Image readImage(String _sName) {
		return Toolkit.getDefaultToolkit().getImage(_sName);
	}

	public void registerImage(int _nId, String _sName) {
		Image img = readImage("res/images/" + _sName);

		Integer i = new Integer(_nId);
		dictImageNames.put(i, _sName);
		dictImages.put(i, img);
		System.out.println("Registered image '" + _sName + "'");
	}

	public Image getImage(int _nId) {
		return (Image)dictImages.get(new Integer(_nId));
	}

	public String getImageName(int _nId) {
		return (String)dictImageNames.get(new Integer(_nId));
	}

	public void registerTileSet(int _nId, String _sName) {
		Image tileMap = readImage("res/Tiles/" + _sName + "Tiles_65x65.gif");
		ImageStrip tiles = new ImageStrip(tileMap,65,65,null);

		Integer i = new Integer(_nId);
		dictTileNames.put(i, _sName);
		dictTileMaps.put(i, tileMap);
		dictTiles.put(i, tiles);

		System.out.println("Registered tileset '" + _sName + "' (#" + _nId + ")");
	}

	public ImageStrip getTileSet(int _nId) {
		return (ImageStrip)dictTiles.get(new Integer(_nId));
	}

	public String getTileSetName(int _nId) {
		return (String)dictTileNames.get(new Integer(_nId));
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