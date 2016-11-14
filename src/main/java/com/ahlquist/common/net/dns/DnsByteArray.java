
package com.ahlquist.common.net.dns;

import com.ahlquist.common.util.MyObject;

// FUTURE: Consider a more descriptive class name

/**
 * This class decodes dns primitives from a byte array and keeps state
 * (position). (Some similarities to a ByteArrayInputStream.)
 *
 * @author Douglas Ahlquist 12/29/2002
 */
final class DnsByteArray extends MyObject {
	private byte[] array;
	private int length;
	private int offset;

	private StringBuffer name;

	/**
	 * Constructor
	 */
	DnsByteArray(byte[] array, int length) {
		this.array = array;
		this.length = length;
		offset = 0;
	}

	/**
	 * If we are past the end, i.e. the offset has passed the length of the
	 * DnsByteArray.
	 */
	boolean isPastEnd() {
		// If offset == length, we have not yet read past the end.
		// It is the next read that would make us go past the end.
		// The statement "array[offset++]" does increment offset
		// before throwing an ArrayIndexOutOfBoundsException.
		// Therefore this ("offset > length") is correct.
		return offset > length;
	}

	/**
	 * Skips i bytes in the DnsByteArray and advances the current position.
	 *
	 * @param i
	 *            the number of byte positions to advance
	 */
	void skip(int i) {
		offset += i;
	}

	/**
	 * Reads the next byte from the array
	 */
	byte readByte() {
		return array[offset++];
	}

	/**
	 * Reads a 16-bit (2-byte) integer from the array
	 */
	int readTwoByteInt() {
		return ((array[offset++] & 0xFF) << 8) | (array[offset++] & 0xFF);
	}

	/**
	 * Reads a 32-bit (4-byte) integer from the array
	 */
	int readFourByteInt() {
		return ((array[offset++] & 0xFF) << 24) | ((array[offset++] & 0xFF) << 16) | ((array[offset++] & 0xFF) << 8)
				| (array[offset++] & 0xFF);
	}

	/**
	 * Reads a domain name (sequence of labels) from the array and does all
	 * necessary decoding and decompression.
	 */
	String readName() {
		if (name == null)
			name = new StringBuffer();
		else
			name.setLength(0);

		int nextOffset = 0;
		int labelLen = array[offset++] & 0xFF;

		// For each label... (End of domain name is marked by a zero-length
		// label.)
		while (labelLen != 0) {
			// Is this a pointer (compressed label) ?
			if ((labelLen & 0xC0) == 0xC0) {
				int pointer = ((labelLen & 0x3F) << 8) | (array[offset++] & 0xFF);
				// Save the offset if this is the first time we find a pointer
				if (nextOffset == 0)
					nextOffset = offset;
				// The new offset is the pointer
				offset = pointer;
				labelLen = array[offset++] & 0xFF;
			}
			// Decode label
			else {
				// REVIEWed: This statement is superflous for a well formed
				// message.
				// The length is the 6 least significant bits (max label length
				// is 63).
				labelLen &= 0x3F;

				// Add the label characters to the StringBuffer
				while (labelLen > 0) {
					name.append((char) array[offset++]);
					labelLen--;
				}

				labelLen = array[offset++] & 0xFF;
				// Append dot if this isn't the last label
				if (labelLen != 0)
					name.append('.');
			}
		}

		// Restore offset in case we got a pointer earlier
		if (nextOffset != 0)
			offset = nextOffset;

		return name.toString();
	}
}
