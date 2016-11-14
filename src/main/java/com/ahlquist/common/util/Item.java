/*   @(#)  Item.java  2002-02-04
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

import java.util.ArrayList;
import java.util.List;

public class Item extends Object {
	private int m_nNoSubItems = 0;
	private int m_nNoFields = 0;
	private List<String> m_vFields = null; // vector of field values vector.

	private List<String> m_vFieldNames = null;
	private List<String> m_vSubItems = null;
	private String m_sName = null;

	public Item(final String sItemName, final int nNoSubItems, final int nNoFields) {
		setName(sItemName);
		setNoSubItems(nNoSubItems);
		setNoFields(nNoFields);
		m_vFields = new ArrayList<String>(nNoSubItems);

		m_vFieldNames = new ArrayList<String>(nNoFields);
		m_vSubItems = new ArrayList<String>(nNoSubItems);
		int i=0;
		//for (i = 0; i < nNoFields; i++) { m_vFieldNames.add(new TBField()); }
		for (i = 0; i < nNoSubItems; i++) { 
			// /* suppose one level */
			//TODO:DKA m_vSubItems.add(new Item(0,0));
		}
		
	}

	public void addFields(final List<String> v) {
		m_vFields.addAll(v);
	}

	public void addField(final String obj) {
		m_vFieldNames.add(obj);
	}

	public void addSubItem(final String obj) {
		m_vSubItems.add(obj);
	}

	// <accessors>
	public final int getNoSubItems() {
		return m_nNoSubItems;
	}

	public void setNoSubItems(final int nNoSubItems) {
		m_nNoSubItems = nNoSubItems;
	}

	public final int getNoFields() {
		return m_nNoFields;
	}

	public void setNoFields(final int nNoFields) {
		m_nNoFields = nNoFields;
	}

	public final List<String> getFieldNames() {
		return m_vFieldNames;
	}

	public final String getFieldName(final int nIndex) {
		return m_vFieldNames.get(nIndex);
	}

	public void setFields(final List<String> vFields) {
		//TODO: DKA fix clone m_vFieldNames = vFields.clone();
	}

	public void setField(final int nIndex, final String obj) {
		m_vFieldNames.set(nIndex, obj);
	}

	public final List<String> getSubItems() {
		return m_vSubItems;
	}

	public final Object getSubItem(final int nIndex) {
		return m_vSubItems.get(nIndex);
	}

	public void setSubItem(final int nIndex, final String obj) {
		m_vSubItems.set(nIndex, obj);
	}

	public List<String> get2dVector() {
		return m_vFields;
	}

	public final String getName() {
		return m_sName;
	}

	public void setName(final String sName) {
		m_sName = sName;
	}
	// </accessors>

	public void addAll(List<String> vVal) {
		// TODO Auto-generated method stub
		
	}
}