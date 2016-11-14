/*   @(#)  QueryString.java  2002-02-04
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

package com.ahlquist.common.net.http;

import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import com.ahlquist.common.util.NVCollection;
//import com.ahlquist.common.net.http.URLEncoder;
//TODO:DKA replace VArray with List 
import com.ahlquist.common.util.VArray;

public class QueryString {

	final static Logger logger = Logger.getLogger(QueryString.class);
	// private static final String delimeter = "\r\n";
	private static final String delimeter = "&";
	private NVCollection collection = null;

	public QueryString() {
	}

	public QueryString(NVCollection _collection) {
		collection = _collection;
	}

	public QueryString(String _name, String _value) {
		collection = new NVCollection();
		collection.put(_name, _value);
	}

	public QueryString(Hashtable<String,String> _hash) {
		collection = new NVCollection();
		Enumeration<String> keys = _hash.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = (String) _hash.get(key);
			collection.put(key, value);
		}
	}

	public void add(String _name, String _value) {
		collection.put(_name, _value);
	}

	/**
	 * This method appends and replaces values where there the name was added
	 * previously
	 * 
	 * @param _hash
	 *            - the hashtable to append or replace
	 */
	public void add(Hashtable<String,String> _hash) {
		Enumeration<String> keys = _hash.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = (String) _hash.get(key);
			collection.put(key, value);
		}
	}

	public String getPostEncodedString() {
		String find[] = { "/", ":", " " };
		String replace[] = { "%2F", "%3A", "+" };
		// String encode = getString();

		String query = new String();
		int hashSize = collection.size();
		int paramHandled = 0;
		if (hashSize > 0) {
			Enumeration _enum = collection.keys();

			while (_enum.hasMoreElements()) {
				String name = (String) _enum.nextElement();
				VArray values = (VArray) collection.get(name);
				for (int i = 0; i < values.size(); i++) {
					String value = (String) values.elementAt(i);
					value.replace(' ', '+');
					if ((paramHandled < hashSize) && (paramHandled > 0)) {
						query += delimeter;
					}
					query += new String(name + "=" + value);
					paramHandled++;
				}
			}
		}
		// debug("query=" + query);

		for (int i = 0; i < find.length; i++) {
			query = com.ahlquist.common.util.MyString.replace(query, find[i], replace[i]);
		}
		return query;
	}

	public String getString() {
		String query = new String();
		int hashSize = collection.size();
		int paramHandled = 0;
		if (hashSize > 0) {
			Enumeration _enum = collection.keys();
			String delimeter = "&";
			while (_enum.hasMoreElements()) {
				String name = (String) _enum.nextElement();
				VArray values = (VArray) collection.get(name);
				for (int i = 0; i < values.size(); i++) {
					String value = (String) values.elementAt(i);
					value.replace(' ', '+');
					if ((paramHandled < hashSize) && (paramHandled > 0)) {
						query += delimeter;
					}
					query += new String(name + "=" + value);
					paramHandled++;
				}

			}
		}
		// debug("query=" + query);
		return query;
	}

	private void debug(String s) {
		System.out.println("QueryString: " + s);
	}
}