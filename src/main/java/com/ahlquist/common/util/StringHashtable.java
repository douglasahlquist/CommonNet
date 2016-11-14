/*   @(#)  StringHashtable.java  2002-02-04
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
 * StringHashtable has a similar interface to java.util.Hashtable but uses
 * String values as keys instead of Objects and ignores case on key operations.
 */
public class StringHashtable  extends MyObject
		implements java.io.Serializable {
	private Hashtable table;

	public StringHashtable() {
		table = new Hashtable();
	}

	public StringHashtable(int initialCapacity) {
		table = new Hashtable(initialCapacity);
	}

	public void clear() {
		table.clear();
	}

	public boolean contains(Object value) {
		return (table.contains(value));
	}

	public boolean containsKey(String key) {
		return (table.containsKey(key.toLowerCase()));
	}

	public Object get(String key) {
		return (table.get(key.toLowerCase()));
	}

	public Enumeration getElements() {
		return (table.elements());
	}

	public Enumeration getKeys() {
		return (table.keys());
	}

	public boolean isEmpty() {
		return (table.isEmpty());
	}

	public Object put(String key, Object value) {
		return (table.put(key.toLowerCase(), value));
	}

	public Object remove(String key) {
		return (table.remove(key.toLowerCase()));
	}

	public int size() {
		return (table.size());
	}

	public String toString() {
		return (table.toString());
	}

	public Object clone() {
		StringHashtable sht = new StringHashtable();
		sht.table = (Hashtable) this.table.clone();
		return (sht);
	}
}
