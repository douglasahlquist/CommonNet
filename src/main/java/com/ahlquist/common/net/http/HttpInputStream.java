/*   @(#)  HttpInputStream.java  2002-02-04
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
*/
//package com.ahlquist.common.net.http;
package com.ahlquist.common.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
//import com.ahlquist.common.util.*;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.ahlquist.common.util.ByteBuffer;
import com.ahlquist.common.util.Util;

/**
 * HttpInputStream class is a package class and is used to read data from Http
 * session.
 * <p>
 *
 * This class contains an InputStream to read data from. For performance and
 * internationalization reason, when we read data from input Http session, we
 * want to read/save as byte array.
 * <p>
 * It also provides a readline method to read Http headers.
 * <p>
 *
 * @author Douglas Ahlquist
 * 
 */
public class HttpInputStream // extends myObject
{
	final static Logger logger = Logger.getLogger(HttpInputStream.class);
	/** constant */
	public final String HTTP_NEW_LINE = "\r\n";
	/** max size of a message */
	// private final long MAX_MESSAGE_SIZE = Config.getInt("Http.MaxMsgSize");

	/** we expect InputStream is a MyInputStream */
	private InputStream in;
	private ByteBuffer buffer = new ByteBuffer(128); // reusable buffer

	/**
	 * Construct an HttpInputStream by a live socket
	 *
	 * @param socket
	 *            a live socket talking HTTP language
	 */
	public HttpInputStream(Socket socket) throws IOException {
		this(new com.ahlquist.common.util.MyInputStream("HttpInputStream", socket.getInputStream()));
	}

	/**
	 * Construct an HttpInputStream by an InputStream
	 *
	 * @param in
	 *            an InputStream
	 * 
	 */
	public HttpInputStream(InputStream in) {
		this.in = in;
	}

	/**
	 * this method will close InputStream
	 */
	public void close() {
		buffer = null;
		Util.close(in);
	}

	/**
	 * Read up to len bytes of data from this input stream into an array of
	 * bytes. <br>
	 * This method blocks until some input is available. If the argument b is
	 * null, a NullPointerException is thrown.
	 *
	 * @param b
	 *            the byte array to read to
	 * @param off
	 *            starting point to read to
	 * @param len
	 *            the length of bytes to read up to
	 *
	 * @return byte[] byte array
	 *
	 * @exception IOException
	 */
	int read(byte[] b, int off, int len) throws IOException {
		return (in.read(b, off, len));
	}

	/**
	 * Read a line from the input stream. A line is terminated by a NL (lone NL
	 * or CR-NL). The line terminator is not returned as part of the returned
	 * String. Returns empty string if no data is available.
	 * <p>
	 *
	 *
	 * @return String a line read from inputStream, return empty string if hit
	 *         end of file before read any chars. <br>
	 *         Should we return null for SocketException and
	 *         InterruptedIOException?
	 * @exception IOException
	 */
	public String readLine() throws IOException {
		// clean the buffer
		if (buffer == null)
			buffer = new ByteBuffer(128);
		else
			buffer.clear();

		int c1;

		while ((c1 = in.read()) != -1) {
			if (c1 == '\n') { // Got NL, outa here.
				break;
			} else if (c1 == '\r') {
				// Got CR, is the next char NL ?
				int c2 = in.read();
				if (c2 != '\n') {
					// If not NL, push it back
					if (!(in instanceof PushbackInputStream))
						in = new PushbackInputStream(in);
					((PushbackInputStream) in).unread(c2);
					// REVIEW: this change the myInputStream to
					// PushbackInputStream.
					// Will this ever happen? HTTP should use CRLF
					// as new line, not single "\r".
				}
				break; // outa here.
			}

			// Not CR, NL or CR-NL ...
			// .. Insert the byte into our byte buffer
			buffer.append((byte) c1);
		}

		if (c1 == -1) {
			if (buffer.length() == 0)
				logger.debug("Read end of file before reading anything!");
			else
				logger.debug("Read end of file before finish reading a line!");
		}

		logger.debug("reading buffer: " + new String(buffer.getBytes()));
		return new String(buffer.getBytes());
	}

	/**
	 * Read a Http data from the containing input stream. Read until the end of
	 * file. Returns null if no data is available.
	 * <p>
	 *
	 * @return byte[] byte array
	 * @exception IOException
	 */
	byte[] readData() throws IOException {
		// clean up buffer
		if (buffer == null)
			buffer = new ByteBuffer(128);
		else
			buffer.clear();
		int c1;

		while ((c1 = in.read()) != -1) {
			buffer.append((byte) c1);
		}

		// get byte[] from buffer
		return buffer.getBytes();
	}

	/**
	 * private method, append char to buffer.
	 *
	 * @param b
	 *            byte to append
	 */
	protected void append(byte b) throws IOException {
		buffer.append(b);
		// Prevent too much data from being input
		// Don't mind the strange hard coded numbers.
		// cbRead += line.length() + 100; // Add bytes for each line, esp empty.
		if (++cbRead > 1000000)
			throw new IOException("Input too large");
	}

	private int cbRead = 0;

	public InputStream getInputStream() {
		return in;
	}

}
