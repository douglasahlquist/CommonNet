package com.ahlquist.common.util;

public class SessionId {
	//private static long currentId;

	public static long getNewId() {
		return System.currentTimeMillis();
	}

}