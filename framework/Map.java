import java.util.Vector;
import java.util.Enumeration;
import java.io.*;
import java.awt.Color;
import java.awt.Point;


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
	protected int m_nHeight;
	
	protected Vector mObjectStack;
	
	Parcel() {
		mObjectStack = new Vector();
	}
	
	public void addObject(GameObject gobject) {
		mObjectStack.addElement(gobject);
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
	
	public void setTerrain(Terrain _terrain) {
		m_terrain = _terrain;
	}
	
	public int getBaseHeight() {
		return m_nHeight;
	}
	
	public void setBaseHeight(int _nHeight) {
		m_nHeight = _nHeight;
	}
}


class ParcelMap {
	protected Parcel mvParcels[][];
	protected int mMapWidth;
	protected int mMapHeight;
	
	ParcelMap(int _mapWidth, int _mapHeight) {
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
	HeightMap getHeightMap();
	ParcelMap getParcelMap();
	Point gameXYToParcelXY(long _posX, long _posY);
	Parcel getParcel(long _posX, long _posY);
	Point getParcelPosition(Parcel _parcel);
	MapView getRange();
	MapView getRange(Point _center, int _width, int _height);
	MapView getRange(int _centerX, int _centerY, int _width, int _height);
	void moveObject(GameObject _obj, Point _from, Point _to);
	Point getCenter();
	long getWidth();
	long getHeight();
	
}


class GameMap implements Map {
	protected HeightMap mHeightMap;
	protected ParcelMap mParcelMap;
	protected long mMapWidth;		// Width of map in game units
	protected long mMapHeight;		// Height of map in game units
	protected Point mMapCenter;
	protected int mParcelWidth;		// Width of a Parcel in game units
	protected int mParcelHeight;	// Height of a Parcel in game units
	protected int mParcelMapWidth;	// Width of map in Parcels
	protected int mParcelMapHeight;	// Height of map in Parcels
	
	GameMap(long _width, long _height, int _parcelWidth, int _parcelHeight) {
		mMapWidth = _width;
		mMapHeight = _height;
		mMapCenter = new Point((int)(_width / 2), (int)(_height / 2));
		mParcelWidth = _parcelWidth;
		mParcelHeight = _parcelHeight;
		mParcelMapWidth  = (int)(mMapWidth / mParcelWidth);
		mParcelMapHeight = (int)(mMapHeight / mParcelHeight);
		mParcelMap = new ParcelMap(mParcelMapWidth, mParcelMapHeight);
	}
	
	// Width of map in game units
	public long getWidth() { return mMapWidth; }
	
	// Height of map in game units
	public long getHeight()	{ return mMapHeight; }
	
	public Point getCenter() { return mMapCenter; }
	
	public HeightMap getHeightMap() {
		return mHeightMap;
	}
	
	public ParcelMap getParcelMap() {
		return mParcelMap;
	}
	
	public Point gameXYToParcelXY(long _posX, long _posY) {
		// Go from game coordinates to map coordinates
		Point p = new Point(
			(int)(_posX / mParcelWidth),
			(int)(_posY / mParcelHeight)
		);
		return p;
	}
	
	public Parcel getParcel(long _posX, long _posY) {
		// Go from game coordinates to map coordinates
		Point p = gameXYToParcelXY(_posX, _posY);
		
		if ((p.x >=0) && (p.x < mParcelMapWidth) && (p.y >= 0) && (p.y <= mParcelMapHeight))
			return mParcelMap.getParcel(p.x, p.y);
		else
			return null;
	}
	
	protected Parcel getParcel(Point _pos) {
		return getParcel(_pos.x, _pos.y);
	}
	
	public Point getParcelPosition(Parcel _parcel) {
		for (int x=0; x < mParcelMapWidth; x++) {
			for (int y=0; y < mParcelMapHeight; y++) {
				if (mParcelMap.getParcel(x, y) == _parcel) {
					return new Point(x, y);
				}
			}
		}
		return null;
	}
	
	public MapView getRange() {
		long cX = mMapWidth / 2;
		long cY = mMapHeight / 2;
		return new MapView(this, cX, cY, mMapWidth, mMapHeight);
	}
	
