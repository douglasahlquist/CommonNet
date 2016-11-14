
package com.ahlquist.common.net.dns;

import org.apache.log4j.Logger;

import com.ahlquist.common.util.*;

/**
 * This class finds a (list of) mail host(s) for a particular domain. It
 * encapsulates the specific logic that is used to find, select and sort DNS MX
 * (Mail Exchange) and/or A records.
 *
 * MailHostResolver uses the DnsResolver class for the actual DNS lookups.
 *
 * <P>
 * The following example prints all mail hosts for the domain "ahlquist.com":
 * </P>
 * 
 * <pre>
 * MailHostList hostList = MailHostResolver.getMailHosts("ahlquist.com");
 * if (hostList.getResult() == MailHostList.RESULT_SUCCESS)
 * 	for (int i = 0; i < hostList.size(); i++) {
 * 		MailHost host = hostList.getMailHost(i);
 * 		System.out.println(host.getName() + " " + host.getAddress());
 * 	}
 * else
 * 	System.out.println("Could not find any mail hosts");
 *
 * </pre>
 *
 * @author Douglas Ahlquist 12/23/2002
 */
public final class MailHostResolver {
	final static Logger logger = Logger.getLogger(MailHostResolver.class);
	
	/** Maximum number of MailHosts that are A records for a list */
	private static final int MAX_RECORDS_FOR_HOST_LIST = 2;

	/**
	 * Constructor never called!
	 */
	private MailHostResolver() {
	}

	/**
	 * Returns a list of mail exchange hosts for a domain. This value will never
	 * be null, but the list can be empty. The mail hosts are first looked up in
	 * a cache, then a DNS query is done if no hosts were found in the cache.
	 *
	 * @param domainName
	 *            the domain name to be resolved (can be null or empty)
	 * @see com.ahlquist.common.net.dns.MailHostList
	 */
	public static MailHostList getMailHosts(String domainName) {
		if (domainName == null || domainName.length() == 0) {
			// No domain name
			logger.error("Bad domain name: " + domainName);
			return new MailHostList(MailHostList.RESULT_BAD_DOMAIN);
		}

		MailHostListCache cache = MailHostListCache.getInstance();
		// Try to find the mail hosts in the cache
		MailHostList hostList = cache.get(domainName);
		if (hostList == null) {
			// Nothing (alive) in the cache, query the DNS server
			hostList = lookupMailHosts(domainName);
			if (hostList.getResult() == MailHostList.RESULT_SUCCESS)
				// Only cache successful lookups
				cache.put(domainName, hostList);
		} else
			logger.debug(domainName + ": Found hostList in cache: " + hostList);

		return hostList;
	}

	/**
	 * Returns a list of mail exchange hosts for a domain as a result of a DNS
	 * lookup. This value will never be null, but the list can be empty.
	 *
	 * @param domainName
	 *            the domain name to be resolved (must not be null or empty)
	 * @see com.ahlquist.common.net.dns.MailHostList
	 */
	private static MailHostList lookupMailHosts(String domainName) {
		// Check if the domain name is a domain-literal, i.e. of type [1.2.3.4]
		if (domainName.charAt(0) == '[') {
			// [ is not allowed in a regular domain name, so this is either a
			// domain-literal
			// or an illegal domain name
			return makeHostListFromDomainLiteral(domainName);
		}

		// Try to find MX records for domainName
		DnsResponse response = DnsResolver.lookup(domainName, DnsRecord.TYPE_MX);

		// Check error/result code
		int rCode = response.getResponseCode();

		// No error, lookup succeeded
		if (rCode == DnsResponse.RCODE_NOERROR) {
			// Check if we found any MX records
			if (hasRecordsOfType(response, DnsRecord.TYPE_MX))
				// If so, make a host list from the MX records
				return makeHostListFromMx(response);

			// If no MX records are found, try a CNAME lookup followed by an MX
			// lookup on the CNAME

			// Check if we already got CNAMEs in the response
			boolean hasCnameRecords = hasRecordsOfType(response, DnsRecord.TYPE_CNAME);
			if (!hasCnameRecords) {
				// If we did not have CNAMEs in the response, do a CNAME lookup
				response = DnsResolver.lookup(domainName, DnsRecord.TYPE_CNAME);
				hasCnameRecords = hasRecordsOfType(response, DnsRecord.TYPE_CNAME);
			}

			// Did we get any CNAME(s)?
			if (hasCnameRecords) {
				// If so, do an MX lookup on the CNAME result
				String cname = selectCname(response);
				response = DnsResolver.lookup(cname, DnsRecord.TYPE_MX);

				// Check if we found MX records
				if (hasRecordsOfType(response, DnsRecord.TYPE_MX))
					// If so, make a host list from the MX records
					return makeHostListFromMx(response);
			}

			// If we still don't have any MX records, try an A record lookup
			response = DnsResolver.lookup(domainName, DnsRecord.TYPE_A);
			// Check if we found A records
			if (hasRecordsOfType(response, DnsRecord.TYPE_A))
				// If so, make a host list from the A records
				return makeHostListFromA(response);

			// The domain name exists, but it isn't a mail exchange or an A
			// record...
			return new MailHostList(MailHostList.RESULT_BAD_DOMAIN);
		} else if (rCode == DnsResponse.RCODE_NAME_ERROR) {
			// Domain doesn't exist
			return new MailHostList(MailHostList.RESULT_BAD_DOMAIN);
		} else {
			// Other error, treat as temporary error
			return new MailHostList(MailHostList.RESULT_TEMPORARY_ERROR);
		}
	}

