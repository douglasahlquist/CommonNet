/*   @(#)  HexNumber.java  2002-02-04
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
                      HEXIDECIMAL TO DECIMAL CONVERSION
 ---------------------------------------------------------------------------
                        HEXIDECIMAL  COLUMNS       
     6                5             4            3          2          1
 ---------------------------------------------------------------------------
 hex dec-------   hex dec----   hex dec--    hex dec-   hex dec   hex  dec
  0           0    0        0    0      0     0     0    0    0    0     0
  1   1,048,576    1   65,536    1   4,096    1    256   1   16    1     1
  2   2,097,152    2  131,072    2   8,192    2    512   2   32    2     2
  3   3,145,728    3  196,608    3  12,288    3    768   3   48    3     3
  4   4,194,304    4  262,144    4  16,384    4  1,024   4   64    4     4
  5   5,242,880    5  327,680    5  20,480    5  1,280   5   80    5     5
  6   6,291,456    6  393,216    6  24,576    6  1,536   6   96    6     6
  7   7,340,032    7  458,752    7  28,672    7  1,792   7  112    7     7
  8   8,388,608    8  524,288    8  32,768    8  2,048   8  128    8     8
  9   9,437,184    9  589,824    9  36,864    9  2,304   9  144    9     9
  A  10,485,760    A  655,360    A  40,960    A  2,560   A  160    A    10
  B  11,534,336    B  720,896    B  45,056    B  2,816   B  176    B    11
  C  12,582,912    C  768,432    C  49,152    C  3,072   C  192    C    12
  D  13,631,488    D  851,968    D  53,248    D  3,328   D  208    D    13
  E  14,680,064    E  917,504    E  57,344    E  3,548   E  224    E    14
  F  15,728,640    F  983,040    F  61,440    F  3,840   F  240    F    15
 --------------------------------------------------------------------------
*/

/**
 * Converts numbers from hexidecimal to decimal
 */

public class HexNumber {

	/**
	 * Converts a String from hex to decimal
	 * 
	 * @param hex
	 * @return
	 * @throws NumberFormatException
	 */
	public static int hexToDec(String hex) throws NumberFormatException {
		hex = hex.toUpperCase();
		int result = 0;
		int pow = 0;
		for (int i = 0; i < hex.length(); i++) {
			// get the substring at index[i]
			int charVal = 0;
			char c = hex.charAt(i);
			// get the ascii value of that char
			if (c > 47 && c < 58) { // 48=0 57=9
				charVal = c - 48;
			} else if (c > 64 && c < 71) {
				charVal = c - 65 + 10;
			} else {
				throw new NumberFormatException(hex + " not valid hex number");
			}
			pow = (int) Math.pow((double) 16, (double) hex.length() - i - 1);
			result += charVal * pow;
		}
		return result;
	}
}
