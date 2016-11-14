
package com.ahlquist.common.net.dns;

/**
 * This class is an PTR resource record (RR). An PTR record identifies a domain
 * name pointer. Use for doing reverse DNS lookup, i.e mapping and IP address to
 * a domain name
 *
 * @author Douglas Ahlquist 01/26/2003
 */
public final class DnsPtrRecord extends DnsRecord {
	/** domain name pointer string */
	private String ptrDname;

	/**
	 * Constructor called by DnsRecord factory method.
	 */
	DnsPtrRecord(String name, int type, DnsByteArray ba) {
		super(name, type, ba);

		ptrDname = ba.readName();
	}

	/**
	 * Returns the domain name pointer string
	 */
	public String getDomainPtr() {
		return ptrDname;
	}

	/**
	 * Returns a String object representing this RR.
	 */
	public String toString() {
		return super.toString() + " DOMAINPTR=" + ptrDname;
	}
}
