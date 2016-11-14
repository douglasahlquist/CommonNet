/*   @(#)  URLEncoder.java  2002-02-04
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
 * Copied from java.net.URLEncoder and rewritten by Douglas Ahlquist
 * for performance reason.
 *
 * Updated by Douglas Ahlquist 9/15/00 to handle international characters.
 * => UTF-8 encode characters as needed.
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package com.ahlquist.common.net.http;

import java.io.UnsupportedEncodingException;
import java.util.BitSet;

import com.ahlquist.common.sysparam.CharsetSysParam;
//import com.ahlquist.common.util.*;
import com.ahlquist.common.sysparam.http.UrlFilenameEncodingCharset;
import com.ahlquist.common.sysparam.http.UrlParameterEncodingCharset;

/**
 * The class contains a utility method for converting a <code>String</code> into
 * a MIME format called "<code>x-www-form-urlencoded</code>" format.
 * <p>
 * To convert a <code>String</code>, each character is examined in turn:
 * <ul>
 * <li>The ASCII characters '<code>a</code>' through '<code>z</code>', '
 * <code>A</code>' through '<code>Z</code>', and '<code>0</code>' through '
 * <code>9</code>' remain the same.
 * <li>The space character '<code>&nbsp;</code>' is converted into a plus sign '
 * <code>+</code>'.
 * <li>All other characters are converted into the 3-character string
 * "<code>%<i>xy</i></code>", where <i>xy</i> is the two-digit hexadecimal
 * representation of the lower 8-bits of the character.
 * </ul>
 *
 * @author Herb Jellinek
 * @version 1.12, 07/01/98
 * @since JDK1.0
 */
public class URLEncoder {
	static BitSet dontNeedEncoding;

	final static String[] hex = { "%00", "%01", "%02", "%03", "%04", "%05", "%06", "%07", "%08", "%09", "%0A", "%0B",
			"%0C", "%0D", "%0E", "%0F", "%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17", "%18", "%19", "%1A",
			"%1B", "%1C", "%1D", "%1E", "%1F", "%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27", "%28", "%29",
			"%2A", "%2B", "%2C", "%2D", "%2E", "%2F", "%30", "%31", "%32", "%33", "%34", "%35", "%36", "%37", "%38",
			"%39", "%3A", "%3B", "%3C", "%3D", "%3E", "%3F", "%40", "%41", "%42", "%43", "%44", "%45", "%46", "%47",
			"%48", "%49", "%4A", "%4B", "%4C", "%4D", "%4E", "%4F", "%50", "%51", "%52", "%53", "%54", "%55", "%56",
			"%57", "%58", "%59", "%5A", "%5B", "%5C", "%5D", "%5E", "%5F", "%60", "%61", "%62", "%63", "%64", "%65",
			"%66", "%67", "%68", "%69", "%6A", "%6B", "%6C", "%6D", "%6E", "%6F", "%70", "%71", "%72", "%73", "%74",
			"%75", "%76", "%77", "%78", "%79", "%7A", "%7B", "%7C", "%7D", "%7E", "%7F", "%80", "%81", "%82", "%83",
			"%84", "%85", "%86", "%87", "%88", "%89", "%8A", "%8B", "%8C", "%8D", "%8E", "%8F", "%90", "%91", "%92",
			"%93", "%94", "%95", "%96", "%97", "%98", "%99", "%9A", "%9B", "%9C", "%9D", "%9E", "%9F", "%A0", "%A1",
			"%A2", "%A3", "%A4", "%A5", "%A6", "%A7", "%A8", "%A9", "%AA", "%AB", "%AC", "%AD", "%AE", "%AF", "%B0",
			"%B1", "%B2", "%B3", "%B4", "%B5", "%B6", "%B7", "%B8", "%B9", "%BA", "%BB", "%BC", "%BD", "%BE", "%BF",
			"%C0", "%C1", "%C2", "%C3", "%C4", "%C5", "%C6", "%C7", "%C8", "%C9", "%CA", "%CB", "%CC", "%CD", "%CE",
			"%CF", "%D0", "%D1", "%D2", "%D3", "%D4", "%D5", "%D6", "%D7", "%D8", "%D9", "%DA", "%DB", "%DC", "%DD",
			"%DE", "%DF", "%E0", "%E1", "%E2", "%E3", "%E4", "%E5", "%E6", "%E7", "%E8", "%E9", "%EA", "%EB", "%EC",
			"%ED", "%EE", "%EF", "%F0", "%F1", "%F2", "%F3", "%F4", "%F5", "%F6", "%F7", "%F8", "%F9", "%FA", "%FB",
			"%FC", "%FD", "%FE", "%FF" };

