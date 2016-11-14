package com.ahlquist.common.net.http;
//package com.ahlquist.common.net.http;

import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

import com.ahlquist.common.util.NVCollection;
import com.ahlquist.common.util.NameValue;
//import java.util.StringTokenizer;
import com.ahlquist.common.util.VArray;

public class RequestHeader {
	final static Logger logger = Logger.getLogger(RequestHeader.class);

	public String method = new String(); // The Request type GET, POST... (only
											// GET is supported)
	public String resource = new String(); // The specific page requested
	private String type = new String();
	public String context = new String();
	// private Hashtable uriParams = new Hashtable(); 
	NVCollection collection = new NVCollection();
	public String target = new String(); // define the target frame
	public String protocol = new String(); // HTTP/1.1
	public VArray headerPairs = new VArray(); // Browser specific NVPairs
	// collection of Cookies
	public Hashtable<String, String> cookies = new Hashtable<String, String>(); 

	private RequestHeader() {
	}

	public RequestHeader(List<String> _lines) throws ProtocolException {
		// Process the First Line
		String firstLine = (String) _lines.get(0);

		parseUri(firstLine);

		// Process the remaining Lines
		for (int i = 1; i < _lines.size(); i++) {
			String line = (String) _lines.get(i);
			logger.debug("next_line =" + line);

			// Search for separating character
			int slice = line.indexOf(':');

			// Error if no separating character
			if (slice == -1) {
				display();
				throw new ProtocolException("Invalid HTTP header: Line" + i + " <" + line + ">");
			} else {
				// Separate at the slice character into name, value
				String name = line.substring(0, slice).trim();
				String value = line.substring(slice + 1).trim();
				headerPairs.addElement(new NameValue(name, value));
			}
		}

	}

	public String getHeaderParam(String _key) {
		if (_key == null)
			return null;
		for (int i = 0; i < headerPairs.size(); i++) {
			String name = ((NameValue) headerPairs.elementAt(i)).getName();
			String value = ((NameValue) headerPairs.elementAt(i)).getValue();
			logger.debug("Name=" + name + " Value=" + value);
			if (_key.equals(name)) {
				return value;
			}
		}
		return null;
	}

	public VArray getHeaderParamKeys() {
		VArray array = new VArray();
		for (int i = 0; i < headerPairs.size(); i++) {
			array.addElement(((NameValue) headerPairs.elementAt(i)).getName());
		}
		return array;
	}

	public String getResource() {
		return (resource);
	}

	public String getResourceType() {
		return (type);
	}

	public String getContext() {
		return (context);
	}

	public String getCookie(String _key) {
		// TODO:
		return ("NOT IMPLEMENTED");
	}

	/**
	 * parse the uri
	 * TODO:DKA remove the logging to improve performance
	 * @param _uri
	 */
	public void parseUri(String _uri) {
		logger.debug("1_uri= " + _uri);
		int fIndex = 0, sIndex = 0, eIndex = 0;

		int index = _uri.lastIndexOf(" ");
		if (index != -1) {

			protocol = _uri.substring(index).trim();
			logger.debug("protocol= " + protocol);
			_uri = _uri.substring(0, index);
			logger.debug("2_uri= " + _uri);

			index = _uri.indexOf(" ");
			method = _uri.substring(0, index);
			logger.debug("method= " + method);
			_uri = _uri.substring(index + 1);
			logger.debug("3_uri= " + _uri);

			// remove target
			if ((fIndex = _uri.indexOf("target=")) != -1) {
				target = _uri.substring(fIndex + "target=".length());
				logger.debug("target= " + target);
				_uri = _uri.substring(0, fIndex - 3);
				logger.debug("4_uri= " + _uri);
			}

		}

		// remove resource
		if ((fIndex = _uri.indexOf("?")) != -1) {
			resource = _uri.substring(0, fIndex);
			logger.debug("resource= " + resource);
			_uri = _uri.substring(fIndex);
			logger.debug("5_uri= " + _uri);
		} else {
			resource = _uri.substring(0);
			logger.debug("resource= " + resource);
			// _uri = _uri.substring(fIndex);
			logger.debug("5_uri= " + _uri);
		}

		// now get the Context from the resource
		fIndex = resource.indexOf("/");
		int lIndex = -1;
		if (resource.length() > 1) {
			lIndex = resource.indexOf("/", fIndex + 1);
		}
		logger.debug("Getting Context");
		logger.debug("fIndex=" + fIndex + " lIndex=" + lIndex);

		if (fIndex > -1 && lIndex > fIndex) {
			context = resource.substring(0, lIndex);
			resource = resource.substring(lIndex);
		} else {
			context = "/";
			resource = resource.substring(0);
		}
		logger.debug("Context=" + context);

		// Get the TYPE from the resource
		if ((fIndex = resource.lastIndexOf(".")) != -1) {
			type = resource.substring(fIndex + 1);
		} else {
			type = "";
		}
		logger.debug("type=" + type);

		String delimeter = "?";
		// int i = 0;

		String key = null;
		String value = null;

		while ((fIndex = _uri.indexOf(delimeter)) != -1) {
			logger.debug("6_uri= " + _uri);
			if ((eIndex = _uri.indexOf("=")) != -1) {

				// didn't find second "&"
				if ((sIndex = _uri.indexOf("&", eIndex)) == -1) {
					logger.debug("didn't find later '&'");
					key = _uri.substring(fIndex + 1, eIndex);
					logger.debug("7_uri.length() == eIndex ," + _uri.length() + " " + eIndex);
					if (_uri.length() == eIndex) {
						value = "";
					} else {
						value = _uri.substring(eIndex + 1);
					}
					collection.put(key, value);
					logger.debug("NO MORE STRING TO PARSE");
					_uri = "";
					// yes we did find a later "&"
				} else {
					logger.debug("found later '&'");
					key = _uri.substring(1, eIndex);
					value = _uri.substring(eIndex + 1, sIndex);
					_uri = _uri.substring(sIndex);
				}
			}
			logger.debug("key= " + key + " value= " + value);
			addUriParam(key, value);
			if (delimeter.equals("?")) {
				delimeter = "&";
			}
		}
	}

