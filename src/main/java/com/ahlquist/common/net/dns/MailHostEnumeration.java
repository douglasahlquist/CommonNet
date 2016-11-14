
package com.ahlquist.common.net.dns;

import java.util.Enumeration;
import com.ahlquist.common.util.MyObject;
import com.ahlquist.common.util.VArray;

/**
 * This class enumerates mail hosts for a MailHostList.
 *
 * @author Douglas Ahlquist 1/6/2002
 */
@SuppressWarnings("rawtypes")
public final class MailHostEnumeration extends MyObject implements Enumeration {
	private VArray hostList;
	private int firstHostIndex;
	private int currentIndex = 0;

	/**
	 * Creates an enumeration object for mail hosts. The first host to be
	 * returned is randomized.
	 *
	 * @param hostList
	 *            the list of MailHost objects to enumerate
	 * @param firstHostIndex
	 *            the index of the first mail host to return
	 */
	MailHostEnumeration(VArray hostList, int firstHostIndex) {
		this.hostList = hostList;
		this.firstHostIndex = firstHostIndex;
	}

	/**
	 * Tests if this enumeration contains more elements.
	 *
	 * @return <code>true</code> if and only if this enumeration object contains
	 *         at least one more element to provide; <code>false</code>
	 *         otherwise.
	 */
	public boolean hasMoreElements() {
		return currentIndex < hostList.size();
	}

	/**
	 * Returns the next element of this enumeration if this enumeration object
	 * has at least one more element to provide.
	 *
	 * @return the next element of this enumeration.
	 */
	public Object nextElement() {
		return nextMailHost();
	}

	/**
	 * Returns the next MailHost of this enumeration.
	 *
	 * @return the next MailHost of this enumeration.
	 */
	public MailHost nextMailHost() {
		if (!hasMoreElements())
			return null;

		int returnIndex;
		// "Swap" the firstHostIndex and 0 index hosts
		if (currentIndex == 0)
			returnIndex = firstHostIndex;
		else if (currentIndex == firstHostIndex)
			returnIndex = 0;
		else
			returnIndex = currentIndex;

		currentIndex++;

		return (MailHost) hostList.elementAt(returnIndex);
	}

	/**
	 * Returns a String representing this object.
	 */
	public String toString() {
		return "FIRSTHOST=" + firstHostIndex + " HOSTLIST=" + hostList;
	}
}
