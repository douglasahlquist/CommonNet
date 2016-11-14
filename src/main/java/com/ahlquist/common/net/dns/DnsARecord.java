
package com.ahlquist.common.net.dns;

/**
 * This class is a A resource record (RR). An A record represents a host
 * address.
 *
 * @author Douglas Ahlquist 12/29/2002
 */
public final class DnsARecord extends DnsRecord {
	/** IP address as a dotted string */
	private String ipAddress;

	/**
	 * Constructor called by DnsRecord factory method.
	 */
	DnsARecord(String name, int type, DnsByteArray ba) {
		super(name, type, ba);

		ipAddress = (ba.readByte() & 0xFF) + "." + (ba.readByte() & 0xFF) + "." + (ba.readByte() & 0xFF) + "."
				+ (ba.readByte() & 0xFF);
	}

	/**
	 * Returns the IP address as a dotted string
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * Returns a String object representing this RR.
	 */
	public String toString() {
		return super.toString() + " IPADDRESS=" + ipAddress;
	}
}
