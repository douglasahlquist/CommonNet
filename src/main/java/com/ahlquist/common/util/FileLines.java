/*   @(#)  FileLines.java  2002-02-04
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

import java.io.*;
import java.util.Vector;

/**
 * This Utility class reads a file into a Vector line by line
 * 
 */

public class FileLines {
	public static String DOS_NEWLINE = "\r\n";

	private String newline = "";

	Vector lines = new Vector();

	String filename = null;

	/**
	 * Use when writing a file only
	 *
	 */
	public FileLines() {
	}

	/**
	 * @param String
	 *            - the filename to read the lines from
	 */
	public FileLines(String _filename) throws FileNotFoundException, IOException {
		try {
			this.filename = _filename;
			File file = new File(filename);
			FileReader reader = new FileReader(file);
			BufferedReader br = new BufferedReader(reader);
			String l;
			while ((l = br.readLine()) != null) {
				lines.addElement(l);
			}
		} catch (FileNotFoundException e) {
			System.err.println("[FileLines] ctor FileNotFoundException thrown filename=" + this.filename);
			e.fillInStackTrace();
		} catch (IOException e) {
			System.err.println("[FileLines] ctor IOException thrown filename=" + this.filename);
			e.fillInStackTrace();

		}

	}

	/**
	 * @return VArray - the array of all line found in the file
	 */
	public Vector getLines() {
		return (lines);
	}

	public void setNewLine(String _newline) {
		this.newline = _newline;
	}

	public String getNewLine() {
		return newline;
	}

	public void setLines(Vector lines) {
		this.lines = lines;
	}

	public boolean saveFile() {
		return saveFile(filename);
	}

	public boolean saveFile(String destFile) {
		try {
			// find the lcoation where to create the new File
			int index = destFile.lastIndexOf(File.separator);
			String parentDir = destFile.substring(0, index);

			File newDir = new File(parentDir);
			boolean bSuccess = newDir.mkdirs();
			if (bSuccess) {
				// log("Creation of " + parentDir + " successfull");
			} else {
				// log("Creation of " + parentDir + " Not successfull");
			}
			File file = new File(destFile);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			FileOutputStream fout = null;
			try {
				fout = new FileOutputStream(file);
			} catch (IOException ex) {
				// log("IOException throw opening FileOutputStream on: "
				// + parentDir);
				return false;
			}

			PrintWriter out = new PrintWriter(fout);
			for (int i = 0; i < lines.size(); i++) {
				out.print((String) lines.elementAt(i) + newline);
			}
			out.close();
		} catch (IOException ex1) {
			// logEx(ex1);
			return false;
		}
		return true;
	}
}
