
package com.ahlquist.common.net.dns;

import com.ahlquist.common.util.*;

import org.apache.log4j.Logger;

/**
 * This class encapsulates a list of mail exchange hosts for a domain. It is
 * used as a return value by the MailHostResolver class. The mail hosts are
 * sorted in preference order with the most "preferrable" host first.
 *
 * @author Douglas Ahlquist 12/23/2002
 */
public final class MailHostList extends MyObject {
	
	final static Logger logger = Logger.getLogger(MailHostList.class);
	
	/** Success, one or more mail hosts found */
	public static final int RESULT_SUCCESS = 0;
	/** Temporary error such as I/O failure */
	public static final int RESULT_TEMPORARY_ERROR = 1;
	/** The domain name could not be found or did not have any mail hosts */
	public static final int RESULT_BAD_DOMAIN = 2;

	/** Let the TTL be 10 seconds for failed lookup/negative caching */
	private static final int ERROR_TTL = 10;

	/** result/error code */
	private int result;

	/** Mail host list */
	private VArray hostList;

	/** Time when the host list TTL expires */
	private long expires;

	/**
	 * How many hosts that have the lowest preference value. Used when selecting
	 * a host randomly.
	 */
	private int numberOfHostsWithLowestPreference = 0;

	/**
	 * Constructor, error condition.
	 *
	 * @param result
	 *            the error code (see public result codes)
	 *
	 *            Package access only.
	 */
	MailHostList(int result) {
		hostList = new VArray(0);
		this.result = result;
		expires = MyTime.getCurrentMillis() + ERROR_TTL * 1000;
		logger.debug("MailHostList created: " + toString());
	}

	/**
	 * Constructor, successful lookup.
	 *
	 * @param list
	 *            the list of MailHosts sorted by preference
	 * @param ttl
	 *            the time-to-live value in seconds for this list
	 *
	 *            Package access only.
	 */
	MailHostList(VArray list, int ttl) {
		hostList = list;
		result = RESULT_SUCCESS;
		expires = MyTime.getCurrentMillis() + ttl * 1000;

		// Find the number of hosts with lowest preference (list is already
		// sorted)
		// We need this to randomize the first host (over all hosts with the
		// lowest preference.
		int lowestPreference = ((MailHost) hostList.elementAt(0)).getPreference();
		int i = 1;
		while (i < hostList.size()) {
			if (((MailHost) hostList.elementAt(i)).getPreference() > lowestPreference)
				break;
			i++;
		}
		numberOfHostsWithLowestPreference = i;

		logger.debug("MailHostList created: " + toString());
	}

	/**
	 * Returns the result code for the operation that generated this object.
	 * Users of this class should check this value before calling other methods.
	 */
	public int getResult() {
		return result;
	}

	/**
	 * If this host list has expired, i.e. if the TTL (time to live) has
	 * elapsed.
	 */
	public boolean hasExpired() {
		return MyTime.getCurrentMillis() >= expires;
	}

	/**
	 * The TTL (time to live) value of the host list in seconds.
	 */
	private int getTTL() {
		return (int) (expires - MyTime.getCurrentMillis()) / 1000;
	}

	/**
	 * Returns an enumeration for the mail host list. The first host is
	 * randomized among all hosts with the lowest preference value. This avoids
	 * that the same mail host is used every time, instead the load gets spread
	 * over all "preferred" mail hosts.
	 *
	 * @see com.ahlquist.common.net.dns.MailHostEnumeration
	 */
	public MailHostEnumeration getElements() {
		int randomIndex = 0;
		// Only randomize if we have more than one host
		if (numberOfHostsWithLowestPreference > 1) {
			// Generate a random index from 0 to
			// numberOfHostsWithLowestPreference-1
			randomIndex = (int) (numberOfHostsWithLowestPreference * java.lang.Math.random());
			if (randomIndex >= numberOfHostsWithLowestPreference)
				randomIndex = numberOfHostsWithLowestPreference - 1;
			logger.debug("MailHostList randomized - selected host " + randomIndex + " for: " + toString());
		}

		return new MailHostEnumeration(hostList, randomIndex);
	}

	/**
	 * Returns a String object representing this mail host.
	 */
	public String toString() {
		return "RESULT=" + result + " TTL=" + getTTL() + " HOSTLIST=" + hostList;
	}
}
