
package com.ahlquist.common.net.dns;

import com.ahlquist.common.util.*;
import org.apache.log4j.Logger;

import java.net.*;
import java.io.*;

/**
 * This class is a DNS stub resolver. It extracts DNS information from name
 * servers in response to client requests. Since this is a stub resolver we
 * assume the name server will handle recursive queries for us.
 *
 * <P>
 * The following example prints all mail exchange hosts for the domain
 * "ahlquist.com":
 * </P>
 * 
 * <pre>
 * DnsResponse dnsResponse = DnsResolver.lookup("ahlquist.com", DnsRecord.TYPE_MX);
 * for (int i = 0; i < dnsResponse.getAnswerCount(); i++) {
 * 	DnsRecord dnsRR = dnsResponse.getAnswer(i);
 * 	if (dnsRR.getType() == DnsRecord.TYPE_MX)
 * 		System.out.println(dnsRR.getName() + " " + ((MxRecord) dnsRR).getPreference() + " "
 * 				+ ((MxRecord) dnsRR).getMailExchange());
 * }
 * </pre>
 *
 * @author Douglas Ahlquist 12/15/2002
 */
public final class DnsResolver extends MyObject {
	final static Logger logger = Logger.getLogger(DnsResolver.class);

	/**
	 * Error monitor, notify administrator after DNS.MaxNameServerFailures
	 * sequencial failures
	 */
	private static ErrorMonitor errorMonitorInstance = null;

	/** If the package has been initialized */
	private static boolean isFirstTime = true;

	/** Object pool - save on object and socket creation */
	private static DnsResolver[] pool;
	/** Maximum size of the object pool */
	private static int maxPoolSize = 0;
	/** Current size of the object pool */
	private static int poolSize = 0;

	/** message/query id */
	private static int queryId = 0;

	/** UDP socket */
	private DatagramSocket udpSocket;
	/** Query message */
	private byte[] qMessage;
	/** Response message */
	private byte[] responseMessage;

	/**
	 * Creates an object pool for object reuse. This is a performance
	 * optimization to save on object creation. If this method isn't called, the
	 * default size is 0 and no pooling will be done.
	 *
	 * @param size
	 *            the maximum size of the object pool
	 */
	public static synchronized void createPool(int size) {
		maxPoolSize = size;
		pool = new DnsResolver[maxPoolSize];
		poolSize = 0;
	}

	/**
	 * Gets an instance of a DnsResolver. If we have a cached object in the pool
	 * we will return it, otherwise we will create a new object.
	 */
	private static synchronized DnsResolver getInstance() {
		// TODO: Make this into a ring buffer instead of a stack
		if (poolSize > 0)
			return pool[--poolSize];
		else
			return new DnsResolver();
	}

	/**
	 * Returns an instance of a DnsResolver to the object pool. If the pool is
	 * full, destroy the object.
	 *
	 * @param object
	 *            the object instance to return to the pool
	 */
	private static synchronized void releaseInstance(DnsResolver object) {
		// TODO: Make this into a ring buffer instead of a stack
		if (poolSize < maxPoolSize)
			pool[poolSize++] = object;
		else
			object.close();
	}

	/**
	 * Constructs a DNS resolver object
	 *
	 */
	private DnsResolver() {
		qMessage = new byte[512];
		responseMessage = new byte[512];
	}

	/**
	 * Closes socket if open
	 */
	private void close() {
		if (udpSocket != null) {
			udpSocket.close();
			udpSocket = null;
		}
	}

	/**
	 * Queries a DNS name server and returns a DNS message/response. The
	 * returned DnsResponse will never be null.
	 *
	 * @param qName
	 *            the domain name to be resolved
	 * @param qType
	 *            the type of query to be performed. See DnsRecord for possible
	 *            types.
	 * @see com.ahlquist.common.net.dns.DnsResponse
	 * @see com.ahlquist.common.net.dns.DnsRecord
	 */
	public static DnsResponse lookup(String qName, int qType) {
		if (isFirstTime)
			initialize();

		DnsResolver resolver = getInstance();
		DnsResponse response = resolver.query(qName, qType);
		// Close resolver if lookup fails, otherwise cache it
		if (response.getResponseCode() != DnsResponse.RCODE_NOERROR) {
			resolver.close();
			getErrorMonitor().failed();
		} else {
			releaseInstance(resolver);
			getErrorMonitor().succeeded();
		}
		return response;
	}

	// Begin implementation methods

