/*   @(#)  Password.java  2002-02-04
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
 * placeholder for security. <br>
 * Up to now this class implements a simple symmetric stream ciphers encryption
 * technique. Be careful of new line.
 */
public final class Password {
	/** cipher consists of a random string */
	private static final String cipher = "darthvader";
	private static int keyLength = cipher.length();

	public static String encrypt(String password) {
		return (crypt(password));
	}

	public static String decrypt(String password) {
		return (crypt(password));
	}

	/**
	 * this method will do encryption. Since we are implementing a symmetric
	 * encryption technique, encrypt and decrypt use same method.
	 */
	private static String crypt(String input) {
		int length = input.length();
		StringBuffer result = new StringBuffer(length);
		for (int i = 0; i < length; i++) {
			char ch = input.charAt(i);
			int j = i % keyLength;
			char keyCh = cipher.charAt(j);
			char resultCh = (char) (ch ^ keyCh);
			result.append(resultCh);
		}
		return result.toString();
	}
}
