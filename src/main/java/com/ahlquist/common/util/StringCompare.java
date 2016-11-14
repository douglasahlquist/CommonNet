/*   @(#)  StringCompare.java  2002-02-04
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

import java.text.StringCharacterIterator;

public class StringCompare implements com.ahlquist.common.util.Compare {
	public StringCompare() {
	}

	/**
	 * Compare the two Stringss.
	 * 
	 * @return the value 0 if the obj2 is equal to obj1; a value less than 0 if
	 *         obj1 is less than obj2; and a value greater than 0 if obj1 is
	 *         greater than obj2.
	 */
	public int compare(Object obj1, Object obj2) {

		int retval = 0;
		String s1 = (String) obj1;
		String s2 = (String) obj2;

		StringCharacterIterator sci1 = new StringCharacterIterator(s1);
		StringCharacterIterator sci2 = new StringCharacterIterator(s2);
		char c1 = sci1.first();
		char c2 = sci2.first();
		if ((retval = compareChar(c1, c2)) != 0) {
			return (retval);
		}

		for (int i = 1; i < s1.length(); i++) {
			c1 = sci1.next();
			try {
				c2 = sci2.next();
			} catch (Exception e) {
				return 1;
			}
			if ((retval = compareChar(c1, c2)) != 0) {
				return (retval);
			}
		}
		return retval;
	}

	private int compareChar(char c1, char c2) {
		int n1 = (int) c1;
		int n2 = (int) c2;
		if (n1 == n2) {
			return 0;
		} else if (n1 < n2) {
			return -1;
		} else {
			return 1;
		}
	}

}