	/*
	 * The list of characters that are not encoded have been determined by
	 * referencing O'Reilly's "HTML: The Definitive Guide" (page 164).
	 */

	static {
		dontNeedEncoding = new BitSet(256);
		int i;
		for (i = 'a'; i <= 'z'; i++) {
			dontNeedEncoding.set(i);
		}
		for (i = 'A'; i <= 'Z'; i++) {
			dontNeedEncoding.set(i);
		}
		for (i = '0'; i <= '9'; i++) {
			dontNeedEncoding.set(i);
		}
		dontNeedEncoding.set(' '); /*
									 * encoding a space to a + is done in the
									 * encode() method
									 */
		dontNeedEncoding.set('-');
		dontNeedEncoding.set('_');
		dontNeedEncoding.set('.');
		dontNeedEncoding.set('*');
	}

	/**
	 * You can't call the constructor.
	 */
	private URLEncoder() {
	}

	/**
	 * Translates a string into <code>x-www-form-urlencoded</code> format. The
	 * characters are first encoded.
	 *
	 * Unfortunately there is no clear standard that mandates which character
	 * set to use, and different web servers handles it differently.
	 *
	 * This implementation allows for one character set to be used for the
	 * directory/filename part of the URL, and another character set to be used
	 * for the parameter part of the URL. These characters sets are system
	 * parameters.
	 *
	 * @param s
	 *            <code>String</code> to be translated.
	 * @return the translated <code>String</code>.
	 */
	public static String encode(String s) {
		return encode(s, false);
	}

	/**
	 * Translates a URL parameter string into <code>x-www-form-urlencoded</code>
	 * format. The characters are first encoded using the character set
	 * specified for parameters.
	 *
	 * Unfortunately there is no clear standard that mandates which character
	 * set to use, and different web servers handles it differently.
	 *
	 * @param s
	 *            <code>String</code> to be translated.
	 * @return the translated <code>String</code>.
	 */
	public static String encodeParameter(String s) {
		StringBuffer out = new StringBuffer(s.length());

		// Parameter part
		encodePart(s, 0, s.length() - 1, UrlParameterEncodingCharset.getInstance(), false, out);
		return out.toString();
	}

	/**
	 * Translates a string into urlencoded format.
	 *
	 * @param s
	 *            the String to be encoded
	 * @param encodeNonAsciiOnly
	 *            if only chars > 127 should be encoded
	 * @return the translated String
	 */
	private static String encode(String s, boolean encodeNonAsciiOnly) {
		StringBuffer out = new StringBuffer(s.length());

		// Encode filename and parameters differently
		int paramIndex = s.indexOf('?');
		if (paramIndex < 0) {
			// No parameters
			encodePart(s, 0, s.length() - 1, UrlFilenameEncodingCharset.getInstance(), encodeNonAsciiOnly, out);
		} else {
			// Filename part
			encodePart(s, 0, paramIndex - 1, UrlFilenameEncodingCharset.getInstance(), encodeNonAsciiOnly, out);
			// Parameter part
			encodePart(s, paramIndex, s.length() - 1, UrlParameterEncodingCharset.getInstance(), encodeNonAsciiOnly,
					out);
		}

		return out.toString();
	}

