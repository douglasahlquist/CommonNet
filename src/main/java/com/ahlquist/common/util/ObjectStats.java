/*   @(#)  ObjectStats.java  2002-02-04
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

public class ObjectStats // Can *not* extend MyObject!
{
	private String name;

	private long cobjCreated = 0;

	private long cobjDestroyed = 0;

	ObjectStats(String name) {
		this.name = name;
	}

	/** @return the name of the object type for which we gather statistics. */
	public String getName() {
		return (name);
	}

	public long getCreated() {
		return (cobjCreated);
	}

	public long getDestroyed() {
		return (cobjDestroyed);
	}

	public long getCurrent() {
		return (cobjCreated - cobjDestroyed);
	}

	/** Register the creation of an object. */
	public synchronized void created() {
		cobjCreated++;
	}

	/** Register the destruction of an object. */
	public synchronized void destroyed() {
		cobjDestroyed++;
	}

	public int hashCode() {
		return (getName().hashCode());
	}

	public boolean equals(Object o) {
		return (name.equals(((ObjectStats) o).getName()));
	}

	public synchronized String toString() {
		return (name + ": cur = " + getCurrent() + "; total = " + getCreated());
	}
}
