/*   @(#)  CookieJar.java  2002-02-04
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
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * http://home.netscape.com/newsref/std/cookie_spec.html
 */

public class CookieJar {

	final static Logger logger = Logger.getLogger(CookieJar.class);
	// public static final String className =
	// "com.ahlquist.common.net.http.CookieJar";

	@SuppressWarnings("rawtypes")
	private Hashtable hash = new Hashtable();

	public CookieJar() {
		logger.debug("created empty CookieJar");
	}

	@SuppressWarnings("unchecked")
	public CookieJar(String cookies) {
		if (cookies == null)
			return;
		// split appart the Cookies string
		StringTokenizer tok = new StringTokenizer(cookies, ";");
		while (tok.hasMoreElements()) {
			// get the next element
			String temp = (String) tok.nextElement();
			int index = temp.indexOf("=");
			String name = temp.substring(0, index);
			String value = temp.substring(index + 1, temp.length());
			// debug("name=" + name + " value=" + value);

			if (name != null && value != null) {
				name.trim();
				while (name.startsWith(" "))
					name = name.substring(1, name.length());
				value.trim();
				while (value.startsWith(" "))
					value = value.substring(1, value.length());

				// check if the last char of the value is a ';'
				// if it is strip it off
				int _index = value.indexOf(";");
				if (_index != -1) {
					value = value.substring(0, _index - 1);
					logger.debug("; found in value");
				}
				logger.debug("nvpair name=" + name + " value=" + value);
				hash.put(name, value);
			}

		}
	}

	public CookieJar(@SuppressWarnings("rawtypes") Hashtable _hash) {
		this.hash = _hash;
	}

	@SuppressWarnings("unchecked")
	public void addCookie(Cookie cookie) {
		this.hash.put(cookie.getName(), cookie);
	}

	/**
	 * Note: both the name and the value must be none null
	 * 
	 * @deprecated
	 */
	@SuppressWarnings("unchecked")
	public void addCookie(String name, String value, String path, String expire_date) {
		Cookie cookie = new Cookie(name, value, path, expire_date);
		hash.put(name, cookie);
	}

	/**
	 * reads a similar string and saves it as a Cookie CUSTOMER=WILE_E_COYOTE;
	 * path=/; expires=Wednesday, 09-Nov-99 23:12:40 GMT
	 */
	@SuppressWarnings("unchecked")
	public void setCookiesFromHeaderString(String str) {
		String name = "", value = "", expire_date = "", domain = "", path = "";
		boolean secure = false;
		StringTokenizer tok = new StringTokenizer(str, ";");
		while (tok.hasMoreElements()) {
			String whole = tok.nextToken();
			logger.debug("whole=" + whole);
			int index = whole.indexOf("=");
			if (index == -1) { // tet t see if _value is secure
				if (whole.startsWith("secure")) {
					secure = true;
				}
			} else {
				String _name = whole.substring(0, index);
				String _value = whole.substring(index + 1, whole.length());
				while (_name.startsWith(" ")) {
					_name = _name.substring(1);
				}
				_name.trim();
				_value.trim();
				logger.debug("name=" + _name + " value=" + _value);
				if (_name.equals("path")) {
					path = _value;
				} else if (_name.equals("expires")) {
					expire_date = _value;
				} else if (_name.equals("domain")) {
					domain = _value;
				} else { // this must be the cookie name
					name = _name;
					value = _value;
				}
			}
		}

		Cookie cookie = new Cookie(name, value, path, expire_date, domain, secure);
		hash.put(name, cookie);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addCookies(Hashtable cookies) {
		Enumeration _enum = cookies.keys();
		while (_enum.hasMoreElements()) {
			String _name = (String) _enum.nextElement();
			Cookie _value = (Cookie) cookies.get(_name);
			logger.debug("Adding " + _name + "=" + _value);
			hash.put(_name, _value);
		}
	}

	@SuppressWarnings("rawtypes")
	public void addCookies(CookieJar cookies) {
		Hashtable temp = cookies.getHash();
		this.addCookies(temp);
	}

	public Cookie getCookie(String name) {
		return (Cookie) hash.get(name);
	}

	@SuppressWarnings("rawtypes")
	public Hashtable getHash() {
		return hash;
	}

	public void remove(String name) {
		hash.remove(name);
	}

	public int getCount() {
		return hash.size();
	}

	/**
	*
	*/
	@SuppressWarnings("rawtypes")
	public String getRequestNameValueString() {
		String temp = new String();
		int count = 1;
		Enumeration _enum = hash.keys();
		while (_enum.hasMoreElements()) {
			String name = (String) _enum.nextElement();
			Cookie cookie = (Cookie) hash.get(name);
			temp += new String(name + "=" + cookie.getValue());
			if (count < hash.size()) {
				temp += "; ";
			}
			count++;
		}
		logger.debug("Cookie String: " + temp);
		return temp.trim();

	}

}
