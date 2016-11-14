/*   @(#)  ByteBuffer.java  2002-02-04
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
 * @(#).java    1.0 01/02/24
 * Copyright 1999-2001 Ahlquist Consulting & Idaho Consulting, Inc. All Rights Reserved.
 *
 * Ahlquist Consulting & Idaho Consulting grants you ("Licensee") a non-exclusive, 
 * royalty free, license to use and modify.  The redistribution of this software in 
 * source and binary code in any form is strictly prohibited without written permission.
 * Apon written approval licensee shall provide notice of
 *    i) this copyright notice and license appear on all copies of the software; and 
 *   ii) Licensee does not utilize the software in a manner which is disparaging 
 *       to Ahlquist Consulting & Idaho Consulting.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. AHLQUIST CONSULTING, IDAHO CONSULTING AND 
 * ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE 
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. 
 * IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, 
 * PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER  * CAUSED AND REGARDLESS 
 * OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO 
 * USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */

package com.ahlquist.common.util;

/**
 * ByteBuffer class encapsulates a byte array. It will grow as needed. <br>
 *
 * This class does not handle concurrency. No method is synchronized.
 *
 * @author Douglas Ahlquist
 */
public final class ByteBuffer extends MyObject {
	/** The stored byte array */
	private byte value[];

	/** The length of the array */
	private int length;

	/** The number of byte stored */
	private int count;

	/**
	 * Constructor with default (16) size.
	 */
	public ByteBuffer() {
		this(16);
	}

	/**
	 * Constructor
	 *
	 * @param length
	 *            the initial size of the string
	 */
	public ByteBuffer(int length) {
		count = 0;
		value = new byte[length];
		this.length = length;
	}

	/**
	 * Clears the string, i.e. sets its length to zero.
	 */
	public void clear() {
		count = 0;
	}

	/**
	 * Returns the length of this byteArray.
	 *
	 * @return the number of bytes.
	 */
	public int length() {
		return count;
	}

	/**
	 * Sets the length of this byte array. If the new length is longer than the
	 * current length, the byte array will grow and get filled with null bytes.
	 *
	 * @param newLength
	 *            the new length of this byte array
	 */
	public void setLength(int newLength) {
		// Grow the array as needed
		if (newLength > length)
			grow(newLength);

		// Fill with null characters if new length is longer
		if (count < newLength) {
			while (count < newLength)
				value[count++] = 0;
		} else
			count = newLength;
	}

	/**
	 * Returns the byte at index position.
	 *
	 * @param index
	 *            the position of the byte to return
	 * @return byte the byte at index position
	 */
	public byte byteAt(int index) {
		return (value[index]);
	}

	/**
	 * Appends an byte array.
	 *
	 * @param byteArray
	 *            a byte array to append to this buffer
	 *
	 * @return ByteBuffer this ByteBuffer object
	 */
	public ByteBuffer append(byte[] byteArray) {
		append(byteArray, 0, byteArray.length);
		return this;
	}

	/**
	 * Appends an byte array to the end of this ByteBuffer.
	 *
	 * @param byteArray
	 *            a byte array to append to this buffer
	 * @param start
	 *            starting point to copy from
	 * @param end
	 *            end point to copy from
	 *
	 * @return ByteBuffer this ByteBuffer object
	 */
	public ByteBuffer append(byte[] byteArray, int start, int end) {
		int len = end - start;
		int newcount = count + end - start;
		if (newcount > value.length)
			grow(newcount);
		System.arraycopy(byteArray, start, value, count, len);
		count = newcount;

		return this;
	}

	/**
	 * Appends an ByteBuffer object to the end of this ByteBuffer.
	 *
	 * @param byteBuffer
	 *            the ByteBuffer to append
	 *
	 * @return ByteBuffer this ByteBuffer object
	 */
	public ByteBuffer append(ByteBuffer byteBuffer) {
		int len = byteBuffer.length();
		int newcount = count + len;
		if (newcount > length)
			grow(newcount);
		System.arraycopy(byteBuffer.getBytes(), 0, value, count, len);
		count = newcount;

		return this;
	}

	/**
	 * Appends a byte at the end of this ByteBuffer.
	 *
	 * @param c
	 *            the byte to append
	 *
	 * @return ByteBuffer this ByteBuffer object
	 */
	public ByteBuffer append(byte c) {
		if (count == length)
			grow(count + 1);
		value[count++] = c;
		return this;
	}

	/**
	 * append a string to the ByteBuffer
	 * 
	 * @param s
	 *            - the string to append
	 * @return the new ByteBuffer
	 */
	public ByteBuffer append(String s) {
		append(s.getBytes(), 0, s.length());
		return this;
	}

	/**
	 * Returns data of this byte array.
	 * 
	 * @return byte[] byte array
	 */
	public byte[] getBytes() {
		byte[] bytes = new byte[count];
		System.arraycopy(value, 0, bytes, 0, count);
		return bytes;
	}

	/**
	 * Grows the byte array.
	 *
	 * @param minimumCapacity
	 *            the new minimum size
	 */
	private void grow(int minimumCapacity) {
		int newCapacity = (length + 1) * 2;
		if (minimumCapacity > newCapacity)
			newCapacity = minimumCapacity;
		byte newValue[] = new byte[newCapacity];
		System.arraycopy(value, 0, newValue, 0, count);
		value = newValue;
		length = newCapacity;
	}

	/**
	 * Converts this object to a String object.
	 *
	 * @return String object
	 */
	public String toString() {
		return new String(value);
	}
}
