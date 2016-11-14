
package com.ahlquist.common.net.dns;

import com.ahlquist.common.util.MyObject;

/**
 * This class is an abstraction of a DNS response message. It is used to get
 * information from a response for a DNS query.
 *
 * @author Douglas Ahlquist 12/15/2002
 */
public final class DnsResponse extends MyObject {
	/** Standard DNS response code: No error condition */
	public static final int RCODE_NOERROR = 0;
	/**
	 * Standard DNS response code: Format error - The name server was unable to
	 * interpret the query.
	 */
	public static final int RCODE_FORMAT_ERROR = 1;
	/**
	 * Standard DNS response code: Server failure - The name server was unable
	 * to process this query due to a problem with the name server.
	 */
	public static final int RCODE_SERVER_FAILURE = 2;
	/**
	 * Standard DNS response code: Name Error - Meaningful only for responses
	 * from an authoritative name server, this code signifies that the domain
	 * name referenced in the query does not exist.
	 */
	public static final int RCODE_NAME_ERROR = 3;
	/**
	 * Standard DNS response code: Not Implemented - The name server does not
	 * support the requested kind of query.
	 */
	public static final int RCODE_NOT_IMPLEMENTED = 4;
	/**
	 * Standard DNS response code: Refused - The name server refuses to perform
	 * the specified operation for policy reasons. For example, a name server
	 * may not wish to provide the information to the particular requester, or a
	 * name server may not wish to perform a particular operation (e.g., zone
	 * transfer) for particular data.
	 */
	public static final int RCODE_REFUSED = 5;
	/**
	 * Implementation specific DNS response code: I/O failure - An I/O failure
	 * such as a time-out has occured.
	 */
	public static final int RCODE_SPECIAL_IO_FAILURE = 101;
	/**
	 * Implementation specific DNS response code: Wrong packet id - The
	 * identifier of the response was different from the query.
	 */
	public static final int RCODE_SPECIAL_WRONG_ID = 102;
	/**
	 * Implementation specific DNS response code: Format error - The DNS
	 * response packet was not in valid format.
	 */
	public static final int RCODE_SPECIAL_FORMAT_ERROR = 103;

	// Header
	/** id to match up queries and answers */
	private int id;
	/** Authorative answer */
	private boolean aa;
	/** Message truncated */
	private boolean tc;
	/** Response code */
	private int rCode = -1;
	/** Number of entries in the question section */
	private int qdCount = 0;
	/** Number of resource records in the answer section */
	private int anCount = 0;
	/** Number of resource records in the authority section */
	private int nsCount = 0;
	/** Number of resource records in the additional section */
	private int arCount = 0;

	/** Answer records */
	private DnsRecord[] answers;
	/** Authority records */
	private DnsRecord[] authorities;
	/** Additional records */
	private DnsRecord[] additionals;

	/**
	 * Constructor. Decodes the complete response message.
	 *
	 * @param ba
	 *            the packet of bytes to decode
	 */
	DnsResponse(DnsByteArray ba) {
		decode(ba);
	}

	/**
	 * Constructor. Creates an empty response message with only an error code.
	 *
	 * @param result
	 *            the error code
	 */
	DnsResponse(int result) {
		rCode = result;
	}

	/**
	 * The message id of the response. It is not exposed as public, it is only
	 * used by the resolver.
	 */
	int getID() {
		return id;
	}

	/**
	 * If this is a authoritative answer
	 */
	public boolean isAuthorative() {
		return aa;
	}

	/**
	 * If this DnsResponse is truncated. This is not necessarily that bad, the
	 * answer section will still hold useful information.
	 */
	public boolean isTruncated() {
		return tc;
	}

	/**
	 * The result of the query.
	 * 
	 * @return one of the RCODE_* response codes.
	 */
	public int getResponseCode() {
		return rCode;
	}

	/**
	 * Returns the number of answer RRs found
	 */
	public int getAnswerCount() {
		return anCount;
	}

	/**
	 * Returns the number of authority RRs found
	 */
	public int getAuthorityCount() {
		return nsCount;
	}

	/**
	 * Returns the number of additional RRs found
	 */
	public int getAdditionalCount() {
		return arCount;
	}

	/**
	 * Return an answer RR (resource record).
	 *
	 * @param i
	 *            the index of the record to return.
	 */
	public DnsRecord getAnswer(int i) {
		return answers[i];
	}

	/**
	 * Return an authority RR (resource record).
	 *
	 * @param i
	 *            the index of the record to return.
	 */
	public DnsRecord getAuthority(int i) {
		return authorities[i];
	}

	/**
	 * Return an aditional RR (resource record).
	 *
	 * @param i
	 *            the index of the record to return.
	 */
	public DnsRecord getAdditional(int i) {
		return additionals[i];
	}

	/**
	 * Decodes the response message bytes.
	 */
	private void decode(DnsByteArray ba) {
		// Decode header section
		id = ba.readTwoByteInt();
		byte b = ba.readByte();
		aa = (b & 4) == 4;
		tc = (b & 2) == 2;
		rCode = ba.readByte() & 0xF;
		qdCount = ba.readTwoByteInt();
		anCount = ba.readTwoByteInt();
		nsCount = ba.readTwoByteInt();
		arCount = ba.readTwoByteInt();

		// Questions
		decodeQuestions(ba, qdCount);

		// Decode resources
		answers = decodeRecords(ba, anCount);
		authorities = decodeRecords(ba, nsCount);
		additionals = decodeRecords(ba, arCount);
	}

	/**
	 * Decodes a set of resource records
	 */
	private DnsRecord[] decodeRecords(DnsByteArray ba, int numRecords) {
		DnsRecord[] records = new DnsRecord[numRecords];

		for (int i = 0; i < numRecords; i++) {
			records[i] = DnsRecord.createRecord(ba);
		}
		return records;
	}

	/**
	 * Decodes a set of question entries. We don't save these, we just toss them
	 * for now.
	 */
	private void decodeQuestions(DnsByteArray ba, int num) {
		for (int i = 0; i < num; i++) {
			String qName = ba.readName();
			int qType = ba.readTwoByteInt();
			int qClass = ba.readTwoByteInt();
		}
	}

	/**
	 * Returns a String object representing this response message.
	 */
	public String toString() {
		return "ID=" + id + " RCODE=" + rCode + " AA=" + aa + " TC=" + tc +
		/* "QUESTIONS="+qdCount+ */
				" ANSWERS=" + anCount + " AUTHORITIES=" + nsCount + " ADDITIONALS=" + arCount;
	}

	/**
	 * Returns a String object representing this response message for debugging
	 * purposes.
	 */
	public String toStringDebug() {
		StringBuffer sb = new StringBuffer("ID=" + id + " RCODE=" + rCode + " AA=" + aa + " TC=" + tc);

		sb.append("\n   ANSWERS=" + anCount);
		for (int i = 0; i < getAnswerCount(); i++)
			sb.append("\n      " + getAnswer(i));
		sb.append("\n   AUTHORITIES=" + nsCount);
		for (int i = 0; i < getAuthorityCount(); i++)
			sb.append("\n      " + getAuthority(i));
		sb.append("\n   ADDITIONALS=" + arCount);
		for (int i = 0; i < getAdditionalCount(); i++)
			sb.append("\n      " + getAdditional(i));

		return sb.toString();
	}
}
