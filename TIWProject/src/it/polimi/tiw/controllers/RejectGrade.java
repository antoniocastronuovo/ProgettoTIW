package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.Course;
import it.polimi.tiw.beans.ExamResult;
import it.polimi.tiw.beans.Student;
import it.polimi.tiw.dao.CourseDAO;
import it.polimi.tiw.dao.ExamSessionDAO;
import it.polimi.tiw.dao.StudentDAO;
import it.polimi.tiw.handlers.ConnectionHandler;

/**
 * Servlet implementation class RejectGrade
 */
@WebServlet("/RejectGrade")
public class RejectGrade extends HttpServlet {
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
		Integer studentPersonCode = null;
		
		try {
			courseId = Integer.parseInt(request.getParameter("courseId"));
			datetime = Timestamp.valueOf(request.getParameter("date"));
			studentPersonCode = Integer.parseInt(request.getParameter("personCode"));
		}catch (NullPointerException | IllegalArgumentException e ) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}
		
		//Get the user from the session
		Student student = (Student) request.getSession(false).getAttribute("student");
		
		ExamSessionDAO examSessionDAO = new ExamSessionDAO(connection);
		CourseDAO courseDAO = new CourseDAO(connection);
		StudentDAO studentDAO = new StudentDAO(connection);
		
		ExamResult result = null;
		try {
			//Check if the course exists and it is followed by the student
			Course course = courseDAO.getCourseById(courseId);
			if(course == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				return;
			}

			//Get student courses
			List<Course> courses = studentDAO.getFollowedCoursesDesc(student.getPersonCode());
			//Check if the course belongs to the student's courses
			final int cId = courseId.intValue();
			if(courses.stream().filter(c -> cId == c.getCourseID()).collect(Collectors.toList()).size() != 1) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
				return;
			}
			//Check if the grade exists and it is a student's grade
			result = examSessionDAO.getStudentExamResult(studentPersonCode, courseId, datetime);
			if(result == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				return;
			}
			if(result.getStudent().getPersonCode() != student.getPersonCode()) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
				return;
			}
			if(!result.getGradeStatus().equals("PUBBLICATO") || result.getGrade() < 18) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User cannot reject the grade.");
				return;
			}
			
			//Checks passed, reject grade
			examSessionDAO.rejectExamResult(studentPersonCode, courseId, datetime);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
		}
		
		//Redirect to the grade page detail
		String path = String.format("%s/GetStudentGrade?courseId=%d&date=%s&personCode=%d&rej=true", getServletContext().getContextPath(), courseId, datetime.toString(), studentPersonCode);
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
