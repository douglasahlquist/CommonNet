/*   @(#)  md5.java  2002-02-04
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

/* Class for implementing md5 hash algorithms.
 * There are constructors for prepping the hash algorithm (doing the
 * padding, mainly) for a String or a byte[], and an mdcalc() method 
 * for generating the hash. The results can be accessed as an int array 
 * by getregs(), or as a String of hex digits with toString().
 */

public class md5 extends md {
	public static void main(String[] args) {
		// md5 test = new md5("pete:pete:ef01a8c0000009c7"); //from sniffer
		md5 test = new md5("pete:pete:8801a8c00001002"); // from dougs code
		test.calc();
		System.out.println(test);

		// String expected = "012effb2e9412b61d9cc13bec452dfb5"; //from sniffer
		String expected = "2d002eb0704a4521883f8520feb73bf8"; // from dougs code
		System.out.println(expected);
		if (!expected.equals(test.toString()))
			System.out.println("FAILED");
		else
			System.out.println("SUCCESS");

	}

	public md5(String s) {
		super(s);
	}

	public md5(byte in[]) {
		super(in);
	}

	public static int F(int x, int y, int z) {
		return ((x & y) | (~x & z));
	}

	public static int G(int x, int y, int z) {
		return ((x & z) | (y & ~z));
	}

	public static int H(int x, int y, int z) {
		return (x ^ y ^ z);
	}

	public static int I(int x, int y, int z) {
		return (y ^ (x | ~z));
	}

	public void round1(int blk) {
		A = rotintlft(A + F(B, C, D) + d[0 + 16 * blk] + 0xd76aa478, 7) + B;
		D = rotintlft(D + F(A, B, C) + d[1 + 16 * blk] + 0xe8c7b756, 12) + A;
		C = rotintlft(C + F(D, A, B) + d[2 + 16 * blk] + 0x242070db, 17) + D;
		B = rotintlft(B + F(C, D, A) + d[3 + 16 * blk] + 0xc1bdceee, 22) + C;
		A = rotintlft(A + F(B, C, D) + d[4 + 16 * blk] + 0xf57c0faf, 7) + B;
		D = rotintlft(D + F(A, B, C) + d[5 + 16 * blk] + 0x4787c62a, 12) + A;
		C = rotintlft(C + F(D, A, B) + d[6 + 16 * blk] + 0xa8304613, 17) + D;
		B = rotintlft(B + F(C, D, A) + d[7 + 16 * blk] + 0xfd469501, 22) + C;
		A = rotintlft(A + F(B, C, D) + d[8 + 16 * blk] + 0x698098d8, 7) + B;
		D = rotintlft(D + F(A, B, C) + d[9 + 16 * blk] + 0x8b44f7af, 12) + A;
		C = rotintlft(C + F(D, A, B) + d[10 + 16 * blk] + 0xffff5bb1, 17) + D;
		B = rotintlft(B + F(C, D, A) + d[11 + 16 * blk] + 0x895cd7be, 22) + C;
		A = rotintlft(A + F(B, C, D) + d[12 + 16 * blk] + 0x6b901122, 7) + B;
		D = rotintlft(D + F(A, B, C) + d[13 + 16 * blk] + 0xfd987193, 12) + A;
		C = rotintlft(C + F(D, A, B) + d[14 + 16 * blk] + 0xa679438e, 17) + D;
		B = rotintlft(B + F(C, D, A) + d[15 + 16 * blk] + 0x49b40821, 22) + C;
	}

	public void round2(int blk) {
		A = rotintlft(A + G(B, C, D) + d[1 + 16 * blk] + 0xf61e2562, 5) + B;
		D = rotintlft(D + G(A, B, C) + d[6 + 16 * blk] + 0xc040b340, 9) + A;
		C = rotintlft(C + G(D, A, B) + d[11 + 16 * blk] + 0x265e5a51, 14) + D;
		B = rotintlft(B + G(C, D, A) + d[0 + 16 * blk] + 0xe9b6c7aa, 20) + C;
		A = rotintlft(A + G(B, C, D) + d[5 + 16 * blk] + 0xd62f105d, 5) + B;
		D = rotintlft(D + G(A, B, C) + d[10 + 16 * blk] + 0x02441453, 9) + A;
		C = rotintlft(C + G(D, A, B) + d[15 + 16 * blk] + 0xd8a1e681, 14) + D;
		B = rotintlft(B + G(C, D, A) + d[4 + 16 * blk] + 0xe7d3fbc8, 20) + C;
		A = rotintlft(A + G(B, C, D) + d[9 + 16 * blk] + 0x21e1cde6, 5) + B;
		D = rotintlft(D + G(A, B, C) + d[14 + 16 * blk] + 0xc33707d6, 9) + A;
		C = rotintlft(C + G(D, A, B) + d[3 + 16 * blk] + 0xf4d50d87, 14) + D;
		B = rotintlft(B + G(C, D, A) + d[8 + 16 * blk] + 0x455a14ed, 20) + C;
		A = rotintlft(A + G(B, C, D) + d[13 + 16 * blk] + 0xa9e3e905, 5) + B;
		D = rotintlft(D + G(A, B, C) + d[2 + 16 * blk] + 0xfcefa3f8, 9) + A;
		C = rotintlft(C + G(D, A, B) + d[7 + 16 * blk] + 0x676f02d9, 14) + D;
		B = rotintlft(B + G(C, D, A) + d[12 + 16 * blk] + 0x8d2a4c8a, 20) + C;
	}

