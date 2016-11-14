/*   @(#)  HttpResponse.java  2002-02-04
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
import java.net.Socket;

import org.apache.log4j.Logger;

import com.ahlquist.common.util.ByteBuffer;
import com.ahlquist.common.util.Numbers;
import com.ahlquist.common.util.Parser;

/**
 * HttpResponse is a class encapsulate Http Response information. This is a data
 * object not communication object. Therefore it is safe to expose this object.
 * <br>
 *
 * For Http server, we could construct an empty HttpResponse, populate the
 * response object, deliver to HttpServerConnection to respond a HttpRequest.
 * Or, at Http client, after we send out a HTTP Request, we could construct by
 * listening from an Socket.
 */
public class HttpResponse extends HttpMessage implements Cloneable {

	final static Logger logger = Logger.getLogger(HttpResponse.class);
	protected int responseCode = 0;
	protected String responsePhrase;

	private String charset;

	/**
	 * default constructor
	 */
	protected HttpResponse() {
	}

	/**
	 * Construct a brand new HttpResponse
	 *
	 * @param responseCode
	 * @param responsePhrase
	 */
	public HttpResponse(int responseCode, String responsePhrase) {
		this.responseCode = responseCode;
		this.responsePhrase = responsePhrase;
	}

	/**
	 * Construct a HttpResponse object based on a live socket. <br>
	 * 
	 * @param socket
	 *            a live socket holding a HTTP communication channel
	 */
	HttpResponse(Socket socket) throws IOException {
		HttpInputStream reader = new HttpInputStream(socket);

		try {
			// Read Response and Headers
			String line = reader.readLine();

			if (handleFirstLine(line)) {
				logger.debug(line);
				super.readHeaders(reader);
				super.readBody(reader);
			}
		} finally {
			reader.close();
		}
	}

	/**
	 * clone
	 */
	public Object clone() {
		HttpResponse response = (HttpResponse) super.clone();
		response.responseCode = responseCode;
		response.responsePhrase = responsePhrase;
		response.charset = charset;
		return response;
	}

	/**
	 * handle first line
	 *
	 * @param lineInput
	 *            the first line information of HttpResponse
	 */
	private boolean handleFirstLine(String line) throws InvalidHttpResponseException {
		if (line == null)
			throw new InvalidHttpResponseException("Bad HTTP response: first line null");

		int ich = line.indexOf(' ');
		if (ich == -1)
			throw new InvalidHttpResponseException("Bad HTTP response: " + line);

		setHttpVersion(line.substring(0, ich));

		int ich2 = line.indexOf(' ', ich + 1);
		if (ich2 == -1) {
			// throw new InvalidHttpResponseException("Bad HTTP response: " +
			// line);
			responseCode = Numbers.getInt(line.substring(ich + 1), -1);
		} else {
			responseCode = Numbers.getInt(line.substring(ich + 1, ich2), -1);
			responsePhrase = line.substring(ich2 + 1);
		}
		if (responseCode == -1)
			return false;
		else
			return true;
	}

	/**
	 * Return the Charset of HttpResponse content
	 *
	 * @return String Charset, null if no Charset specified
	 */
	public String getCharset() {
		if (charset == null) {
			String contentType = getHeaderFieldValue(CONTENT_TYPE);
			// Example: ContentType: text/plain; charset="Shift-JIS"
			if (contentType != null) {
				String[] attributes = Parser.getTokenArray(contentType, ';');
				for (int i = 0; i < attributes.length; i++) {
					// Split up in name and value
					int index = attributes[i].indexOf('=');
					// Is this a charset parameter?
					if (index > 0 && ((attributes[i].substring(0, index)).trim()).equalsIgnoreCase("charset")) {
						charset = (attributes[i].substring(index + 1)).trim();
						// Make sure we remove the quotes if it is a quoted
						// string
						if (charset.length() > 0 && charset.charAt(0) == '"')
							charset = charset.substring(1, charset.length() - 1);
						break;
					}
				}
			}
		}
		return charset;
	}

