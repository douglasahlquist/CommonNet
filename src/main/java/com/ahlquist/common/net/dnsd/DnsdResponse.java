
package com.ahlquist.common.net.dnsd;

import java.util.ArrayList;
import java.util.List;

/**
 * A DnsdResponse includes:
 * <p>
 * <li>Header
 * <li>Question
 * <li>Answer: with a list of record
 * <li>Authority: with a list of record
 * <li>Additionals: with a list of record
 *
 * @author Douglas Ahlquist
 * @see DnsdMessage
 * @see DnsdRecord
 */
public class DnsdResponse extends DnsdMessage {
	/** Answer records */
	private List<DnsdRecord> answers;

	/* Authority records */
	//TODO:DKA private List authorities;

	/* Additional records */
	//TODO:DKA private List additionals;

	/**
	 * Creates DnsdResponse from an incoming request.
	 * 
	 * @param a
	 *            request object with info in the Header and Question
	 */
	public DnsdResponse(DnsdRequest request) {
		super(request.getHeader(), request.getQuestions());
		answers = new ArrayList<DnsdRecord>();
	}

	/**
	 * Pack up a DnsdResponse into Byte Array.
	 * 
	 * @param bytebuilder
	 *            hold a byte array and help transfer data into binary format
	 */
	void pack(DnsdByteBuilder builder) {
		getHeader().setAnswerCount(answers.size());

		// pack up the header and the questions
		super.pack(builder);

		// pack the answers
		for (int i = 0; i < answers.size(); i++) {
			// Pack the proper subclass of DnsdRecord (MX or A Record)
			((DnsdRecord) answers.get(i)).pack(builder);
		}
	}

	/**
	 * Add a record to the answer list.
	 * 
	 * @param record
	 *            the DNS record to be added to the answer section of the
	 *            Response
	 */
	public void addAnswer(DnsdRecord record) {
		answers.add(record);
	}
}