package com.contacts.web.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.contacts.web.service.ContactService;

/**
 * Servlet implementation class DeleteContact
 */
@WebServlet("/DeleteContact")
public class DeleteContact extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteContact() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int contactId = Integer.parseInt(request.getParameter("contact_id"));
	    ContactService service = new ContactService(getServletContext().getInitParameter("apiUrl"));
	    try {
		    service.deleteContact(contactId);
	    } catch (Exception ex) {
	    	throw new ServletException(ex);
	    }
	    response.sendRedirect(request.getContextPath());		
	}

}
