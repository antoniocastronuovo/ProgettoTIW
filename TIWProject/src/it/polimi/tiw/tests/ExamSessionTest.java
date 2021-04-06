package it.polimi.tiw.tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import it.polimi.tiw.beans.ExamResult;
import it.polimi.tiw.dao.ExamSessionDAO;

public class ExamSessionTest {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		//testGetRegisteredStudentsResults();
		//testGetStudentExamResult();
		//testRejectExamResult();
		//testUpdateExamResult();
		//testGetReportedGrades();
		//testPublishExamSessionGrades();
	}
	
	public static void testGetRegisteredStudentsResults() {
		System.out.println("Test for ExamSessionDAO getRegisteredStudentsResults()");
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/polionline";
			String user = "poliadmin";
			String dbpassword = "Gruppo27";
			
			Class.forName(driver);
			Connection connection = DriverManager.getConnection(url, user, dbpassword);

			ExamSessionDAO examSessionDAO = new ExamSessionDAO(connection);
			Timestamp timestamp = Timestamp.valueOf("2020-01-14 12:00:00");
			List<ExamResult> results = examSessionDAO.getRegisteredStudentsResults(1, timestamp);
			
			System.out.println("Size: " + results.size());
			for(ExamResult result: results) {
				System.out.println(result.getExamSession().getCourse().getName() + " - " + result.getExamSession().getDateTime() + " - " + result.getStudent().getLastName() + " " + result.getStudent().getFirstName() + " - " + result.getGrade() + " - " + result.getGradeStatus());
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
	
	public static void testGetStudentExamResult() {
		System.out.println("Test for ExamSessionDAO getStudentExamResult()");
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/polionline";
			String user = "poliadmin";
			String dbpassword = "Gruppo27";
			
			Class.forName(driver);
			Connection connection = DriverManager.getConnection(url, user, dbpassword);
			
			ExamSessionDAO examSessionDAO = new ExamSessionDAO(connection);
			Timestamp timestamp = Timestamp.valueOf("2020-01-14 12:00:00");
			
			ExamResult result = examSessionDAO.getStudentExamResult(11, 1, timestamp);
			
			if(result == null)
				System.out.println("Result not found.");
			else
				System.out.println(result.getExamSession().getCourse().getName() + " - " + result.getExamSession().getDateTime() + " - " + result.getStudent().getLastName() + " " + result.getStudent().getFirstName() + " - " + result.getGrade() + " - " + result.getGradeStatus());
			
			
			if (connection != null) {
				connection.close();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void testRejectExamResult() {
		//Supposing the exam grade is in state "PUBBLICATO"
		//Otherwise, put it in the state "PUBBLICATO" in the DB
		System.out.println("Test for ExamSessionDAO getStudentExamResult()");
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/polionline";
			String user = "poliadmin";
			String dbpassword = "Gruppo27";
			
			Class.forName(driver);
			Connection connection = DriverManager.getConnection(url, user, dbpassword);
			
			ExamSessionDAO examSessionDAO = new ExamSessionDAO(connection);
			Timestamp timestamp = Timestamp.valueOf("2020-01-14 12:00:00");
			
			examSessionDAO.rejectExamResult(11, 1, timestamp);
			ExamResult result = examSessionDAO.getStudentExamResult(11, 1, timestamp);
			if(result == null || !result.getGradeStatus().equals("RIFIUTATO"))
				System.out.println("Something is wrong...");
			else
				System.out.println("Exam grade state is: " +result.getGrade() + " - "+result.getGradeStatus());
			
			
			if (connection != null) {
				connection.close();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void testUpdateExamResult() {
		//Supposing the exam grade is in state "PUBBLICATO"
		//Otherwise, put it in the state "PUBBLICATO" in the DB
		System.out.println("Test for ExamSessionDAO getStudentExamResult()");
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/polionline";
			String user = "poliadmin";
			String dbpassword = "Gruppo27";
			
			Class.forName(driver);
			Connection connection = DriverManager.getConnection(url, user, dbpassword);
			
			ExamSessionDAO examSessionDAO = new ExamSessionDAO(connection);
			Timestamp timestamp = Timestamp.valueOf("2020-01-14 12:00:00");
			
			examSessionDAO.updateExamResult(11, 1, timestamp, 30, true);
			ExamResult result = examSessionDAO.getStudentExamResult(11, 1, timestamp);
			String laude = result.isLaude() ? "L" : "";
			//GradeStatus should remain PUBBLICATO
			System.out.println("Exam grade state is: " +result.getGrade() + laude +" - "+result.getGradeStatus());
			
			
			if (connection != null) {
				connection.close();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void testGetReportedGrades() {
		System.out.println("Test for ExamSessionDAO getReportedGrades()");
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/polionline";
			String user = "poliadmin";
			String dbpassword = "Gruppo27";
			
			Class.forName(driver);
			Connection connection = DriverManager.getConnection(url, user, dbpassword);

			ExamSessionDAO examSessionDAO = new ExamSessionDAO(connection);
			Timestamp timestamp = Timestamp.valueOf("2020-01-14 12:00:00");
			List<ExamResult> results = examSessionDAO.getReportedGrades(1, timestamp);
			
			System.out.println("Size: " + results.size());
			for(ExamResult result: results) {
				System.out.println(result.getExamSession().getCourse().getName() + " - " + result.getExamSession().getDateTime() + " - " + result.getStudent().getLastName() + " " + result.getStudent().getFirstName() + " - " + result.getGrade() + " - " + result.getGradeStatus());
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
	
	public static void testPublishExamSessionGrades() {
		System.out.println("Test for ExamSessionDAO getReportedGrades()");
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/polionline";
			String user = "poliadmin";
			String dbpassword = "Gruppo27";
			
			Class.forName(driver);
			Connection connection = DriverManager.getConnection(url, user, dbpassword);

			ExamSessionDAO examSessionDAO = new ExamSessionDAO(connection);
			Timestamp timestamp = Timestamp.valueOf("2020-01-14 12:00:00");
			examSessionDAO.publishExamSessionGrades(1, timestamp);
			
			List<ExamResult> results = examSessionDAO.getRegisteredStudentsResults(1, timestamp);
			System.out.println("Size: " + results.size());
			for(ExamResult result: results) {
				System.out.println(result.getExamSession().getCourse().getName() + " - " + result.getExamSession().getDateTime() + " - " + result.getStudent().getLastName() + " " + result.getStudent().getFirstName() + " - " + result.getGrade() + " - " + result.getGradeStatus());
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
