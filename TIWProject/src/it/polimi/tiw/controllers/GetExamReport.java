package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Course;
import it.polimi.tiw.beans.ExamReport;
import it.polimi.tiw.beans.ExamResult;
import it.polimi.tiw.beans.ExamSession;
import it.polimi.tiw.beans.Teacher;
import it.polimi.tiw.dao.CourseDAO;
import it.polimi.tiw.dao.ExamReportDAO;
import it.polimi.tiw.dao.ExamSessionDAO;

/**
 * Servlet implementation class GetExamReport
 */
@WebServlet("/GetExamReport")
public class GetExamReport extends HttpServlet {
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
		templateResolver.setSuffix(".html");
    	try {
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UnavailableException("Couldn't get db connection");
		}
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
		ExamReportDAO examReportDAO = new ExamReportDAO(connection);
		
		ExamSession exam = null;
		List<ExamResult> grades = null;
		ExamReport examReport = null;
		
		try {
			//Check if the course exists and it is taught by the user
			Course course = courseDAO.getCourseByCourseId(courseId);
			if(course == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				return;
			}
			if(course.getTeacher().getPersonCode() != teacher.getPersonCode()) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
				return;
			}
			//Check if the exam report exists
			examReport = examReportDAO.getExamReport(courseId, datetime);
			if(examReport == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Exam report not found");
				return;
			}
			
			exam = examSessionDAO.getExamSessionByCourseIdDateTime(courseId, datetime);
			grades = examSessionDAO.getReportedGrades(courseId, datetime);
			examReport = examReportDAO.getExamReport(courseId, datetime);	
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
		}
		
		String path = "examreport.html";
		ServletContext context = getServletContext();
		final WebContext ctx = new WebContext(request, response, context, request.getLocale());
		ctx.setVariable("exam", exam);
		ctx.setVariable("grades", grades);
		ctx.setVariable("report", examReport);
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
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

}
