
package com.ahlquist.common.net.dns;

import com.ahlquist.common.util.*;

import org.apache.log4j.Logger;

import com.ahlquist.common.counter.*;
import com.ahlquist.common.sysparam.dns.HostListCacheSize;
import com.ahlquist.server.lib.*;

/**
 * This class is a cache for MailHostLists. (It is a refined DNS cache.) It is a
 * hash table with a maximum size (HashCache) that allows us to retrieve a
 * MailHostList object based on the domain name.
 *
 * @author Douglas Ahlquist 01/29/2003
 */

final class MailHostListCache extends MyObject {
	
	final static Logger logger = Logger.getLogger(MailHostListCache.class);
	
	private HashCache cache;

	private static MailHostListCache instance = null;

	/** Cache hits */
	private Counter pcCacheHits = new Counter(CounterDefinition.DNS_HOSTLIST_CACHE_HITS);

	/** Cache hits */
	private Counter pcCacheMisses = new Counter(CounterDefinition.DNS_HOSTLIST_CACHE_MISSES);

	/**
	 * Creates a HashCache
	 */
	private MailHostListCache() {
		cache = new HashCache(HostListCacheSize.getInstance().getValueNoCache());
	}

	/**
	 * Singleton
	 */
	static MailHostListCache getInstance() {
		if (instance == null) {
			synchronized (MailHostListCache.class) {
				if (instance == null)
					instance = new MailHostListCache();
			}
		}
		return instance;
	}

	/**
	 * Returns the MailHostList for a domain name. If the MailHostList has
	 * expired, it is removed from the cache and null is returned.
	 *
	 * @param domainName
	 *            the domain name
	 *
	 * @return the MailHostList object or null
	 */
	synchronized MailHostList get(String domainName) {
		MailHostList hostList = (MailHostList) cache.get(domainName);

		// Check if TTL has expired
		if (hostList != null && hostList.hasExpired()) {
			Debug.DNSV.println(domainName + ": MailHostListCache -  HostList TTL has expired: " + hostList
					+ ", removing from cache.");
			// If so, remove from cache
			cache.remove(domainName);
			hostList = null;
		}

		// Keep statistics on cache hits
		if (hostList == null)
			pcCacheMisses.incrementValue();
		else
			pcCacheHits.incrementValue();

		return hostList;
	}

	/**
	 * Put a MailHostList in the cache. If the cache is full, the oldest
	 * MailHostList will be pushed out and returned. Also, if a HostList for
	 * this domain already is cached, the old entry will be replaced and
	 * returned.
	 *
	 * @param domainName
	 *            the domain name to use as the key
	 * @param hostList
	 *            the MailHostList to cache
	 *
	 * @return the old MailHostList that was pushed out or null
	 */
	synchronized MailHostList put(String domainName, MailHostList hostList) {
		// Check if TTL has expired
		if (hostList.hasExpired()) {
			Debug.DNSV.println(domainName + ": MailHostListCache -  HostList TTL has expired: " + hostList);
			return null;
		}

		MailHostList agedHostList = (MailHostList) cache.put(domainName, hostList);
		if (agedHostList != null) {
			Debug.DNSV.println(domainName + ": MailHostListCache -  HostList has been pushed out: " + agedHostList
					+ " for: " + hostList);
		}
		return agedHostList;
	}
}
