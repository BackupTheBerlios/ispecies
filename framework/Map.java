import java.util.Vector;
import java.util.Enumeration;
import java.io.*;
import java.awt.Color;


class Vector3D {
	protected double mx;
	protected double my;
	protected double mz;

	Vector3D(double _x, double _y, double _z) {
		mx = _x;
		my = _y;
		mz = _z;
	}

	public Vector3D add(Vector3D v) {
		mx += v.mx;
		my += v.my;
		mz += v.mz;
		return this;
	}

	public Vector3D substract(Vector3D v) {
		mx -= v.mx;
		my -= v.my;
		mz -= v.mz;
		return this;
	}

	public Vector3D multiply(double f) {
		mx *= f;
		my *= f;
		mz *= f;
		return this;
	}

	public double dot(Vector3D v) {
		double nRes = mx * v.mx;
		nRes += my * v.my;
		nRes += mz * v.mz;
		return nRes;
	}
}


class Parcel {

	protected Terrain m_terrain;

	protected Vector mObjectStack;

	Parcel()
	{
		mObjectStack = new Vector();
	}

	public void addObject(GameObject gobject) {
		mObjectStack.addElement(gobject);
	}

	public void placeObject(GameObject gobject) {
		addObject(gobject);
		gobject.setParcel(this);
	}

	public void moveObject(GameObject gobject, Parcel newparcel) {
		if (newparcel != this) {
			removeObject(gobject);
			newparcel.placeObject(gobject);
		}
	}

	public void removeObject(GameObject gobject) {
		mObjectStack.removeElement(gobject);
		//gobject.setParcel(null);
	}

	public Enumeration objects() {
		return mObjectStack.elements();
	}

	public Terrain getTerrain() {
		return m_terrain;
	}
	
	public void setTerrain (Terrain _terrain ) {
		m_terrain = _terrain;
	}

}


class ParcelMap {
	protected Parcel mvParcels[][];
	protected int mMapWidth;
	protected int mMapHeight;

	ParcelMap(int _mapWidth, int _mapHeight) 
	{
		mMapWidth = _mapWidth;
		mMapHeight = _mapHeight;
		mvParcels = new Parcel[_mapWidth][_mapHeight];
		for (int x=0; x < mvParcels.length; x++)
			for (int y=0; y<mvParcels[x].length; y++)
				mvParcels[x][y] = new Parcel();
	}
	
	public int getWidth() {
		return mMapWidth;
	}

	public int getHeight() {
		return mMapHeight;
	}

	public Parcel getParcel(int posX, int posY) {
		if ((posX >=0) && (posX < mMapWidth) && (posY >= 0) && (posY <= mMapHeight))
			return mvParcels[posX][posY];
		else
			return null;
	}
}


class HeightMap {

	protected int mvnHeights[][];

	public void readMap(DataInputStream in) {
		int w = 10, h = 10, nHeight;

		try {
			StreamTokenizer st = new StreamTokenizer(new BufferedReader(new InputStreamReader(in)));
			st.eolIsSignificant(false);
			st.commentChar('#');
			if (st.nextToken() == StreamTokenizer.TT_NUMBER) {
				w = (int)st.nval;
			}
			if (st.nextToken() == StreamTokenizer.TT_NUMBER) {
				h = (int)st.nval;
			}
			mvnHeights = new int[w][h];
			while (st.nextToken() == StreamTokenizer.TT_NUMBER) {
				nHeight = (int)st.nval;
			}
			in.close();
			if (st.ttype != StreamTokenizer.TT_EOF) {
//				throw new FileFormatException(st.toString());
			}
		} catch (IOException e) {
		}
	}

	// Returns the height of the given point.
	// Positions between (0.0, 0.0) - (1.0, 1.0)
	// are within Parcel.
	public double getHeight(double xPos, double yPos) {
		// TODO: Intersection calculations
		return 0.0;
	}

	// Stupid name
	// Returns the vector of the direction an object
	// would slide in when left the the effects of gravity.
	public Vector3D getSlope(double xPos, double yPos) {
		// TODO: Some slope calculations
		return new Vector3D(0, 0, 0);
	}
}


