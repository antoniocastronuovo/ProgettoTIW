package it.polimi.tiw.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GoToHome
 */
@WebServlet("/GoToHome")
public class GoToHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToHome() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = getServletContext().getContextPath();
		if(request.getSession(false) != null && request.getSession(false).getAttribute("teacher") != null) {
			path = path + "/GetTeacherCourses";
		}else if(request.getSession(false) != null && request.getSession(false).getAttribute("student") != null){
			path = path + "/GetStudentCourses";
		}else {
			path = path + "/Welcome";
		}
		response.sendRedirect(path);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
