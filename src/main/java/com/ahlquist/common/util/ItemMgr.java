/*   @(#)  ItemMgr.java  2002-02-04
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
 * Title:        OMS
 * Description:  Test
 * Copyright:    Copyright (c) 2001
 * Company:      Ahlquist.com .
 * @author Iven Han
 * @version
 */

import java.util.*;

public class ItemMgr {
	// private Vector m_vItem;
	private HashCache m_hash; // 1 -> item.
	private String m_fields[];

	// public void ItemMgr() { m_vItem = new Vector(); }
	public ItemMgr(final int nNoItems) {
		// m_vItem = new Vector(nNoItems);
		m_hash = new HashCache(nNoItems);
	}

	public void setItem(final String sName, final Object item) {
		System.out.println("setting item named=" + sName);
		m_hash.put(sName, item);
	}

	public Object getItem(final String sName) {
		return m_hash.get(sName);
	}

	public Enumeration getItems() {
		return m_hash.getElements();
	}
	// public void setItem(final int nIndex, Item item) { m_vItem.set(nIndex,
	// item); }
	// public Item getItem(final int nIndex) { return
	// (Item)m_vItem.elementAt(nIndex); }
	// public final Vector getItems() { return m_vItem; }

	public Vector getRemovedItems() {
		/* --todo */ return new Vector();
	}

	public void setRemovedItem(final int nIndex) {
		/* --todo */ }
}