	public MapView getRange(Point _center, int _width, int _height) {
		return new MapView(this, _center, _width, _height);
	}
	
	public MapView getRange(int _centerX, int _centerY, int _width, int _height) {
		return new MapView(this, _centerX, _centerY, _width, _height);
	}

	public void moveObject(GameObject _obj, Point _from, Point _to) {
		Parcel fromParcel =  (_from != null) ? getParcel( _from.x, _from.y ) : null;
		Parcel toParcel = (_to != null) ? getParcel( _to.x, _to.y ) : null;
		if (fromParcel != toParcel) {
			// TODO: invalidate fromParcel and toParcel to reduce drawing
			{
				String sFrom = (fromParcel != null) ? fromParcel+" ("+fromParcel.getTerrain().getClass().getName()+")" : "<nowhere>" ;
				String sTo = (toParcel != null) ? toParcel+" ("+toParcel.getTerrain().getClass().getName()+")" : "<nowhere>" ;
				System.out.println("Moving '"+_obj.getName()+"' from "+sFrom+" to "+sTo);
			}
			if (fromParcel != null) {
				fromParcel.removeObject(_obj);
			}
			if (toParcel != null) {
				toParcel.addObject(_obj);
			}
		}
	}
}


class MapView implements Map {
	protected Map mMap;
	protected Point mCenter, mOffset;
	protected long mMapWidth, mMapHeight;
	
	MapView(Map _map, long _centerX, long _centerY, long _width, long _height) {
		mMap = _map;
		mCenter = new Point((int)_centerX, (int)_centerY);
		Point mapcent = _map.getCenter();
		mOffset = new Point(
		(int)((_centerX - _width/2) - (mapcent.x - _map.getWidth()/2)),
		(int)((_centerY - _height/2) - (mapcent.y - _map.getHeight()/2))
		);
		mMapWidth = _width;
		mMapHeight = _height;
	}
	
	MapView(Map _map, Point _center, long _width, long _height) {
		this(_map, _center.x, _center.y, _width, _height);
	}
	
	// Width of map in game units
	public long getWidth() { return mMapWidth; }
	
	// Height of map in game units
	public long getHeight()	{ return mMapHeight; }
	
	public Point getCenter() { return mCenter; }
	
	public void setCenter(long _centerX, long _centerY) {
		mCenter = new Point((int)_centerX, (int)_centerY);
	}
	
	public void move(int _dX, int _dY) {
		mCenter.translate(_dX, _dY);
	}
	
	// Return a default Parcel enumerator
	public ParcelEnumeration getParcelEnumeration() {
		int mode = ParcelEnumeration.LEFT_RIGHT
			| ParcelEnumeration.TOP_DOWN
			| ParcelEnumeration.HORIZONTAL_FIRST
		;
		return new ParcelEnumeration(this, mode);
	}
	
	// Return a default GameObject enumerator
	public ObjectEnumeration getObjectEnumeration() {
		int mode = ObjectEnumeration.LEFT_RIGHT
			| ObjectEnumeration.TOP_DOWN
			| ObjectEnumeration.HORIZONTAL_FIRST
		;
		return new ObjectEnumeration(this, mode);
	}
	
	public HeightMap getHeightMap() {
		return mMap.getHeightMap();
	}
	
	public ParcelMap getParcelMap() {
		return mMap.getParcelMap();
	}
	
	public Point gameXYToParcelXY(long _posX, long _posY) {
		/* TODO: This is where the enumerations go wrong !!!! The subview isn't placed properly within its parent */
		return mMap.gameXYToParcelXY(
			_posX + mOffset.x,
			_posY + mOffset.y
		);
	}
	
	public Parcel getParcel(long _posX, long _posY) {
		return mMap.getParcel(_posX, _posY);
	}
	
	public Point getParcelPosition(Parcel _parcel) {
		return mMap.getParcelPosition(_parcel);
	}
	
	public MapView getRange() {
		return new MapView(this, mCenter.x, mCenter.y, mMapWidth, mMapHeight);
	}
	
