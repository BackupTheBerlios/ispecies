import java.awt.Point;

// Game object. Has behaviour. Does not know how to draw
// itself, but has a reference to a Visual;
// TODO: Maybe add an "isA" method that checks the base object
//		 of a decorator. This is useful when we want to treat
//       a RandomMoving(Radar) like a Radar. Da's dan weer een
//       soort QueryInterface, dus dan is Bill ook weer blij. ;-)
interface GameObject
	// Ik denk dat de setParcel() method niet public mag
	// zijn. Feitelijk is 't een afgeleid attribuut (van
	// position) en die kun je beter niet van buitenaf 
	// laten wijzigen.
{
	public Point  getPosition();
	public void   setPosition(Point _position);
	public void   setParcel(Parcel _parcel);
	//public Visual getVisual(); 
}

class BaseGameObject 
	implements GameObject
{
	GameMap mMap;
	Point mPosition;
	Parcel mParcel;

	BaseGameObject(GameMap _map, Point _position) 
	{
		mMap = _map;
		mPosition = new Point(_position.x, _position.y);
		setPosition(_position);
	}

	BaseGameObject(GameMap _map, int x, int y)
	{
		this( _map, new Point(x,y) );
	}

	protected void finalize() 
	{
		mParcel.removeObject(this);
	}

	public Point getPosition()
	{
		return mPosition;
	}

	public void setPosition(Point _position)
	{
		mPosition.move(_position.x, _position.y);
		setParcel(mMap.getParcel(mPosition.x,mPosition.y));
	}

	public Parcel getParcel() 
	{
		return mParcel;
	}

	public void setParcel(Parcel _parcel) 
	{
		if (_parcel == mParcel)
			return;

		if (mParcel != null)
			mParcel.removeObject(this);
		mParcel = _parcel;
		if (mParcel != null)
			mParcel.addObject(this);
	}
}

class GameObjectDecorator
	implements GameObject
//
// A GameObjectDecorator extends the behavior of a game
// object. This base decorator just forwards all behavior
// to it's base, you should subclass this class and override
// specific methods to make it useful.
//
// if you just got a hammer, everything looks like a nail.
// kortom: ik moet zo nodig alles met patterns doen....
//
{
	GameObject base;

	GameObjectDecorator(GameObject _base) { base = _base; }
	public Point getPosition(){ return base.getPosition(); }
	public void  setPosition(Point _position) { base.setPosition(_position); }
	public void  setParcel(Parcel _parcel) { base.setParcel(_parcel); }
}

class RandomGameObjectMover extends GameObjectDecorator
	implements TimerReceiver
{
	int interval = 4;
	TimerTrigger trigger;
	Point direction = new Point(1,1); // move to bottom right initially
	Universe game;

	RandomGameObjectMover(GameObject _base, Universe _game, int _interval)
	{
		super(_base);
		game = _game;
		interval = _interval;
		// add to heartbeat
		trigger = new TimerTrigger(this);
		trigger.setRepeat(true);
		game.heartBeat.addRel ( trigger, interval );
	}
	protected void finalize() 
	{
		game.heartBeat.remove(trigger);
	}

	public void doTimer(TimerTrigger tt) 
	{
		// move the object
		// determine new direction
		if (Math.random() < 0.05)
		{
			if (Math.random() < 0.50)
				direction.x = -direction.x;
			else
				direction.y = -direction.y;
		}
		if (Math.random() < 0.50)
		{
			Point pos = base.getPosition();
			pos.x += direction.x;
			if (pos.x < 0)
				pos.x = 0;
			else if (pos.x >= game.map.mMapWidth)
				pos.x = (int)game.map.mMapWidth-1;
			pos.y += direction.y;
			if (pos.y < 0)
				pos.y = 0;
			else if (pos.y >= game.map.mMapHeight)
				pos.y = (int)game.map.mMapHeight-1;
			base.setPosition(pos);
		}
	}

}
