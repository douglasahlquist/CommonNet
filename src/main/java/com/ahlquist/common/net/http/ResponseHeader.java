package com.ahlquist.common.net.http;

import java.util.Date;
import java.util.Enumeration;

/**
HTTP/1.1 200 OK
Date: Fri, 31 Dec 1999 23:59:59 GMT
Content-Type: text/plain
Content-Length: 42
some-footer: some-value
another-footer: another-value

abcdefghijklmnoprstuvwxyz1234567890abcdef

*/

/**
 * 
 * Some valid content-types
 image/gif
 image/x-xbitmap,
 image/jpeg
 image/pjpeg

 application/vnd.ms-excel
 application/vnd.ms-powerpoint,
 application/msword,
 application/x-shockwave-flash,
 *//*  where the slash is a single slash
	
	* 
	*/

import java.util.Hashtable;

import org.apache.log4j.Logger;

import com.ahlquist.common.util.MyTime;
//import com.ahlquist.common.util.Numbers;
import com.ahlquist.common.util.NameValue;

/**
 * The Response Header
 * TODO:DKA Need to update this for HTTP2.0 per RFC-7540
 */
public class ResponseHeader {

	final static Logger logger = Logger.getLogger(ResponseHeader.class);
	public static final String HTTP1_1 = "HTTP/1.1";
	public static final String HTTP1_0 = "HTTP/1.0";
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String CONTENT_TYPE = "Content-Type";

	public static final String IMAGE_GIF = "image/gif";
	public static final String IMAGE_X_XBITMAP = "image/x-xbitmap";
	public static final String IMAGE_JPG = "image/jpeg";
	public static final String IMAGE_PJPEG = "image/pjpeg";

	public static final String PLAIN_TEXT = "plain/text";
	public static final String TEXT_HTML = "text/html";
	public static final String TEXT_CSS = "text/css";

	public static final String MS_EXCEL = "application/vnd.ms-excel";
	public static final String MS_POWERPOINT = "application/vnd.ms-powerpoint";
	public static final String MSWORD = "application/msword";
	public static final String SHOCKWAVE_FLASH = "application/x-shockwave-flash";
	public static final String JAVASCRIPT = "application/x-javascript";

	private int response_code;
	private String protocol;
	private String response_description;
	private int content_length = 0;
	private String date;
	private String content_type;

	private Hashtable<String, String> headerPairs = null;

	public ResponseHeader(int _response_code, String _response_description) {
		headerPairs = new Hashtable<String, String>();
		this.protocol = ResponseHeader.HTTP1_1;
		this.response_code = _response_code;
		this.response_description = _response_description;
	}

	public void setResponseCode(int _response_code) {
		this.response_code = _response_code;
	}

	public void setProtocol(String _protocol) {
		this.protocol = _protocol;
	}

	public void setResponseDescription(String _response_description) {
		this.response_description = _response_description;
	}

	public void setContentLength(int _content_length) {
		content_length = _content_length;
	}

	public void setDate(Date _date) {
		this.date = MyTime.getRfc822Date(_date);
	}

	public void setContentType(String _content_type) {
		// TODO: expand the list of types
		if ("html".equalsIgnoreCase(_content_type) || "htm".equalsIgnoreCase(_content_type)
				|| "jtp".equalsIgnoreCase(_content_type)) {
			this.content_type = ResponseHeader.TEXT_HTML;
		} else if ("css".equalsIgnoreCase(_content_type)) {
			this.content_type = ResponseHeader.TEXT_CSS;
		} else if ("txt".equalsIgnoreCase(_content_type)) {
			this.content_type = ResponseHeader.PLAIN_TEXT;
		} else if ("gif".equalsIgnoreCase(_content_type)) {
			this.content_type = ResponseHeader.IMAGE_GIF;
		} else if ("jpg".equalsIgnoreCase(_content_type)) {
			this.content_type = ResponseHeader.IMAGE_JPG;
		} else if ("bmp".equalsIgnoreCase(_content_type)) {
			this.content_type = ResponseHeader.IMAGE_X_XBITMAP;
		} else if ("js".equalsIgnoreCase(_content_type)) {
			this.content_type = ResponseHeader.JAVASCRIPT;
		} else {
			// TODO --- For now this defaults to plain/text
			this.content_type = ResponseHeader.PLAIN_TEXT;
		}

	}

	public int getResponseCode() {
		return (response_code);
	}

	public String getProtocol() {
		return (protocol);
	}

	public String getResponseDescription() {
		return (response_description);
	}

	public int getContentLength() {
		return (content_length);
	}

	public String getDate() {
		return (date);
	}

	public String getContentType() {
		return (content_type);
	}

	public void addHeaderPair(String _key, String _value) {
		this.headerPairs.put(_key, _value);
	}

	public NameValue[] getHeaderPairs() {
		NameValue[] pairs = new NameValue[headerPairs.size()];
		int index = 0;
		Enumeration<String> keys = headerPairs.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = (String) headerPairs.get(key);
			pairs[index++] = new NameValue(key, value);
		}
		return pairs;
	}

	public StringBuilder getHeader() {
		StringBuilder sbuf = new StringBuilder();
		sbuf.append(protocol + " " + response_code + " " + response_description + "\r\n");
		//Maybe I should do something else here, but Why Not
		sbuf.append("Server: ahlquist\r\n");  
		sbuf.append("Content-Type: " + this.getContentType() + "\r\n");
		sbuf.append("Accept-Ranges: bytes\r\n");
		sbuf.append("Content-Length: " + this.getContentLength() + "\r\n");

		//int index = 0;
		Enumeration<String> keys = headerPairs.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = (String) headerPairs.get(key);
			sbuf.append(key + ": " + value);
		}
		// finally append a blank line
		sbuf.append("\r\n");
		return sbuf;
	}

}