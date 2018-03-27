package com.contacts.web.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.contacts.web.model.Contact;
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
		Contact update = new Contact(Integer.parseInt(request.getParameter("contact_id")), 
									 request.getParameter("first_name"), 
									 request.getParameter("last_name"), 
									 request.getParameter("email"));
	    boolean submitButtonPressed = request.getParameter("submit") != null;
	    
	    if (submitButtonPressed) {
		    ContactService service = new ContactService(getServletContext().getInitParameter("apiUrl"));
		    try {
			    service.updateContact(update);
		    } catch (Exception ex) {
		    	throw new ServletException(ex);
		    }
	    }
	    response.sendRedirect(request.getContextPath());
	}

}
