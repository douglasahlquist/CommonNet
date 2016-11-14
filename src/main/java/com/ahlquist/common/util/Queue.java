/*   @(#)  Queue.java  2002-02-04
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

import org.apache.log4j.Logger;

//
// Queue
//
//    Implements a fixed length queue.  Caller must provide sychronization as
//    well as any sleep/wakeup mechanism; indeed, enqueuing to a full queue
//    (or dequeuing from an empty queue) causes an assertion.
//
// NB: There is always one empty element in the queue, so we tend to make sizes
//        one more than their intended value.  This is an implementation decision/
//        flaw.  The parent must handle the size translation (we include the
//        empty element in our counts).
//
// FUTURE: Right now, readers and writers wait on the same object (this).
//           This causes excessive wakeup events, as a reader dequeuing an
//           an element should only wakeup the writers and vice versa.
//           In addition, this means we must use notifyAll().  What we need is
//           a way to lock the wait variable, drop our current lock, then wait
//           on the wait variable; I don't know how to do this.
class Queue {
	
	final static Logger logger = Logger.getLogger(Queue.class);
	private int iobjMax;
	private int iobjNextRead = 0;
	private int iobjNextWrite = 0;
	private Object rgobj[];
	private int cobj; // Keep this current!

	Queue(int iobjMax) {
		this.iobjMax = iobjMax;
		rgobj = new Object[iobjMax];
		// {{INIT_CONTROLS
		// }}
	}

	private Queue(Queue q) {
		iobjMax = q.iobjMax;
		iobjNextRead = q.iobjNextRead;
		iobjNextWrite = q.iobjNextWrite;
		rgobj = new Object[iobjMax];

		// Copy q array: q.rgobj[0,q.iobjMax) -> rgobj[0,iobjMax)
		System.arraycopy(q.rgobj, 0, rgobj, 0, iobjMax);
	}

	boolean isQueueFull() // Caller should hold lock.
	{
		return (((iobjNextWrite + 1) % iobjMax) == iobjNextRead);
	}

	boolean isQueueEmpty() // Caller should hold lock.
	{
		return (iobjNextWrite == iobjNextRead);
	}

	final int elementCount() {
		Debug._assert((iobjNextWrite - iobjNextRead + ((iobjNextWrite < iobjNextRead) ? iobjMax : 0)) == cobj);
		return (cobj);
	}

	void clearAll() {
		cobj = iobjNextRead = iobjNextWrite = 0;
	}

	// setSize() -- resize the queue, returning any objects that no longer fit.
	// If all objects fit (elementCount() < iobjMaxNew), returns null.
	Queue setSize(int iobjMaxNew) {
		if (isQueueEmpty()) {
			clearAll();
			iobjMax = iobjMaxNew;
			rgobj = new Object[iobjMax];
			return (null);
		}

		// Copy our queue, clear it out, then copy the old queue to us.
		// If iobjMaxNew < elementCount(), copy objects until we are full,
		// and return the remaining elements in old queue to caller.
		Queue qOld = new Queue(this); // copy queue
		boolean fCheckFull = iobjMaxNew < elementCount();

		clearAll();
		iobjMax = iobjMaxNew;

		while (!qOld.isQueueEmpty() && !(fCheckFull && isQueueFull()))
			enqueue(qOld.dequeue());

		// fCheckFull is true iff qOld still contains objects (is not empty).
		Debug._assert(fCheckFull != qOld.isQueueEmpty());
		return (fCheckFull ? qOld : null);
	}

	void enqueue(Object obj) {
		Debug._assert(!isQueueFull());
		rgobj[iobjNextWrite++] = obj;
		iobjNextWrite %= iobjMax;
	}

	Object dequeue() {
		Debug._assert(!isQueueEmpty());
		Object obj = rgobj[iobjNextRead++];
		iobjNextRead %= iobjMax;

		return (obj);
	}

	// peek checks bounds and returns null if no object is present.
	Object peek(int iobjRelative) {
		Debug._assert(iobjRelative >= 0);
		if (elementCount() <= iobjRelative)
			return (null);

		return (rgobj[(iobjNextRead + iobjRelative) % iobjMax]);
	}

	// remove checks bounds and returns null if no object is present.
	Object remove(int iobjRelative) {
		Debug._assert(iobjRelative >= 0);
		if (elementCount() <= iobjRelative)
			return (null);

		int iobj = (iobjNextRead + iobjRelative) % iobjMax;
		Object obj = rgobj[iobj];
		try {
			if (iobj < iobjNextWrite) {
				// Shift array: [iobj+1,iobjNextWrite) -> [iobj,iobjNextWrite-1)
				System.arraycopy(rgobj, iobj + 1, rgobj, iobj, iobjNextWrite - iobj - 1);
			} else {
				// Need two array copies, since we've wrapped around.
				Debug._assert(iobj != iobjNextWrite);

				// Shift array tail: [iobj+1,iobjMax) -> [iobj,iobjMax-1)
				if (iobj < iobjMax - 1)
					System.arraycopy(rgobj, iobj + 1, rgobj, iobj, iobjMax - iobj - 1);
				rgobj[iobjMax - 1] = rgobj[0];

				// Shift array head: [1,iobjNextWrite) -> [0,iobjNextWrite-1)
				if (iobjNextWrite > 1)
					System.arraycopy(rgobj, 1, rgobj, 0, iobjNextWrite - 1);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// Just a chance to set a breakpoint, really.
			logger.error("Out of bounds");
			throw e;
		}

		iobjNextWrite = (iobjNextWrite + iobjMax - 1) % iobjMax;

		return (obj);
	}
	// {{DECLARE_CONTROLS
	// }}
}
