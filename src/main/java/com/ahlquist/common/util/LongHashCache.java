/*   @(#)  LongHashCache.java  2002-02-04
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

/**
 * LongHashCache provides fast lookup of Objects by their unique key (long). It
 * has an uppper-limit to protect memory abuse. When full, the oldest or least
 * recently used objects are dropped from the cache.
 */
public class LongHashCache extends MyObject implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	HashCache table;

	/** construct a new cache with the specified size */
	public LongHashCache(int limit) {
		table = new HashCache(limit);
	}

	/**
	 * Fetch the object associated with the specified key. Returns null if
	 * object not present in cache The object, if found, will now be considered
	 * "freshest".
	 */
	public synchronized Object get(long key) {
		return (table.get(new Long(key)));
	}

	/**
	 * Add the specified object to the cache using the specified key
	 *
	 * If the cache is full, the least recently used object is pushed out.
	 */
	public synchronized void put(long key, Object value) {
		table.put(new Long(key), value);
	}

	public boolean containsKey(long key) {
		return (table.containsKey(new Long(key)));
	}

	public Object remove(long key) {
		return (table.remove(new Long(key)));
	}

	public void clear() {
		table.clear();
	}

	public int size() {
		return (table.size());
	}

	public boolean isEmpty() {
		return (table.isEmpty());
	}

	public boolean isFull() {
		return (table.isFull());
	}

	public synchronized void setLimit(int limit) {
		table.setLimit(limit);
	}

	// FUTURE: implement toString and clone
}