interface Map {
	public HeightMap getHeightMap();
	public ParcelMap getParcelMap();
	public Parcel getParcel(long _posX, long _posY);
	public MapView getRange();
	public MapView getRange(int _centerX, int _centerY, int _width, int _height);
	public long getWidth();
	public long getHeight();
}


class GameMap implements Map {
	protected HeightMap mHeightMap;
	protected ParcelMap mParcelMap;
	protected long mMapWidth;		// Width of map in game units
	protected long mMapHeight;		// Height of map in game units
	protected int mParcelWidth;		// Width of a Parcel in game units
	protected int mParcelHeight;	// Height of a Parcel in game units
	protected int mMapParcelWidth;	// Width of map in Parcels
	protected int mMapParcelHeight;	// Height of map in Parcels

	GameMap(long _width, long _height, int _parcelWidth, int _parcelHeight) {
		mMapWidth = _width;
		mMapHeight = _height;
		mParcelWidth = _parcelWidth;
		mParcelHeight = _parcelHeight;
		mMapParcelWidth  = (int)(mMapWidth / mParcelWidth);
		mMapParcelHeight = (int)(mMapHeight / mParcelHeight);
		mParcelMap = new ParcelMap(mMapParcelWidth,mMapParcelHeight);
	}

	// Width of map in game units
	public long getWidth() { return mMapWidth; }

	// Height of map in game units
	public long getHeight()	{ return mMapHeight; }
	
	public HeightMap getHeightMap() {
		return mHeightMap;
	}

	public ParcelMap getParcelMap() {
		return mParcelMap;
	}

	public Parcel getParcel(long _posX, long _posY) {
		int x, y;

		// Go from game coordinates to map coordinates
		x = (int)(_posX / mParcelWidth);
		y = (int)(_posY / mParcelHeight);

		if ((x >=0) && (x < mMapWidth) && (y >= 0) && (y <= mMapHeight))
			return mParcelMap.getParcel(x, y);
		else
			return null;
	}

	public MapView getRange() {
		int cX = getParcelMap().getWidth() / 2;
		int cY = getParcelMap().getHeight() / 2;
		return new MapView(this, cX, cY, mMapParcelWidth, mMapParcelHeight);
	}

	public MapView getRange(int _centerX, int _centerY, int _width, int _height) {
		return new MapView(this, _centerX, _centerY, _width, _height);
	}
}


class MapView implements Map {
	protected Map mMap;
	protected long mCenterX, mCenterY;
	protected long mMapWidth, mMapHeight;

	MapView(Map _map, long _centerX, long _centerY, long _width, long _height) {
		mMap = _map;
		mCenterX = _centerX;
		mCenterY = _centerY;
		mMapWidth = _width;
		mMapHeight = _height;
	}

	// Width of map in game units
	public long getWidth() { return mMapWidth; }

	// Height of map in game units
	public long getHeight()	{ return mMapHeight; }
	
	public void setCenter(long _centerX, long _centerY) {
		mCenterX = _centerX;
		mCenterY = _centerY;
	}

	public void move(int _dX, int _dY) {
		mCenterX += _dX;
		mCenterY += _dY;
	}

	// Return a default Parcel enumerator
	public ParcelEnumeration getParcelEnumeration() {
		int mode = ParcelEnumeration.LEFT_RIGHT
				| ParcelEnumeration.TOP_DOWN
				| ParcelEnumeration.HORIZONTAL_FIRST;
		return new ParcelEnumeration(this, mode);
	}

	// Return a default GameObject enumerator
	public ObjectEnumeration getObjectEnumeration() {
		int mode = ObjectEnumeration.LEFT_RIGHT
				| ObjectEnumeration.TOP_DOWN
				| ObjectEnumeration.HORIZONTAL_FIRST;
		return new ObjectEnumeration(this, mode);
	}

	public HeightMap getHeightMap() {
		return mMap.getHeightMap();
	}

	public ParcelMap getParcelMap() {
		return mMap.getParcelMap();
	}

