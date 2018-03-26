<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.*"  %>
<%@page import="com.contacts.web.model.Contact"  %>
<%@page import="com.contacts.web.service.ContactService"  %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%= "All contacts" %></title>
</head>
<body>
	<%
		try {
			ContactService service = new ContactService(getServletContext().getInitParameter("apiUrl"));
			List<Contact> contacts = service.loadAllContacts();
			%><header><h1>Contacts</h1></header><ol><%
			for (Contact next : contacts) {
	%><li><a href="contact?contactId=<%= next.getId() %>"><%= next.getLastName() %>, <%= next.getFirstName() %></a></li><%
			}
			%></ol><%
		} catch (Exception ex) {
			%><%= "Failed to load contact list" %><%
		}
	%>
</body>
</html>