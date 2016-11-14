/*   @(#)  UniqueIdGenerator.java  2002-02-04
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

/**
 * The UniqueIdGenerator class generates integer IDs unique on this VM.
 * Instantiate this class to identify all objects in a group uniquely (then
 * store the unique ID generated in those objects). <br>
 * This class is provided mainly so we can easily localize code (and locate its
 * clients) responsible for ID generation when the approach finally breaks down
 * (i.e., the IDs wrap past 32 bits).
 *
 * Concurrency: Class provides synchronization.
 */
public final class UniqueIdGenerator extends MyObject {
	/** the next ID to use. */
	private int idNext = 0;

	/** an invalid ID which the generator will never return. */
	public final static int InvalidId = -1;

	/** Constructs the object. */
	public UniqueIdGenerator() {
	}

	/** Starting seed value */
	private int seed = 0;

	/** Constructor given a seed value */
	public UniqueIdGenerator(int seed) {
		this.seed = seed;
	}

	/** Creates a trace peer ID unique to this VM. */
	public synchronized int getNextId() {
		if (idNext == InvalidId)
			idNext++;
		if (idNext == Integer.MAX_VALUE)
			MyRuntime.getInstance().exit("UniqueIdGenerator wrapped IDs; must restart.");
		return (seed + idNext++);
	}
}
