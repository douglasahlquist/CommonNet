package com.ahlquist.common.net.http;

/*
 *  This class creates a new Request object
 *  
 * 
 * 		POST /e/myg HTTP/1.1
		Accept: *//*     the previous accept should include only one slash
					Referer: http://132.122.5.3/index.html
					Accept-Language: en-us
					Content-Type: multipart/form-data;
					+
					Accept-Encoding: gzip, deflate
					User-Agent: PhoenixTestTool 1.0
					Content-Length: 797  where the number is the content length
					Connection: Keep-Alive
					Cache-Control: no-cache
					
					test_mode=   &current_test_case=Cosby&favorite+flavor=flies
					
					* 
					* @author Douglas_Ahlquist
					*
					*/
public class MultipartBuilder {
	RequestHeader header;
	StringBuilder sbuf;

	public MultipartBuilder(RequestHeader _header, StringBuilder _sbuf) {
		this.header = _header;
		this.sbuf = _sbuf;
	}

	public StringBuilder getResponse() {
		String image_file_name = "";
		return new StringBuilder().append("POST /saveImage HTTP/1.1").append("Accept: */*")
				.append("Referer: " + header.getUriParam("remote_host")).append("Accept-Language: en-us")
				.append("Content-Type: multipart/form-data;").append("boundary=---------------------------7d31a435d08")
				.append("User-Agent: PhoenixTestTool 1.0").append("Content-Length: " + sbuf.length())
				.append("Connection: Keep-Alive").append("Cache-Control: no-cache").append("\r\n")
				.append("image_file_name=" + image_file_name).append("\r\n")
				.append("-----------------------------7d31a435d08")
				// TODO: add dispotion length and file name attributes
				.append(sbuf).append("-----------------------------7d31a435d08").append("\r\n");

	}
}