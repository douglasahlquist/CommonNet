/*   @(#)  ErrorTypes.java  2002-02-04
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
 * IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, 
 * PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER  * CAUSED AND REGARDLESS 
 * OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO 
 * USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */

package com.ahlquist.common.util;

public abstract class ErrorTypes {
    public final static int SUCCESS = 0;

    public static final int minDiagnostic = 1;
    public static final int maxDiagnostic = 1000;
    public static final int minWarning = 1001;
    public static final int maxWarning = 2000;
    public static final int minError = 2001;
    public static final int maxError = 3000;
    public static final int minCritical = 3001;
    public static final int maxCritical = 4000;

    // DKA NOTE TO SELF:
    // Whenever you add a new Error Type,
    // you must added a row to SeedErrors in the initdb package

    // WARNING: Do NOT renumber the error types. These values are used as
    // unique ids in the database. (Do not use missing numbers.)

    // Diagnostic
    public final static int FW_STREAM_CORRUPTED = minDiagnostic + 8;
    public final static int MO_CANNOT_CREATE_VM = minDiagnostic + 10;
    public final static int MO_CANNOT_KILL_VM = minDiagnostic + 11;
    public final static int JAVA_WAIT_INTERRUPTED = minDiagnostic + 14;
    public final static int HTTP_UNEXPECTED_RESPONSE = minDiagnostic + 23;

    // Warning
    public final static int DB_CONNECTION_FAILURE = minWarning + 2;
    public final static int SM_DATA_NOT_FOUND = minWarning + 21;
    public final static int DB_CLOCKS_OUT_OF_SYNC = minWarning + 22;
    public final static int PORT_BIND_FAILURE = minWarning + 24;
    public final static int CN_CONNECTION_FAILURE = minWarning + 26;

    // Error
    public final static int DB_ROLLBACK_SEGMENT_TOO_SMALL = minError + 15;
    public final static int DNS_NAME_SERVER_FAILURE = minError + 17;
    public final static int DB_VERSION_MISMATCH = minError + 20;
    public final static int ST_FILE_CORRUPT = minError + 25;
    public final static int ST_FILE_FAILURE_TOLERATED = minError + 26;
    public final static int ST_FILE_FAILURE = minError + 27;

    // Critical
    public final static int DB_DRIVER_NOT_FOUND = minCritical + 0;
    public final static int DB_PK_VIOLATION = minCritical + 1;
    public final static int DB_PARSE_ERROR = minCritical + 2;
    public final static int DB_CRITICAL_ERROR = minCritical + 3;
    public final static int ASSERTION_FAILURE = minCritical + 4;
    public final static int RUNTIME_EXCEPTION = minCritical + 5;
    public final static int UNEXPECTED_EXCEPTION = minCritical + 6;
    public final static int DB_FK_VIOLATION = minCritical + 7;
    public final static int SYSTEM_PARAMETER_MISSING = minCritical + 10;
    public final static int SYSTEM_PARAMETER_INVALID = minCritical + 11;
    public final static int DB_DRIVER_NOT_LOADABLE = minCritical + 13;
    public final static int MO_CANNOT_UPDATE_BUSYFILE = minCritical + 16;
    public final static int FW_CANNOT_BIND_TO_PORT = minCritical + 17;
    public final static int FW_UNKNOWN_LOCAL_HOST = minCritical + 18;
    public final static int FILE_WRITE_FAILURE = minCritical + 26;
    public final static int DB_DATABASE_UNAVAILABLE = minCritical + 27;
    public final static int IO_FAILURE = minCritical + 34;
    public final static int MO_CANNOT_START_COMPONENT = minCritical + 35;
    public final static int MO_CONFIG_ERROR = minCritical + 36;
    public final static int SEED_DATA_MISSING = minCritical + 37;

    public static int getSeverity(int errorType) {
        if (errorType == SUCCESS) {
            return (Severity.NONE);
        } else if (errorType >= minDiagnostic && errorType <= maxDiagnostic) {
            return (Severity.DIAGNOSTIC);
        } else if (errorType >= minWarning && errorType <= maxWarning) {
            return (Severity.WARNING);
        } else if (errorType >= minError && errorType <= maxError) {
            return (Severity.ERROR);
        } else {
            Debug._assert(errorType >= minCritical && errorType <= maxCritical);
            return (Severity.CRITICAL);
        }
    }
}
