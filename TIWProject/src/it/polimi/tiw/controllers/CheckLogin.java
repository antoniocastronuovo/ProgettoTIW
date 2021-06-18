package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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
import it.polimi.tiw.handlers.ConnectionHandler;
import it.polimi.tiw.handlers.SharedPropertyMessageResolver;

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
		this.templateEngine.setMessageResolver(new SharedPropertyMessageResolver(context, "i18n", "index"));
		templateResolver.setSuffix(".html");
		
    	connection = ConnectionHandler.getConnection(getServletContext());
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
		
		//Credential are bad formatted
		if(!loginForm.areCredetialsOk()) {
			final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
			path = "/WEB-INF/templates/index.html";
			ctx.setVariable("loginForm", loginForm);
			templateEngine.process(path, ctx, response.getWriter());
			/* Redirect is done to avoid to submit again the login form, as in the
			 * case of forwarding.*/
		}else { //Credential are well formatted
			try {
				int personCode = loginForm.getPersonCode();
				StudentDAO students = new StudentDAO(connection);
				Student studentToLogIn = students.checkCredentials(personCode, password);
				if(studentToLogIn == null) {
					TeacherDAO teachers = new TeacherDAO(connection);
					Teacher teacherToLogIn = teachers.checkCredentials(personCode, password);
					if(teacherToLogIn == null) { //Wrong credentials
						loginForm.setLoginOk(false);
						final WebContext ctx = new WebContext(request, response, getServletContext(), request.getLocale());
						path = "/WEB-INF/templates/index.html";
						
						// Here we pass the login form bean to the context (for setting the alerts)
						ctx.setVariable("loginForm", loginForm);
						templateEngine.process(path, ctx, response.getWriter());
						return;
					}else { //Teacher is logged
						request.getSession().setAttribute("teacher", teacherToLogIn);
					}
				}else { //User is logged
					//Associate the user to the session, if it already exists, it is replaced
					request.getSession().setAttribute("student", studentToLogIn);
				}
				path = path + "/GoToHome";
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
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
