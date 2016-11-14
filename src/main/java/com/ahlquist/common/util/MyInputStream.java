/*   @(#)  MyInputStream.java  2002-02-04
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

import java.io.*;
import java.net.Socket;

/**
 * This class encapsulates an InputStream to record its creation, destruction
 * and usage.
 */
public final class MyInputStream extends InputStream {
	private InputStream in;
	private Throwable createTrace;

	private String name;

	public MyInputStream(String name, Socket socket) throws IOException {
		this(name, socket.getInputStream());
	}

	public MyInputStream(String streamName, InputStream in) {
		this.in = in;
		this.name = streamName + " in";

		// REVIEW: add a debug flag to make this conditional
		createTrace = new Throwable(); 
	}

	public void finalize() throws Throwable {
		close();
		super.finalize();
	}

	public void close() throws IOException {
		if (in != null) {
			in.close();
			in = null;
		}
	}

	/**
	 * The remaining methods simply call the corresponding method in the given
	 * InputStream <code>in</code>, tracking the number of bytes read.
	 */
	public synchronized int read() throws IOException {
		int b = in.read();
		return (b);
	}

	public synchronized int read(byte b[]) throws IOException {
		int cb = in.read(b);
		return (cb);
	}

	public synchronized int read(byte b[], int off, int len) throws IOException {
		int cb = in.read(b, off, len);
		return (cb);
	}

	public long skip(long n) throws IOException {
		return (in.skip(n));
	}

	public int available() throws IOException {
		return (in.available());
	}

	public synchronized void mark(int readlimit) {
		in.mark(readlimit);
	}

	public synchronized void reset() throws IOException {
		in.reset();
	}

	public boolean markSupported() {
		return (in.markSupported());
	}

	public synchronized void rename(String newName) {

	}

	public String toString() {
		return name;
	}
}
