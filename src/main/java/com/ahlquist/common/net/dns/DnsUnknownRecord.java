
package com.ahlquist.common.net.dns;

/**
 * This class is a resource record (RR) of unknown type
 *
 * @author Douglas Ahlquist 12/29/2002
 */
final class DnsUnknownRecord extends DnsRecord {
	/**
	 * Constructor called by DnsRecord factory method.
	 */
	DnsUnknownRecord(String name, int type, DnsByteArray ba) {
		super(name, type, ba);

		// Just skip by the extra payload
		ba.skip(rdLength);
	}

	/**
	 * Returns a String object representing this RR.
	 */
	public String toString() {
		return super.toString() + " RDLENGTH=" + rdLength;
	}
}