	/**
	 * Translates a partial string into urlencoded format.
	 *
	 * @param s
	 *            the String to be encoded
	 * @param start
	 *            the start index of s
	 * @param end
	 *            the end index of s
	 * @param charset
	 *            the character set to use for the encoding
	 * @param encodeNonAsciiOnly
	 *            if only chars > 127 should be encoded
	 * @return the translated String
	 */
	private static void encodePart(String s, int start, int end, CharsetSysParam charset, boolean encodeNonAsciiOnly,
			StringBuffer out) {
		for (int i = start; i <= end; i++) {
			char ch = s.charAt(i);
			int c = (int) ch;
			// If we only should encode non ASCII characters
			if (encodeNonAsciiOnly && c <= 0x007f)
				// If so, don't encode ASCII characters
				out.append(ch);
			else if (dontNeedEncoding.get(c)) {
				if (ch == ' ')
					out.append('+');
				else
					out.append(ch);
			} else {
				if (c <= 0x007f)
					// ASCII
					out.append(hex[c]);
				else if (charset.isLatin1())
					// Latin-1 (ISO-8859-1)
					out.append(hex[c & 0xFF]);
				else if (charset.isUTF8())
					// UTF-8
					encodeUTF8Char(c, out);
				else
					// Other Non-ASCII
					encodeChar(c, charset, out);
			}
		}
	}

	/**
	 * Translates all non-ASCII characters of a string into
	 * <code>x-www-form-urlencoded</code> format.
	 *
	 * Unfortunately there is no clear standard that mandates which character
	 * set to use, and different web servers handles it differently.
	 *
	 * This implementation allows for one character set to be used for the
	 * directory/filename part of the URL, and another character set to be used
	 * for the parameter part of the URL. These characters sets are system
	 * parameters.
	 *
	 * @param s
	 *            <code>String</code> to be translated.
	 * @return the translated <code>String</code>.
	 */
	public static String encodeNonAscii(String s) {
		return encode(s, true);
	}

	/**
	 * Translates one character into <code>x-www-form-urlencoded</code> format.
	 * The character is first UTF-8 encoded.
	 *
	 * @param c
	 *            <code>character</code> to be translated.
	 * @param out
	 *            the string buffer to append encoded characters to
	 */
	private static void encodeUTF8Char(int c, StringBuffer out) {
		// UTF-8 encode on the fly
		if (c <= 0x007f) { // other ASCII
			out.append(hex[c]);
		} else if (c <= 0x07FF) { // non-ASCII <= 0x7FF
			out.append(hex[0xc0 | (c >> 6)]);
			out.append(hex[0x80 | (c & 0x3F)]);
		} else { // 0x7FF < c <= 0xFFFF
			out.append(hex[0xe0 | (c >> 12)]);
			out.append(hex[0x80 | ((c >> 6) & 0x3F)]);
			out.append(hex[0x80 | (c & 0x3F)]);
		}
		// We don't support surrogate pairs for now, these are
		// very rare. (Unicode 3.0 doesn't contain these.)
	}

	/**
	 * Translates one character into <code>x-www-form-urlencoded</code> format.
	 * The character is first encoded using the desired character set.
	 *
	 * @param c
	 *            <code>character</code> to be translated.
	 * @param charset
	 *            the character set to use for the encoding
	 * @param out
	 *            the string buffer to append encoded characters to
	 */
	private static void encodeChar(int c, CharsetSysParam charset, StringBuffer out) {
		char value[] = { (char) c };
		try {
			byte b[] = (new String(value)).getBytes(charset.getCharset());
			for (int i = 0; i < b.length; i++)
				out.append(hex[b[i] & 0xFF]);
		} catch (UnsupportedEncodingException e) {
			// This should never happen since the charset should already be
			// validated
			// Generate a critical error
			/*
			 * TODO new MyError(ErrorTypes.SYSTEM_PARAMETER_INVALID,
			 * "Invalid character set system parameter: " +
			 * charset.getParamName() + ", value=" +
			 * charset.getCharset()).escalate();
			 */
		}
	}
}
