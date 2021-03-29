package it.polimi.tiw.controllers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.Student;

/**
 * Servlet implementation class GetStudentCourses
 */
@WebServlet("/GetStudentCourses")
public class GetStudentCourses extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetStudentCourses() {
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
		Student student = (Student) request.getSession(false).getAttribute("student");
		out.println("<h1>This is the student home page.</h1>");
		out.println("<p>Welcome " + student.getFirstName() + " " + student.getLastName() + "</p>");
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
