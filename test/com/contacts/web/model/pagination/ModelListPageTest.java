package com.contacts.web.model.pagination;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import javax.ws.rs.core.Link;

import org.junit.jupiter.api.Test;

import com.contacts.web.model.ContactModel;
import com.fasterxml.jackson.databind.ObjectMapper;

class ModelListPageTest {

	@Test
	void testModelListPage() {
		ContactModel m1 = new ContactModel(1, "f1", "l1", "f1.l1@mail.de");
		ContactModel m2 = new ContactModel(2, "f2", "l2", "f2.l2@mail.de");
		ContactModel[] models = {m1, m2};		
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String content = objectMapper.writeValueAsString(models);
			
			String URI_FORMAT = "http://localhost:8080/addressbook/api/contacts?offset=%d&limit=5";
			StringBuilder links = new StringBuilder();
			links.append(Link.fromUri(String.format(URI_FORMAT, 0)).rel(ModelListPage.FIRST).build().toString()).append(ModelListPage.SPLITTER).
				  append(Link.fromUri(String.format(URI_FORMAT, 1)).rel(ModelListPage.PREV).build().toString()).append(ModelListPage.SPLITTER).
				  append(Link.fromUri(String.format(URI_FORMAT, 2)).rel(ModelListPage.NEXT).build().toString()).append(ModelListPage.SPLITTER).
				  append(Link.fromUri(String.format(URI_FORMAT, 3)).rel(ModelListPage.LAST).build().toString());

			ModelListPage<ContactModel> page = new ModelListPage<ContactModel>(content, links.toString(), ContactModel.class);
			assertEquals(models.length, page.getPageItems().size(), "Unexpected number of page items: " + page.getPageItems().size());
			assertEquals(String.format(URI_FORMAT, 0), page.getFirstPageLink().getLink(), "Unexpected link: " + page.getFirstPageLink().getLink());
			assertEquals(0, page.getFirstPageLink().getOffset(), "Unexpected link offset: " + page.getFirstPageLink().getOffset());
			assertEquals(5, page.getFirstPageLink().getLimit(), "Unexpected link limit: " + page.getFirstPageLink().getLimit());
			assertEquals(String.format(URI_FORMAT, 0), page.getFirstPageLink().getLink(), "Unexpected link: " + page.getFirstPageLink().getLink());
			assertEquals(String.format(URI_FORMAT, 1), page.getPrevPageLink().getLink(), "Unexpected link: " + page.getPrevPageLink().getLink());
			assertEquals(String.format(URI_FORMAT, 2), page.getNextPageLink().getLink(), "Unexpected link: " + page.getNextPageLink().getLink());
			assertEquals(String.format(URI_FORMAT, 3), page.getLastPageLink().getLink(), "Unexpected link: " + page.getLastPageLink().getLink());
		} catch (Exception ex) {
			fail("Didn't expect to fail: " + ex.getMessage());
		}
	}
	
	@Test
	void testEmptyList() {
		try {
			ModelListPage<ContactModel> page = new ModelListPage<ContactModel>("[]", "", ContactModel.class);
			assertEquals(0, page.getPageItems().size(), "Unexpected number of page items: " + page.getPageItems().size());
		} catch (Exception e) {
			fail("Didn't expect to fail: " + e.getMessage());
		}	
	}
	
	@Test
	void testInvalidLink() {
		try {
			String URI_FORMAT = "invalid";
			StringBuilder links = new StringBuilder();
			links.append(Link.fromUri(URI_FORMAT).rel(ModelListPage.FIRST).build().toString());
			new ModelListPage<ContactModel>("[]", links.toString(), ContactModel.class);
			fail("Didn't expect to proceed");
		} catch (Exception e) {
			assertEquals(NullPointerException.class, e.getClass());
		}	
	}

}
