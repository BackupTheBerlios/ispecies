
import java.util.*;

/**
 *@author     Tako
 *@created    5 november 2002
 */

interface Trigger {
	/**
	 *  Activates the trigger (all TriggerReceivers will be notified)
	 */
	public void activate();


	/**
	 *  Returns a boolean indicating if this is a repeating trigger
	 *
	 *@return    True if trigger repeats, otherwise False
	 */
	public boolean isRepeating();
}

/**
 *  This class holds a "pool" of triggers all waiting to be activated
 *
 *@author     Tako
 *@created    5 november 2002
 */
class TriggerPool {
	final Vector mTriggers = new Vector();
	final Vector mRemoved = new Vector();


	/**
	 *  Adds a Trigger to the pool
	 *
	 *@param  t  The Trigger to add
	 */
	public void add(Trigger t) {
		mTriggers.addElement(t);
	}


	/**
	 *  Removes a Trigger from the pool
	 *
	 *@param  t  The Trigger to remove
	 */
	public void remove(Trigger t) {
		if (!mTriggers.removeElement(t)) {
			// Trigger not found, it is probably active. Add to secondary list to be removed when activation finishes
			mRemoved.add(t);
		}
	}


	/**
	 *  Gets a reference to the internal Vector of Triggers
	 *
	 *@return    A Vector
	 */
	public Vector getTriggers() {
		return mTriggers;
	}
	

	/**
	 *  Activates the first Trigger in the pool.
	 *  The Trigger gets re-added to the pool if it was repeating
	 *  otherwise it will be removed.
	 */
	public void trigger() {
		if (!mTriggers.isEmpty()) {
			Trigger t = (Trigger)mTriggers.remove(0);
			t.activate();
			if (t.isRepeating()) {
				if (!mRemoved.contains(t)) {
					//mLogger.info("Putting trigger back into pool ("+t+")");
					add(t);
				} else {
					//mLogger.info("Trigger was removed while it was active ("+t+")");
					mRemoved.removeElement(t);
				}
			}
		}
	}
}

/**
 *  Call-back interface for timer events
 *
 *@author     Tako
 *@created    5 november 2002
 */
interface TimerReceiver {


	/**
	 *  Gets called when the given Trigger was activated
	 *
	 *@param  tt  Description of the Parameter
	 */
	public void doTimer(TimerTrigger tt);
}

/**
 *  A timer-based implementation of a Trigger
 *
 *@author     Tako
 *@created    5 november 2002
 */
class TimerTrigger implements Trigger {
	TimerReceiver mReceiver;
	long mlTriggertime;
	int mnPeriod;
	int mnCount;
	int mnCurrcount;


	/**
	 *  Constructor for the TimerTrigger object
	 *
	 *@param  _recv  Description of the Parameter
	 */
	TimerTrigger(TimerReceiver _recv) {
		mReceiver = _recv;
		mlTriggertime = 0;
		mnPeriod = 1;
		mnCount = 1;
	}


	/**
	 *  Gets the time when the trigger will activate
	 *
	 *@return    The activation time
	 */
	public long getTriggertime() {
		return mlTriggertime;
	}
	

	/**
	 *  Sets the time when the trigger will activate
	 */
	public void setTriggertime(long _time) {
		mlTriggertime = _time;
	}
	

	/**
	 *  Returns the current value for the repeat mode. It's a number
	 *  indicating the number of times this TimerTrigger will be
	 *  activated before it's removed from the TimerTriggerPool.
	 *  A value of -1 will keep it in the pool indefinitely or until
	 *  it's "manually" removed.
	 *
	 *@return    The repeat value
	 */
	public int getRepeat() {
		return mnCount;
	}


	/**
	 *  Sets the repeat mode to once or forever depending on the
	 *  value of 'repeating' (false=once, true=forever).
	 *
	 *@param  repeating  The new repeat value
	 */
	public void setRepeat(boolean _repeating) {
		if (_repeating) {
			mnCount = -1;
		} else {
			mnCount = 1;
		}
	}


	/**
	 *  Sets the repeat mode to repeat exactly the number of times
	 *  specified by 'cnt'.
	 *
	 *@param  cnt  The new repeat value
	 */
	public void setRepeat(int _cnt) {
		if (_cnt < 1) {
			mnCount = 1;
		} else {
			mnCount = _cnt;
		}
	}


