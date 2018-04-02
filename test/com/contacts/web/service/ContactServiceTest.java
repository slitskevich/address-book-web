package com.contacts.web.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Link;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.HTTP;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.contacts.web.model.ContactModel;
import com.contacts.web.model.pagination.ModelListPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class ContactServiceTest {
	
	ContactService tested;
	
	private final static String API_PROTOCOL = "http";
	private final static String API_HOST = "api.com";
	
	@BeforeEach
	void setUp() throws Exception {
		tested = new ContactService(API_PROTOCOL + "://" + API_HOST + "/");
	}

	@Test
	void testHandleOkStatusResponse() {
		StatusLine status = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "");
		HttpResponse response = new BasicHttpResponse(status);
		
		try {
			tested.handleStatusCode(response);
		} catch (Exception e) {
			fail("Didn't expect an error: " + e.getMessage());
		}
	}

	@Test
	void testHandleApiStatusResponse() {
		try {
			StatusLine status = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 400, "");
			HttpResponse response = new BasicHttpResponse(status);

			String statusMessage = "test message";
			setApiStatus(response, statusMessage);

			tested.handleStatusCode(response);
		} catch (Exception e) {
			assertEquals(ApiException.class, e.getClass(), "Expected API Exception instance");
		}
	}

	@Test
	void testHandleErrorResponse() {
		try {
			StatusLine status = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 500, "");
			HttpResponse response = new BasicHttpResponse(status);

			String statusMessage = "test message";
			setApiStatus(response, statusMessage);

			tested.handleStatusCode(response);
		} catch (Exception e) {
			assertNotEquals(ApiException.class, e.getClass(), "Didn't expect API Exception instance");
		}
	}

	@Test
	void testHandleDeleteResponse() {
		StatusLine status = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "");
		HttpResponse response = new BasicHttpResponse(status);

		try {
			tested.handleDeleteResponse(response);
		} catch (Exception e) {
			fail("Didn't expect an error: " + e.getMessage());
		}
	}

	@Test
	void testBuildLoadContactsUri() {
		try {
			int TEST_OFFSET = 10;
			int TEST_LIMIT = 7;
			URI uri = tested.buildLoadContactsUri(TEST_OFFSET, TEST_LIMIT);
			assertEquals(API_PROTOCOL, uri.getScheme());
			assertEquals(API_HOST, uri.getHost());
			assertEquals("/" + ContactService.CONTACTS_PATH, uri.getPath());
			String query = uri.getQuery();
			String[] items = query.split("&");
			Map<String, String> parameters = new HashMap<String, String>();
			for (String next : items) {
				String[] entry = next.split("=");
				parameters.put(entry[0], entry[1]);
			}
			assertEquals(TEST_OFFSET, Integer.parseInt(parameters.get("offset")));
			assertEquals(TEST_LIMIT, Integer.parseInt(parameters.get("limit")));
			
		} catch (Exception e) {
			fail("Didn't expect an error: " + e);
		}
	}
	
	@Test
	void testHandleLoadContactsResponse() {
		StatusLine status = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "");
		HttpResponse response = new BasicHttpResponse(status);

		ContactModel m1 = new ContactModel(1, "f1", "l1", "f1.l1@mail.de");
		ContactModel m2 = new ContactModel(2, "f2", "l2", "f2.l2@mail.de");
		ContactModel[] models = {m1, m2};		
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(models);
			StringEntity entity = new StringEntity(json);
			entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			response.setEntity(entity);
			String URI_FORMAT = "http://localhost:8080/addressbook/api/contacts?offset=%d&limit=5";
			StringBuilder links = new StringBuilder();
			links.append(Link.fromUri(String.format(URI_FORMAT, 0)).rel(ModelListPage.FIRST).build().toString()).append(ModelListPage.SPLITTER).
				  append(Link.fromUri(String.format(URI_FORMAT, 1)).rel(ModelListPage.PREV).build().toString()).append(ModelListPage.SPLITTER).
				  append(Link.fromUri(String.format(URI_FORMAT, 2)).rel(ModelListPage.NEXT).build().toString()).append(ModelListPage.SPLITTER).
				  append(Link.fromUri(String.format(URI_FORMAT, 3)).rel(ModelListPage.LAST).build().toString());
			response.setHeader("links", links.toString());

			ModelListPage<ContactModel> result = tested.handleLoadContactsResponse(response);
			boolean allFound = true;
			for (ContactModel next : result.getPageItems()) {
				boolean nextFound = false;
				for (ContactModel original : models) {
					if (original.getId() == next.getId()) {
						nextFound = true;
						break;
					}
				}
				if (!nextFound) {
					allFound = false;
					break;
				}
			}
			assertTrue(allFound, "Didn't find expected contact");
		} catch (Exception e) {
			fail("Didn't expect an error: " + e.getMessage());
		}
	}
	
	@Test
	void testHandleLoadContactByIdResponse() {
		StatusLine status = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "");
		HttpResponse response = new BasicHttpResponse(status);

		ContactModel m1 = new ContactModel(1, "f1", "l1", "f1.l1@mail.de");
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(m1);
			StringEntity entity = new StringEntity(json);
			entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			response.setEntity(entity);

			ContactModel result = tested.handleLoadContactResponse(response);
			assertEquals(m1.getId(), result.getId());
			assertEquals(m1.getFirstName(), result.getFirstName());
			assertEquals(m1.getLastName(), result.getLastName());
			assertEquals(m1.getAddress(), result.getAddress());
		} catch (Exception e) {
			fail("Didn't expect an error: " + e.getMessage());
		}
	}

	@Test
	void testBuildContactRequestUri() {
		try {
			int TEST_ID = 17;
			URI uri = tested.buildContactRequestUri(TEST_ID);
			assertEquals(API_PROTOCOL, uri.getScheme());
			assertEquals(API_HOST, uri.getHost());
			assertEquals("/" + ContactService.CONTACTS_PATH + "/" + TEST_ID, uri.getPath());
		} catch (Exception e) {
			fail("Didn't expect an error: " + e);
		}
	}
	
	@Test
	void testBuildCreateContactUri() {
		try {
			URI uri = tested.buildCreateContactUri();
			assertEquals(API_PROTOCOL, uri.getScheme());
			assertEquals(API_HOST, uri.getHost());
			assertEquals("/" + ContactService.CONTACTS_PATH, uri.getPath());
		} catch (Exception e) {
			fail("Didn't expect an error: " + e);
		}
	}
	
	@Test
	void testBuildSendContactRequest() {
		ContactModel m1 = new ContactModel(1, "f1", "l1", "f1.l1@mail.de");
		try {
			HttpPut method = new HttpPut(tested.buildContactRequestUri(m1.getId()));
			tested.buildSendContactRequest(m1, method);
			ObjectMapper objectMapper = new ObjectMapper();
			BufferedReader reader = new BufferedReader(new InputStreamReader(method.getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
				builder.append(line).append("\n");
			}
			ContactModel result = objectMapper.readValue(builder.toString(), ContactModel.class);
			assertEquals(1, result.getId());
		} catch (Exception e) {
			fail("Didn't expect an error: " + e);
		}
	}
	
	private void setApiStatus(HttpResponse response, String statusMessage) throws JsonProcessingException, UnsupportedEncodingException {
		ObjectMapper objectMapper = new ObjectMapper();
		ApiStatus content = new ApiStatus(statusMessage);
		String json = objectMapper.writeValueAsString(content);
		StringEntity entity = new StringEntity(json);
		entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		response.setEntity(entity);
	}

}
