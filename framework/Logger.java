/*
 *  Logger.java
 */
 
/**
 *  Description of the Class
 *
 *@author     Tako
 *@created    5 november 2002
 */
class Logger {

	/**
	 *  Writes a message to the log
	 *
	 *@param  _msg  The message to write to the log
	 */
	public static void log(String _msg) {
		System.out.println(_msg);
	}
	
	public static void info(String _msg) {
		System.out.println(_msg);
	}

}

/*
 *  Revision history, maintained by CVS.
 *  $Log: Logger.java,v $
 *  Revision 1.2  2003/06/05 14:41:33  puf
 *  Added an info method to make it more compatible with the JDK1.4 logging mechanism.
 *  Removed an unused import.
 *
 *  Revision 1.1  2002/11/05 15:33:37  quintesse
 *  First check-in of a globally usable Logger class.
 *
 */

