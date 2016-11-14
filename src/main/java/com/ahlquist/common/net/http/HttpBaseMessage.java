/*   @(#)  HttpBaseMessage.java  2002-02-04
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

public interface HttpBaseMessage {
	public static final String CRLF = "\r\n";
	public static final String HTTP_VERSION10 = "HTTP/1.0";
	public static final String HTTP_VERSION11 = "HTTP/1.1";

	// header type
	public static final String HEADER_KEY_REFERER = "Referer";
	public static final String HEADER_KEY_CONNECTION = "Connection";
	public static final String HEADER_KEY_CONTENT_TYPE = "Content-type";
	public static final String HEADER_KEY_ACCEPT_ENCODING = "Accept-Encoding";
	public static final String HEADER_KEY_ACCEPT_LANGUAGE = "Accept-Language";
	public static final String HEADER_KEY_ACCEPT_CHARSET = "Accept-Charset";
	public static final String HEADER_KEY_USER_AGENT = "User-Agent";
	public static final String HEADER_KEY_ACCEPT = "Accept";
	public static final String HEADER_KEY_HOST = "Host";

	// Used in POSTs and Server Responses
	public static final String HEADER_KEY_CONTENT_LENGTH = "Content-length";

	// Used in the the Client Reqest
	public static final String HEADER_KEY_COOKIE = "Cookie";

	// Used in the Server Response
	public static final String HEADER_KEY_SET_COOKIE = "Set-Cookie";
	public static final String HEADER_KEY_SERVER = "Server";
	public static final String HEADER_KEY_CACHE_CONTROL = "Cache-Control";
	public static final String HEADER_KEY_PRAGMA = "PRAGMA";
	public static final String HEADER_KEY_TRANSFER_ENCODING = "Transfer_Encoding";
	public static final String HEADER_KEY_DATE = "Date";

	public String HEADER_KEYS[] = { HEADER_KEY_REFERER, // 0
			HEADER_KEY_CONNECTION, // 1
			HEADER_KEY_USER_AGENT, // 2
			HEADER_KEY_HOST, // 3
			HEADER_KEY_ACCEPT, // 4
			HEADER_KEY_ACCEPT_ENCODING, // 5
			HEADER_KEY_ACCEPT_LANGUAGE, // 6
			HEADER_KEY_ACCEPT_CHARSET, // 7

			HEADER_KEY_COOKIE, // 8
			HEADER_KEY_CONTENT_TYPE, // 9
			HEADER_KEY_CONTENT_LENGTH, // 10
			HEADER_KEY_SET_COOKIE, HEADER_KEY_SERVER, HEADER_KEY_CACHE_CONTROL, HEADER_KEY_PRAGMA,
			HEADER_KEY_TRANSFER_ENCODING, HEADER_KEY_DATE };

	public static final String CONTENT_TYPE_TEXT_HTML = "text/html";
	public static final String CONTENT_TYPE_IMAGE_GIF = "image/gif";
	public static final String CONTENT_TYPE_IMAGE_JPG = "image/jpg";
	public static final String CONTENT_TYPE_IMAGE_BITMAP = "image/bitmap";
	public static final String CONTENT_TYPE_URL_ENCODED = "application/x-www-form-urlencoded";
	public static final String CONTENT_TYPE_MULTIPART_FORM_DATA = "mailtipart/form-data";

	/*
	 * from:
	 * jdk1.5.0_01/docs/guide/javaws/developersguide/downloadservletguide.html#
	 * examples Extension Default MIME type
	 * ------------------------------------------- .jnlp
	 * application/x-java-jnlp-file .jar application/x-java-archive .jardiff
	 * application/x-java-archive-diff
	 * 
	 */

	public static final String CONTENT_TYPE_WEBSTRART = "application/x-java-jnlp-file";
	public static final String CONTENT_TYPE_JAVA_ARCHIVE = "application/x-java-archive";
	public static final String CONTENT_TYPE_JARDIFF = "application/x-java-archive-diff";

	/*
	 * public abstract String getReferer(); public abstract String
	 * getConnection(); public abstract String getUserAgent(); public abstract
	 * String getHost(); public abstract String getAccept(); public abstract
	 * String getCharset(); public abstract String getEncoding(); public
	 * abstract String getLanguage(); public abstract String getCookies();
	 * public abstract String getContentType(); // public abstract String
	 * getContentLength();
	 * 
	 * public abstract void setReferer(String s); public abstract void
	 * setConnection(String s); public abstract void setUserAgent(String s);
	 * public abstract void setHost(String s); public abstract void
	 * setAccept(String s); public abstract void setCharset(String s); public
	 * abstract void setEncoding(String s); public abstract void
	 * setLanguage(String s); // public abstract void setCookies(String s);
	 * public abstract void setContentType(String s); // public abstract void
	 * setContentLength(String s);
	 */
}
