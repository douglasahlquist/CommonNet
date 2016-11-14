/*   @(#)  HashCache.java  2002-02-04
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

/*
 * @(#).java    1.0 01/02/24
 * Copyright 1999-2001 Ahlquist Consulting & Idaho Consulting, Inc. All Rights Reserved.
 *
 * Ahlquist Consulting & Idaho Consulting grants you ("Licensee") a non-exclusive, 
 * royalty free, license to use and modify.  The redistribution of this software in 
 * source and binary code in any form is strictly prohibited without written permission.
 * Apon written approval licensee shall provide notice of
 *    i) this copyright notice and license appear on all copies of the software; and 
 *   ii) Licensee does not utilize the software in a manner which is disparaging 
 *       to Ahlquist Consulting & Idaho Consulting.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. AHLQUIST CONSULTING, IDAHO CONSULTING AND 
 * ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE 
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. 
 * IN NO EVENT WILL THE ABOVE OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, 
 * PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER  * CAUSED AND REGARDLESS 
 * OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO 
 * USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */

package com.ahlquist.common.util;

import java.util.Hashtable;
import org.apache.log4j.Logger;
import java.util.Enumeration;

/**
 * HashCache provides fast lookup of Objects by their unique key. It has an
 * uppper-limit to protect memory abuse. When full, the oldest or least recently
 * used objects are dropped from the cache.
 *
 * @author Douglas Ahlquist
 */
public class HashCache extends MyObject {
	final static Logger logger = Logger.getLogger(HashCache.class);
	@SuppressWarnings("rawtypes")
	private Hashtable table; // holds hashkey entries
	private int limit; // max number of entries allowed

	private HashCacheEntry head = null; // most fresh
	private HashCacheEntry tail = null; // most stale

	/** construct a new cache with the specified size */
	@SuppressWarnings("rawtypes")
	public HashCache(int limit) {
		this.limit = limit;
		table = new Hashtable(); // use default initial size
	}

	/**
	 * Fetch the object associated with the specified key. Returns null if
	 * object not present in cache The object, if found, will now be considered
	 * "freshest".
	 */
	public synchronized Object get(Object key) {
		HashCacheEntry entry = (HashCacheEntry) table.get(key);
		if (entry != null) {
			// move this entry to the front: it is now the most fresh
			removeFromList(entry);
			insertInFront(entry);
			return (entry.value);
		}
		return (null);
	}

	/**
	 * Add the specified object to the cache using the specified key
	 *
	 * If the key already exists, the old value is replaced with the new and the
	 * old object is returned.
	 *
	 * If the cache is full, the least recently used object is pushed out and
	 * returned.
	 *
	 * @return Object that was removed (may be null)
	 */
	@SuppressWarnings("unchecked")
	public synchronized Object put(Object key, Object value) {
		// handle limit == 0; disable HashCache
		if (isFull() && isEmpty()) {
			// the limit must be zero
			return value;
		}

		// First remove the old object with same key if it exists
		Object removedObject = remove(key);

		if (isFull() && tail != null) {
			if (removedObject == null) {
				logger.error("Cache is still full even though we removed an object!");
			}
			table.remove(tail.key); // remove the most stale item
			removedObject = tail.value;
			removeFromList(tail);
		}

		HashCacheEntry entry = new HashCacheEntry(key, value);
		table.put(key, entry);
		insertInFront(entry);

		return (removedObject); // may be null
	}

	public boolean containsKey(Object key) {
		return (table.containsKey(key));
	}

	public synchronized Object remove(Object key) {
		HashCacheEntry entry = (HashCacheEntry) table.remove(key);
		if (entry != null) {
			removeFromList(entry);
			return (entry.value);
		}

		return (null);
	}

	public synchronized void clear() {
		table.clear();
		head = tail = null;
	}

	public int size() {
		return (table.size());
	}

	public boolean isEmpty() {
		return (table.isEmpty());
	}

	public boolean isFull() {
		return (size() == limit);
	}

	/**
	 * Alter the size limit of the cache.
	 *
	 * If smaller, the "oldest" items are pushed out...
	 *
	 */
	public synchronized void setLimit(int newLimit) {
		if (limit > newLimit) {
			while (size() > newLimit && tail != null) {
				table.remove(tail.key);
				removeFromList(tail);
			}
		}

		limit = newLimit;
	}

	@SuppressWarnings("rawtypes")
	public java.util.Enumeration getElements() {
		return new HashCacheEnumerator(head, false);
	}

	@SuppressWarnings("rawtypes")
	public java.util.Enumeration getKeys() {
		return new HashCacheEnumerator(head, true);
	}

	/** place entry at the front of the linked list */
	private void insertInFront(HashCacheEntry entry) {
		entry.prev = null;
		entry.next = head;
		if (head != null)
			head.prev = entry;
		head = entry;
		if (tail == null)
			tail = entry;
	}

	/** remove entry from the linked list */
	private void removeFromList(HashCacheEntry entry) {
		if (entry.prev != null)
			entry.prev.next = entry.next;
		if (entry.next != null)
			entry.next.prev = entry.prev;
		if (entry == head)
			head = entry.next;
		if (entry == tail)
			tail = entry.prev;
	}

}

class HashCacheEntry {
	HashCacheEntry prev = null;
	HashCacheEntry next = null;
	Object key;
	Object value;

	HashCacheEntry(Object key, Object value) {
		this.key = key;
		this.value = value;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return (true);
		if (obj == null)
			return (false);
		if (this.getClass() != obj.getClass())
			return (false);

		HashCacheEntry entry = (HashCacheEntry) obj;

		return (this.key == entry.key && this.value.equals(entry.value));
	}
}

/**
 * A hashtable enumerator class. This class should remain opaque to the client.
 * It will use the Enumeration interface.
 */
@SuppressWarnings("rawtypes")
class HashCacheEnumerator implements Enumeration {
	boolean keys;
	HashCacheEntry current;

	HashCacheEnumerator(HashCacheEntry head, boolean keys) {
		this.keys = keys;
		this.current = head;
	}

	public boolean hasMoreElements() {
		return (current != null);
	}

	public Object nextElement() {
		if (current == null)
			throw new java.util.NoSuchElementException("HashCacheEnumerator");

		Object element = keys ? current.key : current.value;
		current = current.next;
		return element;
	}
}
