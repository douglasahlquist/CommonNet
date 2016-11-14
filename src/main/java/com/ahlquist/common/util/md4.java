/*   @(#)  md4.java  2002-02-04
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

public class md4 extends md {
	public md4(String s) {
		super(s);
	}

	public md4(byte in[]) {
		super(in);
	}

	public static int F(int x, int y, int z) {
		return ((x & y) | (~x & z));
	}

	public static int G(int x, int y, int z) {
		return ((x & y) | (x & z) | (y & z));
	}

	public static int H(int x, int y, int z) {
		return (x ^ y ^ z);
	}

	public void round1(int blk) {
		A = rotintlft((A + F(B, C, D) + d[0 + 16 * blk]), 3);
		D = rotintlft((D + F(A, B, C) + d[1 + 16 * blk]), 7);
		C = rotintlft((C + F(D, A, B) + d[2 + 16 * blk]), 11);
		B = rotintlft((B + F(C, D, A) + d[3 + 16 * blk]), 19);

		A = rotintlft((A + F(B, C, D) + d[4 + 16 * blk]), 3);
		D = rotintlft((D + F(A, B, C) + d[5 + 16 * blk]), 7);
		C = rotintlft((C + F(D, A, B) + d[6 + 16 * blk]), 11);
		B = rotintlft((B + F(C, D, A) + d[7 + 16 * blk]), 19);

		A = rotintlft((A + F(B, C, D) + d[8 + 16 * blk]), 3);
		D = rotintlft((D + F(A, B, C) + d[9 + 16 * blk]), 7);
		C = rotintlft((C + F(D, A, B) + d[10 + 16 * blk]), 11);
		B = rotintlft((B + F(C, D, A) + d[11 + 16 * blk]), 19);

		A = rotintlft((A + F(B, C, D) + d[12 + 16 * blk]), 3);
		D = rotintlft((D + F(A, B, C) + d[13 + 16 * blk]), 7);
		C = rotintlft((C + F(D, A, B) + d[14 + 16 * blk]), 11);
		B = rotintlft((B + F(C, D, A) + d[15 + 16 * blk]), 19);
	}

	public void round2(int blk) {
		A = rotintlft((A + G(B, C, D) + d[0 + 16 * blk] + 0x5a827999), 3);
		D = rotintlft((D + G(A, B, C) + d[4 + 16 * blk] + 0x5a827999), 5);
		C = rotintlft((C + G(D, A, B) + d[8 + 16 * blk] + 0x5a827999), 9);
		B = rotintlft((B + G(C, D, A) + d[12 + 16 * blk] + 0x5a827999), 13);

		A = rotintlft((A + G(B, C, D) + d[1 + 16 * blk] + 0x5a827999), 3);
		D = rotintlft((D + G(A, B, C) + d[5 + 16 * blk] + 0x5a827999), 5);
		C = rotintlft((C + G(D, A, B) + d[9 + 16 * blk] + 0x5a827999), 9);
		B = rotintlft((B + G(C, D, A) + d[13 + 16 * blk] + 0x5a827999), 13);

		A = rotintlft((A + G(B, C, D) + d[2 + 16 * blk] + 0x5a827999), 3);
		D = rotintlft((D + G(A, B, C) + d[6 + 16 * blk] + 0x5a827999), 5);
		C = rotintlft((C + G(D, A, B) + d[10 + 16 * blk] + 0x5a827999), 9);
		B = rotintlft((B + G(C, D, A) + d[14 + 16 * blk] + 0x5a827999), 13);

		A = rotintlft((A + G(B, C, D) + d[3 + 16 * blk] + 0x5a827999), 3);
		D = rotintlft((D + G(A, B, C) + d[7 + 16 * blk] + 0x5a827999), 5);
		C = rotintlft((C + G(D, A, B) + d[11 + 16 * blk] + 0x5a827999), 9);
		B = rotintlft((B + G(C, D, A) + d[15 + 16 * blk] + 0x5a827999), 13);

	}

	void round3(int blk) {
		A = rotintlft((A + H(B, C, D) + d[0 + 16 * blk] + 0x6ed9eba1), 3);
		D = rotintlft((D + H(A, B, C) + d[8 + 16 * blk] + 0x6ed9eba1), 9);
		C = rotintlft((C + H(D, A, B) + d[4 + 16 * blk] + 0x6ed9eba1), 11);
		B = rotintlft((B + H(C, D, A) + d[12 + 16 * blk] + 0x6ed9eba1), 15);

		A = rotintlft((A + H(B, C, D) + d[2 + 16 * blk] + 0x6ed9eba1), 3);
		D = rotintlft((D + H(A, B, C) + d[10 + 16 * blk] + 0x6ed9eba1), 9);
		C = rotintlft((C + H(D, A, B) + d[6 + 16 * blk] + 0x6ed9eba1), 11);
		B = rotintlft((B + H(C, D, A) + d[14 + 16 * blk] + 0x6ed9eba1), 15);

		A = rotintlft((A + H(B, C, D) + d[1 + 16 * blk] + 0x6ed9eba1), 3);
		D = rotintlft((D + H(A, B, C) + d[9 + 16 * blk] + 0x6ed9eba1), 9);
		C = rotintlft((C + H(D, A, B) + d[5 + 16 * blk] + 0x6ed9eba1), 11);
		B = rotintlft((B + H(C, D, A) + d[13 + 16 * blk] + 0x6ed9eba1), 15);

		A = rotintlft((A + H(B, C, D) + d[3 + 16 * blk] + 0x6ed9eba1), 3);
		D = rotintlft((D + H(A, B, C) + d[11 + 16 * blk] + 0x6ed9eba1), 9);
		C = rotintlft((C + H(D, A, B) + d[7 + 16 * blk] + 0x6ed9eba1), 11);
		B = rotintlft((B + H(C, D, A) + d[15 + 16 * blk] + 0x6ed9eba1), 15);

	}

	void round4(int blk) {
		System.out.println(" must be md5, in round4!");
	}
}