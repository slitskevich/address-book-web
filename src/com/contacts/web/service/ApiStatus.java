package com.contacts.web.service;

public class ApiStatus {
	private String message;
	
	public ApiStatus() {
		
	}
	
	public ApiStatus(String message) {
		this.message = message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
