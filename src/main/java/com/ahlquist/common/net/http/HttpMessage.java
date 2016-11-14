/*   @(#)  HttpMessage.java  2002-02-04
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

//package com.ahlquist.common.net.http;
package com.ahlquist.common.net.http;

import java.io.IOException;
import java.util.ArrayList;
//import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

import com.ahlquist.common.util.ByteBuffer;
import com.ahlquist.common.util.Numbers;


/**
 * HttpMessage is the ancestor of HttpRequest (request from client to server)
 * and HttpResponse (responses from server to client). <br>
 *
 * It has all the common interface to store/deal with Http header fields and
 * read body code. This is an abstract class so you could not initiate it.
 *
 */
public abstract class HttpMessage implements Cloneable {

	final static Logger logger = Logger.getLogger(HttpMessage.class);
	public static final String CRLF = "\r\n";
	public static final String HTTP_VERSION10 = "HTTP/1.0";
	public static final String HTTP_VERSION11 = "HTTP/1.1";
	//TODO:DKA 2.0 added as a final though not implemented per 
	//   https://tools.ietf.org/html/rfc7540
	public static final String HTTP_VERSION20 = "HTTP/2.0";
	

	//TODO:DKA private Hashtable header = new Hashtable();

	private List<String> headerFields;

	// used for searching
	private List<String> headerFieldsLC;
	private List<String> headerValues;
	private byte[] body;
	private String httpVersion = HTTP_VERSION11; // default

	// by default, persistent connected for 1.1
	private boolean closeConnection = true;

	// however, we need to handle this
	private boolean chunkedTransferEncoding = false;

	protected HttpMessage() {
		init();
	}

	protected List<String> getHeaderFields() {
		return (headerFields);
	}

	private void init() {
		//header = new Hashtable();

		headerFields = new ArrayList<String>();
		headerFieldsLC = new ArrayList<String>(); // used for searching //
													// searching
		headerValues = new ArrayList<String>();
		closeConnection = true; // by default, persistent connection
								// for 1.1
		chunkedTransferEncoding = false; // however, we need to handle this

	}

	/**
	 * Add header
	 *
	 * @param field
	 *            the name of the field
	 * @param value
	 *            the value of the field
	 */
	public void addHeader(String field, String value) {
		String fieldUC = field.toUpperCase();
		if (fieldUC.equals(CONNECTION)) {
			closeConnection = value.equalsIgnoreCase("close");
		}

		if (fieldUC.equals(TRANSFER_ENCODING)) {
			chunkedTransferEncoding = value.equalsIgnoreCase("chunked");
			if (chunkedTransferEncoding)
				return; // for chunked transfer encoding
						// decode when read body, don't
						// save the header
		}

		headerFields.add(field);
		headerFieldsLC.add(field.toLowerCase());
		headerValues.add(value);
	}

	/**
	 * Clone itself
	 */
	public Object clone() {
		HttpMessage msg = null;
		try {
			msg = (HttpMessage) super.clone();
			msg.httpVersion = httpVersion;
			msg.closeConnection = closeConnection;
			msg.chunkedTransferEncoding = chunkedTransferEncoding;
			msg.headerFields.addAll(new ArrayList<>(headerFields));
			msg.headerFieldsLC.addAll(new ArrayList<>(headerFieldsLC));
			msg.headerValues.addAll(new ArrayList<>(headerValues));
			if (body != null) {
				msg.body = new byte[body.length];
				System.arraycopy(body, 0, msg.body, 0, body.length);
			}
		} catch (CloneNotSupportedException ex) {
			logger.error("Exception", ex);
		}
		return (Object) msg;
	}

