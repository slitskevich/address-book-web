<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.contacts.web.model.Contact"  %>
<%@page import="com.contacts.web.service.*"  %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Contact details</title>
</head>
<body>
	<a href="<%=request.getContextPath()%>">Full contact list</a>
	<header><h1>Contact details</h1></header><ol>
	<% 	String errorMessage = (String)request.getAttribute("errorMessage");
		if (errorMessage != null) { %>
		<p style="color:red;">Error: <%= errorMessage %></p><%
		}
		try {
			ContactService service = new ContactService(getServletContext().getInitParameter("apiUrl"));
			String idParameter = request.getParameter("contactId");
			String firstName = "";
			String lastName = "";
			String email = "";
			String action = "/CreateContact";
			int contactId = -1;
			if (idParameter != null) {
				contactId = Integer.parseInt(idParameter);
				Contact contact = service.loadContactById(contactId);
				firstName = contact.getFirstName();
				lastName = contact.getLastName();
				email = contact.getAddress();
				action = "/UpdateContact";
			}
				%>
			<form action="${pageContext.request.contextPath}/<%= action %>" method="post">
				<input name="contact_id" type="hidden" value="<%= contactId %>" />
				<input name="reload_path" type="hidden" value="<%= request.getServletPath() %>?<%= request.getQueryString() %>" />
				<br/>First name: <input name="first_name" type="text" id="first_name_input" value="<%= firstName %>" />
				<br/>Last name: <input name="last_name" type="text" id="last_name_input" value="<%= lastName %>" />
				<br/>E-mail: <input name="email" type="text" id="email_input" value="<%= email %>" />
    			<br/><input type="submit" name="submit" value="submit" />
			</form>
			<% if (contactId > -1) { %>
				<form action="${pageContext.request.contextPath}/DeleteContact" method="post">
					<input type="hidden" name="contact_id" value="<%= contactId %>"/>
					<input type="submit" name="submit" value="delete" />
				</form>
			<% }
		} catch (Exception ex) {
			%><%= "Failed to load contact details: " + ex %><%
		}
	%>

</body>
</html>