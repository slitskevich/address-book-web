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

/**
 * Represents model list page
 *
 * @param <T> the generic type
 */
public class ModelListPage <T> {
	
	/** The page items. */
	private List<T> pageItems;
	
	/**
	 * Gets the page items.
	 *
	 * @return the page items
	 */
	public List<T> getPageItems() {
		return pageItems;
	}

	/** Pagination link rel value */
	public final static String PREV = "prev";
	
	/** Pagination link rel value */
	public final static String NEXT = "next";
	
	/** Pagination link rel value */
	public final static String LAST = "last";
	
	/** Pagination link rel value */
	public final static String FIRST = "first";
	
	/** Pagination links splitter */
	public final static String SPLITTER = ",";
	
	/** Pagination links. */
	private Map<String, PageLink> links = new HashMap<String, PageLink>();
	
	/**
	 * Instantiates a new model list page.
	 *
	 * @param pageContent JSON representation for the page items array
	 * @param linksHeader the links header value
	 * @param clazz the model class
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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

	/**
	 * Gets the first page link.
	 *
	 * @return the first page link
	 */
	public PageLink getFirstPageLink() {
		return links.get(FIRST);
	}

	/**
	 * Gets the prev page link.
	 *
	 * @return the prev page link
	 */
	public PageLink getPrevPageLink() {
		return links.get(PREV);
	}

	/**
	 * Gets the next page link.
	 *
	 * @return the next page link
	 */
	public PageLink getNextPageLink() {
		return links.get(NEXT);
	}

	/**
	 * Gets the last page link.
	 *
	 * @return the last page link
	 */
	public PageLink getLastPageLink() {
		return links.get(LAST);
	}
}
