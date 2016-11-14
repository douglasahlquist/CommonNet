/*   @(#)  NotImplementedException.java  2002-02-04
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

public final class NotImplementedException extends Exception {
	private Exception e;

	public NotImplementedException() {
		super();
	}

	public NotImplementedException(String st) {
		super(st);
	}

	public NotImplementedException(Exception e, String st) {
		super(st);
		this.e = e;
	}
}