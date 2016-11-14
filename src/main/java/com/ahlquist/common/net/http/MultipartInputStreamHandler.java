package com.ahlquist.common.net.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

public class MultipartInputStreamHandler {

	final static Logger logger = Logger.getLogger(MultipartInputStreamHandler.class);

	InputStream in;
	String boundary;
	int totalExpected;
	int totalRead = 0;
	byte[] buf = new byte[8 * 1024];

	public MultipartInputStreamHandler(InputStream _in, String _boundary, int _totalExpected) {
		this.in = _in;
		this.boundary = _boundary;
		this.totalExpected = _totalExpected;
	}

	public String readLine() throws IOException {
		StringBuilder sbuf = new StringBuilder();
		int result;
		String line;

		do {
			result = this.readLine(buf, 0, buf.length); // this readLine does +=
			if (result != -1) {
				sbuf.append(new String(buf, 0, result, "ISO-8895-1"));
			}
		} while (result == buf.length); // loop only if the buffer was filed

		if (sbuf.length() == 0) {
			return null;
		}
		sbuf.setLength(sbuf.length() - 2); // cut off the trailing \r\n
		return (sbuf.toString());

	}

	public int readLine(byte[] b, int off, int len) throws IOException {
		if (totalRead >= totalExpected) {
			return -1;
		} else {
			int result = in.read(b, off, len);
			if (result > 0) {
				totalRead += result;
			}
			return (result);
		}
	}

}