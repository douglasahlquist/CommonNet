/*   @(#)  HttpException.java  2002-02-04
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

import java.io.IOException;

/**
 * HttpException is thrown when we have too many Exceptions (IOException or
 * MalformedException) to get a Response (client) or Request Object (server).
 * HttpException is holding a reference of the exception.
 *
 * @see java.io.IOException
 */
public class HttpException extends IOException {

	private static final long serialVersionUID = -9058377261872190856L;
	private Throwable throwable;

	public HttpException(String s, Throwable t) {
		super(s);
		throwable = t;
	}

	public Throwable getThrowable() {
		return (this.throwable);
	}
}