	public void addUriParam(String _key, String _value) {
		collection.put(_key, _value);
	}

	/**
	 * Get the array of values associated with a single key used when the
	 * <select> tag has the multiple option e.g.
	 * &param_one=some_value&param_one=another_value
	 * 
	 * @param _key
	 * @return VArray the array of name values pairs combinations in the uri
	 */
	public VArray getUriParamArray(String _key) {
		return (VArray) collection.get(_key);
	}

	public Hashtable getUriHash() {
		return collection.getHash();
	}

	public String getUriParam(String _key) {
		logger.debug("Getting " + _key);
		Object obj = collection.get(_key);
		if (obj == null) {
			return null;
		}

		if (obj instanceof VArray) {
			VArray array = (VArray) obj;
			return (String) array.firstElement();
		} else if (obj instanceof String) {
			logger.debug("Something is goofy here");
			return "";
		}
		return "";
	}

	/**
	 * @deprecated
	 * @param _key
	 * @param _value
	 */
	public void addParamKeyValue(String _key, String _value) {
		addUriParam(_key, _value);
	}

	public void setMethod(String _method) {
		this.method = _method;
	}

	public void setResource(String _resource) {
		this.resource = _resource;
	}

	public void setContext(String _context) {
		this.context = _context;
	}

	// public void setUriParams(Hashtable _hash) {this.uriParams = _hash;}
	public void setTarget(String _target) {
		this.target = _target;
	}

	public void setProtocol(String _protocol) {
		this.protocol = _protocol;
	}

	public void setHeaderPairs(VArray _headerPairs) {
		this.headerPairs = _headerPairs;
	}

	public void setCookies(Hashtable _cookies) {
		this.cookies = _cookies;
	}

	public void display() {
		logger.debug("Method:      " + method);
		logger.debug("Resource:    " + resource);
		logger.debug("Context:     " + context);

		logger.debug("URI NVPairs: ");
		Enumeration keys = collection.keys();
		while (keys.hasMoreElements()) {
			String name = (String) keys.nextElement();
			VArray values = (VArray) collection.get(name);
			logger.debug("Values size=" + values.size());
			for (int i = 0; i < values.size(); i++) {
				String value = (String) values.elementAt(i);
				logger.debug("Name=" + name + " Value" + (i + 1) + "=" + value);
			}

		}
		logger.debug("Target:      " + target);

		logger.debug("Protocol:    " + protocol);

		logger.debug("Header NVPairs:");
		for (int i = 0; i < headerPairs.size(); i++) {
			String name = ((NameValue) headerPairs.elementAt(i)).getName();
			String value = ((NameValue) headerPairs.elementAt(i)).getValue();
			logger.debug("Name=" + name + " Value=" + value);
		}

		System.out.println("Cookies:");
		keys = cookies.keys();
		if (keys != null) {
			while (keys.hasMoreElements()) {
				String name = (String) keys.nextElement();
				String value = (String) cookies.get(name);
				System.out.println("Name=" + name + " Value=" + value);
			}
		}
	}

	public static void main(String[] args) throws ProtocolException {

		// String a1= "GET /index.php%20target=_self HTTP/1.1";
		// String a2= "POST /test.html HTTP/1.1";
		// String a3= "GET
		// /test.html?method=method&one=one&two=&three=three&four=%20target=_self
		// HTTP/1.1";
		// String a3= "GET
		// /test.html?method=method&one=one&two=&three=three&four=four1&four=four2%20target=_self
		// HTTP/1.1";
		String a4 = "GET /testcases/dom1/dom-html/HTMLAnchorElement/HTMLAnchorElement_rev.html HTTP/1.1";
		// 0123456789012345678901234567890123456789012345678901234567890123456789
		// 1 2 3 4 5 6
		String b1 = "Accept: image/gif, image/x-xbitmap, image/jpeg, application/msword, application/x-shockwave-flash, */*";
		String b2 = "Referer: http://127.0.0.1:8081";
		String b3 = "Accept-Language: en-us";
		String b4 = "Accept-Encoding: gzip, deflate";
		// String b5= "User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT
		// 5.1; .NET CLR 1.0.3705)";
		// String b6= "Host: 127.0.0.1:2000";
		// String b7= "Connection: Keep-Alive";

		List<String> lines = new ArrayList<String>();
		// lines.addElement(a1);
		// lines.addElement(a2);
		lines.add(a4);
		lines.add(b1);
		lines.add(b2);
		lines.add(b3);
		lines.add(b4);
		// lines.addElement(b5);
		// lines.addElement(b6);
		// lines.addElement(b7);

		RequestHeader h = null;
		// try{
		h = new RequestHeader(lines);
		System.out.println("Context:  " + h.getContext());
		System.out.println("Resourse: " + h.getResource());
		System.out.println("Type:     " + h.getResourceType());

		// }catch(Exception e){}
		h.display();
	}
}
