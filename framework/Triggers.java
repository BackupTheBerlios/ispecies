
import java.util.*;

/****************************************************************************
 *
 ****************************************************************************/

interface Trigger {
	public void activate();
	public boolean repeat();
}

class TriggerPool {
	final Vector triggers = new Vector();

	/* Method declarations
	public void add(Trigger t);
	public void remove(Trigger t);
	public void trigger();
	*/

	public void add(Trigger t) {
	triggers.addElement(t);
	}

	public void remove(Trigger t) {
		triggers.removeElement(t);
	}

	public void trigger() {
		if (!triggers.isEmpty()) {
			Trigger t = (Trigger)triggers.firstElement();
			triggers.removeElementAt(0);
			t.activate();
			if (t.repeat()) {
				add(t);
			}
		}
	}
}

/****************************************************************************
 *
 ****************************************************************************/

interface TimerReceiver {
	public void doTimer(TimerTrigger tt);
}

class TimerTrigger implements Trigger {
	TimerReceiver receiver;
	long triggertime;
	int period;
	int count;
	int currcount;

	/* Method declarations
	TimerTrigger(TimerReceiver recv);
	public int getRepeat();
	public void setRepeat(boolean repeating);
	public void setRepeat(int cnt);
	public void reset();
	public void activate();
	public boolean repeat();
	*/

	TimerTrigger(TimerReceiver recv) {
		receiver = recv;
		period = 1;
		count = 1;
	}

	/*
	 * Returns the current value for the repeat mode. It's a number
	 * indicating the number of times this TimerTrigger will be
	 * activated before it's removed from the TimerTriggerPool.
	 * A value of -1 will keep it in the pool indefinitely or until
	 * it's "manually" removed.
	 */
	public int getRepeat() {
		return count;
	}

	/*
	 * Sets the repeat mode to once or forever depending on the
	 * value of 'repeating' (false=once, true=forever).
	 */
	public void setRepeat(boolean repeating) {
		if (repeating) {
			count = -1;
		} else {
			count = 1;
		}
	}

	/*
	 * Sets the repeat mode to repeat exactly the number of times
	 * specified by 'cnt'.
	 */
	public void setRepeat(int cnt) {
		if (cnt < 1) {
			count = 1;
		} else {
			count = cnt;
		}
	}

	/*
	 * Resets the current repeat count to the value of 'count' as it
	 * was set on construction of this TimerTrigger or as it was set
	 * by the last call to setRepeat().
	 */
	public void reset() {
		currcount = count;
	}

	/*
	 * Activates this TimerTrigger. It's receiving object will have
	 * its doTimer() method called with this TimerTrigger as its only
	 * parameter. The 'currcount' will be decremented by one and a new
	 * 'triggertime' calculated based upon the current value and the
	 * value of 'period'.
	 */
	public void activate() {
		receiver.doTimer(this);
		if (repeat())
		{
			triggertime += period;
			if (currcount > 0)
				currcount--;
		}
	}

	/*
	 * Returns a boolean indicating if this TriggerTimer wants to stay
	 * in the TimerTriggerPool.
	 */
	public boolean repeat() {
		return (currcount != 0);
	}
}

class TimerTriggerPool extends TriggerPool {
	long gametime = 0;

	/* Method declarations
	public void addAbs(TimerTrigger t, long time);
	public void addRel(TimerTrigger t, int period);
	public void tick();
	public void add(Trigger t);
	*/

	/*
	 * Adds a new TimerTrigger to this TimerTriggerPool's SimpleList of
	 * TimerTriggers. The new TimerTrigger will be activated when
	 * 'gametime' reaches the value indicated by 'time', if 'time'
	 * is smaller than 'gametime' the TimerTrigger will be activated
	 * at the first possible call to tick().
	 */
	public void addAbs(TimerTrigger t, long time) {
		t.reset();
		t.triggertime = time;
		insert(t);
	}

	/*
	 * Adds a new TimerTrigger to this TimerTriggerPool's SimpleList of
	 * TimerTriggers. The new TimerTrigger will be activated when
	 * as many calls to tick() have been made as indicated by 'period'.
	 */
	public void addRel(TimerTrigger t, int period) {
		t.reset();
		t.triggertime = (gametime + period);
		insert(t);
	}

	/*
	 * Increases the 'gametime' tick counter by one and looks if any
	 * of the TimerTriggers at the front of the trigger SimpleList need to
	 * be activated (the 'gametime' will be greater than or at least
	 * equal to their 'triggertime').
	 */
	public void tick() {
		gametime++;
		while (!triggers.isEmpty()
		 && gametime >= ((TimerTrigger)triggers.firstElement()).triggertime) {
			trigger();
		}
	}

	/*
	 * (Maybe the hierarchy should change because this works but it's a bit of a kludge)
	 */
	public void add(Trigger t) {
		insert((TimerTrigger)t);
	}

	/*
	 * Inserts a TimerTrigger at the proper place in the trigger SimpleList.
	 * The list of TimerTriggers is sorted on increasing 'triggertime'.
	 */
	protected void insert(TimerTrigger t) {
		if (!triggers.isEmpty()) {
			int i = 0;
			int sz = triggers.size();
			while (i < sz && ((TimerTrigger)triggers.elementAt(i)).triggertime < t.triggertime) {
				i++;
			}
			if (i < sz) {
				triggers.insertElementAt(t, i);
			} else {
				triggers.addElement(t);
			}
		} else {
			triggers.addElement(t);
		}
	}
}

