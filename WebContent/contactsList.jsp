<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.*"  %>
<%@page import="com.contacts.web.model.ContactModel"  %>
<%@page import="com.contacts.web.service.*"  %>
<%@page import="com.contacts.web.Constants" %>
<%@ page errorPage="error.jsp" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%="Contacts"%></title>
</head>
<body>
	<%
		String query = request.getQueryString();
			String reloadPath = request.getServletPath() + (query != null ? query : "");

			String errorMessage = request.getParameter(Constants.ERROR_MESSAGE_PARAMETER);
			if (errorMessage != null) {
	%><p style="color:red;">Error: <%=errorMessage%></p><%
		}
		try {
			ContactService service = new ContactService(getServletContext().getInitParameter(Constants.API_URL_PARAMETER));
			String offsetParameter = request.getParameter(Constants.OFFSET_PARAMETER);
			int offset = offsetParameter != null ? Integer.parseInt(offsetParameter) : 0;
			String limitParameter = request.getParameter(Constants.LIMTI_PARAMETER);
			int limit = limitParameter != null ? Integer.parseInt(limitParameter) : 5;
			ModelListPage<ContactModel> contacts = service.loadContacts(offset, limit);
	%><header><h1>Contacts</h1></header>   <a href="contact.jsp">Add new Contact</a></a><ol><%
   	for (ContactModel next : contacts.getPageItems()) {
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
	<%	
		} catch (ApiException apiEx) {
			%><p style="color:red;">Error: <%= apiEx.getMessage() %></p><%
		}
	%>
</body>
</html>