package com.contacts.web.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.core.Link;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class EntityListPage <T> {
	
	private static final Logger LOGGER = Logger.getLogger(EntityListPage.class.getName());
	
	private List<T> pageItems;
	
	public List<T> getPageItems() {
		return pageItems;
	}

	private final static String PREV = "prev";
	private final static String NEXT = "next";
	private final static String LAST = "last";
	private final static String FIRST = "first";
	private Map<String, PageLink> links = new HashMap<String, PageLink>();
	
	public EntityListPage(String pageContent, String linksHeader, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		TypeFactory typeFactory = objectMapper.getTypeFactory();
		
		for (String next : linksHeader.split(",")) {
			Link link = Link.valueOf(next);
			links.put(link.getRel(), PageLink.parse(link.getUri()));
		}	
		
		this.pageItems = objectMapper.readValue(pageContent, typeFactory.constructCollectionType(List.class, clazz));
	}

	public PageLink getFirstPageLink() {
		return links.get(FIRST);
	}

	public PageLink getPrevPageLink() {
		return links.get(PREV);
	}

	public PageLink getNextPageLink() {
		return links.get(NEXT);
	}

	public PageLink getLastPageLink() {
		return links.get(LAST);
	}
}
