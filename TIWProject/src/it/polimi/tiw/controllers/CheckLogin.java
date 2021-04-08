package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
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

import it.polimi.tiw.beans.Student;
import it.polimi.tiw.beans.Teacher;
import it.polimi.tiw.beans.formbeans.LoginForm;
import it.polimi.tiw.dao.StudentDAO;
import it.polimi.tiw.dao.TeacherDAO;
import it.polimi.tiw.handlers.ErrorsHandler;

/**
 * Servlet implementation class CheckLogin
 */
@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Get and parse all parameters from request
		String personCodeString = request.getParameter("personCode");
    	String password = request.getParameter("password");
    	
    	//Create and initialize the form bean with user's input
    	LoginForm loginForm = new LoginForm();
    	loginForm.setPersonCode(personCodeString);
    	loginForm.setPassword(password);
    	
		String path = getServletContext().getContextPath();
		
		
		if(!loginForm.areCredetialsOk()) {
			path = path + "index.html";
			request.setAttribute("loginForm", loginForm);
			RequestDispatcher dispatcher = request.getRequestDispatcher(path);
			dispatcher.forward(request, response);
			return;
		}
		
		
		if(personCodeString == null || personCodeString.isEmpty() || password == null || password.isEmpty()) {			
			ErrorsHandler.displayErrorMessage("Error", "Missing parameters");
			path = path + "index.html";
			response.sendRedirect(path);
		}else {
			try {
				int personCode = loginForm.getPersonCode();
				StudentDAO students = new StudentDAO(connection);
				Student studentToLogIn = students.checkCredentials(personCode, password);
				if(studentToLogIn == null) {
					TeacherDAO teachers = new TeacherDAO(connection);
					Teacher teacherToLogIn = teachers.checkCredentials(personCode, password);
					if(teacherToLogIn == null) { //Wrong credentials
						loginForm.setLoginOk(false);
						path = "index.html";
						ServletContext context = getServletContext();
						final WebContext ctx = new WebContext(request, response, context, request.getLocale());
						ctx.setVariable("loginForm", loginForm);
						templateEngine.process(path, ctx, response.getWriter());
						return;
					}else { //Teacher is logged
						request.getSession().setAttribute("teacher", teacherToLogIn);
						path = path + "/GetTeacherCourses";
					}
				}else { //Ok
					//Associate the user to the session, if it already exists, it is replaced
					request.getSession().setAttribute("student", studentToLogIn);
					path = path + "/GetStudentCourses";
				}
				response.sendRedirect(path);
			}catch(SQLException e){
				e.printStackTrace();
				throw new UnavailableException("Couldn't get db connection");
			}
		}
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
