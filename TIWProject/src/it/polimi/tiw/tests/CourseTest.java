package it.polimi.tiw.tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.List;

import it.polimi.tiw.beans.ExamSession;
import it.polimi.tiw.beans.Student;
import it.polimi.tiw.dao.CourseDAO;
import it.polimi.tiw.dao.StudentDAO;

public class CourseTest {

	public static void main(String[] args) {
		testGetExamSession();

	}
	
	public static void testGetExamSession() {
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/polionline";
			String user = "poliadmin";
			String dbpassword = "Gruppo27";
			
			Class.forName(driver);
			Connection connection = DriverManager.getConnection(url, user, dbpassword);
			
			CourseDAO courseDAO = new CourseDAO(connection);
			List<ExamSession> exams = courseDAO.getExamSessionByCourseId(1);
			
			for(ExamSession exam: exams) {
				System.out.println(exam.getCourse().getCourseID() + " - " + exam.getCourse().getName() + " - " + exam.getDateTime().toString());
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

}
