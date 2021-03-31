package it.polimi.tiw.tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;

import it.polimi.tiw.beans.ExamReport;
import it.polimi.tiw.dao.ExamReportDAO;

public class ExamReportTest {

	public static void main(String[] args) {
		//testGetExamReport();
		testPublishExamReport();
	}

	public static void testGetExamReport() {
		System.out.println("Test for ExamReportDAO getExamReport()");
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/polionline";
			String user = "poliadmin";
			String dbpassword = "Gruppo27";
			
			
			Class.forName(driver);
			Connection connection = DriverManager.getConnection(url, user, dbpassword);
			
			Timestamp timestamp = Timestamp.valueOf("2020-01-14 12:00:00");
			
			ExamReportDAO examReportDAO = new ExamReportDAO(connection);
			ExamReport examReport = examReportDAO.getExamReport(1, timestamp);
			
			System.out.println(examReport.getExamReportId() + " - " + examReport.getDateTime() + " " + examReport.getExamSession().getCourse().getName());
			
			if (connection != null) {
				connection.close();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void testPublishExamReport() {
		System.out.println("Test for ExamReportDAO getExamReport()");
		try {
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/polionline";
			String user = "poliadmin";
			String dbpassword = "Gruppo27";
			
			
			Class.forName(driver);
			Connection connection = DriverManager.getConnection(url, user, dbpassword);
			
			Timestamp timestamp = Timestamp.valueOf("2020-01-14 12:00:00");
			
			ExamReportDAO examReportDAO = new ExamReportDAO(connection);
			ExamReport examReport = examReportDAO.publishExamReport(1, timestamp);
			
			System.out.println(examReport.getExamReportId() + " - " + examReport.getDateTime() + " " + examReport.getExamSession().getCourse().getName());
			
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