	/**
	 * gets the next query/message id
	 */
	private static synchronized int getNextId() {
		queryId = (queryId + 1) & 0xFFFF;
		return queryId;
	}

	/**
	 * Queries a DNS name server and returns a DNS message/response
	 *
	 * @param qName
	 *            the domain name to be resolved
	 * @param qType
	 *            the type of query to be performed
	 * @see com.ahlquist.common.net.dns.DnsResponse
	 */
	private DnsResponse query(String qName, int qType) {
		// Make query/question Message
		int id = getNextId();
		int qLength = createQueryMessage(id, qName, qType, qMessage);
		// REVIEWed: Handle invalid qName
		if (qLength == 0) {
			// We got a malformed packet
			logger.debug("Invalid qName: ID=" + id + " NAME=" + qName + " TYPE=" + qType);
			return new DnsResponse(DnsResponse.RCODE_SPECIAL_FORMAT_ERROR);
		}

		logger.debug("Query:    ID=" + id + " NAME=" + qName + " TYPE=" + qType);

		// Send query and get response using UDP
		int responseLength = sendReceiveUdp(qMessage, qLength, responseMessage, responseMessage.length);
		if (responseLength <= 0) {
			logger.debug("Failed query: ID=" + id + " NAME=" + qName + " TYPE=" + qType);
			return new DnsResponse(DnsResponse.RCODE_SPECIAL_IO_FAILURE);
		}

		// Decode response packet
		DnsByteArray ba = new DnsByteArray(responseMessage, responseLength);

		// Build the DNS response from the byte array
		DnsResponse response;
		try {
			response = new DnsResponse(ba);
		} catch (ArrayIndexOutOfBoundsException e) {
			// Check if we read past the end of the response message
			if (!ba.isPastEnd()) {
				// Bad code somewere else, rethrow!
				throw e;
			}
			response = null;
		}

		// Check if we read past the end of the response message
		if (ba.isPastEnd()) {
			// We got a malformed packet
			logger.debug("Malformed response: ID=" + id + " NAME=" + qName + " TYPE=" + qType);
			return new DnsResponse(DnsResponse.RCODE_SPECIAL_FORMAT_ERROR);
		}

		// Check if the response packet id is the same as the query packet id
		if (id != response.getID()) {
			logger.debug("Wrong response (for ID=" + id + "): " + response.toString());
			return new DnsResponse(DnsResponse.RCODE_SPECIAL_WRONG_ID);
		}

		logger.debug("Response: " + response.toString());

		return response;
	}

	/**
	 * Sends a UDP packet and waits for a response. Returns the length of the
	 * received response.
	 *
	 * @param qMessage
	 *            the query message (in)
	 * @param qLength
	 *            the query message length (in)
	 * @param rMessage
	 *            the response message (in/out)
	 * @param rLength
	 *            the response message length (in)
	 */
	private int sendReceiveUdp(byte[] qMessage, int qLength, byte[] rMessage, int rLength) {
		// Query packet
		DnsNameServer server = getNameServer();
		DatagramPacket queryPacket = new DatagramPacket(qMessage, qLength, server.getAddress(), server.getPort());

		// Response packet
		DatagramPacket responsePacket = new DatagramPacket(rMessage, rLength);

		// Create socket
		if (udpSocket == null) {
			try {
				udpSocket = new DatagramSocket();
			} catch (SocketException e) {
				// This could be a bad thing?
				// Can it be temporary or would it be permanent?
				// Just assume it is temporary for now...
				// Maybe in the future => throw new DnsException()
				return 0;
			}
		}

		// Timeout value for each retry
		int tryTimeout = getTimeout();

		boolean success = false;

		int tries = getRetries() + 1;
		for (int i = 0; i < tries; i++) {
			// Send query packet and receive response packet
			try {
				udpSocket.setSoTimeout(tryTimeout);
				udpSocket.send(queryPacket);
				udpSocket.receive(responsePacket);
				success = true;
				break;
			} catch (IOException e) {
				// Temporary IO failure, try again
			}

			logger.debug("Failed query attempt: ID=" + (((qMessage[0] & 0xFF) << 8) | (qMessage[1] & 0xFF))
					+ " (RETRIES=" + i + ", SERVER=[" + server + "])");

			// Double the timeout for each retry
			tryTimeout *= 2;

			// Fail this server
			server.failed();

			// Get the currently best server to try
			server = getNameServer();
			queryPacket.setAddress(server.getAddress());
			queryPacket.setPort(server.getPort());
		}

		if (success) {
			int responseLength = responsePacket.getLength();
			if (responseLength >= 4) {
				int rCode = responsePacket.getData()[3] & 0xF;
				if (rCode == DnsResponse.RCODE_NOERROR || rCode == DnsResponse.RCODE_NAME_ERROR) {
					logger.debug("Query success: ID=" + (((qMessage[0] & 0xFF) << 8) | (qMessage[1] & 0xFF))
							+ " (RCODE=" + rCode + ", SERVER=[" + server + "])");
					// Success, return packet length
					return responseLength;
				} else {
					logger.debug("Failed query: ID=" + (((qMessage[0] & 0xFF) << 8) | (qMessage[1] & 0xFF)) + " (RCODE="
							+ rCode + ", SERVER=[" + server + "])");
				}
			}
			// REVIEWed: Fail server also when responseLength < 4
			else {
				logger.debug("Failed query: Response length=" + responseLength + ", SERVER=[" + server + "])");
			}
			server.failed();
		}
		return 0;
	}

