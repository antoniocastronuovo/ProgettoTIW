package it.polimi.tiw.controllers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.Teacher;

/**
 * Servlet implementation class GetTeacherCourses
 */
@WebServlet("/GetTeacherCourses")
public class GetTeacherCourses extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetTeacherCourses() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set response content type
	    response.setContentType("text/html");
	      
		PrintWriter out = response.getWriter();
		Teacher teacher = (Teacher) request.getSession(false).getAttribute("teacher");
		out.println("<h1>This is the teacher home page.</h1>");
		out.println("<p>Welcome " + teacher.getFirstName() + " " + teacher.getLastName() + "</p>");
		String logout = getServletContext().getContextPath() + "/Logout";
		out.print("<a href=\"" + logout + "\">Logout</a>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
