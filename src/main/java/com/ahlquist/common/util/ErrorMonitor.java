/*   @(#)  ErrorMonitor.java  2002-02-04
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

/*
 * @(#).java    1.0 01/02/24
 * Copyright 1999-2001 Ahlquist Consulting & Idaho Consulting, Inc. All Rights Reserved.
 *
 * Ahlquist Consulting & Idaho Consulting grants you ("Licensee") a non-exclusive, 
 * royalty free, license to use and modify.  The redistribution of this software in 
 * source and binary code in any form is strictly prohibited without written permission.
 * Apon written approval licensee shall provide notice of
 *    i) this copyright notice and license appear on all copies of the software; and 
 *   ii) Licensee does not utilize the software in a manner which is disparaging 
 *       to Ahlquist Consulting & Idaho Consulting.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. AHLQUIST CONSULTING, IDAHO CONSULTING AND 
 * ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE 
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. 
 * IN NO EVENT WILL THE ABOVE OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, 
 * PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER  * CAUSED AND REGARDLESS 
 * OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO 
 * USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */

package com.ahlquist.common.util;

import org.apache.log4j.Logger;

/**
 * This class records successes and failures of a given operation and escalates
 * different errors when the operation fails a number of consecutive times.
 */
public class ErrorMonitor extends MyObject {
	
	final static Logger logger = Logger.getLogger(ErrorMonitor.class);
	private String errorMessage = null;

	private int cFailures = 0;

	private VArray vEscalations = new VArray();

	public ErrorMonitor() {
	}

	public ErrorMonitor(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public synchronized void addEscalation(int cFailures, int errorType) {
		vEscalations.addElement(new CountEscalation(cFailures, errorType));
	}

	public synchronized void succeeded() {
		cFailures = 0;
	}

	/**
	 * @param errMsg
	 *            the error message for the latest failure
	 * @return true if any of Escalations reaches the escalation point.
	 *         otherwise false
	 */
	public synchronized boolean failed(String errMsg) {
		boolean returnValue = false; // default
		cFailures++;
		for (int i = 0; i < vEscalations.size(); i++) {
			CountEscalation ce = (CountEscalation) vEscalations.elementAt(i);
			if (ce.cFailures == cFailures) {
				String lerr = errorMessage;
				if (lerr == null)
					lerr = "The last failure is: " + errMsg;
				else if (errMsg != null)
					lerr += Util.newLine + "The last failure is: " + errMsg;
				logger.error(lerr);
				returnValue = true;
			}
		}
		return returnValue;
	}

	/**
	 * @return true if any of Escalations reaches the escalation point.
	 *         otherwise false
	 */
	public synchronized boolean failed() {
		return failed(null);
	}

	public String toString() {
		return ("ErrorMonitor { cFailures = " + cFailures + "; errorMessage = " + errorMessage + " } ");
	}

	class CountEscalation extends MyObject {
		int cFailures;
		int errorType;

		CountEscalation(int cFailures, int errorType) {
			this.cFailures = cFailures;
			this.errorType = errorType;
		}
	}
}
