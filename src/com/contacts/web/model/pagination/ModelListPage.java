package com.contacts.web.model.pagination;

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

public class ModelListPage <T> {
	
	private static final Logger LOGGER = Logger.getLogger(ModelListPage.class.getName());
	
	private List<T> pageItems;
	
	public List<T> getPageItems() {
		return pageItems;
	}

	public final static String PREV = "prev";
	public final static String NEXT = "next";
	public final static String LAST = "last";
	public final static String FIRST = "first";
	
	public final static String SPLITTER = ",";
	
	private Map<String, PageLink> links = new HashMap<String, PageLink>();
	
	public ModelListPage(String pageContent, String linksHeader, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		TypeFactory typeFactory = objectMapper.getTypeFactory();
		
		if (linksHeader != null && !linksHeader.isEmpty()) {
			for (String next : linksHeader.split(SPLITTER)) {
				Link link = Link.valueOf(next);
				links.put(link.getRel(), PageLink.parse(link.getUri()));
			}	
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
