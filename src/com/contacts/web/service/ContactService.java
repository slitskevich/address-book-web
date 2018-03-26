package com.contacts.web.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.contacts.web.model.Contact;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class ContactService {
	
	private static final String ALL_CONTACTS_PATH = "contacts";
	
	private String apiUrl;
	
	public ContactService(String apiUrl) {
		this.apiUrl = apiUrl;
	}
	
	private String requestEntity(String path) throws ClientProtocolException, IOException {
		final HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpGetRequest = new HttpGet(apiUrl + "contacts");
		HttpResponse httpResponse = httpClient.execute(httpGetRequest);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
		StringBuilder builder = new StringBuilder();
		for (String line = null; (line = reader.readLine()) != null;) {
		    builder.append(line).append("\n");
		}
		return builder.toString();
	}
	
	private <T> List<T> loadList(String path, Class<T> clazz) throws ClientProtocolException, IOException {
		String response = requestEntity(path);

		ObjectMapper objectMapper = new ObjectMapper();
		TypeFactory typeFactory = objectMapper.getTypeFactory();
		return objectMapper.readValue(response, typeFactory.constructCollectionType(List.class, clazz));
	}
	
	public List<Contact> loadAllContacts() throws ClientProtocolException, IOException {
		List<Contact> contacts = loadList(ALL_CONTACTS_PATH, Contact.class);
		
		return contacts;
	}

}
