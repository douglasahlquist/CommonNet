
package com.ahlquist.common.net.dns;

/**
 * This class is a CNAME resource record (RR). A CNAME record identifies the
 * canonical name of an alias.
 *
 * @author Douglas Ahlquist 12/29/2002
 */
public final class DnsCnameRecord extends DnsRecord {
	/** canonical name string */
	private String cname;

	/**
	 * Constructor called by DnsRecord factory method.
	 */
	DnsCnameRecord(String name, int type, DnsByteArray ba) {
		super(name, type, ba);

		cname = ba.readName();
	}

	/**
	 * Returns the canonical name string
	 */
	public String getCname() {
		return cname;
	}

	/**
	 * Returns a String object representing this RR.
	 */
	public String toString() {
		return super.toString() + " CNAME=" + cname;
	}
}
