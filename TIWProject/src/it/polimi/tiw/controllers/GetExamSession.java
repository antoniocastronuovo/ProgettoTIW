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
import it.polimi.tiw.beans.ExamReport;
import it.polimi.tiw.beans.ExamResult;
import it.polimi.tiw.beans.ExamSession;
import it.polimi.tiw.beans.Teacher;
import it.polimi.tiw.dao.CourseDAO;
import it.polimi.tiw.dao.ExamReportDAO;
import it.polimi.tiw.dao.ExamSessionDAO;
import it.polimi.tiw.handlers.ConnectionHandler;
import it.polimi.tiw.handlers.SharedPropertyMessageResolver;

/**
 * Servlet implementation class GetExamSession
 */
@WebServlet("/GetExamSession")
public class GetExamSession extends HttpServlet {
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
		this.templateEngine.setMessageResolver(new SharedPropertyMessageResolver(context, "i18n", "examsessiondetails"));
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
		Integer orderCol = null;
		Integer last = null;
		Boolean asc = null;
		Timestamp datetime = null;
		Boolean pub = null;
		String message = null;
		
		try {
			courseId = Integer.parseInt(request.getParameter("courseId"));
			datetime = Timestamp.valueOf(request.getParameter("date"));
			orderCol = Integer.parseInt(request.getParameter("ord"));
			last = Integer.parseInt(request.getParameter("last"));
			asc = Boolean.parseBoolean(request.getParameter("asc"));
			pub = Boolean.parseBoolean(request.getParameter("pub"));
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
		List<ExamReport> examReports = null;
		boolean canPublish = false;
		boolean canReport = false;
		
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
			
			exam = examSessionDAO.getExamSessionByCourseIdDateTime(courseId, datetime);
			grades = examSessionDAO.getRegisteredStudentsResultsOrderedBy(courseId, datetime, orderCol, last, asc);
			examReports = examReportDAO.getExamReports(courseId, datetime);
			canPublish = examSessionDAO.canPublish(courseId, datetime);
			canReport = !grades.stream().filter(r -> r.getGradeStatus().equals("PUBBLICATO")).collect(Collectors.toList()).isEmpty();
			
			asc = ((last == orderCol && asc) ? false : true);
			
			message = ((pub) ? "Voti pubblicati correttamente" : null);
			
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access failed");
		}
		
		//Redirect to the correct view with the grades as parameter
		String path = "/WEB-INF/templates/ExamSessionDetails.html";
		ServletContext context = getServletContext();
		final WebContext ctx = new WebContext(request, response, context, request.getLocale());
		ctx.setVariable("exam", exam);
		ctx.setVariable("grades", grades);
		ctx.setVariable("reports", examReports);
		ctx.setVariable("canPublish", canPublish);
		ctx.setVariable("canReport", canReport);
		ctx.setVariable("last", orderCol);
		ctx.setVariable("asc", asc);
		ctx.setVariable("pubOk", message);
		response.setCharacterEncoding("UTF-8");
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
