/*   @(#)  ProgressMonitor.java  2002-02-04
*
*  Copyright(C) 2002, All Rights Reserved.
*  Ahlquist.com
*  516 Suisse Drive
*  San Jose, California 95123
*  U.S.A.
*
*  This document contains information proprietary and confidential to
*  Ahlquist.com, which is either copyrighted or which a
*  patent has been applied and/or protected by trade secret laws.
*
*  This document, or any parts thereof, may not be used, disclosed,
*  or reproduced in any form, by any method, or for any purpose without
*  the express written permission of Ahlquist.com.
*
*
*/

package com.ahlquist.common.util;

import com.ahlquist.common.util.MyRuntime;

import java.util.*;

public final class ProgressMonitor {
	public interface FailureHandler {
		void failed(Object watchKey);
	}

	public static final FailureHandler DEFAULT_FAILURE_HANDLER = new FailureHandler() {
		public void failed(Object watchKey) {
			MyRuntime.getInstance().exit(watchKey + " failed to update in last " + INSTANCE.getUpdateInterval(watchKey)
					+ " milliseconds: exiting");
		}
	};

	private class WatchData {
		public long timestamp;
		public long updateInterval;
		public final Object watchKey;
		public FailureHandler handler;

		public WatchData(Object watchKey, long updateInterval, FailureHandler handler) {
			this.timestamp = System.currentTimeMillis();
			this.updateInterval = updateInterval;
			this.watchKey = watchKey;
			this.handler = handler;
		}
	}

	private static final ProgressMonitor INSTANCE = new ProgressMonitor();
	private static final long MIN_CHECK_INTERVAL = 500; // 0.5 sec
	private static final long MAX_CHECK_INTERVAL = 1000 * 30; // 0.5 min
	private static final int CHECK_FREQUENCY = 8;

	private final HashMap watchMap = new HashMap();
	private final Collection values = watchMap.values();
	private long minUpdateInterval;

	/**
	 *
	 * @return the singlton instance of ProgressMonitor
	 */
	public static ProgressMonitor getInstance() {
		return INSTANCE;
	}

	private ProgressMonitor() {
		Thread t = new Thread("ProgressMonitor") {
			public void run() {
				while (true) {
					synchronized (watchMap) {
						while (watchMap.isEmpty()) {
							try {
								watchMap.wait();
							} catch (InterruptedException exI) {
							}
						}

						long now = System.currentTimeMillis();

						for (Iterator iter = values.iterator(); iter.hasNext();) {
							WatchData next = (WatchData) iter.next();

							if (now - next.timestamp > next.updateInterval)
								next.handler.failed(next.watchKey);
						}

						try {
							if (minUpdateInterval / CHECK_FREQUENCY <= MIN_CHECK_INTERVAL)
								watchMap.wait(MIN_CHECK_INTERVAL);
							else if (minUpdateInterval / CHECK_FREQUENCY >= MAX_CHECK_INTERVAL)
								watchMap.wait(MAX_CHECK_INTERVAL);
							else
								watchMap.wait(minUpdateInterval / CHECK_FREQUENCY);
						} catch (InterruptedException exI) {
						}
					}
				}
			}
		};

		t.setPriority(Thread.MAX_PRIORITY);
		t.start();
	}

	/**
	 *
	 * @throws IllegalArgumentException
	 *             if updateInterval is less than or equal to zero
	 * @throws IllegalArgumentException
	 *             if watchKey has already been added
	 */
	public void add(Object watchKey, long updateInterval) {
		add(watchKey, updateInterval, DEFAULT_FAILURE_HANDLER);
	}

