/*   @(#)  Compare.java  2002-02-04
*
*  Copyright(C) 2002, All Rights Reserved.
*  Ahlquist.com
*  516 Suisse Drive
*  San Jose, California 95123
*  U.S.A.
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

/**
 * an interface for comparing.
 */
public interface Compare {
	/**
	 * Compare the two objects.
	 *
	 * @return the value 0 if the obj2 is equal to obj1; a value less than 0 if
	 *         obj1 is less than obj2; and a value greater than 0 if obj1 is
	 *         greater than obj2.
	 */
	public int compare(Object obj1, Object obj2);
}
