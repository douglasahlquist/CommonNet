
package com.ahlquist.common.net.dns;

/**
 * This class is an MX resource record (RR). An MX record identifies a mail
 * exchange for the domain.
 *
 * @author Douglas Ahlquist 12/29/99
 */
public final class DnsMxRecord extends DnsRecord {
	/** mail exchange domain name string */
	private String mailExchange;

	/** preference value for this mail host */
	private int preference;

	/**
	 * Constructor called by DnsRecord factory method.
	 */
	DnsMxRecord(String name, int type, DnsByteArray ba) {
		super(name, type, ba);

		preference = ba.readTwoByteInt();
		mailExchange = ba.readName();
	}

	/**
	 * Returns the mail exchange domain name string
	 */
	public String getMailExchange() {
		return mailExchange;
	}

	/**
	 * Returns the preference value for this mail host. Lower values are more
	 * preferred.
	 */
	public int getPreference() {
		return preference;
	}

	/**
	 * Returns a String object representing this RR.
	 */
	public String toString() {
		return super.toString() + " PREFERENCE=" + preference + " MAILEXCHANGE=" + mailExchange;
	}
}
