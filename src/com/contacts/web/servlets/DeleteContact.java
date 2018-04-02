package com.contacts.web.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.contacts.web.Constants;
import com.contacts.web.service.ApiException;
import com.contacts.web.service.ContactService;

/**
 * Servlet to process delete contact requests
 */
@WebServlet("/DeleteContact")
public class DeleteContact extends HttpServlet {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
       
	/**
	 * Calls REST API to delete contact defined by ID parameters to the servlet. If delete fails due to recoverable error 
	 * reloads the request with added error message
	 *
	 * @param request the request
	 * @param response the response
	 * @throws Exception if API fails to delete the contact 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int contactId = Integer.parseInt(request.getParameter(Constants.CONTACT_ID_PARAMETER));
	    ContactService service = new ContactService(getServletContext().getInitParameter(Constants.API_URL_PARAMETER));
	    try {
		    service.deleteContact(contactId);
		    response.sendRedirect(request.getContextPath());		
	    } catch (ApiException apiEx) {
	    	request.setAttribute(Constants.ERROR_MESSAGE_PARAMETER, apiEx.getMessage());
	    	request.getRequestDispatcher(request.getParameter(Constants.RELOAD_PATH_PARAMETER)).forward(request, response);
	    } catch (Exception ex) {
	    	throw new ServletException(ex);
	    }
	}

}
