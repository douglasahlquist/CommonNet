
package com.ahlquist.common.net.dnsd;

/**
 * A DNS request includes the Header and the Question and empty answer authority
 * and addition fields.
 * 
 * @author Douglas Ahlquist
 * @version 0.1
 * @see DnsdMessage
 */
public class DnsdRequest extends DnsdMessage {
	/**
	 * Create DnsdMessage with DnsdByteParser which helps to parse the binary
	 * data.
	 * 
	 * @param parser
	 *            parse out the message from the byte array
	 */
	DnsdRequest(DnsdByteParser parser) {
		super(parser);
	}

}
