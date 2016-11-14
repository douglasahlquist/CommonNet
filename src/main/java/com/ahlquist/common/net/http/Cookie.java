/*   @(#)  Cookie.java  2002-02-04
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

import org.apache.log4j.Logger;

public class Cookie {
	final static Logger logger = Logger.getLogger(Cookie.class);

	public String name = null;
	public String value = null;
	public String path = "";
	public String domain = "";
	public boolean secure = false;
	public String expire_date = "";

	// Client requests a document, and receives in the response:
	// Set-Cookie: CUSTOMER=COOKIE_MONSTER; path=/; expires=Wednesday,
	// 09-Nov-1=2003 23:12:40 GMT
	// When client requests a URL in path "/" on this server, it sends:
	// Cookie: CUSTOMER=COOKIE_MONSTER

	public Cookie(String name, String value, String path, String expire_date) {
		// future ensure that the combined does not exceed 4kb
		this.name = name;
		this.value = value;
		setPath(path);
		setExpireDate(expire_date);
	}

	public Cookie(String name, String value, String path, String expire_date, String domain, boolean secure) {
		// future ensure that the combined does not exceed 4kb
		this.name = name;
		this.value = value;
		setPath(path);
		setExpireDate(expire_date);
		setDomain(domain);
		setSecure(secure);
	}

	public String getName() {
		return (name);
	}

	public String getValue() {
		return (value);
	}

	public String getPath() {
		return (path);
	}

	public String getDomain() {
		return (domain);
	}

	public boolean getSecure() {
		return (secure);
	}

	public void setPath(String path) {
		if (path == null) {
			this.path = "";
		} else {
			this.path = path;
		}
	}

	public void setExpireDate(String date) {
		if (path == null) {
			this.path = "";
		} /*
			 * else{ this.path=path; }
			 */
	}

	public void setDomain(String domain) {
		if (path == null) {
			this.path = "";
		} /*
			 * else{ this.path=path; }
			 */
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

}
