
import java.awt.Point;

// Game object. Has behaviour. Does not know how to draw
// itself, but has a reference to a Visual;
interface GameObject {
	Point getPosition();
	void  setPosition(Point _position);
	void setName(String _name);
	String getName();
}

class BaseGameObject implements GameObject {
	GameMap mMap;
	Point mPosition;
	String mName;

	BaseGameObject(GameMap _map, Point _position) {
		this( _map, _position.x, _position.y );
	}
	
	BaseGameObject(String _name, GameMap _map, Point _position) {
		this(_name, _map, _position.x, _position.y );
	}
	
	BaseGameObject(GameMap _map, int x, int y) {
		this(null, _map, x, y);
	}
	
	BaseGameObject(String _name, GameMap _map, int x, int y) {
		mName = _name;
		mMap = _map;
		mPosition = new Point(x, y);
		setPosition(mPosition);
	}
	
	public Point getPosition() {
		return mPosition;
	}
	
	public void setPosition(Point _position) {
		mMap.moveObject(this, getPosition(), _position);
		//mPosition.move(_position.x, _position.y);
		mPosition = _position;
	}
	
	public void setName(String _name) {
		mName = _name;
	}
	
	public String getName() {
		return mName;
	}
} // BaseGameObject

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
	public void setName(String _name) { base.setName(_name); }
	public String getName() { return base.getName(); }
}

class RandomGameObjectMover extends GameObjectDecorator
	implements TimerReceiver
{
	int interval = 4;
	int speed = 1;
	TimerTrigger trigger;
	Point direction;
	Universe game;

	RandomGameObjectMover(GameObject _base, Universe _game, int _interval)
	{
		super(_base);
		game = _game;
		interval = _interval;
		speed = (int)(Math.random() * 3) + 1;
		direction = new Point(speed, speed);
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
			if (Math.random() < 0.50) {
				double r = Math.random();
				if (r < 0.33)
					direction.x = -speed;
				else if (r > 0.66)
					direction.x = 0;
				else
					direction.x = speed;
			}
			if (Math.random() < 0.50) {
				double r = Math.random();
				if (r < 0.33)
					direction.y = -speed;
				else if (r > 0.66)
					direction.y = 0;
				else
					direction.y = speed;
			}
		}
		if (Math.random() < 0.50)
		{
			Point pos = new Point(base.getPosition());
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
