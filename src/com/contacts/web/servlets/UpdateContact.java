package com.contacts.web.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.contacts.web.Constants;
import com.contacts.web.model.ContactModel;
import com.contacts.web.service.ApiException;
import com.contacts.web.service.ContactService;

/**
 * Servlet to process update contact requests
 */
@WebServlet("/UpdateContact")
public class UpdateContact extends HttpServlet {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
       
	/**
	 * Calls REST API to update contact with values passed as parameters to the servlet. If update fails due to recoverable error 
	 * reloads the request with added error message
	 *
	 * @param request the request
	 * @param response the response
	 * @throws Exception if API fails to update the contact 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String contactId = request.getParameter(Constants.CONTACT_ID_PARAMETER);
		ContactModel update = new ContactModel(Integer.parseInt(contactId), 
									 request.getParameter(Constants.FIRST_NAME_PARAMETER), 
									 request.getParameter(Constants.LAST_NAME_PARAMETER), 
									 request.getParameter(Constants.EMAIL_PARAMETER));
	    
	    ContactService service = new ContactService(getServletContext().getInitParameter(Constants.API_URL_PARAMETER));
	    try {
		    service.updateContact(update);
		    response.sendRedirect(request.getContextPath());
	    } catch (ApiException apiEx) {
	    	request.setAttribute(Constants.ERROR_MESSAGE_PARAMETER, apiEx.getMessage());
	    	request.getRequestDispatcher(request.getParameter(Constants.RELOAD_PATH_PARAMETER)).forward(request, response);
	    } catch (Exception ex) {
	    	ex.printStackTrace();
	    	throw new ServletException(ex);
	    }
	}

}
