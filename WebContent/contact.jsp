<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.contacts.web.model.ContactModel"  %>
<%@page import="com.contacts.web.service.*"  %>
<%@page import="com.contacts.web.Constants"  %>
<%@ page errorPage="error.jsp" %> 
<%!private String attributeString(HttpServletRequest request, String name, String defaultValue) {
	String requestValue = (String)request.getAttribute(name);
	return requestValue != null ? requestValue : defaultValue;
}%>
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
		String query = request.getQueryString();
			String reloadPath = request.getServletPath() + (query != null ? "?" + query : "");
			String errorMessage = (String)request.getAttribute(Constants.ERROR_MESSAGE_PARAMETER);
			String firstName = attributeString(request, Constants.FIRST_NAME_PARAMETER, "");
			String lastName = attributeString(request, Constants.LAST_NAME_PARAMETER, "");
			String email = attributeString(request, Constants.EMAIL_PARAMETER, "");
			if (errorMessage != null) {
	%>
		<p style="color:red;">Error: <%=errorMessage%></p><%
			}
				try {
			ContactService service = new ContactService(getServletContext().getInitParameter(Constants.API_URL_PARAMETER));
			String idParameter = request.getParameter("contactId");
			String action = "/CreateContact";
			int contactId = -1;
			if (idParameter != null) {
				contactId = Integer.parseInt(idParameter);
				ContactModel contact = service.loadContactById(contactId);
				firstName = contact.getFirstName();
				lastName = contact.getLastName();
				email = contact.getAddress();
				action = "/UpdateContact";
			}
		%>
			<form action="${pageContext.request.contextPath}/<%= action %>" method="post">
				<input name="<%= Constants.CONTACT_ID_PARAMETER %>" type="hidden" value="<%= contactId %>" />
				<input name="<%= Constants.RELOAD_PATH_PARAMETER %>" type="hidden" value="<%= reloadPath %>" />
				<br/>First name: <input name="<%= Constants.FIRST_NAME_PARAMETER %>" type="text" id="first_name_input" value="<%= firstName %>" />
				<br/>Last name: <input name="<%= Constants.LAST_NAME_PARAMETER %>" type="text" id="last_name_input" value="<%= lastName %>" />
				<br/>E-mail: <input name="<%= Constants.EMAIL_PARAMETER %>" type="text" id="email_input" value="<%= email %>" />
    			<br/><input type="submit" name="submit" value="submit" />
			</form>
			<% if (contactId > -1) { %>
				<form action="${pageContext.request.contextPath}/DeleteContact" method="post">
					<input name="<%= Constants.CONTACT_ID_PARAMETER %>" type="hidden" value="<%= contactId %>"/>
					<input name="<%= Constants.RELOAD_PATH_PARAMETER %>" type="hidden" value="<%= reloadPath %>" />
					<input type="submit" name="submit" value="delete" />
				</form>
			<% }
		} catch (ApiException apiEx) {
			%><p style="color:red;">Error: <%= apiEx.getMessage() %></p><%
		}

	%>

</body>
</html>