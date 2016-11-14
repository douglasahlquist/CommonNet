/*   @(#)  LongHashtable.java  2002-02-04
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

import java.util.*;

/**
 * LongHashtable has a similar interface to java.util.Hashtable but uses long
 * values as keys instead of Objects.
 *
 * @author Douglas Ahlquist
 */
public class LongHashtable extends MyObject implements Cloneable, java.io.Serializable {
	// FUTURE: consider replacing the internal Hashtable with something
	// more efficient...

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Hashtable<Long, Object> table;

	public LongHashtable() {
		table = new Hashtable<Long, Object>();
	}

	public LongHashtable(int initialCapacity) {
		table = new Hashtable<Long, Object>(initialCapacity);
	}

	public void clear() {
		table.clear();
	}

	public boolean contains(Object value) {
		return (table.contains(value));
	}

	public boolean containsKey(long key) {
		return (table.containsKey(new Long(key)));
	}

	public Object get(long key) {
		return (table.get(new Long(key)));
	}

	public Enumeration<Object> getElements() {
		return (table.elements());
	}

	public long[] getKeys() {
		long[] elements = new long[table.size()];
		Enumeration<Long> e = table.keys();
		for (int i = 0; i < elements.length; i++) {
			// Debug._assert(e.hasMoreElements());
			elements[i] = ((Long) e.nextElement()).longValue();
		}
		return (elements);
	}

	public boolean isEmpty() {
		return (table.isEmpty());
	}

	public Object put(long key, Object value) {
		return (table.put(new Long(key), value));
	}

	public Object remove(long key) {
		return (table.remove(new Long(key)));
	}

	public int size() {
		return (table.size());
	}

	public String toString() {
		return (table.toString());
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		LongHashtable lht = new LongHashtable();
		lht.table = (Hashtable<Long, Object>) this.table.clone();
		return (lht);
	}
}
