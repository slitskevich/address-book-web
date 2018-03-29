package com.contacts.web.service.pagination;

import java.net.URI;
import java.util.logging.Logger;

import com.contacts.web.Constants;

public class PageLink {
	
	private static final Logger LOGGER = Logger.getLogger(PageLink.class.getName());

	private final static int DEFAULT_LIMIT = 5;
	private final static int DEFAULT_OFFSET = 0;
	
	private String link;
	private int offset;
	private int limit;
	
	private PageLink(String link, int offset, int limit) {
		LOGGER.info("this.offset: " + offset + ", limit: " + limit + ", link: " + link);
		this.link = link;
		this.limit = limit;
		this.offset = offset;
	}
	
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
	
	public String getLink() {
		return link;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public int getLimit() {
		return limit;
	}		
}
