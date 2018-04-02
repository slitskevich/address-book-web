package com.contacts.web.service;

/**
 * Represents REST API execution message
 */
public class ApiStatus {
	
	/** The message. */
	private String message;
	
	/**
	 * Instantiates a new api status.
	 */
	public ApiStatus() {
		
	}
	
	/**
	 * Instantiates a new api status.
	 *
	 * @param message the message
	 */
	public ApiStatus(String message) {
		this.message = message;
	}
	
	/**
	 * Sets the message.
	 *
	 * @param message the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}
}
