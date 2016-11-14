
package com.ahlquist.common.net.dnsd;


import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * DnsdARecord holds the ipAddress of domain.
 * 
 * @author Douglas Ahlquist
 * @see DnsdResponse
 * @see DnsdRecord
 */
public class DnsdARecord extends DnsdRecord {
	final static Logger logger = Logger.getLogger(DnsdARecord.class);
	/** Dot decimal format of the ip for the domain name */
	private String ipAddress;

	/**
	 * Creates a DNS A type Record.
	 *
	 * @param name
	 *            the domain name (ahlquist.com)
	 * @param ttl
	 *            time to live how long the record should be valid in seconds
	 * @param ipAddress
	 *            the dot decimal format ip of the domain name
	 */
	public DnsdARecord(String name, int ttl, String ipAddress) {
		super(name, TYPE_A, ttl);
		this.ipAddress = ipAddress;
	}

	/**
	 * Packs up ipAddress into Byte Array.
	 * 
	 * @param bytebuilder
	 *            hold a byte array and help transfer data into binary format
	 */
	void pack(DnsdByteBuilder bytebuilder) {
		super.pack(bytebuilder);
		// Pack the rdLength
		bytebuilder.writeTwoByteInt(4);
		StringTokenizer stok = new StringTokenizer(ipAddress, ".");

		for (int i = 0; i < 4; i++) {
			try {
				bytebuilder.writeByte((Integer.valueOf(stok.nextToken())).byteValue());
			} catch (NumberFormatException e) {
				// do something here??
				logger.debug("Invalid format of VC's IP: " + ipAddress);
			}
		}
	}
}