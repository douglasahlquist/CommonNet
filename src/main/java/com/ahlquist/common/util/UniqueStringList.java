
package com.ahlquist.common.util;

import java.io.*;
import java.util.*;

public class UniqueStringList {
	Hashtable list = new Hashtable();

	/**
	 * read a list of names and creates a list of unique names.
	 */
	public UniqueStringList(String filename) throws FileNotFoundException, IOException {
		int linesRead = 0;
		File file = new File(filename);
		FileReader reader = new FileReader(file);
		BufferedReader br = new BufferedReader(reader);
		String l;
		while ((l = br.readLine()) != null) {
			list.put(l, l);
			linesRead++;
		}
		System.out.println("Lines Read:" + linesRead + " Hash size:" + list.size());

	}

	public void displayList() {
		Enumeration keys = list.keys();
		while (keys.hasMoreElements()) {
			System.out.println((String) keys.nextElement());
		}

	}

	public VArray getList() {
		VArray array = new VArray();
		Enumeration keys = list.keys();
		while (keys.hasMoreElements()) {
			String item = (String) keys.nextElement();
			array.addElement(item);
		}
		return array;
	}

}