	/**
	 * Creates a dns query message packet. Returns the length of the created
	 * message.
	 *
	 * @param qId
	 *            the message query id to be used (in)
	 * @param qName
	 *            the domain name to be resolved (in)
	 * @param qType
	 *            the type of query to be performed (in)
	 * @param qMessage
	 *            the resulting encoded message byte array (out)
	 */
	private int createQueryMessage(int qId, String qName, int qType, byte[] qMessage) {
		int qIndex = 0;

		// Header section
		// Id
		// (The byte order is most significant byte first)
		qMessage[qIndex++] = (byte) (qId >> 8);
		qMessage[qIndex++] = (byte) (qId & 0xFF);
		// QR + OPCODE + ... + RD
		qMessage[qIndex++] = 1;
		// Z + RCODE
		qMessage[qIndex++] = 0;
		// QDCOUNT
		qMessage[qIndex++] = 0;
		qMessage[qIndex++] = 1; // We have one question record
		// ANCOUNT
		qMessage[qIndex++] = 0;
		qMessage[qIndex++] = 0;
		// NSCOUNT
		qMessage[qIndex++] = 0;
		qMessage[qIndex++] = 0;
		// ARCOUNT
		qMessage[qIndex++] = 0;
		qMessage[qIndex++] = 0;

		// Question section
		// QNAME
		qIndex = encodeName(qMessage, qIndex, qName);
		// REVIEWed: Was there any error encoding the qName? (invalid qName)
		if (qIndex == 0)
			return 0;
		// QTYPE
		qMessage[qIndex++] = 0;
		qMessage[qIndex++] = (byte) qType;
		// QCLASS
		qMessage[qIndex++] = 0;
		qMessage[qIndex++] = 1; // Always Internet

		return qIndex; // qIndex == the length of the message
	}

	/**
	 * Encodes a domain name as a sequence of labels. (DNS packed format per
	 * RFC1135. Example: "www.ahlquist.com" => "\003www\004ahlquist\003com\000")
	 * Returns the next available index;
	 *
	 * @param message
	 *            the byte array to hold the encoded name (out)
	 * @param mIndex
	 *            the index to start storing in the message (in)
	 * @param name
	 *            the domain name to encode (in)
	 */
	private int encodeName(byte[] message, int offset, String name) {
		int dotIndex;
		int endIndex;
		int fromIndex = 0;

		// REVIEWed: Make sure the length of the qName isn't too long.
		// (Max 255 chars per RFC1035.)
		if (name.length() > 255)
			return 0;

		// For each label
		do {
			dotIndex = name.indexOf('.', fromIndex);
			if (dotIndex == -1)
				endIndex = name.length();
			else
				endIndex = dotIndex;
			// Store the label length
			int length = endIndex - fromIndex;
			// REVIEWed: Don't allow "empty" labels (will create a false
			// end-of-domain)
			// (Example: www..com)
			if (length == 0)
				return 0;
			// REVIEWed: Labels cannot be larger than 63 bytes per RFC1035
			// (First two bits of length has to be 0)
			if (length > 63)
				return 0;
			message[offset++] = (byte) (length);
			// Store the label characters
			while (fromIndex < endIndex)
				message[offset++] = (byte) name.charAt(fromIndex++);
			fromIndex = endIndex + 1;
		} while (dotIndex != -1);

		// Add the terminating 0 length label
		message[offset++] = 0;

		return offset;
	}

