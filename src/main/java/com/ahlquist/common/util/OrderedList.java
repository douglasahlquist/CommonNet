/*   @(#)  OrderedList.java  2002-02-04
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
 * An OrderedList to keep items in ascending order. List is synchronized for
 * insertion and removal.
 */
public class OrderedList extends MyObject {
	private OrdItem head;
	private int cItem;

	public OrderedList() {
		head = null;
		cItem = 0;
	}

	/** @return the number of items in the ordered list */
	public final int itemCount() {
		return (cItem);
	}

	/** @return the object in the head of the list */
	public synchronized final MyObject getListHeadObject() {
		if (head != null)
			return head.o;
		else
			return null;
	}

	/** @return the key in the head of the list */
	public synchronized final long getListHeadKey() {
		if (head != null)
			return head.key;
		else
			return 0;
	}

	/** @return remove the list head */
	public synchronized final void removeListHead() {
		OrdItem item = head;
		head = head.next;
		cItem--;
	}

	/** Insert a new object and key into the ordered list */
	public synchronized final void insert(long key, MyObject o) {
		OrdItem newItem = new OrdItem(key, o);

		if (itemCount() != 0) {
			Debug._assert(head != null);
			OrdItem cur = head;
			OrdItem prev = head;

			cItem++;
			while (cur != null) {
				// first find the place to insert
				Debug._assert(cur.key != 0);
				if (cur.key < key) {
					prev = cur;
					cur = cur.next;
				} else {
					break;
				}
			}
			// do the insert
			newItem.next = cur;
			if (head == cur) {
				head = newItem;
			} else {
				prev.next = newItem;
			}
			return;
		} else {
			cItem++;
			Debug._assert(head == null);
			head = newItem;
		}
	}

	/**
	 * OrdItem is a private class decribing an entry in the ordered list. List
	 * is ordered on the "key". "Object o" is the data corresponding to each
	 * key.
	 */
	private class OrdItem extends MyObject {
		long key;
		MyObject o;
		OrdItem next;

		public OrdItem(long itemKey, MyObject itemObject) {
			key = itemKey;
			o = itemObject;
			next = null;
		}
	}
}
