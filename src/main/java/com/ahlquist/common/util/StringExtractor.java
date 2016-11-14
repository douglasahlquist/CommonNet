/*   @(#)  StringExtractor.java  2002-02-04
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

import org.apache.log4j.Logger;

public class StringExtractor {
	final static Logger logger = Logger.getLogger(StringExtractor.class);
	
	public static final String className = StringExtractor.class.getName();

	public static final int BY_BOUNDS_BOUNDS_INCLUSIVE = 1;
	public static final int BY_BOUNDS_LEFT_INCLUSIVE = 2;
	public static final int BY_BOUNDS_RIGHT_INCLUSIVE = 3;
	public static final int BY_BOUNDS_NON_INCLUSIVE = 4;

	public static String getString(String str, String pre, String post, int occur, int type) {
		switch (type) {
		case BY_BOUNDS_BOUNDS_INCLUSIVE: {
			return (getStringByBoundsInclusive(str, pre, post, occur));
		}
		case BY_BOUNDS_LEFT_INCLUSIVE: {
			return (getStringByBoundsLeftInclusive(str, pre, post, occur));
		}
		case BY_BOUNDS_RIGHT_INCLUSIVE: {
			return (getStringByBoundsRightInclusive(str, pre, post, occur));
		}
		case BY_BOUNDS_NON_INCLUSIVE: {
			return (getStringByBoundsNonInclusive(str, pre, post, occur));
		}
		default: {
			return (getStringByBoundsNonInclusive(str, pre, post, occur));
		}
		}
	}

	public static String getStringByBoundsNonInclusive(String str, String pre, String post, int occur) {
		// convert the contents to a String

		int startIndex = 0;
		int endIndex = 0;
		int i = 0;
		String result = null;
		for (; i < occur; i++) {
			if (endIndex != 0) {
				startIndex = endIndex + post.length();
			}
			startIndex = str.indexOf(pre, startIndex);
			logger.debug("startIndex=" + startIndex + " on iter=" + i);
			if (startIndex == -1) {
				logger.error("pre not found");
				logger.error("Contents by bounds <pre=" + pre + "> <post=" + post + "> not found");
				return null;
			} else {
				logger.debug("pre found");
				// find the first occurance of post
				endIndex = str.indexOf(post, startIndex + pre.length());
				logger.debug("endIndex=" + endIndex + " on iter=" + i);
				if (endIndex != -1) {
					logger.debug("post found");
					// set startIndex to endIndex + post.length();
				}
			}
		}
		if (endIndex == -1) {
			logger.error("Contents by bounds <pre=" + pre + "> <post=" + post + "> not found");
			return null;
		}
		logger.debug("startIndex=" + startIndex + " on final");
		logger.debug("endIndex=" + endIndex + " on final");
		result = str.substring(startIndex + pre.length(), endIndex);
		return result;
	}

	public static String getStringByBoundsInclusive(String str, String pre, String post, int occur) {
		return (new String(pre + getStringByBoundsNonInclusive(str, pre, post, occur) + post));
	}

	public static String getStringByBoundsLeftInclusive(String str, String pre, String post, int occur) {
		return (new String(pre + getStringByBoundsNonInclusive(str, pre, post, occur)));
	}

	public static String getStringByBoundsRightInclusive(String str, String pre, String post, int occur) {
		return (new String(getStringByBoundsNonInclusive(str, pre, post, occur) + post));
	}

	public static boolean validateTextOccuranceOnPage(String str, String text, int occur) {
		int startIndex = 0;
		for (int i = 1; i < occur + 1; i++) {
			int index = str.indexOf(text, startIndex);
			if (i == occur) {
				if (index == -1) {
					return false;
				} else
					return true;
			}
			if (index == -1)
				return false;
		}
		return true;
	}

	public static int countOccurancesOf(String str, String item) {
		return 0;
	}

}