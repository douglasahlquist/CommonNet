/*   @(#)  Util.java  2002-02-04
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
import java.net.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Disparate collection of static methods.
 */
public final class Util {
	// note: could be one or more chars
	public static String newLine = System.getProperty("line.separator"); 
	/** @see getLocalHostName() */
	public static String localHostName;

	
	public <T> List<T> ListCopy(List<T> list){
		return new ArrayList<>(list);
	}
	

	/**
	 * Returns the name of the machine where this JVM is running. Note: This
	 * method is meant to be used for debugging purposes only since it swallows
	 * errors...
	 */
	public final static String getLocalHostName() {
		if (localHostName == null) {
			try {
				localHostName = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException ex) {
				// TODO: Errors.println(ex); // sic
				localHostName = "<unknown>";
			}
		}
		return (localHostName);
	}

	public final static void sleepMs(long ms) {
		if (ms > 0) {
			try {
				Thread.sleep(ms);
			} catch (InterruptedException e) {
				// Thread.currentThread().dumpStack();
			}
		}
	}

	// used in exponential sleep algorithm
	// CONFIG: consider moving to Config.
	private static final int msMinSleep = 100;
	private static final int msMaxSleep = 60000;

	/**
	 * Implements simple exponential sleep based on number of failures (count).
	 */
	public final static void sleepExp(int countFailures) {
		long ms = (long) (msMinSleep * Math.pow(countFailures, 1.5));
		sleepMs(Math.min(ms, msMaxSleep));
	}

	public static final void sleepRandMs(int ms) {
		sleepMs((long) (Math.random() * ms));
	}

	/**
	 * Convert an exception to a string representing the stacktrace.
	 * @param ex - the Throwable (eg, an Exception) to be converted.
	 */
	public static String getStackTrace(Throwable ex) {
		CharArrayWriter caw = new CharArrayWriter();
		PrintWriter pw = new PrintWriter(caw);
		ex.printStackTrace(pw);
		pw.flush();
		pw.close();
		caw.close();
		return (caw.toString());
	}

	public static String getDescription(Throwable ex) {
		return (Util.getShortName(ex) + ": " + ex.getMessage());
	}

	public static String getShortName(Object obj) {
		String name = obj.getClass().getName();
		int index = name.lastIndexOf('.');
		return (index == -1 ? name : name.substring(index + 1));
	}

	/**
	 * Recursively deletes an entire directory hierarchy.
	 * @return true iff the directory hierarchy was successfully deleted.
	 */
	public static boolean deleteRecursive(File dir) {
		if (!dir.exists())
			return (true);

		boolean success = true;
		String files[] = dir.list();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File file = new File(dir, files[i]);
				if (file.isDirectory())
					success &= deleteRecursive(file);
				else
					success &= file.delete();
			}
		}

		success &= dir.delete();
		return (success);
	}

	public static boolean createFile(File file) {
		OutputStream out = null;
		try {
			out = new MyOutputStream("Util.create file", new FileOutputStream(file));
			return (true);
		} catch (IOException e) {
			return (false);
		} finally {
			close(out);
		}
	}

	public static boolean compareFiles(File file1, File file2) {
		if (file1.length() != file2.length())
			return (false);

		InputStream in1 = null;
		InputStream in2 = null;
		try {
			byte buf1[] = new byte[1024];
			byte buf2[] = new byte[1024];
			in1 = new MyInputStream("Util.compare file", new FileInputStream(file1));
			in2 = new MyInputStream("Util.compare file", new FileInputStream(file2));
			while (true) {
				int cbRead1 = in1.read(buf1);
				int cbRead2 = in2.read(buf2);

				if (cbRead1 == -1 && cbRead2 == -1)
					return (true);
				if (cbRead1 != cbRead2) {
					return (false);
				}
				if (!compare(buf1, buf2, cbRead1))
					return (false);
			}
		} catch (IOException ex) {
			return (false);
		} finally {
			close(in1);
			close(in2);
		}
	}

	public static boolean compare(byte[] buf1, byte[] buf2, int cbMax) {
		for (int ib = 0; ib < cbMax; ib++)
			if (buf1[ib] != buf2[ib])
				return (false);

		return (true);
	}

	public static int copyFileFromTo(File fileFrom, File fileTo) throws IOException {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new MyInputStream("Util.copy file", new FileInputStream(fileFrom));
			out = new MyOutputStream("Util.copy file", new FileOutputStream(fileTo));
			int cb = cbCopyInOut(in, out);
			return (cb);
		} finally {
			close(in);
			close(out);
		}
	}

	/**
	 * Copy bytes from input stream to output stream until input gives EOF.
	 * 
	 * @return the number of bytes copied.
	 */
	public static int cbCopyInOut(InputStream in, OutputStream out) throws IOException {
		int cbRead;
		int cbReadTotal = 0;
		byte rgbCopy[] = new byte[1024];
		while ((cbRead = in.read(rgbCopy)) > 0) {
			cbReadTotal += cbRead;
			out.write(rgbCopy, 0, cbRead);
		}

		return (cbReadTotal);
	}

	public final static void close(InputStream in) {
		// Close the input stream, ignoring errors.
		if (in != null)
		try {
			
				in.close();
		} catch (IOException e) {
		}
	}

	public final static void close(OutputStream out) {
		// Close the output stream, ignoring errors.
		try {
			if (out != null)
				out.close();
		} catch (IOException e) {
		}
	}

	public final static void close(Writer out) {
		// Close the writer, ignoring errors.
		if (out != null)
		try {
			
				out.close();
		} catch (IOException e) {
		}
	}

	public final static void close(Reader in) {
		// Close the reader, ignoring errors.
		if (in != null)
		try {
				in.close();
		} catch (IOException e) {
		}
	}

	public final static void close(java.net.Socket socket) {
		// Close the socket, ignoring errors.
		if (socket != null)
		try {
			
				socket.close();
		} catch (IOException e) {
		}
	}

	public final static void close(java.net.ServerSocket socket) {
		// Close the socket, ignoring errors.
		if (socket != null)
		try {
				socket.close();
		} catch (IOException e) {
		}
	}

	public final static void close(java.net.HttpURLConnection hconn) {
		if (hconn != null)
		try {
				hconn.disconnect();
		} catch (Exception e) {
		}
	}

	// FUTURE: move this method to ServerSocketListener
	public static ServerSocket getServerSocket(int port) {
		int cFailures = 0;
		while (true) {
			try {
				return (new ServerSocket(port));
			} catch (IOException ex) { // BindException
				// TODO: new Exception(ErrorTypes.PORT_BIND_FAILURE,"port = " +
				// port, ex).escalateSleep(++cFailures);
				// TODO: new MyError(ErrorTypes.PORT_BIND_FAILURE,"port = " +
				// port, ex).escalateSleep(++cFailures);
			}
		}
	}

	public static int getNextPrime(int lx) {
		long w = (long) lx;
		long i;

		if (w <= 2)
			return (3);
		if ((w % 2) == 0)
			w++;
		while (true) {
			i = 3;
			while (true) {
				if ((i * i) > w)
					return ((int) w);
				if ((w % i) == 0)
					break;
				i += 2;
			}
			w += 2;
		}
	}

}