	public MapView getRange(Point _center, int _width, int _height) {
		return new MapView(this, _center.x, _center.y, _width, _height);
	}
	
	public MapView getRange(int _centerX, int _centerY, int _width, int _height) {
		return new MapView(this, _centerX, _centerY, _width, _height);
	}
	
	public void moveObject(GameObject _obj, Point _from, Point _to) {
		Parcel fromParcel =  (_from != null) ? getParcel( _from.x, _from.y ) : null;
		Parcel toParcel = (_to != null) ? getParcel( _to.x, _to.y ) : null;
		if (fromParcel != toParcel) {
			// TODO: invalidate fromParcel and toParcel to reduce drawing
			{
				String sFrom = (fromParcel != null) ? fromParcel+" ("+fromParcel.getTerrain().getClass().getName()+")" : "<nowhere>" ;
				String sTo = (toParcel != null) ? toParcel+" ("+toParcel.getTerrain().getClass().getName()+")" : "<nowhere>" ;
				System.out.println("Moving '"+_obj.getName()+"' from "+sFrom+" to "+sTo);
			}
			if (fromParcel != null) {
				fromParcel.removeObject(_obj);
			}
			if (toParcel != null) {
				toParcel.addObject(_obj);
			}
		}
	}
}


class ParcelEnumeration implements Enumeration {
	protected MapView mMapv;
	protected int mMode;
	protected ParcelMap mPMap;
	protected Point mStart, mEnd;
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
		
		// Get the top-left and bottom-right corners of the MapView's range
		// in ParcelMap coordinates.
		mStart = mMapv.gameXYToParcelXY(0, 0);
		mEnd = mMapv.gameXYToParcelXY(mMapv.mMapWidth-1, mMapv.mMapHeight-1);
		
		// Make sure we don't enumerate outside the boundaries of the ParcelMap.
		if (mStart.x < 0) mStart.x = 0;
		if (mStart.y < 0) mStart.y = 0;
		if (mEnd.x < 0) mEnd.x = 0;
		if (mEnd.y < 0) mEnd.y = 0;
		if (mStart.x >= mPMap.getWidth()) mStart.x = mPMap.getWidth()-1;
		if (mStart.y >= mPMap.getHeight()) mStart.y = mPMap.getHeight()-1;
		if (mEnd.x >= mPMap.getWidth()) mEnd.x = mPMap.getWidth()-1;
		if (mEnd.y >= mPMap.getHeight()) mEnd.y = mPMap.getHeight()-1;
		
		if ((mMode & RIGHT_LEFT) == 0) {
			mCurX = mStart.x;
			mDX = 1;
		} else {
			mCurX = mEnd.x;
			mDX = -1;
		}
		
		if ((mMode & DOWN_TOP) == 0) {
			mCurY = mStart.y;
			mDY = 1;
		} else {
			mCurY = mEnd.y;
			mDY = -1;
		}
	}
	
	public boolean hasMoreElements() {
		boolean bMore;
		
		if ((mMode & RIGHT_LEFT) == 0) {
			bMore = (mCurX <= mEnd.x);
		} else {
			bMore = (mCurX >= mStart.x);
		}
		
		if ((mMode & DOWN_TOP) == 0) {
			bMore = bMore && (mCurY <= mEnd.y);
		} else {
			bMore = bMore && (mCurY >= mStart.y);
		}
		
		return bMore;
	}
	
	public Object nextElement() {
		Parcel parcel = mPMap.getParcel(mCurX, mCurY);
		if ((mMode & VERTICAL_FIRST) == 0) {
			mCurX += mDX;
			if ((mCurX < mStart.x) || (mCurX > mEnd.x)) {
				if ((mMode & RIGHT_LEFT) == 0) {
					mCurX = mStart.x;
				} else {
					mCurX = mEnd.x;
				}
				mCurY += mDY;
			}
		} else {
			mCurY += mDY;
			if ((mCurY < mStart.y) || (mCurY > mEnd.y)) {
				if ((mMode & DOWN_TOP) == 0) {
					mCurY = mStart.y;
				} else {
					mCurY = mEnd.y;
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
