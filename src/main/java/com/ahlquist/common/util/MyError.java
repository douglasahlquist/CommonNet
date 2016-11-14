/*   @(#)  MyError.java  2002-02-04
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

/** 
 */
public class MyError extends MyObject implements java.io.Serializable {
    
	private static final long serialVersionUID = 7565373565233704486L;
	
	final static Logger logger = Logger.getLogger(MyError.class);

	/** notification level (Critical, Error or Warning). */
    private int errorType;

    /** an optional message to be appended to the error description. */
    private String message;

    /** the Throwable that caused this error, if any. */
    private transient Throwable throwable;
    private String stackTrace;

    /** time when this object was created */
    private long msCreated = MyTime.getCurrentMillis();
    private String machine = Util.getLocalHostName();

    private int countFailures = 1;

    private int nodeId = 0;
    private int vmid = 0;

    private String nodeName = null;
    private String componentName = null;

    /**
     * Constructs a new MyError
     *
     * @param errorType
     *            a notification level, see constants in this class
     * @param message
     *            a message to add to the error description; null ok.
     * @param throwable
     *            the Throwable that cause the erro; null ok.
     */
    public MyError(int errorType, String message, Throwable throwable) {
        this.errorType = errorType;
        this.message = message;
        if (throwable == null)
            throwable = new Throwable();
        this.throwable = throwable;
        stackTrace = Util.getStackTrace(throwable);
    }

    public MyError(int errorType, Throwable throwable) {
        this(errorType, null, throwable);
    }

    public MyError(int errorType, String message) {
        this(errorType, message, null);
    }

    public MyError(int errorType) {
        this(errorType, null, null);
    }

    /*
     * declared private so that the compiler doesn't create one
     */
    @SuppressWarnings("unused")
    private MyError() {
    }

    public int getType() {
        return (errorType);
    }

    public int getSeverity() {
        return (ErrorTypes.getSeverity(errorType));
    }

    public String getMessage() {
        return (message);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getThrowable() {
        return (throwable);
    }

    public void setThrowable(Throwable ex) {
        throwable = ex;
    }

    public String getTrace() {
        return (stackTrace);
    }

    public final String getComponentName() {
        return componentName;
    }

    public final void setComponentName(String name) {
        componentName = name;
    }

    public final String getNodeName() {
        return nodeName;
    }

    public final void setNodeName(String name) {
        nodeName = name;
    }

    public final int getVmId() {
        return (vmid);
    }

    public final void setVmId(int vmid) {
        this.vmid = vmid;
    }

    public final int getNodeId() {
        return (nodeId);
    }

    public final void setNodeId(int nid) {
        nodeId = nid;
    }

    public final long getTime() {
        return (msCreated);
    }

    private byte[] messageBytes;

    private synchronized final void convertToBytes() {
        if (message != null) {
            messageBytes = message.getBytes();
            message = null;
        }
    }

    private synchronized final void convertToString() {
        if (messageBytes != null) {
            message = new String(messageBytes);
            messageBytes = null;
        }
    }

    public String toString() {
        String description = "some desciption"; //// DKA GET THE DATA FORM PROP
                                                //// FILE
                                                //// Config.getErrorName(getType());
        int sevCode = ErrorTypes.getSeverity(getType());
        String severityString = Severity.getName(sevCode);

        String dateString = MyTime.getDateString(getTime());

        String messageString = ((message == null) ? "" : "\n  " + message);

        String throwableString = "";
        if (Debug.fPrintStackTrace || sevCode == Severity.CRITICAL) {
            throwableString = "\n  " + getTrace();
        } else {
            String throwName = Util.getShortName(throwable);
            if (!throwName.equals("Throwable") || throwable.getMessage() != null) {
                throwableString = "\n  throwable = " + throwName + ": " + throwable.getMessage();
            }
        }

        MyRuntime.getInstance().addContext(this);

        // TODO: DKA if (getNodeName() == null)
        // DKA setNodeName("<unavailable>");
        // DKA if (getComponentName() == null)
        // DKA setComponentName("<unavailable>");

        return ("PLATFORM Error (" + severityString + "): " + description + "; " + dateString + " from component "
                + componentName + " on node " + nodeName + messageString + throwableString);
    }

    public void escalateDiagnostic() {
        Debug._assert(getSeverity() == Severity.DIAGNOSTIC);
        escalate();
    }

    public void escalateWarning() {
        Debug._assert(getSeverity() == Severity.WARNING);
        escalate();
    }

    public void escalateError() {
        Debug._assert(getSeverity() == Severity.ERROR);
        escalate();
    }

    public void escalateAlert() {
        Debug._assert(getSeverity() == Severity.ERROR);
        escalate();
    }

    public void escalateCritical() {
        Debug._assert(getSeverity() == Severity.CRITICAL);
        escalate();
        Debug._assert(false); // NOTREACHED
    }

    /**
     * Indicates to caller that a human being should be notified in some way.
     *
     */
    public boolean shouldNotify() {
        return (/* DKA Config.getInstance() != null && */
        (getSeverity() == Severity.CRITICAL || getSeverity() == Severity.ERROR));
    }

    public void escalate() {
        if (countFailures % 50 == 1)
            logger.error(toString());

        MyRuntime.getInstance().escalateError(this);
    }

    public void escalateSleep(int countFailures) {
        this.countFailures = countFailures;
        escalate();
        Util.sleepExp(countFailures); // DKA FUTURE: Cap max sleep time!
    }

    public VArray getNotificationList() {
        // DKA**************
        // DKAAdminNotifyAddresses ana = new AdminNotifyAddresses();
        VArray array = new VArray();
        array.addElement("douglas@ahlquist.com");

        return array;
    }

    // /** @return an email message describing the error. */
    /*
     * public SimpleEmailMessage getEmail() { SimpleEmailMessage msg = new
     * SimpleEmailMessage();
     * 
     * msg.setRecipients(getNotificationList());
     * 
     * msg.setSubject(Version.productInitials + " Error (" + machine + "): " +
     * Config.getErrorName(errorType)); StringBuffer body = new StringBuffer();
     * body.append("The following error occurred at "); body.append(new
     * Date(msCreated)); body.append(Util.newLine).append(Util.newLine);
     * body.append(toString()); body.append(Util.newLine).append(Util.newLine);
     * body.append("Version: " + Version.version).append(Util.newLine);
     * body.append("Machine: " + machine).append(Util.newLine);
     * 
     * msg.setBody(body.toString());
     * 
     * return(msg); }
     */

    /**
     * Note: the purpose of this routine is to convert message string to byte
     * array
     */
    private synchronized void writeObject(ObjectOutputStream output) throws IOException {
        convertToBytes();
        // defaultWriteObject must be called before our own data serialization
        output.defaultWriteObject();
    }

    /**
     * Note: the purpose of this routine is to convert byte array back to a
     * message string
     */
    private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
        // defaultReadObject must be called before our own data serialization
        input.defaultReadObject();
        convertToString();
    }
}
