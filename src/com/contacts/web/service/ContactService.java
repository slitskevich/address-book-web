package com.contacts.web.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import com.contacts.web.model.Contact;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ContactService {

	private static final Logger LOGGER = Logger.getLogger(ContactService.class.getName());

	private static final String CONTACTS_PATH = "contacts";

	private static final String ENTITY_REQUEST_URL = "%s%s/%s";

	private static final String ENTITY_CREATE_URL = "%s%s";

	private String apiUrl;

	public ContactService(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	private HttpResponse requestEntity(URI uri) throws ClientProtocolException, IOException {
		LOGGER.info("sending request to " + uri.toString());
		final HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpGetRequest = new HttpGet(uri);
		return httpClient.execute(httpGetRequest);
	}

	private String readResponseEntity(HttpResponse response)
			throws UnsupportedEncodingException, UnsupportedOperationException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		StringBuilder builder = new StringBuilder();
		for (String line = null; (line = reader.readLine()) != null;) {
			builder.append(line).append("\n");
		}
		return builder.toString();
	}

	public EntityListPage<Contact> loadContacts(int offset, int limit) throws Exception {
		URI uri = (new URIBuilder(apiUrl + CONTACTS_PATH)).setParameter("offset", Integer.toString(offset))
				.setParameter("limit", Integer.toString(limit)).build();
		HttpResponse response = requestEntity(uri);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			ApiStatus status = getResponseStatus(response);
			throw new ApiException(status.getMessage());
		}
		String content = readResponseEntity(response);
		String links = response.getHeaders("Links")[0].getValue();
		return new EntityListPage<Contact>(content, links, Contact.class);
	}

	public Contact loadContactById(int contactId) throws Exception {
		String address = String.format(ENTITY_REQUEST_URL, apiUrl, CONTACTS_PATH, Integer.toString(contactId));
		URI uri = (new URIBuilder(address)).build();
		HttpResponse response = requestEntity(uri);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			ApiStatus status = getResponseStatus(response);
			throw new ApiException(status.getMessage());
		}
		String content = readResponseEntity(response);
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(content, Contact.class);
	}

	public void updateContact(Contact contact) throws Exception {
		String address = String.format(ENTITY_REQUEST_URL, apiUrl, CONTACTS_PATH, Integer.toString(contact.getId()));
		URI uri = (new URIBuilder(address)).build();
		HttpResponse response = sendContact(contact, new HttpPut(uri));
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200 && statusCode != 304) {
			ApiStatus status = getResponseStatus(response);
			throw new ApiException(status.getMessage());
		}
	}

	public void createContact(Contact contact) throws Exception {
		String address = String.format(ENTITY_CREATE_URL, apiUrl, CONTACTS_PATH);
		URI uri = (new URIBuilder(address)).build();
		HttpResponse response = sendContact(contact, new HttpPost(uri));
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 201) {
			ApiStatus status = getResponseStatus(response);
			throw new ApiException(status.getMessage());
		}
	}

	public void deleteContact(int contactId) throws Exception {
		String address = String.format(ENTITY_REQUEST_URL, apiUrl, CONTACTS_PATH, Integer.toString(contactId));
		URI uri = (new URIBuilder(address)).build();
		final HttpClient httpClient = HttpClientBuilder.create().build();
		HttpResponse response = httpClient.execute(new HttpDelete(uri));
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 204) {
			ApiStatus status = getResponseStatus(response);
			throw new ApiException(status.getMessage());
		}
	}

	private HttpResponse sendContact(Contact contact, HttpEntityEnclosingRequestBase method) throws Exception {
		final HttpClient httpClient = HttpClientBuilder.create().build();
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(contact);
		StringEntity entity = new StringEntity(json);
		entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		method.setHeader("Accept", "application/json");
		method.setHeader("Content-type", "application/json");
		method.setEntity(entity);
		return httpClient.execute(method);
	}

	private ApiStatus getResponseStatus(HttpResponse response) throws Exception {
		String content = readResponseEntity(response);
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(content, ApiStatus.class);
	}
}
