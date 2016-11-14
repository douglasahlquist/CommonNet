
package com.ahlquist.common.net.dns;

import com.ahlquist.common.util.*;
import org.apache.log4j.Logger;
import java.net.*;

/**
 * This class represents a mail exchange host. It is used as a return value by
 * the MailHostList class.
 *
 * @author Douglas Ahlquist 12/23/2002
 */
public final class MailHost extends MyObject {
	
	final static Logger logger = Logger.getLogger(MailHost.class);
	/** The host name */
	private String name;
	/** The preference value from the MX record */
	private int preference;
	/** The IP address of the host */
	private String ipAddress;
	/** The InetAddress of the host. Created on demand. */
	private InetAddress inetAddress;
	/** Time when the A record/IP address TTL expires */
	private long ipExpires;

	/**
	 * Create a MailHost with all attributes.
	 *
	 * @param name
	 *            the host name
	 * @param preference
	 *            the preference value for this host
	 * @param ipAddress
	 *            the IP address if known (can be null)
	 * @param ipTTL
	 *            the time-to-live value for the IP address in seconds
	 */
	MailHost(String name, int preference, String ipAddress, int ipTTL) {
		this.name = name;
		this.preference = preference;
		this.ipAddress = ipAddress;

		// Have a one second grace period to avoid re-lookup the first
		// time we use this IP when TTL=0 (means no caching allowed).
		if (ipTTL == 0)
			setIpTTL(1);
		else
			setIpTTL(ipTTL);
	}

	/**
	 * Returns the name of the mail host. (Cannot be null.)
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the address of the mail host as an InetAddress. This value will
	 * be null if no IP address is found.
	 */
	public InetAddress getAddress() {
		// Check if TTL has expired
		if (ipAddress != null) {
			if (hasIpExpired()) {
				logger.debug("MailHost IP TTL expired: " + toString());
				inetAddress = null;
				ipAddress = null;
			}
		}

		if (inetAddress == null) {
			if (ipAddress == null) {
				DnsARecord rr = MailHostResolver.getARecord(name);
				if (rr != null) {
					ipAddress = rr.getIpAddress();
					setIpTTL(rr.getTTL());
				}
				// Is this an IP address?
				else if (MailHostResolver.isValidIpAddress(name)) {
					// This is a non-compliant MX record, the host name is not
					// supposed to be an IP address, but handle it anyway.
					ipAddress = name;
					// Make the time to live 24 hours
					setIpTTL(24 * 3600);
				}

				// FUTURE: If the lookup failed, make this MailHost less
				// desirable (sorted further back) next time
				// Maybe sort on preference + number of failures (or failures in
				// a row?)? How to weigh?
			}

			if (ipAddress != null) {
				try {
					inetAddress = InetAddress.getByName(ipAddress);
				} catch (UnknownHostException e) {
					// This exception is never thrown since we always pass in an
					// IP address?
					logger.debug("Bad IP address: " + ipAddress + ", MailHost: " + toString());
					inetAddress = null;
				}
			}
		}
		return inetAddress;
	}

	/**
	 * Returns the preference value of the host. (This could be derived from
	 * more than just the MX record preference. We could for example use failure
	 * info here to rank the host lower.) Package access only.
	 */
	int getPreference() {
		return preference;
	}

	/**
	 * Set the time when the IP address (A record) expires
	 */
	private void setIpTTL(int ipTTL) {
		ipExpires = MyTime.getCurrentMillis() + ipTTL * 1000;
	}

	/**
	 * If the IP address (A record) has expired, i.e. if the IP TTL (time to
	 * live) has elapsed.
	 */
	private boolean hasIpExpired() {
		return MyTime.getCurrentMillis() >= ipExpires;
	}

	/**
	 * Returns the TTL (time to live) value for IP address (A record) in
	 * seconds.
	 */
	private int getIpTTL() {
		return (int) (ipExpires - MyTime.getCurrentMillis()) / 1000;
	}

	/**
	 * Returns a String object representing this mail host.
	 */
	public String toString() {
		return "NAME=" + name + " PREFERENCE=" + preference + " IP=" + ipAddress + " INETADDRESS=" + inetAddress
				+ " IP TTL=" + getIpTTL();
	}
}
