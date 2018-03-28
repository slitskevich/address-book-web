<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.*"  %>
<%@page import="com.contacts.web.model.Contact"  %>
<%@page import="com.contacts.web.service.*"  %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%= "Contacts" %></title>
</head>
<body>
	<% 	String errorMessage = request.getParameter("errorMessage");
		if (errorMessage != null) { %>
		Error: <%= errorMessage %><%
		}
		try {
			ContactService service = new ContactService(getServletContext().getInitParameter("apiUrl"));
			String offsetParameter = request.getParameter("offset");
			int offset = offsetParameter != null ? Integer.parseInt(offsetParameter) : 0;
			String limitParameter = request.getParameter("limit");
			int limit = limitParameter != null ? Integer.parseInt(limitParameter) : 5;
			EntityListPage<Contact> contacts = service.loadContacts(offset, limit);			
			
			%><header><h1>Contacts</h1></header>   <a href="contact.jsp">Add new Contact</a></a><ol><%
			for (Contact next : contacts.getPageItems()) {
	%><li><a href="contact.jsp?contactId=<%= next.getId() %>"><%= next.getLastName() %>, <%= next.getFirstName() %></a></li><%
			}%>
			</ol><br/>
			<%
			PageLink firstLink = contacts.getFirstPageLink();
			PageLink prevLink = contacts.getPrevPageLink();
			PageLink nextLink = contacts.getNextPageLink();
			PageLink lastLink = contacts.getLastPageLink();
			%>
			<% if (firstLink != null) { %><a href="?offset=<%= firstLink.getOffset() %>&limit=<%= firstLink.getLimit() %>">First</a><% } %>
			<% if (prevLink != null) { %><a href="?offset=<%= prevLink.getOffset() %>&limit=<%= prevLink.getLimit() %>">Previous</a><% } %>
			<% if (nextLink != null) { %><a href="?offset=<%= nextLink.getOffset() %>&limit=<%= nextLink.getLimit() %>">Next</a><% } %>
			<% if (lastLink != null) { %><a href="?offset=<%= lastLink.getOffset() %>&limit=<%= lastLink.getLimit() %>">Last</a><% } %>
	<%	} catch (Exception ex) {
			%><%= "Failed to load contact list: " + ex %><%
		}
	%>
</body>
</html>