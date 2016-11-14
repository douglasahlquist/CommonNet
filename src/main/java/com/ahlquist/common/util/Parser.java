/*   @(#)  Parser.java  2002-02-04
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
 * Parser is a variant of StringTokenizer.
 * 
 * Parse will always return whatever is between two delimiters, even a empty
 * string.
 *
 * StringTokenizer will skip adjacent delimiters rather than return an empty
 * token.
 * 
 * If empty tokens are important, this parser is handy.
 *
 * Note: the constructor only accepts a single character delimiter.
 * (StringTokenizer allows a string of delimiter chars.)
 *
 * @author Douglas Ahlquist
 */
public class Parser // extends MyObject
{
	/** String to be parsed */
	private String stSource;

	/** delimiter character */
	private char chDelim;

	/** current parsing index */
	private int currIndex;

	/** are there more tokens? */
	private boolean fMore;

	/**
	 * Constructs a parser object for the specified string.
	 *
	 * @param st
	 *            a string to be parsed.
	 * @param ch
	 *            delimiter character
	 */
	public Parser(String st, char ch) {
		stSource = st;
		chDelim = ch;
		currIndex = 0;
		fMore = true;
	}

	/**
	 * Returns the next token in the string.
	 *
	 * If no more tokens are available, an empty string is returned.
	 *
	 * @see Parser#hasMoreTokens
	 *
	 * @return the next token (a String)
	 */
	public String nextToken() {
		String stToken = "";

		if (fMore) {
			int iDelim = stSource.indexOf(chDelim, currIndex);

			if (iDelim == -1) {
				stToken = stSource.substring(currIndex);
				fMore = false;
			} else {
				stToken = stSource.substring(currIndex, iDelim);
				currIndex = iDelim + 1;
			}
		} // (fMore)

		return stToken;

	} // nextToken

	/**
	 * Indicates whether more tokens are available.
	 *
	 * @return true if more tokens are available, otherwise false.
	 */
	public boolean hasMoreTokens() {
		return fMore;
	}

	/**
	 * Returns a complete list of all tokens in the specified string.
	 *
	 * The list is a Vector object. Use the Vector.size() method to determine
	 * the number of tokens
	 *
	 * @param st
	 *            a string to be parsed.
	 * @param ch
	 *            delimiter character
	 * @return VArray list of all tokens.
	 */
	public static VArray getTokenList(String st, char ch) {
		VArray list = new VArray();
		if (st == null || st.equals(""))
			return (list);

		int curr = 0;
		int iDelim = st.indexOf(ch);
		while (iDelim != -1) {
			list.addElement(st.substring(curr, iDelim));
			curr = iDelim + 1;
			iDelim = st.indexOf(ch, ++iDelim);
		}
		list.addElement(st.substring(curr));
		list.trimToSize();

		return (list);
	}

	/**
	 * Returns a complete list of all tokens in the specified string.
	 *
	 * The list is a String array. Use array.length to determine the number of
	 * tokens
	 *
	 * @param st
	 *            a string to be parsed.
	 * @param ch
	 *            delimiter character
	 * @return list of all tokens.
	 */
	public static String[] getTokenArray(String st, char ch) {
		VArray list = getTokenList(st, ch);
		String[] rgst = new String[list.size()];
		list.copyInto(rgst);
		return rgst;
	}

	/**
	 * returns a list of tokens in the specified string, each of which is
	 * delimited by startDelim and endDelim.
	 *
	 * @param st
	 *            a string to be parsed.
	 * @param startDelim
	 *            the starting character delimiter.
	 * @param endDelim
	 *            the end character delimiter.
	 * @return list of tokens.
	 */
	public static String[] getDelimTokenArray(String st, char startDelim, char endDelim) {
		if (st == null || st == "")
			return (null);
		VArray list = new VArray();
		int startIndex = st.indexOf(startDelim);
		while (startIndex != -1) {
			int endIndex = st.indexOf(endDelim, startIndex);
			if (endIndex == -1)
				break;
			String token = st.substring(startIndex + 1, endIndex);
			list.addElement(token.trim());
			startIndex = st.indexOf(startDelim, endIndex);
		}
		if (list.size() == 0)
			return (null);
		String[] stArr = new String[list.size()];
		list.copyInto(stArr);
		return (stArr);
	}

	/**
	 * Advances index in string argument to skip over spaces.
	 *
	 * @param st
	 *            the string
	 * @param index
	 *            the current index in string.
	 * @return the index after skipping over white spaces in string. the
	 *         returned index may be equal to the string length of st if st is
	 *         not trimmed.
	 */

	public static int skipOverSpaces(String st, int index) {
		int i = index;
		while (i < st.length() && Character.isWhitespace(st.charAt(i)))
			i++;
		return (i);
	}

	/**
	 * Finds the next white space in st.
	 *
	 * @param st
	 *            the string
	 * @param index
	 *            the starting index in string.
	 * @return the index of the first white space in st starting at index.
	 */

	public static int findIndexOfWordBoundary(String st, int index) {
		int i = index;
		while (i < st.length() && !Character.isWhitespace(st.charAt(i)))
			i++;
		return (i);
	}
}