	/**
	 * Return the content type. For example, text/plain. This will not return
	 * extra parameters in "Content-type" header, since you could easily get it
	 * by calling getHeaderFieldValue(HttpMessage.CONTENT_TYPE).
	 *
	 * @return String content type, null if no content type specified
	 */
	public String getContentType() {
		String ctype = getHeaderFieldValue(CONTENT_TYPE);
		if (ctype == null)
			return null;
		int index = ctype.indexOf(";");
		if (index > 0)
			ctype = ctype.substring(0, index);
		return (ctype);
	}

	/**
	 * Return response code
	 *
	 * @return int response code
	 */
	public int getResponseCode() {
		return (responseCode);
	}

	/**
	 * Return HTTP response phrase (optional)
	 * 
	 * @return String
	 */
	String getResponsePhrase() {
		return (responsePhrase);
	}

	/**
	 * Return HttpResponse body as byte array
	 *
	 * @return byte[] body, return null if this is not a valid response or no
	 *         body specified
	 * @exception com.ahlquist.common.net.http.HttpException
	 */
	public byte[] getContentBytes() {
		if (getResponseCode() == HttpResponse.HTTP_NO_CONTENT) {
			return null;
		} else {
			return (getBody());
		}
	}

	/*
	 * Return HttpResponse body as String (convert from default system charset)
	 *
	 * @return String String format of the body. Convert the byte array to
	 * String based either on charset in "content-type" field, or default OS'
	 * charset. Return null if the no content specified. public String
	 * getContentString() { if (getContentBytes() == null) return (null); else {
	 * String content; String cSet = getCharset(); if (cSet != null) { try {
	 * content = new String(getContentBytes(), cSet); return content; } catch
	 * (java.io.UnsupportedEncodingException ex) { //it is ok, since if we
	 * failed to convert to charset //specified in Content type, we will return
	 * a new String //based on default charset Debug.HTTP.println(ex); } }
	 * return (new String(getContentBytes())); } }
	 */

	/**
	 * Return content length defined in "Content-Length" http header
	 *
	 * @return int content length, -1 if no content length specified
	 */
	int getContentLength() {
		return (Integer.parseInt(getHeaderFieldValue("Content-Length"), -1));
	}

	public String toString() {
		StringBuffer buff = new StringBuffer();

		buff.append(getHttpVersion()).append(' ');
		buff.append(responseCode).append(' ');
		buff.append(responsePhrase).append(CRLF);

		String msg = super.toString();
		if (msg.length() != 0)
			buff.append(msg);

		return (buff.toString());
	}

	public byte[] getOutputBytes() {
		StringBuffer buff = new StringBuffer();

		buff.append(getHttpVersion()).append(' ');
		buff.append(responseCode).append(' ');
		buff.append(responsePhrase).append(CRLF);

		ByteBuffer bb = new ByteBuffer(buff.length());
		bb.append(buff.toString().getBytes());
		bb.append(super.getOutputBytes());

		return (bb.getBytes());
	}

	public static final int VALID_RESPONSE = 1;
	public static final int REDIRECTION = 2;
	public static final int TRANSIENT_FAILURE = 3;
	public static final int PERMANENT_FAILURE = 4;

	/**
	 * Check if this response a valid response
	 *
	 * @return boolean true if response code in 2xx, false otherwise
	 */
	public boolean isValid() {
		return (getResponseType() == VALID_RESPONSE);
	}

	/**
	 * Check if this is a redirect response
	 *
	 * @return boolean true if response code in 3xx, false otherwise
	 */
	public boolean isRedirection() {
		return (getResponseType() == REDIRECTION);
	}

	/**
	 * Check if the failure a transient failure
	 *
	 * @return boolean true if failure is caused by timeout or gateway prob
	 */
	public boolean isTransientFailure() {
		return (getResponseType() == TRANSIENT_FAILURE);
	}

