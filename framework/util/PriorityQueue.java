package util;

import java.util.*;

/**
 * A collection which orders it's elements based on the priority assigned to them
 *
 * <br>
 * @version $Revision: 1.1 $
 */
public class PriorityQueue extends java.lang.Object {

	// Inner class for items in the queue
	public class QueueItem  implements Comparable {
		public Object m_aObject;
		public long m_iPriority;
		QueueItem(Object _aObject, long _iPriority) {
			m_aObject = _aObject;
			m_iPriority = _iPriority;
		}
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Object arg0) {
			QueueItem aOther = (QueueItem) arg0;
			return (int) (aOther.m_iPriority - this.m_iPriority); 
		}
		
	} // class QueueItem


	// Iterator implementation that iterates over the QueueItem.m_aObject, ie. the contents of the queue.
	private class IteratorImpl implements Iterator {		
		Iterator m_aIterator;
		private IteratorImpl(Iterator _aMasterIterator) {
			m_aIterator = _aMasterIterator;
		}
		public boolean hasNext() { return m_aIterator.hasNext(); }
		public Object next() { return ((QueueItem)m_aIterator.next()).m_aObject; }
		public void remove() { m_aIterator.remove(); }
	}
	
	List m_aList = new LinkedList();
	
	// Constructs a new priority queue.
	public PriorityQueue() {
	}

	// Inserts [_aObject] into this queue with [_iPriority].
	public void add(Object _aObject, long _iPriority) {
		int i=0;
		int stepcount = 0;
		if (false) {
			// perform a binary search to determine the insertion point
			if (size() > 1) {
				int step = size() / 2;
				i = step;
				while (true) {
					stepcount++;
					//System.out.println("i="+i);
					if (
						i < 1 || 
						i >= size() || (
							getItem(i - 1).m_iPriority <= _iPriority &&
							getItem(i).m_iPriority >= _iPriority
						)
					) {
						break;
					}
					else {
						step /= 2;
						if (step < 1) step = 1;
						if (getItem(i).m_iPriority >= _iPriority) {
							i = i - step;
						}
						else {
							i = i + step;
						}
					}
				}
			}
		}
		else {
			// perform a linear search for the insertion point			
			for(java.util.Iterator itEntries = m_aList.iterator(); itEntries.hasNext(); ) {
				stepcount++;
				QueueItem aItem = (QueueItem)itEntries.next();
				if(_iPriority <= aItem.m_iPriority ) {
					break;
				}
				i++;
			}
		}
		System.out.println("i="+i);
		m_aList.add(i, new QueueItem(_aObject, _iPriority));
	}
	
	public Iterator iterator() {
		return new IteratorImpl(m_aList.iterator());
	}

	// Returns and removes the first (highest priority) object from this queue.
	public Object remove() {
		return removeItem(0).m_aObject;
	}

	// Returns the first (highest priority) object from this queue without removing it.
	public Object get() {
		return getItem(0).m_aObject;
	}
	
	// Returns the priority of the first element in this queue.
	public long getPriority() {
		return getItem(0).m_iPriority;
	}

	// Returns the number of objects in this queue.
	public int size() {
		return m_aList.size();
	}

	// Returns the object at index [i] of this queue.
	public Object get(int i) {
		return ((QueueItem)m_aList.get(i)).m_aObject;
	}
	
	// Returns the QueueItem at index [i] of this queue and removes it from the queue.
	public Object remove(int i) {
		return ((QueueItem)m_aList.remove(i)).m_aObject;
	}	
	
	// Removes the object [aObject] from this queue.
	public void remove(Object aObject) {
		int i=0;
	
		for(java.util.Iterator itEntries = m_aList.iterator(); itEntries.hasNext(); ) {
			QueueItem aItem = (QueueItem)itEntries.next();
			if(aObject == aItem.m_aObject ) {
				break;
			}
			i++;
		}
		if (i < m_aList.size()) {
			m_aList.remove(i);
		}
	}
	
	// Returns true if this list contains no objects, false otherwise.
	public boolean isEmpty() {
		return (size() == 0);
	}

	public String toString() {
		String sResult = "";
		for (int i=0; i<size(); i++) {
			sResult += i+"\tpriority = "+getItem(i).m_iPriority+"\tobject = "+getItem(i).m_aObject+"\n";
		}
		return sResult;
	}
	
	// Returns the QueueItem at index [i] of this queue and removes it from the queue.
	public QueueItem removeItem(int i) {
		return (QueueItem)m_aList.remove(i);
	}	

	// Returns the QueueItem at index [i] of this queue.
	public QueueItem getItem(int i) {
		return (QueueItem)m_aList.get(i);
	}

	// Tester	
	public static void main (String args[]) {
		int lCheckSize = 2500;
		try {
			if(args.length > 1) {				
				PriorityQueue q = null;
				for(int x = 0 ; x < 5; x++) {
					q = new PriorityQueue();
					System.out.println("Run #" + x);
					long lStart = System.currentTimeMillis();
					for(int i = 0 ; i < lCheckSize ; i++) {
						q.add("<dummy>", lStart + ((int) (Math.random() * 60000)));
					}
					long lIntermediate = System.currentTimeMillis();
					System.out.println("Added " + lCheckSize + " entries in : " + (lIntermediate - lStart));
					while(q.size() > 0 && q.getPriority() <= lIntermediate) {
						q.remove();
					}
					long l2ndIntermediate = System.currentTimeMillis();
					System.out.println("Removed entries in : " + (l2ndIntermediate - lIntermediate));
					for(int i = 0 ; i < lCheckSize; i++) {
						q.add("<dummy>", l2ndIntermediate + ((int)(Math.random() * 60000)));
					}
					long l3rdIntermediate = System.currentTimeMillis();
					System.out.println("Added " + lCheckSize + " entries in : " + (l3rdIntermediate  - l2ndIntermediate));
					System.out.println("Total cycle time " + (l3rdIntermediate - lStart));
				}
								
			} else {
				PriorityQueue q = new PriorityQueue();
				System.out.println("Press <Enter> to test Queue");
				while (System.in.read() != 'Q') {
					for (int i=0; i < 16; i++) {
						q.add(new byte[1024*1024], 100);
						q.add(new byte[1024*1024], 10);
					}
					for (int i=0; i < 16; i++) {
						q.remove();
						q.remove();
					}
					System.gc();
					System.runFinalization();
					System.out.println("Queue: "+q);
					System.out.println("Memory: "+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())+"/"+Runtime.getRuntime().totalMemory());
				}
			}
		}
		catch (Exception ex) {
			System.err.println("Error" + ex.getMessage());
			ex.printStackTrace();
		}
	}	
	
}
