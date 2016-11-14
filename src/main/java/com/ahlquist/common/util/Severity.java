/*   @(#)  Severity.java  2002-02-04
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

public final class Severity {
    private Severity() {
    }

    /**
     * The component can no longer function without corrective action.
     *
     * Example: The database is out of tablespace.
     */
    public static final int CRITICAL = 10;

    /**
     * A specific operation cannot complete and will not be re-executed until
     * corrective action is taken. However, the component is still able to
     * perform other operations.
     *
     * Example: A targeting query produces a bad sql statement and cannot be
     * used. Creator must fix.
     */
    public static final int ERROR = 20;

    /**
     * A negative or unexpected condition arose, but the component was able to
     * correct for the condition and was able to complete the operation.
     * 
     * Example: a database transaction failed because the connection was lost.
     * Try again.
     */
    public static final int WARNING = 30;

    /**
     * Informational message.
     *
     * Example: Tracker receives a poorly formed HTTP request; or IO failure in
     * redirect.
     */
    public static final int DIAGNOSTIC = 40;

    /**
     * Corresponds to success or no error.
     */
    public static final int NONE = 999;

    /**
     * Called when an error occurs during error escalation or processing.
     * Determines whether error must be reported to log file or may be
     * suppressed.
     *
     * FUTURE: Clearly a hack of sorts.
     *
     * @return true iff errors at this severity level must be reported somehow.
     */
    public static boolean isReportingRequired(int severity) {
        return (severity == ERROR || severity == CRITICAL);
    }

    public static String getName(int severity) {
        switch (severity) {
        case NONE:
            return ("None");
        case DIAGNOSTIC:
            return ("Diagnostic");
        case WARNING:
            return ("Warning");
        case ERROR:
            return ("Alert");
        case CRITICAL:
            return ("Critical");
        default:
            return ("Unknown severity #" + severity);
        }
    }

}
