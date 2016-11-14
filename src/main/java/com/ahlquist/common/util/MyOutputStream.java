/*   @(#)  MyOutputStream.java  2002-02-04
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

//import com.ahlquist.common.dump.*;
import java.io.*;
import java.net.Socket;

/**
 * This class encapsulates an OutputStream to record its creation, destruction
 * and usage.
 */
public final class MyOutputStream extends OutputStream {
	private OutputStream out;
	private Throwable createTrace;

	// TODO: private static IOMonitor ioMonitor = new IOMonitor("Output
	// streams");

	// TODO: private IOStreamStats stats;

	// TODO: public static Dump getStreamDump() { return(ioMonitor.getDump()); }

	// TODO: public static String getReportAll() {
	// return(ioMonitor.getReport()); }

	public MyOutputStream(String name, Socket socket) throws IOException {
		this(name, socket.getOutputStream());
	}

	public MyOutputStream(String name, OutputStream out) {
		this.out = out;

		// TODO: stats = ioMonitor.getStats(name + " out");
		// TODO: stats.created();

		// TODO: Debug.assert(out != null);
		createTrace = new Throwable(); // REVIEW: add a debug flag to make this
										// conditional
	}

	public void finalize() throws Throwable {
		// DO NOT CREATE THE THROWABLE HERE SINCE IT WILL NOT TELL US
		// WHERE THE CONNECTION WAS CREATED.
		// TODO: Debug.assertResourceNull(stats.getName(), out, createTrace);
		close();
		super.finalize();
	}

	public void close() throws IOException {
		if (out != null) {
			out.close();
			// TODO:Debug.MO.println("closing output stream: " +
			// stats.getName());
			out = null;
			// TODO: stats.destroyed();
		}
	}

	/**
	 * Releases stats w/o closing the stream. This is needed by "filter"
	 * streams.
	 */
	public void release() {
		if (out != null) {
			out = null;
			// TODO: stats.destroyed();
		}
	}

	/**
	 * The remaining methods simply call the corresponding method in the given
	 * OutputStream <code>out</code>, tracking number of bytes written.
	 */

	public void write(int b) throws IOException {
		if (out != null) {
			out.write(b);
			// TODO: stats.addBytesWritten(1);
		}
	}

	public void write(byte b[]) throws IOException {
		if (out != null) {
			out.write(b);
			// TODO: stats.addBytesWritten(b.length);
		}
	}

	public void write(byte b[], int off, int len) throws IOException {
		if (out != null) {
			out.write(b, off, len);
			// TODO: stats.addBytesWritten(len);
		}
	}

	public void flush() throws IOException {
		if (out != null)
			out.flush();
	}

	public String toString() {
		// TODO: return stats.getName();
		return "NOT IMPLEMENTED";
	}
}
