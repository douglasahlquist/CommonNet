
package com.ahlquist.common.net.dns;

import com.ahlquist.common.util.MyObject;
import com.ahlquist.common.util.Compare;

/**
 * This class compares mail hosts.
 *
 * @author Douglas Ahlquist 1/5/2002
 * @see com.ahlquist.common.util.VArray
 */
final class MailHostComparator extends MyObject implements Compare {
	/** the singleton instance of this class. */
	private static MailHostComparator instance = null;

	public static MailHostComparator getInstance() {
		// Not synchronized on purpose, it is OK if we would create more than
		// one of these
		if (instance == null)
			instance = new MailHostComparator();

		return instance;
	}

	private MailHostComparator() {
	}

	/**
	 * Compares this MailHost to another MailHost using the preference value. If
	 * the other object is not a MailHost it throws a
	 * <code>ClassCastException</code>.
	 *
	 * @return the value 0 if the obj2 is equal to obj1; a value less than 0 if
	 *         obj1 is less than obj2; and a value greater than 0 if obj1 is
	 *         greater than obj2.
	 */
	public int compare(Object obj1, Object obj2) {
		// Preference is a positive number
		return ((MailHost) obj1).getPreference() - ((MailHost) obj2).getPreference();
	}
}
