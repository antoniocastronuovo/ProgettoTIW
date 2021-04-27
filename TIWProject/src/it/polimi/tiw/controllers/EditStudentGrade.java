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
import it.polimi.tiw.beans.ExamResult;
import it.polimi.tiw.beans.Teacher;
import it.polimi.tiw.dao.CourseDAO;
import it.polimi.tiw.dao.ExamSessionDAO;
import it.polimi.tiw.handlers.ConnectionHandler;

/**
 * Servlet implementation class EditStudentGrade
 */
@WebServlet("/EditStudentGrade")
public class EditStudentGrade extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection = null;
    
    @Override
    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Get and parse all parameters from request
				boolean isBadRequest = false;
				Integer courseId = null;
				Timestamp datetime = null;
				Integer personCode = null;
				Integer grade = null;
				
				try {
					grade = Integer.parseInt(request.getParameter("grade"));
					personCode = Integer.parseInt(request.getParameter("personCode"));
					courseId = Integer.parseInt(request.getParameter("course"));
					datetime = Timestamp.valueOf(request.getParameter("date"));
				}catch (NullPointerException | IllegalArgumentException e ) {
					isBadRequest = true;
					e.printStackTrace();
				}
				if (isBadRequest) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
					return;
				}
				
				ExamSessionDAO examSessionDAO = new ExamSessionDAO(connection);
				CourseDAO courseDAO = new CourseDAO(connection);
				
				//Get the user from the session
				Teacher teacher = (Teacher) request.getSession(false).getAttribute("teacher");
				ExamResult result = null;
				
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
					
					//Check if the grade exists (and also if the exam session exists) 
					result = examSessionDAO.getStudentExamResult(personCode, courseId, datetime);
					if(result==null) {
						response.sendError(HttpServletResponse.SC_NOT_FOUND, "The requested grade does not exist.");
						return;
					}
					if(!result.isEditable()) {
						response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Grade already reported or published.");
						return;
					}
					//Check if the grade is admissible
					if(grade <= -4 || grade > 31) {
						String path = String.format("%s/GetGradeDetail?courseId=%d&date=%s&personCode=%d&err=true", getServletContext().getContextPath(), result.getExamSession().getCourse().getCourseID(), result.getExamSession().getDateTime().toString(), result.getStudent().getPersonCode());
						response.sendRedirect(path);
						return;
					}
					
					//Update the grade
					boolean laude = false;
					if(grade == 31) {
						laude = true;
						grade = 30;
					}
					
					examSessionDAO.updateExamResult(personCode, courseId, datetime, grade, laude);
					result = examSessionDAO.getStudentExamResult(personCode, courseId, datetime);
				}catch (SQLException e) {
					e.printStackTrace();
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
					return;
				}
				
				//Redirect to the detail page
				String path = String.format("%s/GetGradeDetail?courseId=%d&date=%s&personCode=%d&mod=true", getServletContext().getContextPath(), result.getExamSession().getCourse().getCourseID(), result.getExamSession().getDateTime().toString(), result.getStudent().getPersonCode());
				response.sendRedirect(path);
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
