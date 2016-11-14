/*   @(#)  MyObject.java  2002-02-04
*
*  Copyright(C) 2002, All Rights Reserved.
*  Ahlquist.com
*  516 Suisse Drive
*  San Jose, California 95123
*  U.S.A.
*
*  This document contains information proprietary and confidential to
*  Ahlquist.com, which is either copyrighted or which a
*  patent has been applied and/or protected by trade secret laws.
*
*  This document, or any parts thereof, may not be used, disclosed,
*  or reproduced in any form, by any method, or for any purpose without
*  the express written permission of Ahlquist.com.
*
*
*/

package com.ahlquist.common.util;

//import com.ahlquist.common.dump.*;
import java.util.*;
//import java.text.*;

/**
 * MyObject -- This superclass of all my objects tracks object allocations. This
 * is useful for finding memory leaks and for deciding whether to recycle. Of
 * course, we cannot track objects whose hierarchy we do not control.
 */
public class MyObject {
	static Hashtable tableStats; // Does not extend us.

	public MyObject() {
		// TODO: expose all methods in this class
		// if (Debug.fMemory)
		// getStats().created();
	}

	/*
	 * 
	 * protected Object clone() throws CloneNotSupportedException {
	 * if(Debug.fMemory) { Object clone = super.clone(); getStats().created();
	 * return clone; } else return super.clone(); }
	 * 
	 * public void finalize() { if (Debug.fMemory) getStats().destroyed();
	 * 
	 * // REVIEW: Object.finalize throws Throwable. In order to invoke //
	 * super.finalize() here, we must also throw Throwable, as must all our //
	 * callers. This seems wrong to me right now; figure out what is right! //
	 * WAS: super.finalize(); }
	 * 
	 * public final synchronized void waitOnThis() { try { wait(); } catch
	 * (InterruptedException e) { Thread.currentThread().dumpStack(); } }
	 * 
	 * 
	 * public static Dump getStatsDump() { VArray defaultDumpListItemsArray =
	 * new VArray(); Enumeration e = tableStats.elements();
	 * 
	 * while (e.hasMoreElements()) { myObjectStats stats = (myObjectStats)
	 * e.nextElement();
	 * defaultDumpListItemsArray.addElement(stats.getDumpListItem()); }
	 * 
	 * DumpList rc = new DumpList(myObjectStats.dumpListHeaders,
	 * defaultDumpListItemsArray); return(rc); }
	 * 
	 * private final myObjectStats getStats() { return(getStats(getClass())); }
	 * 
	 * private static final synchronized myObjectStats getStats(Class clazz) {
	 * if (tableStats == null) tableStats = new Hashtable();
	 * 
	 * // System.err.println("myObject.getStats: clazz = " + clazz.getName());
	 * 
	 * String className = clazz.getName(); myObjectStats stats = (myObjectStats)
	 * tableStats.get(className); if (stats == null) { stats = new
	 * myObjectStats(className); tableStats.put(className, stats); }
	 * 
	 * return(stats); }
	 * 
	 * public String getHtmlDate(Date date) { if(date == null) return "&nbsp;";
	 * 
	 * SimpleDateFormat formatter = new SimpleDateFormat ("MM/dd/yyyy"); return
	 * formatter.format(date); }
	 * 
	 * public String getHtmlTimeDate(Date date) { if(date == null) return
	 * "&nbsp;";
	 * 
	 * SimpleDateFormat formatter = new SimpleDateFormat (
	 * "MM/dd/yyyy hh:mm:ss a"); return formatter.format(date);
	 * 
	 * }
	 */
}
