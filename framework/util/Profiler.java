/*
 * Created on 2-jun-03
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package util;

import java.util.*;

/**
 * Static class to help in profiling (parts of) an application.
 * <br>
 * @author puf
 */
public final class Profiler {
	private static Stack m_ProfilerStack = new Stack();
	private static class ProfilerStackEntry {
		private String m_description;
		private long m_starttime;
		ProfilerStackEntry(String _description) {
			m_description = _description;
			m_starttime = System.currentTimeMillis();
		}
	}
	private static Map m_ProfilerTotals = new HashMap();
	private static class ProfilerTotalEntry {
		private long m_callcount = 0;
		private long m_totaltime = 0;
		ProfilerTotalEntry(long _time) {
			m_totaltime = _time;
			m_callcount = 1;
		}
		private void add(long _time) {
			m_totaltime += _time;
			m_callcount += 1;
		}
	}
	
	public static final void startProfiling(String _description) {
		m_ProfilerStack.push(new ProfilerStackEntry(_description));
	}

	 public static final void endProfiling() {
		ProfilerStackEntry entry = (ProfilerStackEntry)m_ProfilerStack.pop();
		long time = System.currentTimeMillis() - entry.m_starttime;
		System.out.println(entry.m_description+" took "+time+"ms");
		if (m_ProfilerTotals.containsKey(entry.m_description)) {
			ProfilerTotalEntry total = (ProfilerTotalEntry)m_ProfilerTotals.get(entry.m_description);
			total.add(time);
		}
		else {
			ProfilerTotalEntry total = new ProfilerTotalEntry(time);
			m_ProfilerTotals.put(entry.m_description, total);
		}
	 }
	 
	 public static final long query() {
		ProfilerStackEntry entry = (ProfilerStackEntry)m_ProfilerStack.peek();
		return System.currentTimeMillis() - entry.m_starttime;	 	 
	 }

	 public static final void printTotals() {
	 	Iterator keys = m_ProfilerTotals.keySet().iterator();
	 	while (keys.hasNext()) {
	 		String name = (String)keys.next();
	 		ProfilerTotalEntry entry = (ProfilerTotalEntry) m_ProfilerTotals.get(name);
	 		System.out.println(entry.m_callcount+" calls to '"+name+"' took "+entry.m_totaltime+" ms. Average = "+(entry.m_totaltime / entry.m_callcount)+" per call");
	 	}
	 }
}