	/**
	 *  Resets the current repeat count to the value of 'count' as it
	 *  was set on construction of this TimerTrigger or as it was set
	 *  by the last call to setRepeat().
	 */
	public void reset() {
		mnCurrcount = mnCount;
	}


	/**
	 *  Activates this TimerTrigger. It's receiving object will have
	 *  its doTimer() method called with this TimerTrigger as its only
	 *  parameter. The 'currcount' will be decremented by one and a new
	 *  'triggertime' calculated based upon the current value and the
	 *  value of 'period'.
	 */
	public void activate() {
		mReceiver.doTimer(this);
		if (isRepeating()) {
			mlTriggertime += mnPeriod;
			if (mnCurrcount > 0) {
				mnCurrcount--;
			}
		}
	}


	/**
	 *  Returns a boolean indicating if this TriggerTimer wants to stay
	 *  in the TimerTriggerPool.
	 *
	 *@return    True if repeating, False otherwise
	 */
	public boolean isRepeating() {
		return (mnCurrcount != 0);
	}
}

/**
 *  A TriggerPool for TimeTriggers.
 *
 *@author     Tako
 *@created    5 november 2002
 */
class TimerTriggerPool extends TriggerPool {
	long mlGametime = 0;


	/**
	 *  Adds a new TimerTrigger to this TimerTriggerPool's SimpleList of
	 *  TimerTriggers. The new TimerTrigger will be activated when
	 *  'gametime' reaches the value indicated by 'time', if 'time'
	 *  is smaller than 'gametime' the TimerTrigger will be activated
	 *  at the first possible call to tick().
	 *
	 *@param  _t     The Trigger to add
	 *@param  _time  The absolute time when the Trigger will be activated
	 */
	public void addAbs(TimerTrigger _t, long _time) {
		_t.reset();
		_t.setTriggertime(_time);
		insert(_t);
	}

	/**
	 *  Adds a new TimerTrigger to this TimerTriggerPool's SimpleList of
	 *  TimerTriggers. The new TimerTrigger will be activated when
	 *  as many calls to tick() have been made as indicated by 'period'.
	 *
	 *@param  _t       The Trigger to add
	 *@param  _period  The number of ticks to wait before the Trigger will be activated
	 */
	public void addRel(TimerTrigger _t, int _period) {
		_t.reset();
		_t.setTriggertime(mlGametime + _period);
		insert(_t);
	}


	/**
	 *  Increases the 'gametime' tick counter by one and looks if any
	 *  of the TimerTriggers at the front of the trigger SimpleList need to
	 *  be activated (the 'gametime' will be greater than or at least
	 *  equal to their 'triggertime').
	 */
	public synchronized void tick() {
		mlGametime++;
		Vector triggers = getTriggers();
		while (!triggers.isEmpty() && mlGametime >= ((TimerTrigger)triggers.firstElement()).getTriggertime()) {
			trigger();
		}
	}


	/**
	 *  (Maybe the hierarchy should change because this works but it's a bit of a kludge)
	 *
	 *@param  _t  The trigger to add
	 */
	public void add(Trigger _t) {
		insert((TimerTrigger)_t);
	}


	/**
	 *  Inserts a TimerTrigger at the proper place in the trigger SimpleList.
	 *  The list of TimerTriggers is sorted on increasing 'triggertime'.
	 *
	 *@param  _t  The Trigger to add
	 */
	protected void insert(TimerTrigger _t) {
		Vector triggers = getTriggers();
		if (!triggers.isEmpty()) {
			int i = 0;
			int sz = triggers.size();
			while (i < sz && ((TimerTrigger)getTriggers().elementAt(i)).getTriggertime() < _t.getTriggertime()) {
				i++;
			}
			if (i < sz) {
				triggers.insertElementAt(_t, i);
			} else {
				triggers.addElement(_t);
			}
		} else {
			triggers.addElement(_t);
		}
	}
}

/*
 *  Revision history, maintained by CVS.
 *  $Log: Triggers.java,v $
 *  Revision 1.5  2003/06/05 15:10:07  puf
 *  Removed dependency on the JDK1.4 logging mechanism.
 *
 *  Revision 1.4  2002/11/12 08:31:33  quintesse
 *  Now using official 1.4 JDK logging system.
 *
 *  Revision 1.3  2002/11/05 15:31:07  quintesse
 *  Using Logger.log() instead of System.out.writeln();
 *  Added Javadoc comments.
 *  Added CVS history section.
 *  Made sure method and member names adhere to our standards.
 *
 */