	/**
	 * Check if the failure a permanent failure
	 *
	 * @return boolean true if it is a permanent failure, false otherwise
	 */
	public boolean isPermanentFailure() {
		return (getResponseType() == PERMANENT_FAILURE);
	}

	private int getResponseType() {
		switch (getResponseCode()) {

		// 2XX: generally "OK"
		case HTTP_OK:
		case HTTP_CREATED:
		case HTTP_ACCEPTED:
		case HTTP_NOT_AUTHORITATIVE:
		case HTTP_NO_CONTENT:
		case HTTP_RESET:
		case HTTP_PARTIAL_CONTENT:
			return (VALID_RESPONSE);

		// 3XX: relocation/redirect
		case HTTP_MULT_CHOICE:
		case HTTP_MOVED_PERM:
		case HTTP_FOUND:
		case HTTP_SEE_OTHER:
		case HTTP_NOT_MODIFIED: // REVIEW3: is this one correct?
		case HTTP_USE_PROXY:
		case HTTP_MOVED_TEMP:
			return (REDIRECTION);

		// 4XX: client error
		case HTTP_CLIENT_TIMEOUT:
			return (TRANSIENT_FAILURE);

		// 4XX: client error
		case HTTP_BAD_REQUEST:
		case HTTP_UNAUTHORIZED:
		case HTTP_PAYMENT_REQUIRED: // reserved
		case HTTP_FORBIDDEN:
		case HTTP_NOT_FOUND:
		case HTTP_BAD_METHOD:
		case HTTP_NOT_ACCEPTABLE:
		case HTTP_PROXY_AUTH_REQUIRED:
		case HTTP_CONFLICT:
		case HTTP_GONE:
		case HTTP_LENGTH_REQUIRED: // need to add "Content-Length" field
		case HTTP_PRECONDITION_FAILED: // related to headers
		case HTTP_ENTITY_TOO_LARGE: // FUTURE: examine Retry-After header field
		case HTTP_REQ_TOO_LONG: // we probably need to use POST
		case HTTP_UNSUPPORTED_TYPE:
		case HTTP_CANNOT_SATISFY_RANGE:
		case HTTP_EXPECTATION_FAILED: // ??
			return (PERMANENT_FAILURE);

		// 5XX: server error
		case HTTP_SERVICE_UNAVAILABLE: // FUTURE: examine Retry-After header
										// field
		case HTTP_GATEWAY_TIMEOUT: // Note: some servers use 400 or 500 instead!
			return (TRANSIENT_FAILURE);

		// 5XX: server error
		case HTTP_INTERNAL_SERVER_ERROR:
		case HTTP_NOT_IMPLEMENTED:
		case HTTP_BAD_GATEWAY:
		case HTTP_VERSION_NOT_SUPPORTED:
			return (PERMANENT_FAILURE);

		default:
			// new MyError(ErrorTypes.HTTP_UNEXPECTED_RESPONSE, toString())
			// .escalateDiagnostic();
			return (TRANSIENT_FAILURE);
		}
	}

	// HTTP 1.1 standard response codes
	// see RFC 2616 for detailed descriptions

	/*
	 * 1XX: Informational
	 * 
	 * "100" ; Section 10.1.1: Continue "101" ; Section 10.1.2: Switching
	 * Protocols
	 * 
	 * A client MUST be prepared to accept one or more 1xx status responses
	 * prior to a regular response, even if the client does not expect a 100
	 * (Continue) status message. Unexpected 1xx status responses MAY be ignored
	 * by a user agent.
	 */

	/*
	 * 2XX: generally "OK"
	 * 
	 * This class of status code indicates that the client's request was
	 * successfully received, understood, and accepted.
	 * 
	 * Note: is this application, we really only expect 200.
	 */
	public static final int HTTP_OK = 200;
	public static final int HTTP_CREATED = 201;
	public static final int HTTP_ACCEPTED = 202;
	public static final int HTTP_NOT_AUTHORITATIVE = 203;
	public static final int HTTP_NO_CONTENT = 204;
	public static final int HTTP_RESET = 205;
	public static final int HTTP_PARTIAL_CONTENT = 206;