	/**
	 * Returns an A record for a host name.
	 *
	 * @param name
	 *            the host name to be resolved
	 * @see com.ahlquist.common.net.dns.DnsARecord
	 */
	static DnsARecord getARecord(String name) {
		// Try to find A records for name
		DnsResponse response = DnsResolver.lookup(name, DnsRecord.TYPE_A);

		// Check error/result code
		// No error, lookup succeeded
		if (response.getResponseCode() == DnsResponse.RCODE_NOERROR) {
			// Pick first A record in the answer
			for (int i = 0; i < response.getAnswerCount(); i++) {
				if (response.getAnswer(i).getType() == DnsRecord.TYPE_A)
					return (DnsARecord) response.getAnswer(i);
			}
		}

		// No A records/lookup failed
		return null;
	}

	/**
	 * Returns a MailHostList from a set of MX records
	 *
	 * @param response
	 *            the response message with MX records
	 * @see com.ahlquist.common.net.dns.MailHostList
	 */
	private static MailHostList makeHostListFromMx(DnsResponse response) {
		// Make MailHost objects from the MX records and add to a list
		int mailHosts = response.getAnswerCount();

		VArray list = new VArray(mailHosts);

		// We want to get the smallest TTL value in the record set
		int minTTL = Integer.MAX_VALUE;

		for (int i = 0; i < mailHosts; i++) {
			if (response.getAnswer(i).getType() == DnsRecord.TYPE_MX) {
				DnsMxRecord mxRecord = (DnsMxRecord) response.getAnswer(i);
				// Try to find this host name in the additional section (to get
				// the IP address)
				DnsARecord aRecord = getARecordFromAdditionalSection(response, mxRecord.getMailExchange());
				String ipAddress = (aRecord == null) ? null : aRecord.getIpAddress();
				int aRecordTTL = (aRecord == null) ? 0 : aRecord.getTTL();

				// Create a MailHost from the MX record
				MailHost host = new MailHost(mxRecord.getMailExchange(), mxRecord.getPreference(), ipAddress,
						aRecordTTL);

				// Add mail host to list
				list.addElement(host);

				// Collect the smallest TTL value
				if (mxRecord.getTTL() < minTTL)
					minTTL = mxRecord.getTTL();
			}
		}

		// Sort the list (by preference)
		list.bubbleSort(MailHostComparator.getInstance());

		return new MailHostList(list, minTTL);
	}

