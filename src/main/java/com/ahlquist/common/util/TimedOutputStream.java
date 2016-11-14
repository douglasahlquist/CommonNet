/*   @(#)  TimedOutputStream.java  2002-02-04
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

import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * TimedOutputStream is an output stream that adds time-outs to write
 * operations. This is needed since the the Java socket time-out only applies to
 * reads. (By default the Java socket write can block forever.) This class
 * contains a watcher thread inner class that periodically checks if a write
 * operation has timed out.
 *
 * @author Douglas Ahlquist
 */
public class TimedOutputStream extends OutputStream {

	final static Logger logger = Logger.getLogger(TimedOutputStream.class);

	/** Socket we are writing to */
	private Socket socket;

	/** The underlying output stream */
	private OutputStream os;

	/** Time when the write operation will time-out (in milliseconds) */
	private long timeOutTime;

	/** The time-out value in milliseconds */
	private long timeOutValue = 60 * 1000;

	/**
	 * Wrap inside list item (for the list of pending writes) for performance
	 */
	private TosListItem listItem;

	/**
	 * Creates a new output stream with time-out. All methods inherited from
	 * OutputStream have been synchronized to serialize access.
	 *
	 * @param socket
	 *            the socket to create an OutputStream for
	 */
	public TimedOutputStream(Socket socket) throws IOException {
		this.socket = socket;
		os = socket.getOutputStream();
		listItem = new TosListItem(this);
	}

	/**
	 * Set the time-out value (in millis).
	 *
	 * @param timeOut
	 *            the time-out value in milliseconds
	 */
	public void setTimeout(int timeOut) {
		timeOutValue = timeOut;
	}

	/**
	 * Write with time-out. Writes the specified byte to this output stream. The
	 * general contract for <code>write</code> is that one byte is written to
	 * the output stream. The byte to be written is the eight low-order bits of
	 * the argument <code>b</code>. The 24 high-order bits of <code>b</code> are
	 * ignored.
	 * <p>
	 * Subclasses of <code>OutputStream</code> must provide an implementation
	 * for this method.
	 *
	 * @param b
	 *            the <code>byte</code>.
	 * @exception IOException
	 *                if an I/O error occurs. In particular, an
	 *                <code>IOException</code> may be thrown if the output
	 *                stream has been closed.
	 */
	public synchronized void write(int b) throws IOException {
		start();
		try {
			os.write(b);
		} finally {
			done();
		}
	}

	/**
	 * Write with time-out. Writes <code>b.length</code> bytes from the
	 * specified byte array to this output stream. The general contract for
	 * <code>write(b)</code> is that it should have exactly the same effect as
	 * the call <code>write(b, 0, b.length)</code>.
	 *
	 * @param b
	 *            the data.
	 * @exception IOException
	 *                if an I/O error occurs.
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	public synchronized void write(byte b[]) throws IOException {
		start();
		try {
			os.write(b);
		} finally {
			done();
		}
	}

	/**
	 * Write with time-out. Writes <code>len</code> bytes from the specified
	 * byte array starting at offset <code>off</code> to this output stream. The
	 * general contract for <code>write(b, off, len)</code> is that some of the
	 * bytes in the array <code>b</code> are written to the output stream in
	 * order; element <code>b[off]</code> is the first byte written and
	 * <code>b[off+len-1]</code> is the last byte written by this operation.
	 * <p>
	 * The <code>write</code> method of <code>OutputStream</code> calls the
	 * write method of one argument on each of the bytes to be written out.
	 * Subclasses are encouraged to override this method and provide a more
	 * efficient implementation.
	 * <p>
	 * If <code>b</code> is <code>null</code>, a
	 * <code>NullPointerException</code> is thrown.
	 * <p>
	 * If <code>off</code> is negative, or <code>len</code> is negative, or
	 * <code>off+len</code> is greater than the length of the array
	 * <code>b</code>, then an <tt>IndexOutOfBoundsException</tt> is thrown.
	 *
	 * @param b
	 *            the data.
	 * @param off
	 *            the start offset in the data.
	 * @param len
	 *            the number of bytes to write.
	 * @exception IOException
	 *                if an I/O error occurs. In particular, an
	 *                <code>IOException</code> is thrown if the output stream is
	 *                closed.
	 */
	public synchronized void write(byte b[], int off, int len) throws IOException {
		start();
		try {
			os.write(b, off, len);
		} finally {
			done();
		}
	}

