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
 * Servlet implementation class CreateContact
 */
@WebServlet("/CreateContact")
public class CreateContact extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ContactModel create = new ContactModel();
		create.setAddress(request.getParameter(Constants.EMAIL_PARAMETER));
		create.setFirstName(request.getParameter(Constants.FIRST_NAME_PARAMETER));
		create.setLastName(request.getParameter(Constants.LAST_NAME_PARAMETER));

		ContactService service = new ContactService(getServletContext().getInitParameter(Constants.API_URL_PARAMETER));
		try {
			service.createContact(create);
			response.sendRedirect(request.getContextPath());
	    } catch (ApiException apiEx) {
	    	request.setAttribute(Constants.ERROR_MESSAGE_PARAMETER, apiEx.getMessage());
	    	request.setAttribute(Constants.EMAIL_PARAMETER, create.getAddress());
	    	request.setAttribute(Constants.FIRST_NAME_PARAMETER, create.getFirstName());
	    	request.setAttribute(Constants.LAST_NAME_PARAMETER, create.getLastName());
	    	request.getRequestDispatcher(request.getParameter(Constants.RELOAD_PATH_PARAMETER)).forward(request, response);
		} catch (Exception ex) {
			throw new ServletException(ex);
		}
	}

}
