package it.polimi.tiw.tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import it.polimi.tiw.beans.Course;
import it.polimi.tiw.beans.Student;
import it.polimi.tiw.dao.StudentDAO;

public class StudentTest {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
			testGetFollowedCourses();
	}
	
	public static void testLogin() {
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/polionline";
			String user = "poliadmin";
			String dbpassword = "Gruppo27";
			
			Class.forName(driver);
			Connection connection = DriverManager.getConnection(url, user, dbpassword);
			
			String email = "simon.kjaer@polionline.com";
			String password = "Milan";
			
			StudentDAO students = new StudentDAO(connection);
			Student student = students.checkCredentials(email, password);
			
			if(student == null) {
				System.out.println("No user found.");
			}else {
				System.out.println("First name: "+student.getFirstName());
				System.out.println("Last name: "+student.getLastName());
			}
			
			if (connection != null) {
				connection.close();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void testGetFollowedCourses() throws ClassNotFoundException, SQLException {
		String driver = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/polionline";
		String user = "poliadmin";
		String dbpassword = "Gruppo27";
		
		Class.forName(driver);
		Connection connection = DriverManager.getConnection(url, user, dbpassword);
		StudentDAO studentDAO = new StudentDAO(connection);
		
		List<Course> courses =studentDAO.getFollowedCourses(1);
		
		for(Course course:courses)
			System.out.println(course);
		
	}

}
