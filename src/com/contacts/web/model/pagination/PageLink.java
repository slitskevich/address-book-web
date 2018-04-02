package com.contacts.web.model.pagination;

import java.net.URI;
import java.util.logging.Logger;

import com.contacts.web.Constants;

/**
 * Model list page link
 */
public class PageLink {
	
	private static final Logger LOGGER = Logger.getLogger(PageLink.class.getName());

	private final static int DEFAULT_LIMIT = 5;
	private final static int DEFAULT_OFFSET = 0;
	
	/** The page link. */
	private String link;
	
	/** The page offset. */
	private int offset;
	
	/** The page limit. */
	private int limit;
	
	/**
	 * Instantiates a new page link.
	 *
	 * @param link the link
	 * @param offset the offset
	 * @param limit the limit
	 */
	private PageLink(String link, int offset, int limit) {
		LOGGER.info("this.offset: " + offset + ", limit: " + limit + ", link: " + link);
		this.link = link;
		this.limit = limit;
		this.offset = offset;
	}
	
	/**
	 * Parses the URI by examining its query string
	 *
	 * @param uri the uri
	 * @return the page link
	 */
	public static PageLink parse(URI uri) {
		int offset = DEFAULT_OFFSET;
		int limit = DEFAULT_LIMIT;
		for (String next : uri.getQuery().split("&")) {
			String[] comps = next.split("=");
			int value = Integer.parseInt(comps[1]);
			if (comps[0].equalsIgnoreCase(Constants.OFFSET_PARAMETER)) {
				offset = value;
			} else if (comps[0].equalsIgnoreCase(Constants.LIMTI_PARAMETER)) {
				limit = value;
			}
		}
		return new PageLink(uri.toString(), offset, limit);
	}
	
	/**
	 * Gets the link.
	 *
	 * @return the link
	 */
	public String getLink() {
		return link;
	}
	
	/**
	 * Gets the offset.
	 *
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}
	
	/**
	 * Gets the limit.
	 *
	 * @return the limit
	 */
	public int getLimit() {
		return limit;
	}		
}
