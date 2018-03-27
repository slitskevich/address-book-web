package com.contacts.web.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
	
	private String readResponseEntity(HttpResponse response) throws UnsupportedEncodingException, UnsupportedOperationException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		StringBuilder builder = new StringBuilder();
		for (String line = null; (line = reader.readLine()) != null;) {
		    builder.append(line).append("\n");
		}
		return builder.toString();
	}
	
	private <T> EntityListPage<T> loadList(String path, Class<T> clazz, int offset, int limit) throws ClientProtocolException, IOException, URISyntaxException {
		URI uri = (new URIBuilder(apiUrl + path)).
									 setParameter("offset", Integer.toString(offset)).
									 setParameter("limit", Integer.toString(limit)).
									 build();
		HttpResponse response = requestEntity(uri);
		String content = readResponseEntity(response);
		String links = response.getHeaders("Links")[0].getValue();
		return new EntityListPage<T>(content, links, clazz);
	}
	
	private <T> T loadEntity(URI uri, Class<T> clazz) throws ClientProtocolException, IOException {
		HttpResponse response = requestEntity(uri);
		String content = readResponseEntity(response);
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(content, clazz);
	}
	
	public EntityListPage<Contact> loadContacts(int offset, int limit) throws Exception {
		return loadList(CONTACTS_PATH, Contact.class, offset, limit);
	}
	
	public Contact loadContactById(int contactId) throws Exception {
		String address = String.format(ENTITY_REQUEST_URL, apiUrl, CONTACTS_PATH, Integer.toString(contactId));
		URI uri = (new URIBuilder(address)).build();
		return loadEntity(uri, Contact.class);
	}
	
	public void updateContact(Contact contact) throws Exception {
		String address = String.format(ENTITY_REQUEST_URL, apiUrl, CONTACTS_PATH, Integer.toString(contact.getId()));
		URI uri = (new URIBuilder(address)).build();
		
		final HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPut httpPutRequest = new HttpPut(uri);
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(contact);
		StringEntity entity = new StringEntity(json);
		entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		httpPutRequest.setHeader("Accept", "application/json");
		httpPutRequest.setHeader("Content-type", "application/json");
		httpPutRequest.setEntity(entity);
		HttpResponse response = httpClient.execute(httpPutRequest);
		int statusCode = response.getStatusLine().getStatusCode(); 
		if (statusCode != 200 && statusCode != 304) {
			throw new Exception("Failed to update contact: " + statusCode);
		}
	}

}