	/**
	 * Returns a MailHostList from a set of A records.
	 *
	 * @param response
	 *            the response message with A records
	 * @see com.ahlquist.common.net.dns.MailHostList
	 */
	private static MailHostList makeHostListFromA(DnsResponse response) {
		// Make MailHost objects from the A records and add to a list
		int mailHosts = response.getAnswerCount();

		VArray list = new VArray(mailHosts);

		// We want to get the smallest TTL value in the record set
		int minTTL = Integer.MAX_VALUE;

		for (int i = 0; i < mailHosts; i++) {
			if (response.getAnswer(i).getType() == DnsRecord.TYPE_A) {
				// Create a MailHost from the A record
				DnsARecord aRecord = (DnsARecord) response.getAnswer(i);
				MailHost host = new MailHost(aRecord.getName(), 0, aRecord.getIpAddress(), aRecord.getTTL());
				list.addElement(host);

				// Collect the smallest TTL value
				if (aRecord.getTTL() < minTTL)
					minTTL = aRecord.getTTL();

				// Add a maximum of X A records to the list, ignore the rest
				if (list.size() >= MAX_RECORDS_FOR_HOST_LIST)
					break;
			}
		}

		return new MailHostList(list, minTTL);
	}

	/**
	 * Returns a MailHostList from a domain-literal (domain name of type
	 * [1.2.3.4]).
	 *
	 * @param name
	 *            the domain-literal
	 * @see com.ahlquist.common.net.dns.MailHostList
	 */
	private static MailHostList makeHostListFromDomainLiteral(String name) {
		// Check if this follows the domain literal syntax correctly
		int nameLength = name.length();
		// Minimum length is 9 characters
		if (nameLength >= 9 && name.charAt(nameLength - 1) == ']') {
			String ipAddress = name.substring(1, nameLength - 1);

			if (isValidIpAddress(ipAddress)) {
				// Make a MailHost object. Use the IP address also as the
				// host name. Use a 24 hour TTL (since the email address
				// better be valid for at least one day!)
				MailHost host = new MailHost(ipAddress, 0, ipAddress, 24 * 3600);

				// Make the list and add the MailHost object
				VArray list = new VArray(1);
				list.addElement(host);

				// Let the TTL be 24 hours
				return new MailHostList(list, 24 * 3600);
			}
		}

		// Bad domain literal syntax
		return new MailHostList(MailHostList.RESULT_BAD_DOMAIN);
	}

	/**
	 * Checks if the name is a valid IP (v4) address (dotted string format)
	 *
	 * @param host
	 *            the string to check
	 * @return true if the string is a valid IPv4 address, false otherwise
	 */
	static boolean isValidIpAddress(String host) {
		int dots = 0;
		int len = host.length();
		if (len < 1)
			return false;

		for (int i = 0; i < len; i++) {
			char c = host.charAt(i);
			if (c < 48 || c > 57)
				return false;
			int b = 0;
			while (c != '.') {
				if (c < 48 || c > 57)
					return false;
				b = b * 10 + c - '0';

				if (++i >= len)
					break;
				c = host.charAt(i);
			}
			if (b > 0xFF)
				return false;
			dots++;
		}
		// The "dots" variable will hold the number of dots found + 1
		if (dots != 4 || host.charAt(len - 1) == '.')
			return false;

		// Looks like we have an OK address
		return true;
	}

	/**
	 * Checks if the answer section of a dns response has records of a specific
	 * type
	 *
	 * @param response
	 *            the response message to check for records
	 * @param type
	 *            the type of record to look for
	 */
	private static boolean hasRecordsOfType(DnsResponse response, int type) {
		for (int i = 0; i < response.getAnswerCount(); i++) {
			if (response.getAnswer(i).getType() == type)
				return true;
		}

		// No recs of desired type found
		return false;
	}

	private static String selectCname(DnsResponse response) {
		// Just pick the first record
		for (int i = 0; i < response.getAnswerCount(); i++) {
			if (response.getAnswer(i).getType() == DnsRecord.TYPE_CNAME)
				return ((DnsCnameRecord) response.getAnswer(i)).getCname();
		}
		return null;
	}

	/**
	 * Tries to find an A record for a host in the additional section of the DNS
	 * response.
	 *
	 * @param response
	 *            the response message to check for records
	 * @param name
	 *            the (host) name to find an IP address for
	 */
	private static DnsARecord getARecordFromAdditionalSection(DnsResponse response, String name) {
		for (int i = 0; i < response.getAdditionalCount(); i++) {
			// Only bother with A RRs
			if (response.getAdditional(i).getType() == DnsRecord.TYPE_A) {
				// Is this the right host/domain?
				if (name.equalsIgnoreCase(response.getAdditional(i).getName()))
					return (DnsARecord) response.getAdditional(i);
			}
		}
		return null;
	}
}
