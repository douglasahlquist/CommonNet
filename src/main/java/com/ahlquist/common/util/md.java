/*   @(#)  md.java  2002-02-04
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

public class md {
	
	
	final static Logger logger = Logger.getLogger(md.class);

	
	public int A, B, C, D;
	public int d[];
	public int numwords;

	/*
	 * For verification of a modicum of sanity, run a few test strings through
	 */
	public static void main(String argv[]) {
		boolean doinmd4;
		String mdtype;
		/* Test cases, mostly taken from rfc 1320 */
		String str[] = { "", "a", "abc", "message digest", "abcdefghijklmnopqrstuvwxyz",
				"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789",
				"12345678901234567890123456789012345678901234567890123456789012345678901234567890",
				"01234567890123456789012345678901234567890123456789012345" };

		if (argv.length == 0) {
			mdtype = "md4";
			doinmd4 = true;
		} else if (argv.length > 2) {
			System.err.println("Usage: md [4|5|md4|md5]");
			return;
		} else if ((argv[0].equals("4")) || (argv[0].equals("md4"))) {
			mdtype = "md4";
			doinmd4 = true;
		} else if ((argv[0].equals("5")) || (argv[0].equals("md5"))) {
			mdtype = "md5";
			doinmd4 = false;
		} else {
			System.err.println("Usage: md [4|5|md4|md5]");
			return;
		}
		for (int i = 0; i < str.length; i++) {
			if (doinmd4) {
				md4 mdc = new md4(str[i]);
				mdc.calc();
				System.out.println(mdtype + "(\"" + str[i] + "\") = " + mdc);
			} else {
				md5 mdc = new md5(str[i]);
				mdc.calc();
				System.out.println(mdtype + "(\"" + str[i] + "\") = " + mdc);
			}
		}
	}

	public md(String s) {
		byte in[] = new byte[s.length()];
		int i;

		for (i = 0; i < s.length(); i++) {
			in[i] = (byte) (s.charAt(i) & 0xff);
		}
		mdinit(in);
	}

	public md(byte in[]) {
		mdinit(in);
	}

	public void mdinit(byte in[]) {
		int newlen, endblklen, pad, i;
		long datalenbits;

		datalenbits = in.length * 8;
		endblklen = in.length % 64;
		if (endblklen < 56) {
			pad = 64 - endblklen;
		} else {
			pad = (64 - endblklen) + 64;
		}
		newlen = in.length + pad;
		byte b[] = new byte[newlen];
		for (i = 0; i < in.length; i++) {
			b[i] = in[i];
		}
		b[in.length] = (byte) 0x80;
		for (i = b.length + 1; i < (newlen - 8); i++) {
			b[i] = 0;
		}
		for (i = 0; i < 8; i++) {
			b[newlen - 8 + i] = (byte) (datalenbits & 0xff);
			datalenbits >>= 8;
		}
		/* init registers */
		A = 0x67452301;
		B = 0xefcdab89;
		C = 0x98badcfe;
		D = 0x10325476;
		this.numwords = newlen / 4;
		this.d = new int[this.numwords];
		for (i = 0; i < newlen; i += 4) {
			this.d[i / 4] = (b[i] & 0xff) + ((b[i + 1] & 0xff) << 8) + ((b[i + 2] & 0xff) << 16)
					+ ((b[i + 3] & 0xff) << 24);
		}
	}

	public String toString() {
		String s;
		return (tohex(A) + tohex(B) + tohex(C) + tohex(D));
	}

	public int[] getregs() {
		int regs[] = { this.A, this.B, this.C, this.D };
		return regs;
	}

	public void calc() {
		int AA, BB, CC, DD, i;

		for (i = 0; i < numwords / 16; i++) {
			AA = A;
			BB = B;
			CC = C;
			DD = D;
			round1(i);
			round2(i);
			round3(i);
			if (this instanceof md5) {
				round4(i);
			}
			A += AA;
			B += BB;
			C += CC;
			D += DD;
		}
	}

	/*
	 * Dummy round*() methods. these are overriden in the md4 and md5 subclasses
	 */
	void round1(int blk) {
		System.err.println("Danger! Danger! Someone called md.round1()!");
	}

	void round2(int blk) {
		System.err.println("Danger! Danger! Someone called md.round2()!");
	}

	void round3(int blk) {
		System.err.println("Danger! Danger! Someone called md.round3()!");
	}

	void round4(int blk) {
		System.err.println("Danger! Danger! Someone called md.round4()!");
	}

	public static int rotintlft(int val, int numbits) {
		return ((val << numbits) | (val >>> (32 - numbits)));
	}

	public static String tohex(int i) {
		int b;
		String tmpstr;

		tmpstr = "";
		for (b = 0; b < 4; b++) {
			tmpstr += Integer.toString((i >> 4) & 0xf, 16) + Integer.toString(i & 0xf, 16);
			i >>= 8;
		}
		return tmpstr;
	}
}
