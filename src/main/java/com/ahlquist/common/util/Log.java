/*   @(#)  Log.java  2002-02-04
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

package com.ahlquist.common.util;

import java.io.*;
import java.text.SimpleDateFormat;

/**
 * This class provides a simple text logging service. <br>
 * Usage: Log.println("message"); <br>
 * Log output is also sent to the current Runtime where it may use it for
 * display, etc.
 */
public final class Log {
	private static Log log = null;
	public static final String logDirectory = ResourceLocator.getLogDir();

	/**
	 * @deprecated
	 */
	public static void init(String logFilePath) {
		log = new Log(logFilePath);
	}

	public static boolean fEnableConsoleOutput = true;
	public static boolean fEnableTraceOutput = true;

	/** Arranges to write all text to console. */
	public static final void enableConsoleOutput() {
		fEnableConsoleOutput = true;
	}

	/** Stops writing text to console. */
	public static final void disableConsoleOutput() {
		fEnableConsoleOutput = false;
	}

	public static final void enableTraceOutput(boolean enabled) {
		fEnableTraceOutput = enabled;
	}

	/** initialized in constructor; set to null in writeText upon failure. */
	private String logFileName = null;

	/**
	 * the log file path to use if init() is not called to provide one before
	 * the first invocation of println().
	 */
	// private static final String defaultLogFilePath = Version.productInitials;
	// // + ".log"; // hard coded
	private static final String defaultLogFilePath = "EmailPlatform"; // +
																		// ".log";
																		// //
																		// hard
																		// coded

	private Log(String logFileName) {
		this.logFileName = logFileName;

		StringWriter temp = new StringWriter();
		PrintWriter writer = new PrintWriter(temp);
		writer.println();
		writer.println();
		writer.println("*********************************************************");
		writer.println(Version.productName + " started at " + new java.util.Date());
		writer.println("   - Version     : " + Version.version);
		writer.println();
		writer.println("Platform Information:");
		writer.println("   - OS              : " + System.getProperty("os.name"));
		writer.println("   - Version         : " + System.getProperty("os.version"));
		writer.println("   - Platform        : " + System.getProperty("os.arch"));
		writer.println("   - JDK Version     : " + System.getProperty("java.version"));
		writer.println("   - JDK Vendor      : " + System.getProperty("java.vendor"));
		writer.println("   - Java Home       : " + System.getProperty("java.home"));
		writer.println("   - Java VM version : " + System.getProperty("java.vm.version"));
		writer.println("   - Java VM vendor  : " + System.getProperty("java.vm.vendor"));
		writer.println("   - Java VM name    : " + System.getProperty("java.vm.name"));
		writer.println("   - Java Compiler   : " + System.getProperty("java.compiler"));
		writer.println();
		writer.println();
		/*
		 * writer.println("System Properties:"); java.util.Properties jvmProps =
		 * System.getProperties(); jvmProps.list(writer); writer.println();
		 * writer.println();
		 */

		writer.close();

		writeText(temp.toString());
	}

	/*
	 * private Log() { this.logFileName = logFileName; StringWriter temp = new
	 * StringWriter(); PrintWriter writer = new PrintWriter(temp);
	 * writer.println(); writer.close();
	 * 
	 * writeText(temp.toString()); }
	 */
	public static void print(String text) {
		print(text, true);
	}

	public static void println(String text) {
		println(text, true);
	}

	public static void printNoTrace(String text) {
		println(text, false);
	}

	private static SimpleDateFormat dfTS = new SimpleDateFormat("HH:mm:ss.SS");

	private synchronized static void println(String text, boolean doTraceOutput) {
		if (log == null) // when init has not been called
			log = new Log(defaultLogFilePath); // REVIEW: race here.

		text = dfTS.format(MyTime.getCurrentDate()) + " " + text;

		log.writeText(text + Util.newLine);

		if (fEnableConsoleOutput)
			System.out.println(text);

		// Give the runtime a chance to do something else with this info.
		// Eg, ServerRuntime sends it in the TraceUI.
		if (fEnableTraceOutput && doTraceOutput)
			MyRuntime.getInstance().appendLog(text);
	}

	/**
	 * Do not terminate this Text with a NewLine
	 */
	private synchronized static void print(String text, boolean doTraceOutput) {
		if (log == null) // when init has not been called
			log = new Log(defaultLogFilePath); // REVIEW: race here.

		text = dfTS.format(MyTime.getCurrentDate()) + " " + text;

		log.writeText(text);

		if (fEnableConsoleOutput)
			System.out.println(text);

		// Give the runtime a chance to do something else with this info.
		// Eg, ServerRuntime sends it in the TraceUI.
		if (fEnableTraceOutput && doTraceOutput)
			MyRuntime.getInstance().appendLog(text);
	}

	public static void println() {
		println("");
	}

	/** write text to log file */
	private static SimpleDateFormat dfLog = new SimpleDateFormat("MMddyy");

	private synchronized void writeText(String text) {
		if (logFileName != null) {
			OutputStream out = null;
			try {
				// REVIEW: this could be more efficient...
				File logDir = new File(logDirectory);
				logDir.mkdir();
				String path = logFileName + "_" + dfLog.format(MyTime.getCurrentDate()) + ".log";
				File logFile = new File(logDir, path);

				// NOTE: Do NOT use MyOutputStream for Log (since
				// MyOutputStream uses Log).
				out = new FileOutputStream(logFile.getCanonicalPath(), true); // true=append
				out.write(text.getBytes());

			} catch (IOException e) {
				System.err.println("Failed to write to Log file " + logFileName + " IOException: " + e);
				// logFileName = null; // Disable future attempts to write.
			} finally {
				// don't call Util.close() here.
				try {
					if (out != null)
						out.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
