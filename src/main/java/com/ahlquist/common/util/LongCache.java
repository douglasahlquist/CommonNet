/*   @(#)  LongCache.java  2002-02-04
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
 * A util class to cache Long Object in the array instead of creating it over
 * and over again singlton pattern is used.
 * 
 * @author Douglas Ahlquist
 */
public class LongCache {

	/**
	 * the maximum value of Long object being catched in the array
	 */
	private final static long bound = 10000;

	private static Long[] longcache = new Long[(int) bound];

	private static LongCache instance;

	protected LongCache() {
	}

	public static LongCache getInstance() {
		if (instance == null) {
			instance = new LongCache();
		}
		return instance;
	}

	/**
	 * if the index is >= the bound or < 0 a new Long object is created else
	 * look up in the array. The corresponding Long object will be returned if
	 * it is already created and stored there. Otherwise a new Long object will
	 * created, stored, and returned.
	 * 
	 * @param index:
	 *            value of the Long to be construct
	 * @return : a Long Object corresponding to the primative long
	 */
	public static Long getLong(long index) {
		if (index >= bound || index < 0) {
			return new Long(index);
		} else {
			if (longcache[(int) index] == null) {
				longcache[(int) index] = new Long(index);
			}
			return longcache[(int) index];
		}
	}

}
