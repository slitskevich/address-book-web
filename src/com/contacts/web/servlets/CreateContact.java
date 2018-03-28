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
 * Servlet implementation class CreateContact
 */
@WebServlet("/CreateContact")
public class CreateContact extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateContact() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Contact create = new Contact();
		create.setAddress(request.getParameter("email"));
		create.setFirstName(request.getParameter("first_name"));
		create.setLastName(request.getParameter("last_name"));

		ContactService service = new ContactService(getServletContext().getInitParameter("apiUrl"));
		try {
			service.createContact(create);
		} catch (Exception ex) {
			throw new ServletException(ex);
		}
		response.sendRedirect(request.getContextPath());
	}

}
