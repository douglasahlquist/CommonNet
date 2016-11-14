
package com.ahlquist.common.net.dns;

import com.ahlquist.common.util.MyObject;

/**
 * This is the base class for DNS resource records (RR)
 *
 * @author Douglas Ahlquist 12/15/2002
 */
public abstract class DnsRecord extends MyObject {
	/** A host address */
	public static final int TYPE_A = 1;
	/** Identifies the canonical name of an alias */
	public static final int TYPE_CNAME = 5;
	/** Domain name pointer */
	public static final int TYPE_PTR = 12;
	/** Identifies a mail exchange for the domain */
	public static final int TYPE_MX = 15;

	private String name;
	private int type;
	private int rrClass;
	private int ttl;
	// Record data length. Used by subclasses.
	int rdLength;

	/**
	 * Constructor called by subclass
	 */
	DnsRecord(String name, int type, DnsByteArray ba) {
		this.name = name;
		this.type = type;
		// Decode all common fields
		rrClass = ba.readTwoByteInt();
		ttl = ba.readFourByteInt();
		rdLength = ba.readTwoByteInt();
	}

	/**
	 * Returns the domain name string
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the RR type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Returns the RR class. (Always IN=1)
	 */
	public int getRRClass() {
		return rrClass;
	}

	/**
	 * Time interval in seconds that record may be cached
	 */
	public int getTTL() {
		return ttl;
	}

	/**
	 * DnsRecord class factory. Returns a DnsRecord subclass.
	 */
	static DnsRecord createRecord(DnsByteArray ba) {
		String name = ba.readName();
		int type = ba.readTwoByteInt();

		DnsRecord record;

		switch (type) {
		case TYPE_MX:
			record = new DnsMxRecord(name, type, ba);
			break;
		case TYPE_A:
			record = new DnsARecord(name, type, ba);
			break;
		case TYPE_CNAME:
			record = new DnsCnameRecord(name, type, ba);
			break;
		case TYPE_PTR:
			record = new DnsPtrRecord(name, type, ba);
			break;
		default:
			record = new DnsUnknownRecord(name, type, ba);
			break;
		}

		return record;
	}

	/**
	 * Returns a String object representing this RR.
	 */
	public String toString() {
		return "NAME=" + name + " TYPE=" + type + " TTL=" + ttl;
	}
}
