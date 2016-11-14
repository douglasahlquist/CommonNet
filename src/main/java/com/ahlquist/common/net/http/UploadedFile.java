package com.ahlquist.common.net.http;

import java.io.File;

public class UploadedFile {
	private String dir;
	private String filename;
	private String type;

	UploadedFile(String _dir, String _filename, String _type) {
		this.dir = _dir;
		this.filename = _filename;
		this.type = _type;
	}

	public String getContentType() {
		return type;
	}

	public String getFilesystemName() {
		return filename;
	}

	public File getFile() {
		if (dir == null || filename == null) {
			return null;
		} else {
			return new File(dir + File.separator + filename);
		}
	}

}