	/*
	 * 3XX: relocation/redirect
	 * 
	 * This class of status codes indicates that further action needs to be
	 * taken by the user agent in order to fulfill the request. The action
	 * required MAY be carried out by the user agent without interaction with
	 * the user if and only if the method used in the second request is GET or
	 * HEAD. A client SHOULD detect infinite redirection loops, since such loops
	 * generate network traffic for each redirection.
	 * 
	 * Note: previous versions of this specification recommended a maximum of
	 * five redirections. Content developers should be aware that there might be
	 * clients that implement such a fixed limitation.
	 */
	public static final int HTTP_MULT_CHOICE = 300;
	public static final int HTTP_MOVED_PERM = 301;
	public static final int HTTP_FOUND = 302;
	public static final int HTTP_SEE_OTHER = 303;
	public static final int HTTP_NOT_MODIFIED = 304;
	public static final int HTTP_USE_PROXY = 305;
	public static final int HTTP_MOVED_TEMP = 307;

	/*
	 * 4XX: client error
	 * 
	 * The 4xx class of status code is intended for cases in which the client
	 * seems to have erred. Except when responding to a HEAD request, the server
	 * SHOULD include an entity containing an explanation of the error
	 * situation, and whether it is a temporary or permanent condition. These
	 * status codes are applicable to any request method. User agents SHOULD
	 * display any included entity to the user.
	 * 
	 * If the client is sending data, a server implementation using TCP SHOULD
	 * be careful to ensure that the client acknowledges receipt of the
	 * packet(s) containing the response, before the server closes the input
	 * connection. If the client continues sending data to the server after the
	 * close, the server's TCP stack will send a reset packet to the client,
	 * which may erase the client's unacknowledged input buffers before they can
	 * be read and interpreted by the HTTP application.
	 */
	public static final int HTTP_BAD_REQUEST = 400;
	public static final int HTTP_UNAUTHORIZED = 401;
	public static final int HTTP_PAYMENT_REQUIRED = 402;
	public static final int HTTP_FORBIDDEN = 403;
	public static final int HTTP_NOT_FOUND = 404;
	public static final int HTTP_BAD_METHOD = 405;
	public static final int HTTP_NOT_ACCEPTABLE = 406;
	public static final int HTTP_PROXY_AUTH_REQUIRED = 407;
	public static final int HTTP_CLIENT_TIMEOUT = 408;
	public static final int HTTP_CONFLICT = 409;
	public static final int HTTP_GONE = 410;
	public static final int HTTP_LENGTH_REQUIRED = 411;
	public static final int HTTP_PRECONDITION_FAILED = 412;
	public static final int HTTP_ENTITY_TOO_LARGE = 413;
	public static final int HTTP_REQ_TOO_LONG = 414;
	public static final int HTTP_UNSUPPORTED_TYPE = 415;
	public static final int HTTP_CANNOT_SATISFY_RANGE = 416;
	public static final int HTTP_EXPECTATION_FAILED = 417;

	/*
	 * 5XX: server error
	 * 
	 * Response status codes beginning with the digit "5" indicate cases in
	 * which the server is aware that it has erred or is incapable of performing
	 * the request. Except when responding to a HEAD request, the server SHOULD
	 * include an entity containing an explanation of the error situation, and
	 * whether it is a temporary or permanent condition. User agents SHOULD
	 * display any included entity to the user. These response codes are
	 * applicable to any request method.
	 * 
	 */
	public static final int HTTP_INTERNAL_SERVER_ERROR = 500;
	public static final int HTTP_NOT_IMPLEMENTED = 501;
	public static final int HTTP_BAD_GATEWAY = 502;
	public static final int HTTP_SERVICE_UNAVAILABLE = 503;
	public static final int HTTP_GATEWAY_TIMEOUT = 504;
	public static final int HTTP_VERSION_NOT_SUPPORTED = 505;

}