	public Parcel getParcel(long _posX, long _posY) {
		long x = _posX + mCenterX;
		long y = _posY + mCenterY;

		// Make sure we're staying inside the boundaries.
		if ((x >= 0) && (x <= mMapWidth) && (y >= 0) && (y <= mMapHeight)) {
			return mMap.getParcel(x, y);
		} else {
			return null;
		}
	}

	public MapView getRange() {
		long cX = mMapWidth / 2;
		long cY = mMapHeight / 2;
		return new MapView(this, cX, cY, mMapWidth, mMapHeight);
	}

	public MapView getRange(int _centerX, int _centerY, int _width, int _height) {
		return new MapView(this, _centerX, _centerY, _width, _height);
	}
}


class ParcelEnumeration implements Enumeration {
	protected MapView mMapv;
	protected int mMode;
	protected ParcelMap mPMap;
	protected int mMapWidth, mMapHeight;
	protected int mCurX, mCurY;
	protected int mDX, mDY;
	
	public static final int LEFT_RIGHT = 0;
	public static final int RIGHT_LEFT = 1;
	public static final int TOP_DOWN = 0;
	public static final int DOWN_TOP = 2;
	public static final int HORIZONTAL_FIRST = 0;
	public static final int VERTICAL_FIRST = 4;
	
	ParcelEnumeration(MapView _mapv, int _mode) {
		mMapv = _mapv;
		mMode = _mode;

		mPMap = mMapv.getParcelMap();
		mMapWidth = mPMap.getWidth();
		mMapHeight = mPMap.getHeight();

		if ((mMode & RIGHT_LEFT) == 0) {
			mCurX = 0;
			mDX = 1;
		} else {
			mCurX = mMapWidth;
			mDX = -1;
		}

		if ((mMode & DOWN_TOP) == 0) {
			mCurY = 0;
			mDY = 1;
		} else {
			mCurY = mMapHeight;
			mDY = -1;
		}
	}

	public boolean hasMoreElements() {
		return ((mCurX < mMapWidth) && (mCurY < mMapHeight));
	}

	public Object nextElement() {
		Parcel parcel = mPMap.getParcel(mCurX, mCurY);
		if ((mMode & VERTICAL_FIRST) == 0) {
			mCurX += mDX;
			if ((mCurX < 0) || (mCurX > mMapWidth)) {
				if ((mMode & RIGHT_LEFT) == 0) {
					mCurX = 0;
				} else {
					mCurX = mMapWidth;
				}
				mCurY += mDY;
			}
		} else {
			mCurY += mDY;
			if ((mCurY < 0) || (mCurY > mMapHeight)) {
				if ((mMode & DOWN_TOP) == 0) {
					mCurY = 0;
				} else {
					mCurY = mMapHeight;
				}
				mCurX += mDX;
			}
		}
		return parcel;
	}
}


class ObjectEnumeration implements Enumeration {
	Vector mObjects;
	Enumeration mObjWalk;

	public static final int LEFT_RIGHT = 0;
	public static final int RIGHT_LEFT = 1;
	public static final int TOP_DOWN = 0;
	public static final int DOWN_TOP = 2;
	public static final int HORIZONTAL_FIRST = 0;
	public static final int VERTICAL_FIRST = 4;
	
	// Objects within a Parcel won't be handled in
	// the correct order yet!!
	ObjectEnumeration(MapView _mapv, int _mode) {
		mObjects = new Vector();
		ParcelEnumeration parcels = new ParcelEnumeration(_mapv, _mode);
		Enumeration parcelObjects;
		while (parcels.hasMoreElements()) {
			Parcel parcel = (Parcel)parcels.nextElement();
			parcelObjects = parcel.objects();
			while (parcelObjects.hasMoreElements()) {
				GameObject obj = (GameObject)parcelObjects.nextElement();
				mObjects.addElement(obj);
			}
		}
		mObjWalk = mObjects.elements();
	}

	public boolean hasMoreElements() {
		return mObjWalk.hasMoreElements();
	}

	public Object nextElement() {
		return mObjWalk.nextElement();
	}
}
