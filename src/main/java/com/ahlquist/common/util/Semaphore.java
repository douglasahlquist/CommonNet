/*   @(#)  Semaphore.java  2002-02-04
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

public final class Semaphore extends MyObject {
	/** The semaphore use counter */
	private int counter;

	/** The number of blocked threads in queue waiting for this semaphore */
	private int queueSize;

	/**
	 * Creates a semaphore with an initial use size.
	 *
	 * @param size
	 *            the initial size of the semaphore
	 */
	public Semaphore(int size) {
		counter = size;
		queueSize = -size;
	}

	/**
	 * Aquire this semaphore. (Will block if max use count has been reached.)
	 */
	public synchronized void acquire() {
		queueSize++;
		while (counter == 0) {
			try {
				this.wait();
			} catch (InterruptedException e) {
			//TODO: DKA	new MyError(ErrorTypes.JAVA_WAIT_INTERRUPTED, "Semaphore", e).escalateDiagnostic();
			}
		}
		counter--;
	}

	/**
	 * Release this semaphore. (If threads are blocked waiting for this
	 * semaphore, one of them will be woken up.)
	 */
	public synchronized void release() {
		queueSize--;
		counter++;
		this.notify();
	}

	/**
	 * Release this semaphore only if other threads are blocked waiting for it
	 */
	public synchronized boolean releaseIfWaiting() {
		if (queueSize > 0) {
			release();
			return true;
		}
		return false;
	}

	/**
	 * Returns the number of blocked threads in queue waiting for the semaphore
	 *
	 * @return the queue size
	 */
	public synchronized int getQueueSize() {
		if (queueSize < 0)
			return 0;
		else
			return queueSize;
	}

	/**
	 * Converts this object to a String object.
	 *
	 * @return String object
	 */
	public String toString() {
		return "Counter: " + counter + ", Queue size: " + getQueueSize();
	}
}
