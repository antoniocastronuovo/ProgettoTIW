package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
import it.polimi.tiw.beans.ExamSession;
import it.polimi.tiw.beans.Student;
import it.polimi.tiw.dao.CourseDAO;
import it.polimi.tiw.dao.StudentDAO;
import it.polimi.tiw.handlers.ConnectionHandler;
import it.polimi.tiw.handlers.SharedPropertyMessageResolver;

/**
 * Servlet implementation class GetCourseExamSessionStudent
 */
@WebServlet("/GetExamSessionsStudent")
public class GetExamSessionsStudent extends HttpServlet {
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
		this.templateEngine.setMessageResolver(new SharedPropertyMessageResolver(context, "i18n", "studenthome"));
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
		Course course = null;
		Boolean isNotEnrolled = false; //Error case in which student selects an exam in which he's not enrolled
		
		try {
			courseId = Integer.parseInt(request.getParameter("courseId"));
			isNotEnrolled = Boolean.parseBoolean(request.getParameter("ne"));
		}catch (NumberFormatException | NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}
		
		//Get the user from the session
		Student student = (Student) request.getSession(false).getAttribute("student");
		
		StudentDAO studentDAO = new StudentDAO(connection);
		CourseDAO courseDAO = new CourseDAO(connection);
		
		List<Course> courses = null;
		List<ExamSession> exams = null;
		
		try {
			//Check if the course exists and it is followed by the student
			course = courseDAO.getCourseById(courseId);
			if(course == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				return;
			}

			//Get student courses
			courses = studentDAO.getFollowedCoursesDesc(student.getPersonCode());
			//Check if the course belongs to the student's courses
			final int cId = courseId.intValue();
			if(courses.stream().filter(c -> cId == c.getCourseID()).collect(Collectors.toList()).size() != 1) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
				return;
			}
			
			exams = courseDAO.getExamSessionsByCourseId(courseId);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
			return;
		}
		
		String path = "/WEB-INF/templates/StudentHome.html";
		ServletContext context = getServletContext();
		final WebContext ctx = new WebContext(request, response, context, request.getLocale());
		ctx.setVariable("courses", courses);
		ctx.setVariable("courseId", courseId);
		ctx.setVariable("name", course.getName());
		ctx.setVariable("exams", exams);
		ctx.setVariable("isNotEnrolled", isNotEnrolled);
		templateEngine.process(path, ctx, response.getWriter());
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
