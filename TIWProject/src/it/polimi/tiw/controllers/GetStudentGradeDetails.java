package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Course;
import it.polimi.tiw.beans.ExamResult;
import it.polimi.tiw.beans.Student;
import it.polimi.tiw.dao.CourseDAO;
import it.polimi.tiw.dao.ExamSessionDAO;
import it.polimi.tiw.dao.StudentDAO;
import it.polimi.tiw.handlers.ConnectionHandler;
import it.polimi.tiw.handlers.SharedPropertyMessageResolver;

/**
 * Servlet implementation class GetStudentGradeDetails
 */
@WebServlet("/GetStudentGradeDetails")
public class GetStudentGradeDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;
    
    @Override
    public void init() throws ServletException {
    	ServletContext context = getServletContext();
    	ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		this.templateEngine.setMessageResolver(new SharedPropertyMessageResolver(context, "i18n", "studentresultdetails"));
		templateResolver.setSuffix(".html");
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
		Boolean hasJustRejected = false;
		
		try {
			courseId = Integer.parseInt(request.getParameter("courseId"));
			datetime = Timestamp.valueOf(request.getParameter("date"));
			hasJustRejected = Boolean.parseBoolean(request.getParameter("rej"));
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
		boolean buttonRejectVisible = true;
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
			
			result = examSessionDAO.getStudentExamResult(student.getPersonCode(), courseId, datetime);
			
			if(result==null) { //If the result does not exist then the student is not enrolled
				//Redirect on the student home page and display an error message
				String path = String.format("%s/GetExamSessionsStudent?courseId=%d&ne=true", getServletContext().getContextPath(), courseId);
				response.sendRedirect(path);
			}else{
				String path = "/WEB-INF/templates/StudentResultDetails.html";
				ServletContext context = getServletContext();
				buttonRejectVisible= (result.getGradeStatus().equals("PUBBLICATO") && result.getGrade() >= 18);
				boolean visibleGrade = (result.getGradeStatus().equals("PUBBLICATO") || result.getGradeStatus().equals("VERBALIZZATO") || result.getGradeStatus().equals("RIFIUTATO"));
				final WebContext ctx = new WebContext(request, response, context, request.getLocale());
				ctx.setVariable("result", result);
				ctx.setVariable("rejectable",buttonRejectVisible);
				ctx.setVariable("visibleGrade", visibleGrade);
				ctx.setVariable("rej", hasJustRejected);
				response.setCharacterEncoding("UTF-8");
				templateEngine.process(path, ctx, response.getWriter());
			}
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
			e.printStackTrace();
		}
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
