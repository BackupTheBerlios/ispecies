
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;

/**
 *  Handles the loading and management of the resources (like Images) used in the application.
 *
 *@created    5 november 2002
 */
public class ResourceManager {
	Dictionary dictImageNames;
	Dictionary dictImages;
	Dictionary dictTileNames;
	Dictionary dictTileMaps;
	Dictionary dictTiles;


	/**
	 *  Constructor for the ResourceManager object
	 */
	ResourceManager() {
		dictImageNames = new Hashtable();
		dictImages = new Hashtable();
		dictTileNames = new Hashtable();
		dictTileMaps = new Hashtable();
		dictTiles = new Hashtable();
	}


	/**
	 *  Opens an InputStream using. It will first try using resources and otherwise the regular method.
	 *
	 *@param  _sName  The path/name of the image to get
	 *@return         An Image object
	 */
	protected static InputStream readFile(String _sName) throws FileNotFoundException {
		InputStream is = ResourceManager.class.getResourceAsStream(_sName);
		if (is == null) {
			is = new FileInputStream(_sName);
		}
		return is;
	}


	/**
	 *  Gets an Image using the standard JDK mechanism
	 *
	 *@param  _sName  The path/name of the image to get
	 *@return         An Image object
	 */
	protected static Image readImage(String _sName) {
		Image img;
		URL url = ResourceManager.class.getResource(_sName);
		if (url != null) {
			img = Toolkit.getDefaultToolkit().createImage(url);
		} else {
			img = Toolkit.getDefaultToolkit().createImage(_sName);
		}
		return img;
	}


	/**
	 *  Registers an Image by Id with the resource manager
	 *
	 *@param  _nId    The Id that will be used from now on the refer to this image
	 *@param  _sName  The path/name of the image to register
	 */
	public void registerImage(int _nId, String _sName) {
		Image img = readImage("res/images/" + _sName);

		Integer i = new Integer(_nId);
		dictImageNames.put(i, _sName);
		dictImages.put(i, img);
		Logger.info("Registered image '" + _sName + "'");
	}


	/**
	 *  Retrieves a previously registered Image by Id
	 *
	 *@param  _nId  The Id of the Image
	 *@return       The requested Image object
	 */
	public Image getImage(int _nId) {
		return (Image)dictImages.get(new Integer(_nId));
	}


	/**
	 *  Retrieves by Id the path/name of a previously registered Image
	 *
	 *@param  _nId  The Id of the Image
	 *@return       The path/name of the requested Image
	 */
	public String getImageName(int _nId) {
		return (String)dictImageNames.get(new Integer(_nId));
	}


	/**
	 *  Returns an enumerator over the Ids of all the images
	 *
	 *@return    An enumerator
	 */
	public Enumeration getImageIds() {
		return dictImages.keys();
	}


	/**
	 *  Registers a tile set by Id with the resource manager.
	 *  A tile set is a set of Images (using a ImageStrip) that can be referenced by index.
	 *
	 *@param  _nId    The Id that will be used from now on the refer to this tile set
	 *@param  _sName  The path/name of the tile set to register
	 */
	public void registerTileSet(int _nId, String _sName) {
		Image tileMap = readImage("res/Tiles/" + _sName + "Tiles_65x65.gif");
		ImageStrip tiles = new ImageStrip(tileMap, 65, 65);

		Integer i = new Integer(_nId);
		dictTileNames.put(i, _sName);
		dictTileMaps.put(i, tileMap);
		dictTiles.put(i, tiles);

		Logger.info("Registered tileset '" + _sName + "' (#" + _nId + ")");
	}


	/**
	 *  Retrieves a previously registered tile set (ImageStrip) by Id
	 *
	 *@param  _nId  The Id of the requested tile set
	 *@return       The tile set (a reference to an ImageStrip object)
	 */
	public ImageStrip getTileSet(int _nId) {
		return (ImageStrip)dictTiles.get(new Integer(_nId));
	}


	/**
	 *  Retrieves by Id the path/name of a previously registered tile set (ImageStrip)
	 *
	 *@param  _nId  The Id of the requested tile set
	 *@return       The path/name of the requested tile set
	 */
	public String getTileSetName(int _nId) {
		return (String)dictTileNames.get(new Integer(_nId));
	}


	/**
	 *  Returns an enumerator over the Ids of all the tile sets
	 *
	 *@return    An enumerator
	 */
	public Enumeration getTileSetIds() {
		return dictTiles.keys();
	}


	/**
	 *  Returns the width for all the tiles that are managed by this object
	 *
	 *@return    The width of the tiles
	 */
	public int getTileWidth() {
		// of course this should come from somewhere!
		// but for now I'll just hard-wire it.
		return 65;
	}


	/**
	 *  Returns the height for all the tiles that are managed by this object
	 *
	 *@return    The height of the tiles
	 */
	public int getTileHeight() {
		return 65;
	}
}

/*
 *  Revision history, maintained by CVS.
 *  $Log: ResourceManager.java,v $
 *  Revision 1.5  2003/06/05 15:30:34  puf
 *  Removed dependency on JDK1.4 logging mechanism.
 *
 *  Revision 1.4  2002/11/12 08:32:54  quintesse
 *  Now using official 1.4 JDK logging system.
 *
 *  Revision 1.3  2002/11/05 15:39:41  quintesse
 *  Added Javadoc comments.
 *  Added a method to open a InputStream on a file (either a physical one or one from the resource bundle).
 *  readImage() can now also get its images from the resource bundle.
 *  Now using Logger.log() instead of System.out.println().
 *  Added CVS history section.
 *
 */

