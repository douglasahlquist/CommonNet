
package com.ahlquist.common.util;

import java.util.Properties;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
//import java.util.ResourceBundle;
//import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;

import org.apache.log4j.Logger;

/**
 * This class is a Properties object that is applet-aware, meaning that it can
 * better deal with the security restriction of running inside of an applet.
 * Base on the current security restrictions, it will either load all system
 * prop or only the sub-set that is allowed by the web browser. Since the class
 * will hold all system system properties, there should be no reason to call
 * System.getProperty() any longer
 */

public class SafeProperties {
	final static Logger logger = Logger.getLogger(SafeProperties.class);
	

	private List<String> FILE_NAMES = new ArrayList<String>();
	private java.util.Properties properties = null;
	private String NODENAME = "NODENAME"; // host name
	private String NODEADDRESS = "NODEADDRESS"; // host address

	private static String START_CONST = "${";
	private static String END_CONST = "}";
	private static final int MAX_SUBST_DEPTH = 10;

	public SafeProperties(String[] filenames) throws FileNotFoundException, IOException {
		for (int i = 0; i < filenames.length; i++) {
			FILE_NAMES.add(filenames[i]);
		}
		START_CONST = "${";
		END_CONST = "}";
		load();
	}

	/**
	 * Constructor. Has the side effect of reading the system properties
	 * 
	 * @param fileName
	 *            the properties file to open
	 **/
	public SafeProperties(String fileName) throws FileNotFoundException, IOException {
		FILE_NAMES.add(fileName);
		START_CONST = "${";
		END_CONST = "}";
		load();
	}

	public SafeProperties(String _fileName, String _start_const, String _end_const)
			throws FileNotFoundException, IOException {
		this.FILE_NAMES.add(_fileName);
		SafeProperties.START_CONST = _start_const;
		SafeProperties.END_CONST = _end_const;
		load();

	}

	private void load() throws FileNotFoundException, IOException {
		this.properties = new java.util.Properties();
		if (isSandBox()) {
			loadSafeProperties();
		} else {
			loadCompanyProperties();
			loadAllProperties();
		}
		loadHostInfo();
		loadGeneralInfo();
	}

	/**
	 * Stores the value String so that is can be found using the String key
	 * later on.
	 * 
	 * @param key
	 *            String to use as a search kep
	 * @param value
	 *            String to use as the properties value
	 **/
	public void setProperty(String key, String value) {
		properties.put(key, value);
	}

	/**
	 * Fetches the value associated with the given key. If no matching key is
	 * found, null is return.
	 * 
	 * @param key
	 *            String to look for
	 * @return the string associated with the key, or null if the key isn't
	 *         found
	 **/
	public String getProperty(String key) {
		String value = getProperty(key, 1);
		return (value);
	}

	/**
	 * Fetches the value associated with the given key. If no matching key is
	 * found, the default value is returned.
	 * 
	 * @param key
	 *            string to look for
	 * @param defaultValue
	 *            the string to use if not value is returned
	 * @return String associated with the key, or the deafult value if none is
	 *         associated
	 **/
	public String getProperty(String key, String defaultValue) {
		String value = getProperty(key, 1);
		if (value == null) {
			return (defaultValue);
		}
		return (value);
	}

	private String getProperty(String key, int level) {
		if (level > MAX_SUBST_DEPTH)
			return ("**** NO VALUE FOUND ****");
		String value = properties.getProperty(key);
		if (value != null) {
			TbPropertyValueString tbvs = new TbPropertyValueString(value, SafeProperties.START_CONST,
					SafeProperties.END_CONST);
			while (tbvs.hasMoreSubElements()) {
				String constName = tbvs.getFirstConstName();
				String propValue = getProperty(constName, level + 1);

				String newStr = tbvs.replaceFirstConstValue(constName, propValue);
				logger.debug("newStr=" + newStr);
				tbvs = new TbPropertyValueString(newStr, SafeProperties.START_CONST, SafeProperties.END_CONST);
				value = newStr;
			}
		}
		return (value);
	}

	/**
	 * Prints the contents of the object of the specified writer.
	 * 
	 * @param out
	 *            The PrintWriter to push the content to.
	 **/
	public void list(PrintWriter out) {
		properties.list(out);
	}

	private void loadGeneralInfo() {
	}

	/**
	 * Attempts to determine if we are running inside the protective secrurity
	 * sandbox of the browser.
	 * 
	 * @return true if we're in the browser and false if not
	 **/
	public boolean isSandBox() {
		boolean restricted = false;
		try {
			System.getProperty("user.name");
		} catch (java.security.AccessControlException e1) {
			restricted = true;
		} catch (java.lang.SecurityException e2) {
			restricted = true;
		}
		return (restricted);
	}

	/**
	 * This method will rad only the Systems properties that are Visible to the
	 * applets running in the browser.
	 **/
	private void loadSafeProperties() {
		for (int i = 0; i < SAFE_KEYS.length; i++) {
			String key = SAFE_KEYS[i];
			String value = System.getProperty(key);
			properties.put(key, value);
		}
	}

