package com.contacts.web.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.contacts.web.model.Contact;
import com.contacts.web.service.ApiException;
import com.contacts.web.service.ContactService;

/**
 * Servlet implementation class UpdateContact
 */
@WebServlet("/UpdateContact")
public class UpdateContact extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateContact() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String contactId = request.getParameter("contact_id");
		Contact update = new Contact(Integer.parseInt(contactId), 
									 request.getParameter("first_name"), 
									 request.getParameter("last_name"), 
									 request.getParameter("email"));
	    
	    ContactService service = new ContactService(getServletContext().getInitParameter("apiUrl"));
	    try {
		    service.updateContact(update);
		    response.sendRedirect(request.getContextPath());
	    } catch (ApiException apiEx) {
	    	request.setAttribute("errorMessage", apiEx.getMessage());
	    	request.getRequestDispatcher(request.getParameter("reload_path")).forward(request, response);
	    } catch (Exception ex) {
	    	ex.printStackTrace();
	    	throw new ServletException(ex);
	    }
	}

}