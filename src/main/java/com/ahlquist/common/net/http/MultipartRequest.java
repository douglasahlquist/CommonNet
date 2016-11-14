package com.ahlquist.common.net.http;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.apache.log4j.Logger;

//import javax.servlet.*;
import com.ahlquist.common.util.Numbers;

public class MultipartRequest {
	final static Logger logger = Logger.getLogger(MultipartRequest.class);

	private static final int DEFAULT_MAX_POST_SIZE = 1024 * 1024; // 1Mg
	private File dir;
	private int maxSize;

	private Hashtable<String, String> paramenter = new Hashtable<String, String>();
	private Hashtable<String, UploadedFile> files = new Hashtable<String, UploadedFile>();
	private RequestHeader header;
	private String saveDirectory;

	public MultipartRequest(RequestHeader _header, String _saveDirectory) {
		this.header = _header;
		this.saveDirectory = _saveDirectory;

	}

	/**
	 * Call this method after calling readRequest(InputStream in)
	 * 
	 * @param _name
	 *            the file directory specific name
	 * @return String - the file's system name retrieved from the UploadedFile
	 *         hashtable
	 * @see com.ahlquist.common.net.http.UploadedFile
	 */
	public String getFileSystemName(String _name) {
		try {
			UploadedFile file = (UploadedFile) files.get(_name);
			return file.getFilesystemName(); // may be null
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Call this method after calling readRequest(InputStream in)
	 * 
	 * @param _name
	 * @return String
	 */
	public String getContentType(String _name) {
		try {
			UploadedFile file = (UploadedFile) files.get(_name);
			return file.getContentType(); // may be null
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Call this method after calling readRequest(InputStream in)
	 * 
	 * @param _filename
	 * @return File
	 */
	public File getFile(String _filename) {
		try {
			UploadedFile file = (UploadedFile) files.get(_filename);
			return file.getFile(); // may be null
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * call this method after calling the ctor
	 * 
	 * @param _in
	 *            - the InputStream from the Socket
	 * @throws IOException
	 */
	public void readRequest(InputStream _in) throws IOException {
		String type = header.getHeaderParam(HttpBaseMessage.HEADER_KEY_CONTENT_TYPE);
		if (type == null || (type.indexOf("multipart/form-data") == -1)) {
			throw new IOException("POSTed content type isn't maulitpart/form-data");
		}
		// check the length =length to prevent denial of service attaches
		int length = 0;
		String sSize = header.getHeaderParam(HttpBaseMessage.HEADER_KEY_CONTENT_LENGTH);
		if ((length = Numbers.getInt(sSize, -1)) > maxSize) {
			throw new IOException("POSTED content length of " + length + " exceeds limit of maxSize)");
		}

		// Get the boundary String: its included in the content type
		// Should look something like
		// "--------------------------1231234123434523"
		String boundary = extractBoundary(type);
		if (boundary == null) {
			throw new IOException("Seperation Boundary was not specified");
		}

		// Construct the special InputStream we'll read from
		MultipartInputStreamHandler minh = new MultipartInputStreamHandler(_in, boundary, length);
		// Read the first line, should be the boundary
		String line = minh.readLine();
		if (line == null) {
			throw new IOException("Corrupt form data: premature ending");
		}

		// verify that that the line is the boundary
		if (!line.startsWith(boundary)) {
			throw new IOException("Corrupt form data: no leading boundary");
		}
		// Now that we're just boyond the first boundary loop over each part
		boolean done = false;
		while (!(done = readNextPart(minh, boundary)))
			;
	}

	/**
	 * @param _minh
	 *            - the MultipartInputStreamHandler object to read from
	 * @param _boundary
	 *            - the String which bounds the Multipart object
	 * @return boolean - return true if the a next object is read
	 * @throws IOException
	 */
	protected boolean readNextPart(MultipartInputStreamHandler _minh, String _boundary) throws IOException {
		// read the first line, should look like this
		// content-disposition: form-data; name="field1"; filename="file1.bmp"
		String line = _minh.readLine();
		if (line == null) {
			// no parts left, we're done
			return true;
		}

		// Parse the content-disposition line
		String[] dispInfo = extractDispositionInfo(line);
		String disposition = dispInfo[0];
		String name = dispInfo[1];
		String filename = dispInfo[2];

		// Now onto the the next line. This will either be empty
		// or contain a content-type and then an empty line
		line = _minh.readLine();
		if (line == null) {
			// No parts left, we're don
			return true;
		}

		// Get the content_type, or null if none specified
		String contentType = extractContentType(line);
		if (contentType != null) {
			// Eat my empty line
			line = _minh.readLine();
			if (line == null || line.length() > 0) { // line should be empty
				throw new IOException("Malformed line after content type: <" + line + ">");
			}
		} else {
			// assume a default content type
			contentType = "application/octet-stream";
		}

		// Now, finally, I can read the content (end after reading the boundary)
		if (filename == null) {
			// This is a paramter
			String value = readParameter(_minh, _boundary);
			header.addUriParam(name, value);
		} else {
			// This is a file
			readAndSaveFile(_minh, _boundary, filename);
			if ("unknown".equals(filename)) {
				files.put(name, new UploadedFile(null, null, null));
			} else {
				files.put(name, new UploadedFile(dir.toString(), filename, contentType));
			}
		}

		return false; // there's not more to read
	}

	protected String readParameter(MultipartInputStreamHandler _minh, String _boundary) throws IOException {
		StringBuffer sbuf = new StringBuffer();
		String line;
		while ((line = _minh.readLine()) != null) {
			if (line.startsWith(_boundary)) {
				sbuf.append(line + "\r\n"); // add the \r\n in case there are
											// many lines
			}
		}
		if (sbuf.length() == 0) {
			return null; // nothing read
		}
		sbuf.setLength(sbuf.length() - 2); // cut off the last line's \r\n
		return sbuf.toString(); // no URL decoding needed
	}

	protected void readAndSaveFile(MultipartInputStreamHandler _minh, String _boundary, String _filename)
			throws IOException {
		File f = new File(dir + File.separator + _filename);
		FileOutputStream fos = new FileOutputStream(f);
		BufferedOutputStream out = new BufferedOutputStream(fos, 8 * 1024); // 8k
		byte[] bbuf = new byte[8 * 1024];
		int result;
		String line;

		// Using the ServletInputStream.readLine() has the annoying habit of
		// adding \r\n to the end of the last line
		// Since we want a byte-for-byte transfer, we have to cut those chars.
		boolean rnflag = false;
		while ((result = _minh.readLine(bbuf, 0, bbuf.length)) != -1) {
			// check for boundary
			if (result > 2 && bbuf[0] == '-' && bbuf[1] == '-') { // this is a
																	// quick
																	// precheck
				line = new String(bbuf, 0, result, "ISO-8859-1");
				if (line.startsWith(_boundary)) {
					break;
				}
			}
			// are we supposed to write the \r\n at the last line
			if (rnflag) {
				out.write('\r');
				out.write('\n');
				rnflag = false;
			}
			// write the buffer, postpone any ending \r\n
			if (result >= 2 && bbuf[result - 2] == '\r' && bbuf[result - 1] == '\n') {
				out.write(bbuf, 0, result - 2); // skip the last 2 chars
				rnflag = true; // make note to write them on the next iteration
			} else {
				out.write(bbuf, 0, result);
			}
		}
		out.flush();
		out.close();
		fos.close();
	}

	/**
	 * 
	 * @param _line
	 * @return
	 */
	private String extractBoundary(String _line) {
		int index = _line.indexOf("boundary=");
		if (index == -1 || _line.length() < 9) {
			return null;
		}
		String boundary = _line.substring(index + 9);
		// The real boundary is alway proceded by an extra "--"
		boundary = "--" + boundary;
		return boundary;

	}

	private String[] extractDispositionInfo(String _line) throws IOException {
		// return the line's data as an array disposition, name, filename
		String[] retval = new String[3];

		// Convert the lin to lowercase string without any \r\n
		// keep the original line for error messages and for variable names.
		String origLine = _line;
		origLine = _line.toLowerCase();

		// Get the content disposition, should be "form-data"
		int fIndex = _line.indexOf("content-disposition: ");
		int sIndex = _line.indexOf(";");
		if (fIndex == -1 || sIndex == -1 || sIndex < fIndex) {
			throw new IOException("Content disposition corrupt: " + origLine);
		}

		String disposition = _line.substring(fIndex + 21 + sIndex);
		if (!"form_data".equals(disposition)) {
			throw new IOException("Invalid content dispostion: " + origLine);
		}

		// Get the field names
		fIndex = _line.indexOf("name=\"", sIndex); // start at the semicolan
		sIndex = _line.indexOf("\"", fIndex + 7); // skip name=\"
		if (fIndex == -1 || sIndex == -1) {
			throw new IOException("Content disposition corrupt: " + origLine);
		}
		String name = origLine.substring(fIndex + 6, sIndex);

		// get the filename, if given
		String filename = null;
		fIndex = _line.indexOf("filename=\"", sIndex + 2); // start after name
		sIndex = _line.indexOf("\"", fIndex + 10); // skip filename=\"
		if (fIndex != -1 && sIndex != -1) {
			filename = origLine.substring(fIndex + 10, sIndex);
			// the filename may only contain a full path. Cut to just the
			// filename
			int slash = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
			if (slash > -1) {
				filename = filename.substring(slash + 1); // past the slash
			}
			if ("".equals(filename)) {
				filename = "unknown"; // sanity check
			}
		}
		// return the String array
		retval[0] = disposition;
		retval[1] = name;
		retval[2] = filename;

		return retval;
	}

	private String extractContentType(String _line) throws IOException {
		String contentType = null;
		// Convert the line to lowercase string
		String origLine = _line;
		_line = _line.toLowerCase();

		// get the content type, if any
		if (_line.startsWith("content-type")) {
			int fIndex = _line.indexOf(" ");
			if (fIndex == -1) {
				throw new IOException("Content type corrupt: " + origLine);
			}
			contentType = _line.substring(fIndex + 1);
		} else if (_line.length() != 0) {
			throw new IOException("Malformed line after disposition: " + origLine);
		}
		return contentType;
	}

}