	/**
	 * Flushes this output stream and forces any buffered output bytes to be
	 * written out. The general contract of <code>flush</code> is that calling
	 * it is an indication that, if any bytes previously written have been
	 * buffered by the implementation of the output stream, such bytes should
	 * immediately be written to their intended destination.
	 * <p>
	 * The <code>flush</code> method of <code>OutputStream</code> does nothing.
	 *
	 * @exception IOException
	 *                if an I/O error occurs.
	 */
	public synchronized void flush() throws IOException {
		start();
		try {
			os.flush();
		} finally {
			done();
		}
	}

	/**
	 * Closes this output stream and releases any system resources associated
	 * with this stream. The general contract of <code>close</code> is that it
	 * closes the output stream. A closed stream cannot perform output
	 * operations and cannot be reopened.
	 * <p>
	 * The <code>close</code> method of <code>OutputStream</code> does nothing.
	 *
	 * @exception IOException
	 *                if an I/O error occurs.
	 */
	public synchronized void close() throws IOException {
		start();
		if (os != null) {
			try {
				os.close();
			} finally {
				done();
			}
		}
	}

	/**
	 * Start a write operation
	 */
	private void start() {
		timeOutTime = System.currentTimeMillis() + timeOutValue;
		Watcher.getInstance().add(listItem);
	}

	/**
	 * Complete a write operation
	 */
	private void done() {
		Watcher.getInstance().remove(listItem);
	}

	/**
	 * Returns a String object representing this connection.
	 */
	public String toString() {
		return "Socket=" + socket + ", time-out value=" + timeOutValue + ", time-out at " + new Date(timeOutTime);
	}

	/**
	 * This is a watcher class that checks if a write operation hasn't completed
	 * within the time-out period.
	 *
	 * @author Douglas Ahlquist
	 */
	private static class Watcher extends Thread {
		/** Singleton instance */
		private static Watcher instance = null;

		/** List of pending writes */
		private TosList list = new TosList();

		/** Return singleton instance */
		private static Watcher getInstance() {
			if (instance == null) {
				synchronized (Watcher.class) {
					if (instance == null) {
						instance = new Watcher();
						instance.start();
					}
				}
			}
			return instance;
		}

		private Watcher() {
			super("TimedOutputStream Watcher");
			this.setDaemon(true);
		}

		/** Add to list of pending writes */
		synchronized void add(TosListItem item) {
			list.insert(item);
		}

		/** Remove from list of pending writes */
		synchronized void remove(TosListItem item) {
			list.remove(item);
		}

		/** Check list of pending writes for time-outs */
		public void run() {
			while (true) {
				// Check every 5 seconds
				Util.sleepMs(5 * 1000);

				long now = System.currentTimeMillis();
				synchronized (this) {
					logger.debug("TimedOutputStream - Starting check");
					for (TosListItem li = list.getFirst(); li != null; li = list.getNext(li)) {
						TimedOutputStream tos = (TimedOutputStream) li.getElement();
						if (tos.timeOutTime < now) {
							// Time-out, force IO exception by closing socket
							logger.debug("TimedOutputStream - Write time-out, closing socket: " + tos);
							try {
								tos.socket.close();
							} catch (IOException e2) {
							}
						} else {
							logger.debug("TimedOutputStream - Write in progress: " + tos);
						}
					}
					logger.debug("TimedOutputStream - Completed check");
				}
			}
		}
	}

	/**
	 * Generic list item
	 *
	 * @author Douglas Ahlquist
	 */
	private static class TosListItem extends MyObject {
		TosListItem prev = null;
		TosListItem next = null;
		private Object element;

		Object getElement() {
			return element;
		}

		TosListItem(Object element) {
			this.element = element;
		}
	}

	/**
	 * Generic list class
	 *
	 * @author Douglas Ahlquist
	 */
	private static class TosList extends MyObject {
		private TosListItem head = null;

		TosList() {
		}

		TosListItem getFirst() {
			return head;
		}

		TosListItem getNext(TosListItem entry) {
			return entry.next;
		}

		/** place entry at the front of the linked list */
		void insert(TosListItem entry) {
			entry.prev = null;
			entry.next = head;
			if (head != null)
				head.prev = entry;
			head = entry;
		}

		/** remove entry from the linked list */
		void remove(TosListItem entry) {
			if (entry.prev != null)
				entry.prev.next = entry.next;
			if (entry.next != null)
				entry.next.prev = entry.prev;
			if (entry == head)
				head = entry.next;
		}
	}
}
