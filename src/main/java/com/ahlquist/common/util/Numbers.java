/*   @(#)  Numbers.java  2002-02-04
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

/**
 * All-static collection of number utilities
 */
public final class Numbers {
	/**
	 * Returns the integer value of the specified string. Or, if any error
	 * occurs, the default value is returned.
	 *
	 * @param spec
	 *            the string to be parsed.
	 * @param def
	 *            the default value to be returned in case of error.
	 * @return the parsed value of <code>spec</code>, or <code>def</code> on
	 *         error.
	 */
	public final static int getInt(String spec, int def) {
		try {
			return Integer.parseInt(spec);
		} catch (NumberFormatException ex) {
			return def;
		}
	}

	/**
	 * Returns the integer value of the specified string. Or, if any error
	 * occurs, the default value is returned.
	 *
	 * @param spec
	 *            the string to be parsed.
	 * @param radix
	 *            radix to parse String value
	 * @param def
	 *            the default value to be returned in case of error.
	 * @return the parsed value of <code>spec</code>, or <code>def</code> on
	 *         error.
	 */
	public final static int getInt(String spec, int radix, int def) {
		try {
			return Integer.parseInt(spec, radix);
		} catch (NumberFormatException ex) {
			return def;
		}
	}

	/**
	 * Returns the long value of the specified string. Or, if any error occurs,
	 * the default value is returned.
	 *
	 * @param spec
	 *            the string to be parsed.
	 * @param def
	 *            the default value to be returned in case of error.
	 * @return the parsed value of <code>spec</code>, or <code>def</code> on
	 *         error.
	 */
	public final static long getLong(String spec, long def) {
		try {
			return Long.parseLong(spec);
		} catch (NumberFormatException ex) {
			return def;
		}
	}

	/**
	 * Returns the double value of the specified string. Or, if any error
	 * occurs, the default value is returned.
	 *
	 * @param spec
	 *            the string to be parsed.
	 * @param def
	 *            the default value to be returned in case of error.
	 * @return the parsed value of <code>spec</code>, or <code>def</code> on
	 *         error.
	 */
	public final static double getDouble(String spec, double def) {
		try {
			if (spec == null)
				return def;
			return Double.valueOf(spec).doubleValue();
		} catch (NumberFormatException ex) {
			return def;
		}
	}

	/** @return true iff the specified string represents a number (real). */
	public final static boolean isNumber(String spec) {
		try {
			return (Double.valueOf(spec) != null);
		} catch (NumberFormatException ex) {
			return (false);
		}
	}

	// hide constructor - all static class
	private Numbers() {
	}
}