	/**
	 * This method will load all the system properties
	 **/
	public void loadAllProperties() {
		// debug("loadAllProperties()");
		Enumeration<Object> keys = System.getProperties().keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = System.getProperty(key);
			if (value == null) {
				value = "null";
			}
			// debug("key=" + key + " value=" + value);
			properties.put(key, value);
		}
	}

	/**
	 * This method loads in properties from each named properties file. Note:
	 * because the basis of the properties file is a hashtable any values found
	 * in property file[n+1] overwrites the name/value pair on property file[n]
	 **/
	private void loadCompanyProperties() throws FileNotFoundException, IOException {
		// debug("loadCompanyProperties()");
		for (int i = 0; i < FILE_NAMES.size(); i++) {
			Properties tempProp = new Properties();
			FileInputStream fin = new FileInputStream((String) FILE_NAMES.get(i));
			tempProp.load(fin);

			Enumeration<Object> e = tempProp.keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = tempProp.getProperty(key);
				// debug("key=" + key + " value=" + value);
				if (value == null) {
					value = "null";
				}
				properties.put(key, value);
			}
		}
	}

	/**
	 * Queries the system for host name and address and palces that information
	 * into the properties object
	 **/
	private void loadHostInfo() {
		try {
			InetAddress localhost = InetAddress.getLocalHost();
			String hostName = localhost.getHostName();
			String hostAddress = localhost.getHostAddress();
			properties.put(NODENAME, hostName);
			properties.put(NODEADDRESS, hostAddress);
		} catch (java.net.UnknownHostException e) {
			// shoudn't happen but I can't log the exception because
			// this object is supposed to be lean enough to run in an applet
			e.printStackTrace();
		}
	}

	/**
	 * This method calculates server information and loads the info into the
	 * properties object.
	 **/
	private void loadGenratedInformation() {
        //TODO: DKA
	}

	/**
	 * Creates a string representation of the object.
	 * 
	 * @return the string representation of all the name value pairs in the
	 *         SafeProperties object
	 **/
	public String toString() {
		String newLine = "\r\n";
		StringBuilder buffer = new StringBuilder();
		Enumeration<Object> e = properties.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = properties.getProperty(key);
			buffer.append(key);
			buffer.append(" : ");
			buffer.append(value);
			buffer.append(newLine);
		}
		return (buffer.toString());
	}

	/**
	 * These are the key that the browser are allowing to be read. This was
	 * determined by examining the java.policy file that comes with the 1.2 JVM
	 **/
	private static final String[] SAFE_KEYS = { "java.version", "java.vendor", "java.vendor.url", "java.class.version",
			"os.name", "os.arch", "file.separator", "path.separator", "line.separator", "java.specification.version",
			"java.specification.vendor", "java.specification.name" };


	/******************************************
	 * Inner class
	 */
	public class TbPropertyValueString {
		
		final Logger log = Logger.getLogger(TbPropertyValueString.class);
		private String START_CONST;
		private String END_CONST;
		public String value;

		public TbPropertyValueString(String value, String startConst, String endConst) {
			this.value = value;
			this.END_CONST = endConst;
			this.START_CONST = startConst;
		}

		private int getFirstIndexOfEndConst() {
			return (value.indexOf(END_CONST));
		}

		private int getLastIndexOfStartConst() {
			int li = getFirstIndexOfEndConst();
			if (li != -1) {
				String temp = value.substring(0, li - 1);
				return temp.lastIndexOf(START_CONST);
			} else {
				return (-1);
			}

		}

		public boolean hasMoreSubElements() {
			int li = getFirstIndexOfEndConst();
			int si = getLastIndexOfStartConst();
			if (si < li) {
				return (true);
			} else {
				return (false);
			}
		}

		public String getFirstConstName() {
			int li = getFirstIndexOfEndConst();
			int si = getLastIndexOfStartConst();
			if (si < li) {
				String sConstName = value.substring(si + 2, li);
				log.debug("firstConstName=" + sConstName);
				return (sConstName);
			} else {
				return (null);
			}

		}

		public String replaceFirstConstValue(String constName, String replacement) {
			log.debug("REPLACING " + constName + " with " + replacement);
			int li = getFirstIndexOfEndConst();
			int si = getLastIndexOfStartConst();
			String newStr = MyString.replaceFirstMatchingPortion(value, new String(START_CONST + constName + END_CONST),
					replacement);
			log.debug("newStr=" + newStr);
			return (newStr);
		}



	}

	/******************************************
	 * main
	 */
	public static void main(String[] args) {
		try {
			String[] names = { "D:\\Work\\mail\\database.properties", "D:\\Work\\mail\\database2.properties" };

			SafeProperties props = new SafeProperties(names);
			String key = "SYSTEM_USER";

			System.out.println(key + "=" + props.getProperty(key));
			System.out.println(props.toString());
			System.out.println("DEFAULT_URL=" + props.getProperty("DEFAULT_URL") + "\n\n");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}