	/**
	 *
	 * @throws IllegalArgumentException
	 *             if updateInterval is less than or equal to zero
	 * @trhows NullPointerException if handler is null
	 * @throws IllegalArgumentException
	 *             if watchKey has already been added
	 */
	public void add(Object watchKey, long updateInterval, FailureHandler handler) {
		if (updateInterval <= 0)
			throw new IllegalArgumentException("updateInterval must be greater than zero");

		if (handler == null)
			throw new NullPointerException("handler cannot be null");

		synchronized (watchMap) {
			if (watchMap.containsKey(watchKey))
				throw new IllegalArgumentException(watchKey + " already added");

			watchMap.put(watchKey, new WatchData(watchKey, updateInterval, handler));

			if ((minUpdateInterval == 0) || (updateInterval < minUpdateInterval)) {
				minUpdateInterval = updateInterval;
				watchMap.notify();
			}
		}
	}

	/**
	 *
	 * @throws IllegalArgumentException
	 *             if watchKey not found
	 */
	public void remove(Object watchKey) {
		synchronized (watchMap) {
			WatchData watchData = (WatchData) watchMap.remove(watchKey);

			if (watchData == null)
				throw new IllegalArgumentException(watchKey + " not found");

			if (watchData.updateInterval == minUpdateInterval) {
				minUpdateInterval = 0;
				for (Iterator iter = values.iterator(); iter.hasNext();) {
					WatchData next = (WatchData) iter.next();
					if ((minUpdateInterval == 0) || (next.updateInterval < minUpdateInterval))
						minUpdateInterval = next.updateInterval;
				}
			}
		}
	}

	public boolean contains(Object watchKey) {
		synchronized (watchMap) {
			return watchMap.containsKey(watchKey);
		}
	}

	/**
	 *
	 * @throws IllegalArgumentException
	 *             if watchKey not found
	 */
	public void update(Object watchKey) {
		synchronized (watchMap) {
			WatchData watchData = (WatchData) watchMap.get(watchKey);

			if (watchData == null)
				throw new IllegalArgumentException(watchKey + " not found");

			watchData.timestamp = System.currentTimeMillis();
		}
	}

	/**
	 *
	 * @throws IllegalArgumentException
	 *             if watchKey not found
	 */
	public long getUpdateInterval(Object watchKey) {
		synchronized (watchMap) {
			WatchData watchData = (WatchData) watchMap.get(watchKey);

			if (watchData == null)
				throw new IllegalArgumentException(watchKey + " not found");

			return watchData.updateInterval;
		}
	}

	/**
	 * 
	 * @throws IllegalArgumentException
	 *             if newUpdateInterval is less than or equal to zero
	 * @throws IllegalArgumentException
	 *             if watchKey not found
	 */
	public void setUpdateInterval(Object watchKey, long newUpdateInterval) {
		if (newUpdateInterval <= 0)
			throw new IllegalArgumentException("newUpdateInterval must be greater than zero");

		synchronized (watchMap) {
			WatchData watchData = (WatchData) watchMap.get(watchKey);

			if (watchData == null)
				throw new IllegalArgumentException(watchKey + " not found");

			watchData.updateInterval = newUpdateInterval;

			if (newUpdateInterval < minUpdateInterval) {
				minUpdateInterval = newUpdateInterval;
				watchMap.notify();
			}
		}
	}

	/**
	 *
	 * @throws IllegalArgumentException
	 *             if watchKey not found
	 */
	public FailureHandler getFailureHandler(Object watchKey) {
		synchronized (watchMap) {
			WatchData watchData = (WatchData) watchMap.get(watchKey);

			if (watchData == null)
				throw new IllegalArgumentException(watchKey + " not found");

			return watchData.handler;
		}
	}

	/**
	 * 
	 * @throws NullPointerException
	 *             if newHandler is null
	 * @throws IllegalArgumentException
	 *             if watchKey not found
	 */
	public void setFailureHandler(Object watchKey, FailureHandler newHandler) {
		if (newHandler == null)
			throw new NullPointerException("newHandler cannot be null");

		synchronized (watchMap) {
			WatchData watchData = (WatchData) watchMap.get(watchKey);

			if (watchData == null)
				throw new IllegalArgumentException(watchKey + " not found");

			watchData.handler = newHandler;
		}
	}
}