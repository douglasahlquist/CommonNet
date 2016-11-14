/*   @(#)  Position.java  2002-02-04
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

/*************************************
*
**************************************/
public class Position {
	/** default ctor */
	public Position() {
	}

	public Position(int line, int index, int status) {
		this.line = line;
		this.index = index;
		this.error = error;
	};

	/** Copy ctor */
	public Position(Position p) {
		line = p.line;
		index = p.index;
		error = p.error;
	}

	/** means that the VArray was completly searched without finding the tag */
	public final static int ERROR = -1;
	public final static int SUCCESS = 0;
	/** index into the array in which the line contains the tag instance */
	public int line = 0;
	/** index into the line where the tag instance starts */
	public int index = 0;
	/** contains the error state 0= success, -1=error */
	public int error = 0;
}
