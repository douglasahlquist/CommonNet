
package com.ahlquist.common.net.dns;

import com.ahlquist.common.util.*;
import java.net.*;

/**
 * This class represents a DNS name server to be used by the resolver for
 * lookups. It is retrieved from a system parameter.
 *
 * @author Douglas Ahlquist 05/27/2002
 */
public final class DnsNameServer extends MyObject implements java.io.Serializable {

	private static final long serialVersionUID = -7259313631704256189L;
	/** The host name */
	private String hostname;
	/** Port */
	private int port;
	/** The InetAddress of the host */
	private transient InetAddress inetAddress;
	/** Total number of failed lookups */
	private transient int failures = 0;

	/**
	 * Creates a DNS name server object from a hostname/IP address and port.
	 */
	public DnsNameServer(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}

	/**
	 * Returns the address of the name server as an InetAddress
	 */
	InetAddress getAddress() {
		if (inetAddress == null) {
			synchronized (this) {
				if (inetAddress == null) {
					try {
						inetAddress = InetAddress.getByName(hostname);
					} catch (UnknownHostException e) {
						inetAddress = null;
					}
				}
			}
		}
		return inetAddress;
	}

	/**
	 * Returns the host name of the name server
	 */
	public String getName() {
		return hostname;
	}

	/**
	 * Returns the port number of the name server
	 */
	public int getPort() {
		return port;
	}

	/**
	 * If this name server is "valid"
	 */
	public boolean isValid() {
		return (getAddress() != null);
	}

	/**
	 * Called if the DNS lookup failed. Used to keep track of total number of
	 * failed lookups.
	 */
	public synchronized void failed() {
		failures++;
	}

	/**
	 * Returns the the total number of failed lookups for this name server
	 */
	public int getFailures() {
		return failures;
	}

	/**
	 * Returns a String representing this object
	 */
	public String toString() {
		return getName() + ":" + getPort() + " IP=" + (getAddress() == null ? "null" : getAddress().getHostAddress())
				+ " failures=" + getFailures();
	}
}
