/*   @(#)  MyFile.java  2002-02-04
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
 */
package com.ahlquist.common.util;

import java.io.*;

import org.apache.log4j.Logger;

/**
 * MyFile has method to do file manupilation
 *
 * @author Douglas Ahlquist
 */
public final class MyFile // extends MyObject
{
	final static Logger logger = Logger.getLogger(MyFile.class);
	
	/**
	 * Creates an archive file in an archive dir.
	 *
	 * @param dirName
	 *            the archive dir name
	 * 
	 * @return File the archive file
	 */
	public static File createArchiveDir(String dirName) {
		File dirArchive = new File(dirName);

		if (!dirArchive.exists()) {
			if (!dirArchive.mkdirs()) {
				logger.error("Failure in archive: can't create directory " + dirArchive);
			}
		}
		return (dirArchive);
	}

	private static long msLastCreate = MyTime.getCurrentMillis();

	// FUTURE: File name should include MessageContext IDs (cust, instance,
	// event).
	/**
	 * Return unique file under a dir
	 *
	 * @param dir
	 *            File directory
	 */
	public static synchronized File getUniqueFile(File dir) {
		// not multi-VM safe
		while (true) {
			File file = new File(dir, Long.toString(++msLastCreate) + ".txt");
			if (!file.exists()) {
				if (!Util.createFile(file)) {
					logger.error(file.toString());
				}
				return (file);
			}
			// REVIEW1: Should file.exists() ever? Maybe
		}
	}

	/**
	 * Archive a message content (in String format) to a archive dir
	 *
	 * @param String
	 *            content
	 * @param dir
	 *            Archive Dir in String format
	 *
	 * @return String the archive file name, return null if it fails to archive
	 */
	public static String archive(String msg, String dir) {
		File fileDir = createArchiveDir(dir);
		if (dir == null)
			return null;

		return (archive(msg, fileDir));
	}

	/**
	 * Archive a message content (in String format) to a archive dir
	 *
	 * @param String
	 *            content
	 * @param dir
	 *            Archive Dir in File format
	 *
	 * @return String the archive file name, return null if it fails to archive
	 */
	public static String archive(String msg, File dir) {
		File file = getUniqueFile(dir);
		PrintWriter out = null;
		try {
			out = new PrintWriter(new MyOutputStream("Archive File", new FileOutputStream(file)));
			out.write(msg);
			out.flush();
			return (file.getCanonicalPath());
		} catch (IOException e) {
			logger.error(file.toString(), e);
		} finally {
			Util.close(out);
		}
		return null;
	}

	/**
	 * Read file
	 *
	 * @param fileName
	 *            the name of a file to be read in
	 */
	public static byte[] readFile(String fileName) {
		ByteBuffer buffer = new ByteBuffer();
		int b;
		File file = new File(fileName);
		FileInputStream fIn = null;
		try {
			fIn = new FileInputStream(file);
			while ((b = fIn.read()) != -1) {
				buffer.append((byte) b);
			}
		} catch (FileNotFoundException fe) {
			logger.error(fe);
		} catch (IOException ie) {
			logger.error(ie);
		} finally {
			Util.close(fIn);
		}
		return (buffer.getBytes());
	}
}