	public void round3(int blk) {
		A = rotintlft(A + H(B, C, D) + d[5 + 16 * blk] + 0xfffa3942, 4) + B;
		D = rotintlft(D + H(A, B, C) + d[8 + 16 * blk] + 0x8771f681, 11) + A;
		C = rotintlft(C + H(D, A, B) + d[11 + 16 * blk] + 0x6d9d6122, 16) + D;
		B = rotintlft(B + H(C, D, A) + d[14 + 16 * blk] + 0xfde5380c, 23) + C;
		A = rotintlft(A + H(B, C, D) + d[1 + 16 * blk] + 0xa4beea44, 4) + B;
		D = rotintlft(D + H(A, B, C) + d[4 + 16 * blk] + 0x4bdecfa9, 11) + A;
		C = rotintlft(C + H(D, A, B) + d[7 + 16 * blk] + 0xf6bb4b60, 16) + D;
		B = rotintlft(B + H(C, D, A) + d[10 + 16 * blk] + 0xbebfbc70, 23) + C;
		A = rotintlft(A + H(B, C, D) + d[13 + 16 * blk] + 0x289b7ec6, 4) + B;
		D = rotintlft(D + H(A, B, C) + d[0 + 16 * blk] + 0xeaa127fa, 11) + A;
		C = rotintlft(C + H(D, A, B) + d[3 + 16 * blk] + 0xd4ef3085, 16) + D;
		B = rotintlft(B + H(C, D, A) + d[6 + 16 * blk] + 0x04881d05, 23) + C;
		A = rotintlft(A + H(B, C, D) + d[9 + 16 * blk] + 0xd9d4d039, 4) + B;
		D = rotintlft(D + H(A, B, C) + d[12 + 16 * blk] + 0xe6db99e5, 11) + A;
		C = rotintlft(C + H(D, A, B) + d[15 + 16 * blk] + 0x1fa27cf8, 16) + D;
		B = rotintlft(B + H(C, D, A) + d[2 + 16 * blk] + 0xc4ac5665, 23) + C;
	}

	public void round4(int blk) {
		A = rotintlft(A + I(B, C, D) + d[0 + 16 * blk] + 0xf4292244, 6) + B;
		D = rotintlft(D + I(A, B, C) + d[7 + 16 * blk] + 0x432aff97, 10) + A;
		C = rotintlft(C + I(D, A, B) + d[14 + 16 * blk] + 0xab9423a7, 15) + D;
		B = rotintlft(B + I(C, D, A) + d[5 + 16 * blk] + 0xfc93a039, 21) + C;
		A = rotintlft(A + I(B, C, D) + d[12 + 16 * blk] + 0x655b59c3, 6) + B;
		D = rotintlft(D + I(A, B, C) + d[3 + 16 * blk] + 0x8f0ccc92, 10) + A;
		C = rotintlft(C + I(D, A, B) + d[10 + 16 * blk] + 0xffeff47d, 15) + D;
		B = rotintlft(B + I(C, D, A) + d[1 + 16 * blk] + 0x85845dd1, 21) + C;
		A = rotintlft(A + I(B, C, D) + d[8 + 16 * blk] + 0x6fa87e4f, 6) + B;
		D = rotintlft(D + I(A, B, C) + d[15 + 16 * blk] + 0xfe2ce6e0, 10) + A;
		C = rotintlft(C + I(D, A, B) + d[6 + 16 * blk] + 0xa3014314, 15) + D;
		B = rotintlft(B + I(C, D, A) + d[13 + 16 * blk] + 0x4e0811a1, 21) + C;
		A = rotintlft(A + I(B, C, D) + d[4 + 16 * blk] + 0xf7537e82, 6) + B;
		D = rotintlft(D + I(A, B, C) + d[11 + 16 * blk] + 0xbd3af235, 10) + A;
		C = rotintlft(C + I(D, A, B) + d[2 + 16 * blk] + 0x2ad7d2bb, 15) + D;
		B = rotintlft(B + I(C, D, A) + d[9 + 16 * blk] + 0xeb86d391, 21) + C;
	}
}
