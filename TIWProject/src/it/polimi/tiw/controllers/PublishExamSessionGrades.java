package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.Course;
import it.polimi.tiw.beans.ExamSession;
import it.polimi.tiw.beans.Teacher;
import it.polimi.tiw.dao.CourseDAO;
import it.polimi.tiw.dao.ExamSessionDAO;
import it.polimi.tiw.handlers.ConnectionHandler;

/**
 * Servlet implementation class PublishExamSessionGrades
 */
@WebServlet("/PublishExamSessionGrades")
public class PublishExamSessionGrades extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection = null;
    
    @Override
    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Get and parse all parameters from request
				boolean isBadRequest = false;
				Integer courseId = null;
				Timestamp datetime = null;
				
				try {
					courseId = Integer.parseInt(request.getParameter("courseId"));
					datetime = Timestamp.valueOf(request.getParameter("date"));
				}catch (NullPointerException | IllegalArgumentException e ) {
					isBadRequest = true;
					e.printStackTrace();
				}
				if (isBadRequest) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
					return;
				}
				
				//Get the user from the session
				Teacher teacher = (Teacher) request.getSession(false).getAttribute("teacher");
				
				CourseDAO courseDAO = new CourseDAO(connection);
				ExamSessionDAO examSessionDAO = new ExamSessionDAO(connection);
				
				ExamSession exam = null;
				
				try {
					//Check if the course exists and it is taught by the user
					Course course = courseDAO.getCourseById(courseId);
					if(course == null) {
						response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
						return;
					}
					if(course.getTeacher().getPersonCode() != teacher.getPersonCode()) {
						response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
						return;
					}
					//Check if the exam session exists and the teacher can publish
					exam = examSessionDAO.getExamSessionByCourseIdDateTime(courseId, datetime);
					if(exam == null) {
						response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
						return;
					}
					if(!examSessionDAO.canPublish(courseId, datetime)) {
						response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "There are no grades to publish.");
						return;
					}
					
					//Publish the grades
					examSessionDAO.publishExamSessionGrades(courseId, datetime);
				}catch (SQLException e) {
					e.printStackTrace();
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
					return;
				}
				
				/* Since this controller performs an update, it does NOT forward a request but makes 
				 * a redirect to the GetExamSession controller that display the page */
				String path = String.format("%s/GetExamSession?courseId=%d&date=%s&ord=1&last=1&asc=false&pub=true", getServletContext().getContextPath(), exam.getCourse().getCourseID(), exam.getDateTime().toString());
				response.sendRedirect(path);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	@Override
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
