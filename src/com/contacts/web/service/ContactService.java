package com.contacts.web.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import com.contacts.web.model.ContactModel;
import com.contacts.web.model.pagination.ModelListPage;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implements calls to REST API.
 */
public class ContactService {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(ContactService.class.getName());

	/** Contacts entity API path */
	static final String CONTACTS_PATH = "contacts";

	/** String format for contact entity calls with ID */
	private static final String ENTITY_REQUEST_URL = "%s%s/%s";

	/** String format for contact entity calls without ID */
	private static final String ENTITY_CREATE_URL = "%s%s";

	/** The REST API URL. */
	private String apiUrl;

	/**
	 * Instantiates a new contact service.
	 *
	 * @param apiUrl REST API URL value
	 */
	public ContactService(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	/**
	 * Calls API to loads entry(-ies) 
	 *
	 * @param uri the API URI
	 * @return the call response
	 * @throws Exception the exception
	 */
	private HttpResponse requestEntity(URI uri) throws Exception {
		LOGGER.info("sending request to " + uri.toString());
		final HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpGetRequest = new HttpGet(uri);
		HttpResponse response = httpClient.execute(httpGetRequest);
		return response;
	}
	
	/**
	 * Checks API response status code 
	 *
	 * @param response the API response
	 * @throws Exception if status code is 500 (internal error) or bigger
	 * @throws ApiException if status code is 4xx - considered to be application flow issue
	 */
	void handleStatusCode(HttpResponse response) throws Exception {
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode >= 400) {
			ApiStatus status = getResponseStatus(response);
			if (statusCode >= 500) {
				throw new Exception(status.getMessage());
			} else {
				throw new ApiException(status.getMessage());
			}
		}
	}

	/**
	 * Read response entity string representation
	 *
	 * @param response the response
	 * @return the entity string representation
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws UnsupportedOperationException the unsupported operation exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String readResponseEntity(HttpResponse response)
			throws UnsupportedEncodingException, UnsupportedOperationException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		StringBuilder builder = new StringBuilder();
		for (String line = null; (line = reader.readLine()) != null;) {
			builder.append(line).append("\n");
		}
		return builder.toString();
	}

	/**
	 * Loads contact list page
	 *
	 * @param offset the page offset
	 * @param limit the page limit
	 * @return the model list page
	 * @throws Exception the exception
	 */
	public ModelListPage<ContactModel> loadContacts(int offset, int limit) throws Exception {
		return handleLoadContactsResponse(requestEntity(buildLoadContactsUri(offset, limit)));
	}
	
	/**
	 * Builds the load contacts uri.
	 *
	 * @param offset the offset
	 * @param limit the limit
	 * @return the uri
	 * @throws Exception the exception
	 */
	URI buildLoadContactsUri(int offset, int limit) throws Exception {
		return (new URIBuilder(apiUrl + CONTACTS_PATH)).setParameter("offset", Integer.toString(offset))
				.setParameter("limit", Integer.toString(limit)).build();
	}
	
	/**
	 * Handle load contacts response. Parses entity and pagination links.
	 *
	 * @param response the response
	 * @return the model list page
	 * @throws Exception the exception
	 */
	ModelListPage<ContactModel> handleLoadContactsResponse(HttpResponse response) throws Exception {		
		handleStatusCode(response);
		String content = readResponseEntity(response);
		String links = response.getHeaders("Links")[0].getValue();
		return new ModelListPage<ContactModel>(content, links, ContactModel.class);
	}

	/**
	 * Builds the contact request uri.
	 *
	 * @param contactId the contact id
	 * @return the uri
	 * @throws Exception the exception
	 */
	URI buildContactRequestUri(int contactId) throws Exception {
		String address = String.format(ENTITY_REQUEST_URL, apiUrl, CONTACTS_PATH, Integer.toString(contactId));
		return (new URIBuilder(address)).build();
	}
	
	/**
	 * Handle load contact response.
	 *
	 * @param response the response
	 * @return the contact model
	 * @throws Exception the exception
	 */
	ContactModel handleLoadContactResponse(HttpResponse response) throws Exception {
		handleStatusCode(response);
		String content = readResponseEntity(response);
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(content, ContactModel.class);
	}
	
	/**
	 * Load contact by id.
	 *
	 * @param contactId the contact id
	 * @return the contact model
	 * @throws Exception the exception
	 */
	public ContactModel loadContactById(int contactId) throws Exception {		
		return handleLoadContactResponse(requestEntity(buildContactRequestUri(contactId)));
	}

	/**
	 * Update contact.
	 *
	 * @param contact the contact
	 * @throws Exception the exception
	 */
	public void updateContact(ContactModel contact) throws Exception {
		sendContact(contact, new HttpPut(buildContactRequestUri(contact.getId())));
	}
	
	/**
	 * Builds the create contact uri.
	 *
	 * @return the uri
	 * @throws Exception the exception
	 */
	URI buildCreateContactUri() throws Exception {
		String address = String.format(ENTITY_CREATE_URL, apiUrl, CONTACTS_PATH);
		return (new URIBuilder(address)).build();
	}

	/**
	 * Creates the contact.
	 *
	 * @param contact the contact
	 * @throws Exception the exception
	 */
	public void createContact(ContactModel contact) throws Exception {
		sendContact(contact, new HttpPost(buildCreateContactUri()));
	}
	
	/**
	 * Delete request.
	 *
	 * @param contactId the contact id
	 * @return the http uri request
	 * @throws Exception the exception
	 */
	HttpUriRequest deleteRequest(int contactId) throws Exception {
		return new HttpDelete(buildContactRequestUri(contactId));
	}
	
	/**
	 * Handle delete response.
	 *
	 * @param response the response
	 * @throws Exception the exception
	 */
	void handleDeleteResponse(HttpResponse response) throws Exception {
		handleStatusCode(response);
	}

	/**
	 * Delete contact.
	 *
	 * @param contactId the contact id
	 * @throws Exception the exception
	 */
	public void deleteContact(int contactId) throws Exception {
		handleDeleteResponse(HttpClientBuilder.create().build().execute(deleteRequest(contactId)));
	}
	
	/**
	 * Builds the send contact request.
	 *
	 * @param contact the contact
	 * @param method the method
	 * @throws Exception the exception
	 */
	void buildSendContactRequest(ContactModel contact, HttpEntityEnclosingRequestBase method) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(contact);
		StringEntity entity = new StringEntity(json);
		entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		method.setHeader("Accept", "application/json");
		method.setHeader("Content-type", "application/json");
		method.setEntity(entity);
	}
	
	/**
	 * Send contact.
	 *
	 * @param contact the contact
	 * @param method the method
	 * @throws Exception the exception
	 */
	private void sendContact(ContactModel contact, HttpEntityEnclosingRequestBase method) throws Exception {
		buildSendContactRequest(contact, method);
		HttpResponse response = HttpClientBuilder.create().build().execute(method);
		this.handleStatusCode(response);
	}

	/**
	 * Gets the response status.
	 *
	 * @param response the response
	 * @return the response status
	 * @throws Exception the exception
	 */
	private ApiStatus getResponseStatus(HttpResponse response) throws Exception {
		String content = readResponseEntity(response);
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(content, ApiStatus.class);
	}
}