	/**
	 * debug method, return all header fields
	 *
	 * @return String
	 */
	public String dumpHeaders() {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < headerFields.size(); i++) {
			String field = (String) headerFields.get(i);
			String value = (String) headerValues.get(i);

			if (i > 0)
				buff.append('\n');

			buff.append(field).append(": ").append(value);
		}
		return (buff.toString());
	}

	/**
	 * Return the body byte array
	 *
	 * @return byte[] body byte array, return null if there's no body
	 */
	public byte[] getBody() {
		return (body);
	}

	/**
	 * Return HttpVersion
	 *
	 * @return String
	 */
	public String getHttpVersion() {
		return (httpVersion);
	}

	/**
	 * Return a field value. For multiple field value, this will only return one
	 * of the values.
	 *
	 * @return String field value, null if there's no value for a field
	 */
	public String getHeaderFieldValue(String field) {
		int index = headerFieldsLC.indexOf(field.toLowerCase());
		return (index == -1 ? null : (String) headerValues.get(index));
	}

	/**
	 * Return HTTP header values as a String array.
	 *
	 * @param headerName
	 *            the name of a header.
	 * @return String[] the header value, return empty array if no header found.
	 */
	public String[] getHeaderFieldValues(String headerName) {
		String lcHeader = headerName.toLowerCase();
		List<String> values = new ArrayList<String>();
		for (int i = 0; i < headerFieldsLC.size(); i++) {
			String field = (String) headerFieldsLC.get(i);
			if (field.equals(lcHeader)) {
				values.add(headerValues.get(i));
			}
		}
		String[] returnValues = new String[values.size()];
		for (int i = 0; i < values.size(); i++) {
			returnValues[i] = (String) values.get(i);
		}

		return returnValues;
	}

	/**
	 * package method. Read http entity body from a buffer reader
	 * 
	 * @param reader
	 *            the stream which contains the body
	 */
	protected void readBody(HttpInputStream reader) throws IOException {
		// int cAttempts = 0;
		ByteBuffer sb = new ByteBuffer();
		if (this.chunkedTransferEncoding) {
			// read and decode chunked transfer encoding
			// int length = 0;
			int chunkSize = readChunkSize(reader.readLine());
			System.out.println("chunk size " + chunkSize);
			// Debug.HTTP.println("chunk size " + chunkSize);
			// read chunk-size, chunk-ext (if any) and CRLF
			while (chunkSize > 0) {
				byte[] readChunk = new byte[chunkSize + 2]; // CRLF
				int totalReaded = reader.read(readChunk, 0, chunkSize + 2);
				int left = chunkSize + 2 - totalReaded;
				while (left > 0) {
					int readed = reader.read(readChunk, totalReaded, left);
					totalReaded += readed;
					left = chunkSize + 2 - totalReaded;
				}
				sb.append(readChunk, 0, chunkSize); // strip off the ending
													// CRLF
				// read chunk-data and CRLF
				// append chunk-data to entity-body
				// length := length + chunk-size
				chunkSize = readChunkSize(reader.readLine());
			}
			// read entity-header
			// while (entity-header not empty) {
			// append entity-header to existing header fields
			// read entity-header
			// }
			// Content-Length := length
		} else {
			System.out.println("Chuck size = 1024");
			int cchChunk = 1024;
			byte[] ach = new byte[cchChunk];

			int cch;
			while ((cch = reader.read(ach, 0, cchChunk)) != -1) {
				System.out.println(cch);
				sb.append(ach, 0, cch);
				System.out.println(sb.toString());
			}
			System.out.println("End While");
		}
		this.body = sb.getBytes();
	}

	private int readChunkSize(String line) {
		if (line == null)
			return 0;

		line = line.trim();
		int size = 0;
		int index = line.indexOf(';');
		if (index == -1) {
			size = Numbers.getInt(line, 16, 0);
		} else {
			size = Numbers.getInt(line.substring(0, index), 16, 0);
		}

		return size;
	}

	/**
	 * package method. Read header from a buffer reader
	 *
	 * @param reader
	 *            the stream to read the header from
	 */
	protected void readHeaders(HttpInputStream reader) throws IOException {
		String line;
		// everything should be header
		while ((line = reader.readLine()).length() != 0) {
			int ichC = line.indexOf(':');
			if (ichC != -1) {
				String field = line.substring(0, ichC);
				String value = line.substring(ichC + 1);
				addHeader(field.trim(), value.trim());
				System.out.println("field: " + field + " Value: " + value);
			}
		}
	}

	/**
	 * Set the body of this HttpMessage. Body is a byte array.
	 *
	 * @param body
	 *            a byte[] which contains the data for the body
	 */
	protected void setBody(byte[] body) {
		this.body = body;
	}

	/**
	 * Set HttpVersion information. The version(s) we support is defined by
	 * HTTP_VERSIONxx constant(s).
	 *
	 * @param version
	 *            the version information
	 * @return boolean
	 */
	public boolean setHttpVersion(String version) {
		if (version.equals(HTTP_VERSION10) || version.equals(HTTP_VERSION11)) {
			this.httpVersion = version;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Return a String representation of this object.
	 *
	 * @return String
	 */
	public String toString() {
		StringBuffer buff = new StringBuffer();

		for (int i = 0; i < headerFields.size(); i++) {
			buff.append((String) headerFields.get(i));
			buff.append(": ");
			buff.append((String) headerValues.get(i));
			buff.append(CRLF);
		}
		// an extra line for HTTP end of headers
		buff.append(CRLF);

		if (this.body != null && body.length > 0)
			// REVIEW: i18n
			buff.append(new String(this.body));

		return (buff.toString());
	}

	/**
	 * Return a byte array representation of this object.
	 * 
	 * @return byte[]
	 */
	protected byte[] getOutputBytes() {
		StringBuilder buff = new StringBuilder();
		for (int i = 0; i < headerFields.size(); i++) {
			buff.append((String) headerFields.get(i));
			buff.append(": ");
			buff.append((String) headerValues.get(i));
			buff.append(CRLF);
		}
		buff.append(CRLF);

		ByteBuffer bb = new ByteBuffer(buff.length());
		// REVIEW: i18n--check if HTTP header is all ASCII chars
		// per RFC 2068, HTTP header is 8 bit chars
		bb.append(buff.toString().getBytes());
		if (this.body != null && body.length > 0)
			bb.append(this.body);

		return (bb.getBytes());
	}

	/**
	 * public constants for HTTP headers HTTP header is case insisitive. We use
	 * all upper case for easy comparision.
	 */
	/** general header field. We only expose used constant */
	protected static final String CACHE_CONTROL = "CACHE-CONTROL";
	public static final String CONNECTION = "CONNECTION";
	public static final String DATE = "DATE";
	protected static final String PRAGMA = "PRAGMA";
	protected static final String TRAILER = "TRAILER";
	public static final String TRANSFER_ENCODING = "TRANSFER-ENCODING";
	protected static final String UPGRADE = "UPGRADE";
	protected static final String VIA = "VIA";
	protected static final String WARNING = "WARNING";

	/** HTTP Request header */
	protected static final String ACCEPT = "ACCEPT";
	protected static final String ACCEPT_CHARSET = "ACCEPT-CHARSET";
	protected static final String ACCEPT_ENCODING = "ACCEPT-ENCODING";
	protected static final String ACCEPT_LANGUAGE = "ACCEPT-LANGUAGE";
	protected static final String AUTHORIZATION = "AUTHORIZATION";
	protected static final String FROM = "FROM";
	public static final String HOST = "HOST";
	protected static final String IF_MODIFIED_SINCE = "IF-MODIFIED-SINCE";
	protected static final String IF_MATCH = "IF_MATCH";
	protected static final String IF_NONE_MATCH = "IF-NONE-MATCH";
	protected static final String IF_RANGE = "IF-RANGE";
	protected static final String IF_UNMODIFIED_SINCE = "IF-UNMODIFIED-SINCE";
	protected static final String MAX_FORWARDS = "MAX-FORWARDS";
	protected static final String PROXY_AUTHORIZATION = "PROXY-AUTHORIZATION";
	protected static final String RANGE = "RANGE";
	protected static final String REFERER = "REFERER";
	protected static final String USER_AGENT = "USER-AGENT";

	/** HTTP Response header */
	protected static final String AGE = "AGE";
	public static final String LOCATION = "LOCATION";
	protected static final String PROXY_AUTHENTICATE = "PROXY-AUTHENTICATE";
	protected static final String PUBLIC = "PUBLIC";
	protected static final String RETRY_AFTER = "RETRY-AFTER";
	protected static final String SERVER = "SERVER";
	protected static final String VARY = "VARY";
	protected static final String WWW_AUTHENTICATE = "WWW-AUTHENTICATE";

	/** HTTP Entity header */
	protected static final String ALLOW = "ALLOW";
	protected static final String CONTENT_BASE = "CONTENT-BASE";
	public static final String CONTENT_ENCODING = "CONTENT-ENCODING";
	protected static final String CONTENT_LANGUAGE = "CONTENT-LANGUAGE";
	public static final String CONTENT_LENGTH = "CONTENT-LENGTH";
	protected static final String CONTENT_LOCATION = "CONTENT-LOCATION";
	protected static final String CONTENT_MD5 = "CONTENT-MD5";
	protected static final String CONTENT_RANGE = "CONTENT-RANGE";
	public static final String CONTENT_TYPE = "CONTENT-TYPE";
	protected static final String ETAG = "ETAG";
	public static final String EXPIRES = "EXPIRES";
	public static final String LAST_MODIFIED = "LAST-MODIFIED";
	protected static final String EXTENSION_HEADER = "EXTENSION-HEADER";
}