/*   @(#)  Debug.java  2002-02-04
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

/*
 * @(#).java    1.0 01/02/24
 * Copyright 1999-2001 Ahlquist Consulting & Idaho Consulting, Inc. All Rights Reserved.
 *
 * Ahlquist Consulting & Idaho Consulting grants you ("Licensee") a non-exclusive, 
 * royalty free, license to use and modify.  The redistribution of this software in 
 * source and binary code in any form is strictly prohibited without written permission.
 * Apon written approval licensee shall provide notice of
 *    i) this copyright notice and license appear on all copies of the software; and 
 *   ii) Licensee does not utilize the software in a manner which is disparaging 
 *       to Ahlquist Consulting & Idaho Consulting.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. AHLQUIST CONSULTING, IDAHO CONSULTING AND 
 * ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE 
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. 
 * IN NO EVENT WILL THE ABOVE OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, 
 * PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER  * CAUSED AND REGARDLESS 
 * OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO 
 * USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */

package com.ahlquist.common.util;

import org.apache.log4j.Logger;

/**
 * Debug contains various methods to aid in the debugging process.
 * <p>
 *
 * Each component should used it own static instance to segregate the the debug
 * output.
 * <p>
 *
 * For instance, use (for Test Harness): <br>
 * <blockquote><code>
 *        Debug.QA.println("your message here.");
 *        Debug.QA.printStackTrace("your message here");
 *
 *        if (Debug.QA.verbose) {
 *            // your special debug code block here that, for instance, walks an
 *            // array printing each item ...
 *        }
 * </code></blockquote>
 *
 * NOTE: It is neither necessary nor desirable to place debug print statements
 * inside an 'if verbose' block. Use "if verbose" blocks only when assert or
 * println statements require other helper statements that we should be run only
 * under debug. Debug.??.println and Debug._assert statements will be
 * conditionalized automatically by a pre-processor prior to final builds.
 * <p>
 * 
 * @author Douglas Ahlquist
 */
public final class Debug // All methods are final implicitly.
{

	public static final int DEBUG_OFF = 0;
	public static final int DEBUG_NORMAL = 1;
	public static final int DEBUG_VERBOSE = 2;
	public static final int DEBUG_VERY_VERBOSE = 3;

	/** Master debug flag. This flag overrides all others in the negative. */
	public final static boolean fDebug = true;

	/**
	 * if enabled, assert() statements are executed. Otherwise, they are
	 * ignored.
	 */
	public static boolean fAssert = true & fDebug;

	/**
	 * if enabled, assertValid() statements are executed (where invoked) to
	 * validate entire objects.
	 * <p>
	 * 
	 * Background: <br>
	 * <blockquote> Complex objects should implement the method <blockquote>
	 * <code>
	 *            public void assertValid() </code></blockquote> This method
	 * should validate its internal state by calling <code>Debug._assert</code>
	 * or <code>assertValid()</code> on its members.
	 *
	 * <code>assertValid()</code> should do nothing and return immediately if
	 * this flag is false. </blockquote>
	 */
	public final static boolean fAssertValid = false & fDebug;

	/**
	 * if enabled, memory allocations are tracked by MyObject. Otherwise, they
	 * are ignored.
	 */
	public final static boolean fMemory = true & fDebug;

	public static final String stackTrace = "StackTrace";
	static boolean fPrintStackTrace = true;

	/** name or abbreviation of a component */
	private String component = null;

	/** name to display in UI */
	private String prettyName = null;

	/**
	 * if False, then calls to print/println/printStackTrace will be ignored.
	 * Defaults to false if no entry is found in "debug.properties".
	 */
	// REVIEW: I changed it to private, and got tons of compiler errors. Don't
	// have time to fix it now. Maybe fix it during code review - Bill.
	public boolean verbose = false;

	/** An array to keep all debug modules */
	private static VArray vComponents = new VArray();

	/** An array to keep the debug modules for UI display */
	private static VArray vDisplay = new VArray();

	/**
	 * Internal constructor.
	 *
	 * Component "name" must match properties file parameter.
	 *
	 * @param component
	 *            name or abbreviation of component
	 */
	private Debug(String component, String prettyName, boolean display) {
		this.component = component;
		this.prettyName = prettyName;
		verbose = false;
		// add this to the array
		vComponents.addElement(this);
		if (display)
			vDisplay.addElement(this);
	}

	private Debug() {
	}

	/** @return the names of the components. */
	public static String[] getComponentNames() {
		String[] rgsz = new String[vComponents.size()];
		for (int isz = 0; isz < vComponents.size(); isz++)
			rgsz[isz] = ((Debug) vComponents.elementAt(isz)).component;
		return (rgsz);
	}

	/**
	 * Prints the specified message (followed by a newline) to the console and
	 * the log file.
	 *
	 * Messages are ignored if <code>verbose</code> is false.
	 *
	 * @param message
	 *            the debug message to be displayed.
	 */
	public void println(Logger log, String message) {
		if (verbose)
			log.debug(component + ": " + message);
	}

	public void startDebugLine(Logger log) {
		if (verbose)
			log.debug(component + ": ");
	}

	/** No Component Name, No Newline */
	public void print(Logger log, String message) {
		if (verbose)
			log.error(message);
	}

	public void println(Logger log, Throwable throwable) {
		if (verbose)
			log.error(throwable);
	}