	/**
	 * Gets the DNS name server to be used for lookups.
	 */
	private DnsNameServer getNameServer() {
		DnsNameServer server = DNSNameServers.getInstance().getServer();
		// REVIEW: What if sys param has been changed under us, and is an
		// invalid hostname?
		// if (server == null || !server.isValid()) ...
		// But what if temporary down? Do we want to crash?
		// (It would be better to validate inside DnsNameServer instead, if we
		// could...)
		if (server == null) {
			synchronized (DnsResolver.class) {
				new MyError(ErrorTypes.SYSTEM_PARAMETER_INVALID,
						"Invalid IP address or hostname: " + DNSNameServers.getInstance().getName() + " value="
								+ DNSNameServers.getInstance().convertToString()).escalateCritical();
			}
		}
		return server;
	}

	/**
	 * Gets called the first time we use the resolver. Validates the DNS name
	 * server(s) system parameter.
	 */
	private static synchronized void initialize() {
		if (isFirstTime) {
			isFirstTime = false;

			boolean isValid = true;
			VArray nameServers = DNSNameServers.getInstance().getDNSNameServers();
			if (nameServers.size() == 0)
				isValid = false;
			for (int i = 0; i < nameServers.size(); i++) {
				DnsNameServer server = (DnsNameServer) nameServers.elementAt(i);
				if (!server.isValid())
					isValid = false;
			}

			if (!isValid) {
				new MyError(ErrorTypes.SYSTEM_PARAMETER_INVALID,
						"Invalid IP address or hostname: " + DNSNameServers.getInstance().getName() + " value="
								+ DNSNameServers.getInstance().convertToString()).escalateCritical();
			}
			// REVIEWed: Validate even if not explicitly asked for
			// Validate by doing a lookup
			tryLookup();
		}
	}

	/**
	 * Get the DNS lookup timout value in milliseconds.
	 */
	private int getTimeout() {
		// Timeout for DNS lookups
		return DNSTimeOutSeconds.getInstance().getInt() * 1000;
	}

	/**
	 * Gets the number of times to retry a DNS lookup that fails with a
	 * temporary error. (The timeout value is doubled for each retry).
	 */
	private int getRetries() {
		// How many times to retry a DNS UDP query before giving up
		return DNSRetries.getInstance().getInt();
	}

	/**
	 * Error monitor, notify administrator after DNS.MaxNameServerFailures
	 * sequencial failures
	 */
	private static ErrorMonitor getErrorMonitor() {
		if (errorMonitorInstance == null) {
			synchronized (DnsResolver.class) {
				if (errorMonitorInstance == null) {
					errorMonitorInstance = new ErrorMonitor();
					int maxNameServerFailures = 0;

					synchronized (MaxNameServerFailures.getInstance()) {
						maxNameServerFailures = MaxNameServerFailures.getInstance().getInt();
						MaxNameServerFailures.getInstance().registerImmutableCached();
					}

					errorMonitorInstance.addEscalation(maxNameServerFailures, ErrorTypes.DNS_NAME_SERVER_FAILURE);
				}
			}
		}
		return errorMonitorInstance;
	}

	/**
	 * Try to connect to the configured name server(s) and see if the 'com' root
	 * domain exists. A critical error will be thrown if this fails.
	 */
	private static void tryLookup() {
		// Try this lookup twice, we don't want go critical unless something
		// really is wrong
		for (int attempt = 0; attempt < 2; attempt++) {
			// Check if the com domain exists. (It better!)
			// This test should work fine both with VC and real DNS servers
			DnsResolver resolver = getInstance();
			DnsResponse response = resolver.query("com", DnsRecord.TYPE_A);
			releaseInstance(resolver);
			if (response.getResponseCode() == DnsResponse.RCODE_NOERROR)
				// All OK, the 'com' domain exists!
				return;
		}

		// Generate a critical error
		new MyError(ErrorTypes.SYSTEM_PARAMETER_INVALID, "DNS server failure. Please check system parameter: "
				+ DNSNameServers.getInstance().getName() + ", value=" + DNSNameServers.getInstance().convertToString())
						.escalate();
	}

	/**
	 * Validates the DNS server setup. We do this by trying to connect to the
	 * configured name server(s) and see if the 'com' root domain exists. A
	 * critical error will be thrown if this fails.
	 */
	public static void validateServer() {
		// Force init and validation
		isFirstTime = true;
		initialize();
	}
}
