/*   @(#)  ModifiedFileList.java  2002-02-04
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
import com.ahlquist.common.util.VArray;

public class ModifiedFileList {

	public static final int RECURSIVE = 1;
	public static final int NON_RECURSIVE = 2;
	private VArray modified_files = new VArray();
	private File[] all_files = null;
	private long modified_time = 0;

	/**
	 * Build a list of modified file whose date is after the modified_time.
	 * Recurses all subdirectory bases on the recursive flag
	 */
	public ModifiedFileList(String directory, long modified_time, int recursive) {
		// create the File (directory) object that your going to compile a
		// File List of modified Files
		this.modified_time = modified_time;
		// try{
		File dir = new File(directory);
		if (dir == null) {
			error("directory " + directory + " does not exist");
		}
		handleDirectory(dir);
		// }catch(IOException e){
		// error(e.toString());
		// }
	}

	private void handleDirectory(File dir) {
		all_files = dir.listFiles();
		debug(all_files.length + " files found");
		// look thought the all_file array for files that have been
		// updated after the modified time
		extractModifiedFiles(all_files);
	}

	private void extractModifiedFiles(File[] files) {
		for (int i = 0; i < files.length; i++) {
			long single_modified_time = (files[i]).lastModified();
			if (modified_time < single_modified_time) {
				if ((files[i]).isFile()) {
					try {
						String name = files[i].getCanonicalPath();
						modified_files.addElement(name);
					} catch (IOException e) {
						error("Error in extractModifiedFiles() reading file name");
					}
				} else if ((files[i]).isDirectory()) {
					try {
						String dir_name = files[i].getCanonicalPath();
						// add directory name
						modified_files.addElement(dir_name);
						handleDirectory(files[i]);
					} catch (IOException e) {
						error("Error in extractModifiedFiles() reading directory name");
					}
				}
			}
		}
	}

	public VArray getModifedFilesByName() {
		modified_files.sort(new StringCompare(), true);
		return modified_files;
	}

	private void debug(String s) {
		System.out.println("Mod: " + s);
	}

	private void error(String s) {
		System.out.println(s);
	}

}
