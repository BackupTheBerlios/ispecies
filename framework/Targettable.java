
// Game object. Has behaviour. Does not know how to draw
// itself, but has a reference to a Visual;
import java.awt.Point;

interface Targettable {
	Point getTarget();
	void setTarget(Point _target);
}

class TargettableGameObject extends GameObjectDecorator implements Targettable {
	Point mTarget = null;
	TargettableGameObject(GameObject _base) {
		super(_base);
	}
	public Point getTarget() { return mTarget; }
	public void setTarget(Point _target) { mTarget = _target; }
}
