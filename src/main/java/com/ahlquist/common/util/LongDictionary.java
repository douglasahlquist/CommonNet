/*   @(#)  LongDictionary.java  2002-02-04
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

public class LongDictionary extends MyObject implements Cloneable {
	private LongHashtable table = new LongHashtable();
	private final Object dummy = new Object();

	public LongDictionary() {
	}

	public boolean put(long id) {
		return (table.put(id, dummy) != null);
	}

	public boolean contains(long id) {
		return (table.containsKey(id));
	}

	public boolean remove(long id) {
		return (table.remove(id) != null);
	}

	public int size() {
		return (table.size());
	}

	public boolean isEmpty() {
		return (size() == 0);
	}

	/**
	 * remove from this all elements in ld.
	 * 
	 * @return how many elements were removed from this.
	 */
	public int subtract(LongDictionary ld) {
		int removed = 0;
		long[] ids = ld.table.getKeys();
		for (int i = 0; i < ids.length; i++) {
			if (remove(ids[i]))
				removed++;
		}

		return (removed);
	}

	/**
	 * add to this all elements in ld.
	 * 
	 * @return how many (new) elements were added to this.
	 */
	public int add(LongDictionary ld) {
		int added = 0;
		long[] ids = ld.table.getKeys();
		for (int i = 0; i < ids.length; i++) {
			if (put(ids[i]))
				added++;
		}

		return (added);
	}

	public long[] getElements() {
		return (table.getKeys());
	}

	public String packElements(String separator) {
		StringBuffer sb = new StringBuffer();
		long[] ids = getElements();
		for (int i = 0; i < ids.length; i++) {
			if (i > 0)
				sb.append(separator);
			sb.append(ids[i]);
		}
		return (sb.toString());
	}

	/** @return a VArray of String: each one a separate, packed list */
	public VArray packElements(String separator, int maxElements) {
		VArray results = new VArray();

		long[] ids = getElements();
		StringBuffer sb = new StringBuffer();
		int cElements = 0;
		for (int index = 0; index < ids.length; index++) {
			if (sb.length() > 0) // don't add separator in front of first
									// element
				sb.append(separator);
			sb.append(ids[index]);
			cElements++;

			if (cElements >= maxElements) {
				results.addElement(sb.toString());
				sb = new StringBuffer();
				cElements = 0;
			}
		}
		results.addElement(sb.toString());

		return (results);
	}

	public Object clone() {
		LongDictionary ld = new LongDictionary();
		ld.table = (LongHashtable) this.table.clone();
		return (ld);
	}

	public String toString() {
		return ("{" + packElements(", ") + "}");
	}
}
