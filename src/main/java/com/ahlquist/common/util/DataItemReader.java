/*   @(#)  DataItemReader.java  2002-02-04
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

public final class DataItemReader {
	final static Logger logger = Logger.getLogger(DataItemReader.class);
	
	private static ItemMgr m_itemMgr = null;
	public static String HEAD = "test";
	public static String DELIMITER = ".";
	public static String NO_OF_ITEMS = "noOfItems";
	public static String NO_OF_FIELDS = "noOfFields";
	public static String FIELD = "field";

	private boolean DEBUG = true;
	InputStream in = null;

	public DataItemReader(String sFileName) {
		try {
			Properties props = new Properties();
			in = new FileInputStream(sFileName);
			props.load(in);
			parseItem(props);
		} catch (FileNotFoundException ex) {
			logger.error("Error: " + ex);
			in = this.getClass().getResourceAsStream("/" + sFileName);
			// "/" means "use filename as is without prepending package".
		} catch (IOException e) {
			logger.error("Error: " + e);
		} finally {
			Util.close(in);
		}
	}

	private void parseItem(Properties props) {
		// test.noOfItems=1
		final int nNoItems = Integer.parseInt(props.getProperty(HEAD + DELIMITER + NO_OF_ITEMS));
		m_itemMgr = new ItemMgr(nNoItems);

		for (int i = 1; i < nNoItems + 1; i++) {
			// test.item1=person
			String sItemName = props.getProperty(HEAD + DELIMITER + "item" + i);

			logger.debug(":[parseItem]item=" + sItemName + " # items=" + nNoItems);
			parseSubItem(props, sItemName);
		}
		verifyItems();
	}

	private void parseSubItem(Properties props, final String sItemName) {
		int nNoSubItems = Integer.parseInt(props.getProperty(HEAD + DELIMITER + sItemName + DELIMITER + NO_OF_ITEMS));
		int nNoFields = Integer.parseInt(props.getProperty(HEAD + DELIMITER + sItemName + DELIMITER + NO_OF_FIELDS));

		logger.debug(":[parseSubItem] # sub items=" + nNoSubItems + " # fields=" + nNoFields);
		Item item = new Item(sItemName, nNoSubItems, nNoFields);
		m_itemMgr.setItem(sItemName, item);

		int i = 0, j = 0;
		String sField = null;

		List<String> v = new ArrayList<String>(nNoFields);
		for (j = 1; j < nNoFields + 1; j++) {
			sField = props.getProperty(HEAD + DELIMITER + sItemName + DELIMITER + FIELD + j);
			logger.debug(HEAD + DELIMITER + sItemName + DELIMITER + FIELD + j + "=" + sField);
			v.add(sField);
		}

		for (i = 1; i < nNoSubItems + 1; i++) {
			// Item subItem = new Item("", 0, nNoFields);
			List<String> vVal = new ArrayList<String>(nNoFields);
			for (j = 1; j < nNoFields + 1; j++) {
				String sQuery = HEAD + DELIMITER + sItemName + i + DELIMITER + v.get(j - 1);
				String sFieldValue = props.getProperty(sQuery);

				logger.debug(sQuery + "=" + sFieldValue);
				vVal.add(sFieldValue);
			}
			item.addAll(vVal);
		}
	}

	public void verifyItems() {
		debug(":[verifyItems]");
		StringBuilder b = new StringBuilder();
		for (Enumeration _enum = m_itemMgr.getItems(); _enum.hasMoreElements();) {
			Item item = (Item) _enum.nextElement();
			List<String> v = item.get2dVector();
			for (Iterator iter = v.iterator(); iter.hasNext();) {
/*	TODO:DKA			List<String> vSub = iter.next();
				for (Iterator subIter = vSub.iterator(); subIter.hasNext();) {
					b.append ((String) subIter.next());
					b.append(" ");
				}
				b.append("");
				*/
			}
			b.append("--------------------------");
		}
		logger.debug(b.toString());
	}

	public static ItemMgr ItemMgrInstance() {
		return m_itemMgr;
	}

	private void debug(String s) {
		if (DEBUG)
			System.out.println(s);
	}
}