	public void printException(Logger log, Throwable throwable, String errorMessage) {
		if (verbose)
			log.error(errorMessage, throwable);
	}

	/**
	 * Prints a full back trace to the console and the log file.
	 *
	 * Ignored if <code>verbose</code> is false.
	 *
	 * @param description
	 *            Description.
	 */
	public void printStackTrace(Logger log, String description) {
		if (verbose)
			log.error(description);
	}

	public String getComponent() {
		return (component);
	}

	public void updateNameValue(Logger log, String name, String value) {
		if (verbose)
			MyRuntime.getInstance().updateNameValue(log, name, value);
	}

	public void updateNameValue(Logger log, String name, int value) {
		if (verbose)
			updateNameValue(log, name, "" + value);
	}

	public void updateNameValue(Logger log, String name, long value) {
		if (verbose)
			updateNameValue(log, name, "" + value);
	}

	/**
	 * Assert is used to catch programming errors.
	 * <p>
	 * 
	 * It is called with a boolean expression that is assumed to be true at all
	 * times. If it is false, a stack trace will be printed. Execution should
	 * not continue.
	 * <p>
	 *
	 * Example: Debug._assert(object != null);
	 *
	 * @param f
	 *            a boolean expression assumed to be true
	 */
	public static void _assert(boolean f) {
		_assert(f, "");
	}

	public static void _assert(boolean f, Throwable ex) {
		if (!f)
			_assert(f, Util.getStackTrace(ex));
	}

	public static void _assert(boolean f, Throwable ex, String mesg) {
		if (!f)
			_assert(f, mesg + Util.newLine + Util.getStackTrace(ex));
	}

	private static boolean fAssertSet = false;

	private static void setAssert() {
		if (!fAssertSet) {
			try {
				fAssert = true; // EnableDebugAssertion.getInstance().getBoolean()
								// & fDebug;
				fAssertSet = true;
				// DKA EnableDebugAssertion.getInstance().registerCallBack(new
				// Debug());
			} catch (NullPointerException e) {
				// SysParam is not in Hashtable yet. Default true.
			}
		}
	}

	/**
	 * Assert is used to catch programming errors.
	 * <p>
	 * 
	 * It is called with a boolean expression that is assumed to be true at all
	 * times. If it is false, a stack trace will be printed. Execution should
	 * not continue.
	 * <p>
	 *
	 * Example: Debug._assert(object != null);
	 *
	 * @param f
	 *            a boolean expression assumed to be true
	 * @param desc
	 *            a message explaining the failure
	 */
	public static void _assert(boolean f, String mesg) {
		setAssert();

		if (fAssert && !f)
			assertFailure(mesg);
	}

	/**
	 * Just complains if the assertion fails, rather than stopping the system.
	 */
	public static void assertSkip(Logger log, boolean f) {
		assertSkip(log, f, "");
	}

	/**
	 * Just complains if the assertion fails, rather than stopping the system.
	 */
	public static void assertSkip(Logger log, boolean f, String mesg) {
		setAssert();

		if (fAssert && !f) {
			String desc = "Skipped assertion failure" + ((mesg != null) ? (": " + mesg) : "") + Util.newLine
					+ Util.getStackTrace(new Throwable());
			log.error(desc);
		}
	}

	/**
	 * Assert that the object is null, logging an error if it is not. Used in
	 * particular by finalize() routines to check whether a resource has not
	 * been released (eg, File, Socket) before finalization. Resources should be
	 * released when appropriate; finalizers should not be used by default to do
	 * this.
	 * <p>
	 *
	 * Note: This debug method should execute in production. Noting the mistake
	 * Example: Debug._assert(object != null);
	 *
	 * @param resource
	 *            an object that should be null.
	 */
	public static void assertResourceNull(Logger log, String description, Object resource, Throwable trace) {
		if (resource != null) {
			if (trace == null) {
				// REVIEW: not really useful...
				trace = new Throwable();
			}

			// FUTURE: Diagnostic MyError.
			log.error("Resource freed late: " + (description != null ? (description + ": ") : "") + resource + '\n'
					+ Util.getStackTrace(trace));
		}
	}

	public static void assertResourceNull(Logger log, String description, Object resource) {
		assertResourceNull(log, description, resource, null);
	}

	public static void assertResourceNull(Logger log, Object resource) {
		assertResourceNull(log, null, resource, null);
	}

	/**
	 * Prints a stack trace when an assertion fails. Good place for a
	 * breakpoint...
	 */
	protected static void assertFailure(String mesg) {
		throw new AssertionFailure(mesg);
	}

	public static VArray getDisplayDebugs() {
		return vDisplay;
	}

	public static Debug getDebugByComponentName(String name) {
		Debug rc = null;
		for (int isz = 0; isz < vComponents.size(); isz++) {
			Debug temp = (Debug) vComponents.elementAt(isz);
			if (name.equals(temp.getComponent())) {
				rc = temp;
			}
		}

		return rc;
	}

	public boolean isEnabled() {
		return verbose;
	}

	public static VArray getDebugModules() {
		return vComponents;
	}

	/*
	 * DKA public void updateCachedSysParam(SysParam newSysparam) {
	 * EnableDebugAssertion param = (EnableDebugAssertion) newSysparam; fAssert
	 * = param.getBoolean(); Debug.TEST.println("Assertion enable is: "
	 * +fAssert); }
	 */

}
