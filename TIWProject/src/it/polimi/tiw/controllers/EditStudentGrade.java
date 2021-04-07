package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;

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

import it.polimi.tiw.beans.ExamResult;
import it.polimi.tiw.beans.formbeans.GradeForm;
import it.polimi.tiw.dao.ExamSessionDAO;

/**
 * Servlet implementation class EditStudentGrade
 */
@WebServlet("/EditStudentGrade")
public class EditStudentGrade extends HttpServlet {
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
		int grade = Integer.parseInt(request.getParameter("grade"));
		int personCode = Integer.parseInt(request.getParameter("personCode"));
		int courseId = Integer.parseInt(request.getParameter("course"));
		Timestamp datetime = Timestamp.valueOf(request.getParameter("date"));
		
		GradeForm gradeForm = new GradeForm(request.getParameter("grade"), request.getParameter("personCode"), request.getParameter("course"), request.getParameter("date"));
		
		ExamSessionDAO examSessionDAO = new ExamSessionDAO(connection);
		try {
			boolean laude = false;
			if(grade == 31) {
				laude = true;
				grade = 30;
			}
			examSessionDAO.updateExamResult(personCode, courseId, datetime, grade, laude);
			ExamResult result = examSessionDAO.getStudentExamResult(personCode, courseId, datetime);
			
			String path = "studentgrade.html";
			ServletContext context = getServletContext();
			final WebContext ctx = new WebContext(request, response, context, request.getLocale());
			ctx.setVariable("result", result);
			ctx.setVariable("gradeForm", gradeForm);
			templateEngine.process(path, ctx, response.getWriter());
		}catch (SQLException e) {
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
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

}