/*   @(#)  URLDecoder.java  2002-02-04
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

package com.ahlquist.common.net.http;

import org.apache.log4j.Logger;

//import com.ahlquist.common.util.*; 
import com.ahlquist.common.net.smtp.AsciiString;
import com.ahlquist.common.sysparam.CharsetSysParam;
import com.ahlquist.common.sysparam.http.UrlFilenameEncodingCharset;
import com.ahlquist.common.sysparam.http.UrlParameterEncodingCharset;

/**
 * The class contains a utility method for converting a <code>String</code>
 * encoded in a MIME format called "<code>x-www-form-urlencoded</code>" format
 * back to its normal form.
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
 * Note: a similar class is included in the JDK1.2.
 *
 * @see java.net.URLEncoder
 */
public final class URLDecoder {

	final static Logger logger = Logger.getLogger(URLDecoder.class);

	/**
	 * Decodes a string from <code>x-www-form-urlencoded</code> format.
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
	 * @return the translated <code>String</code>. *
	 */
	public static final String decode(String s) {
		// Start out using "filename part" encoding
		// DKA add cast to object below
		CharsetSysParam charset = (CharsetSysParam) UrlFilenameEncodingCharset.getInstance();
		int len = s.length();
		StringBuffer out = new StringBuffer(len);
		AsciiString byteBuff = null;

		for (int i = 0; i < len; i++) {
			char ch = s.charAt(i);
			if (ch == '%') {
				// We have a possible %hexcode
				if (byteBuff == null)
					byteBuff = new AsciiString();
				while (ch == '%' && (i + 2 < len)) {
					int b = Character.digit(s.charAt(++i), 16) << 4 | Character.digit(s.charAt(++i), 16);
					if (b >= 0) {
						// We have a valid %hexcode
						byteBuff.append((byte) b);
						// Should we switch to parameter charset?
						if (b == '?') {
							// What we really should do is to make sure that
							// the ? isn't part of a multibyte character.
							// Unfortunately we cannot do this correctly since
							// we don't have good access to the Java character
							// set conversion routines. If we call the Java
							// toString and this a middle byte of a multibyte
							// sequence we will be returned the same result
							// '?' for an unknown character...
							// However, we are safe for UTF-8 and Latin-1.
							if (charset != UrlParameterEncodingCharset.getInstance()) {
								// Only switch if we already haven't done it.
								// This will also minimize the risk of ? being
								// in
								// a multibyte sequence since the "filename"
								// encoding almost always will be in UTF-8.
								addBytes(byteBuff, charset, out);
								charset = (CharsetSysParam) UrlParameterEncodingCharset.getInstance();
							}
						}
					} else {
						// Invalid % sequence, just write out as is
						addBytes(byteBuff, charset, out);
						i -= 2;
						out.append(ch);
					}
					i++;
					if (i >= len)
						break;
					ch = s.charAt(i);
				}
				addBytes(byteBuff, charset, out);
				if (i >= len)
					break;
			}

			if (ch == '+')
				out.append(' ');
			else {
				out.append(ch);
				// Should we switch to parameter charset?
				if (ch == '?')
					charset = UrlParameterEncodingCharset.getInstance();
			}
		}
		return out.toString();
	}

	/**
	 * Converts bytes to characters and appends to a string buffer. Empties the
	 * byte buffer when done.
	 *
	 * @param byteBuff
	 *            the bytes to be decoded
	 * @param charset
	 *            the character set to use for the decoding
	 * @param out
	 *            the string buffer to append decoded characters to
	 */
	private static void addBytes(AsciiString byteBuff, CharsetSysParam charset, StringBuffer out) {
		if (byteBuff != null && byteBuff.length() > 0) {
			if (charset.isLatin1()) {
				// Special case Latin-1
				for (int i = 0; i < byteBuff.length(); i++)
					out.append(byteBuff.charAt(i));
			} else if (charset.isUTF8()) {
				// Special case UTF-8
				int len = byteBuff.length();
				for (int i = 0; i < len; i++) {
					byte b = byteBuff.byteAt(i);
					char ch;
					// Three byte UTF-8
					if ((b & 0xf0) == 0xe0 && (i + 2 < len)) // Startbyte is
																// 1110xxxx
					{
						ch = (char) (((b & 0x1f) << 12) | // 1110xxxx
								((byteBuff.byteAt(++i) & 0x3f) << 6) | // 10xxxxxx
																		// (continuation
																		// byte)
								(byteBuff.byteAt(++i) & 0x3f)); // 10xxxxxx
																// (continuation
																// byte)
					}
					// Two byte UTF-8
					else if ((b & 0xe0) == 0xc0 && (i + 1 < len)) // Startbyte
																	// is
																	// 110xxxxx
					{
						ch = (char) (((b & 0x1f) << 6) | // 110xxxxx
								(byteBuff.byteAt(++i) & 0x3f)); // 10xxxxxx
																// (continuation
																// byte)
					}
					// Single byte (or invalid UTF-8)
					else
						ch = (char) (b & 0xFF);
					out.append(ch);
				}
			} else {
				// All character sets besides Latin-1 and UTF-8
				try {
					out.append(byteBuff.toString(charset.getCharset()));
				} catch (java.io.UnsupportedEncodingException e) {
					// This should never happen since the charset should already
					// be validated
					// Generate a critical error
					/*
					 * DKA 2003-07-16 new
					 * MyError(ErrorTypes.SYSTEM_PARAMETER_INVALID,
					 * "Invalid character set system parameter: " +
					 * charset.getParamName() + ", value=" +
					 * charset.getCharset()).escalate();
					 */
				}
			}
			// Empty the buffer
			byteBuff.clear();
		}
	}
}
