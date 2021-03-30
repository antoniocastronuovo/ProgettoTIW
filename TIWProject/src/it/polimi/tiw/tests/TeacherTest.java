package it.polimi.tiw.tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import it.polimi.tiw.beans.Course;
import it.polimi.tiw.dao.StudentDAO;
import it.polimi.tiw.dao.TeacherDAO;

public class TeacherTest {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		testGetTaughtCoursesDesc();
	}

	public static void testGetTaughtCoursesDesc() throws ClassNotFoundException, SQLException {
		System.out.println("Test for TeacherDAO getTaughtCoursesDesc()");
		
		String driver = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/polionline";
		String user = "poliadmin";
		String dbpassword = "Gruppo27";
		
		Class.forName(driver);
		Connection connection = DriverManager.getConnection(url, user, dbpassword);
		TeacherDAO teacherDAO = new TeacherDAO(connection);
		
		//Get the courses for teacher 1
		List<Course> courses = teacherDAO.getTaughtCoursesDesc(1);
		
		for(Course course:courses)
			System.out.println(course.getCourseID() +" - " + course.getName());
		
		if (connection != null) {
			connection.close();
		}
	}
	
}
