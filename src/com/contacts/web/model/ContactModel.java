package com.contacts.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents contact model
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactModel {
	
	/** The id. */
	private int id;
	
	/** The first name. */
	private String firstName;
	
	/** The last name. */
	private String lastName;
	
	/** The address. */
	private String address;
	
	/**
	 * Instantiates a new contact model.
	 */
	public ContactModel() {
		
	}
	
	/**
	 * Instantiates a new contact model.
	 *
	 * @param id the id
	 * @param firstName the first name
	 * @param lastName the last name
	 * @param address the address
	 */
	public ContactModel(int id, String firstName, String lastName, String address) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
	}

	/**
	 * Gets the first name.
	 *
	 * @return the first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the first name.
	 *
	 * @param firstName the new first name
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Gets the last name.
	 *
	 * @return the last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the last name.
	 *
	 * @param lastName the new last name
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Gets the address.
	 *
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets the address.
	 *
	 * @param address the new address
	 */
	public void setAddress(String address) {
		this.address = address;
	}
}
