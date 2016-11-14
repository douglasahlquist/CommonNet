/*   @(#)  MethodNotSupportedException.java  2002-02-04
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

package com.ahlquist.common.net.http;

public class MethodNotSupportedException extends java.io.IOException {

	private static final long serialVersionUID = 986415616914606101L;

	public MethodNotSupportedException() {
		super();
	}

	public MethodNotSupportedException(String s) {
		super(s);
	}
}
