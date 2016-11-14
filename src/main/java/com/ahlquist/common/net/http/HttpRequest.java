/*   @(#)  HttpRequest.java  2002-02-04
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
*/

package com.ahlquist.common.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ahlquist.common.util.Numbers;

/**
 * HttpRequest is a class encapsulate HTTP Request information. <br>
 *
 * For HTTP client, we could construct an empty HttpRequest, populate the
 * request object, deliver to HttpConnection to do a HTTP request. Or, at HTTP
 * server, we could construct a HttpRequest object by listening from a live
 * Socket.
 *
 * @see com.ahlquist.common.net.http.HttpMessage
 */
public class HttpRequest extends HttpMessage {
	final static Logger logger = Logger.getLogger(HttpRequest.class);
	// private static final boolean DEBUG = true;
	/** constant for HttpRequest method, only support GET, HEAD */
	public static final String GET = "GET";
	public static final String HEAD = "HEAD";
	public static final String OPTIONS = "OPTIONS";
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";
	public static final String TRACE = "TRACE";
	public static final String CONNECT = "CONNECT";

	private RequestHeader header = null;
	private HttpInputStream reader = null;
	// private String uri;
	// private String method; // currently only supports "GET", "HEAD"
	// private String host; //do I need to handle "host" specially?

	/**
	 * Construct a HttpRequest
	 *
	 * @param uri
	 *            the abspath for URL
	 * @param method
	 *            the Http request method.
	 */
	/*
	 * HttpRequest(String uri, String method) { this.uri = uri; this.method =
	 * method; }
	 */

	/**
	 * Construct a HttpRequest object using a uri
	 *
	 * @param uri
	 *            the abspath for URL
	 */
	/*
	 * HttpRequest(String uri) { this(uri, GET); }
	 */
	/**
	 * Construct a HttpRequest object based on a live socket. <br>
	 * 
	 * @param socket
	 *            a live socket holding a HTTP communication chanel
	 */
	public HttpRequest(Socket socket) throws IOException {
		reader = new HttpInputStream(socket);

		// Read Request and Headers
		String line = "";
		List<String> lines = new ArrayList<String>();
		try {
			while (!(line = reader.readLine()).equals("\r\n")) {
				logger.debug("line=<" + line + ">");
				if (line.length() == 0) {
					break;
				}
				lines.add(line);
			}
			header = new RequestHeader(lines);
			logger.debug("Completed reading headers");

			if ("POST".equalsIgnoreCase(header.method)) {
				// read next line
				super.readBody(reader);

				// check to see if the content-type is multipart/form-data
				String content_type = header.getHeaderParam(HttpBaseMessage.HEADER_KEY_CONTENT_TYPE);
				content_type = content_type.toLowerCase();
				if (content_type.startsWith(HttpBaseMessage.CONTENT_TYPE_MULTIPART_FORM_DATA.toLowerCase())) {
					int content_length = Numbers
							.getInt(header.getHeaderParam(HttpBaseMessage.HEADER_KEY_CONTENT_LENGTH), 0);
					String boundary = header.getHeaderParam(HttpBaseMessage.HEADER_KEY_CONTENT_TYPE);
					// TODO: if()
					// MultipartInputStreamHandler mish
					// = new MultipartInputStreamHandler(reader, boundary,
					// content_length);
				}

				logger.debug("Completed reading body");
			} else {
				logger.debug("No body to read");
			}

		} catch (Exception e) {
			logger.error(e);
		} finally {
			// reader.close(); //close this only after the OutputStream has been
			// closed
		}
	}

	public InputStream getInputStream() {
		return reader.getInputStream();
	}

	public void closeStream() {
		reader.close();
	}

	public RequestHeader getRequestHeader() {
		return header;
	}

	/**
	 * Return method
	 */
	public String getMethod() {
		return header.method;
	}

	/**
	 * Return URL
	 * TODO:DKA Need to implement the other methods
	 */

	public String getUri() {
		StringBuilder b = new StringBuilder();
		switch(header.method.toUpperCase()){
		
		case "GET": {
			b.append(header.resource);
			QueryString query = new QueryString(header.getUriHash());
			b.append(query.toString());
			break;
		} 
		case "POST": {
			b.append(header.resource);
			break;
		}
		
		}
		return b.toString();
	}

	/**
	 * set method for HttpRequest. Only accept "GET" and "HEAD". If you set a
	 * method that Http does not support, you will get bad HttpResponse object
	 *
	 * @param _method
	 *            the method by type to set
	 *
	 */
	void setMethod(String _method) {
		// validate input method? I don't think it is necessary since
		// bad method means bad HttpResponse
		header.method = _method;
	}

	/**
	 * set uri for HttpRequest
	 *
	 */
	/*
	 * void setUri(String uri) { this.uri = uri; }
	 */

	public String toString() {
		// GET abspath HTTP/1.1
		// Host: www.host.com

		StringBuilder b = new StringBuilder();

		b.append(header.method.toUpperCase()).append(' ');
		b.append(this.getUri());
		b.append(' ');
		b.append(getHttpVersion());
		b.append(CRLF);

		if ("POST".equalsIgnoreCase(header.method)) {
			b.append(CRLF);
			QueryString query = new QueryString(header.getUriHash());
			b.append(query.toString());
			b.append(CRLF);
		}

		String msg = super.toString();
		if (msg.length() != 0)
			b.append(msg); // REVIEW: ancestor handled already .append(CRLF);

		return (b.toString());
	}

	/**
	 * return output bytes used to send out
	 */
	public byte[] getOutputBytes() {
		return (this.toString().getBytes());
	}

}