
import java.util.EventObject;

class ObjectEvent extends EventObject {
	GameObject mKiller;
	
	public ObjectEvent(GameObject _source, GameObject _killer) {
		super(_source);
		mKiller = _killer;
	}
	
	public ObjectEvent(GameObject _source) {
		this(_source, null);
	}
	
	public GameObject getKiller() {
		return mKiller;
	}
}

