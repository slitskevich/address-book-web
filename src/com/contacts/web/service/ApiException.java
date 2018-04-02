package com.contacts.web.service;

/**
 * Represents REST API call execution error
 */
public class ApiException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new api exception.
	 *
	 * @param message the message
	 */
	public ApiException(String message) {
		super(message);
	}
}
