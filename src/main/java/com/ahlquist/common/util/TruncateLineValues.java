package com.ahlquist.common.util;

import java.io.*;
//import java.util.*;
//import com.ahlquist.common.util.VArray;

public class TruncateLineValues {
	private VArray lines = null;
	private String filename = null;

	public static void main(String[] args) throws Exception {
		TruncateLineValues trunc = new TruncateLineValues(args[0], args[1]);
		trunc.saveFile();

	}

	public TruncateLineValues(String _filename, String _pattern) throws FileNotFoundException, IOException {
		lines = new VArray();
		filename = _filename;
		File file = new File(_filename);
		FileReader reader = new FileReader(file);
		BufferedReader br = new BufferedReader(reader);
		String l;
		while ((l = br.readLine()) != null) {
			int index = l.indexOf(_pattern);
			if (index != -1) {
				l = l.substring(0, index);
			}
			lines.addElement(l);
			System.out.println(l);
		}
		br.close();
	}

	public VArray getLines() {
		return lines;
	}

	public void saveFile() throws IOException {
		File file = File.createTempFile("Temp", "tmp");
		FileWriter writer = new FileWriter(file);

		for (int i = 0; i < lines.size(); i++) {
			writer.write((String) lines.elementAt(i));
		}
		writer.close();

		File newFile = new File(filename);
		file.renameTo(newFile);
	}
}
