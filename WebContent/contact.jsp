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
	<%
		try {
			ContactService service = new ContactService(getServletContext().getInitParameter("apiUrl"));
			int contactId = Integer.parseInt(request.getParameter("contactId"));
			Contact contact = service.loadContactById(contactId);
			%>
			<form action="${pageContext.request.contextPath}/UpdateContact" method="post">
				<input name="contact_id" type="hidden" value="<%= contact.getId() %>" />
				<br/>First name: <input name="first_name" type="text" id="first_name_input" value="<%= contact.getFirstName() %>" />
				<br/>Last name: <input name="last_name" type="text" id="last_name_input" value="<%= contact.getLastName() %>" />
				<br/>E-mail: <input name="email" type="text" id="email_input" value="<%= contact.getEmail() %>" />
				<p>Submit button.
    			<input type="submit" name="submit" value="submit" /></p>
			</form>
	<%	} catch (Exception ex) {
			%><%= "Failed to load contact details: " + ex %><%
		}
	%>

</body>
</html>