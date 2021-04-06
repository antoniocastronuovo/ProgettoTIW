package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.ExamResult;
import it.polimi.tiw.beans.ExamSession;

public class ExamSessionDAO {
	private Connection connection;
	
	public ExamSessionDAO(Connection connection) {
		super();
		this.connection = connection;
	}
	
	public ExamSession getExamSessionByCourseIdDateTime(int courseId, Timestamp examSessionDateTime) throws SQLException {
		
			String query = "select * from examsession as E where E.CourseId=? and E.DateTime=?;";
			try (PreparedStatement pstatement = connection.prepareStatement(query);) {
				pstatement.setInt(1, courseId);
				pstatement.setTimestamp(2, examSessionDateTime);
				ExamSession examSession = new ExamSession();
				try (ResultSet result = pstatement.executeQuery();) {
					if (!result.isBeforeFirst()) // no results, credential check failed
						return null;
					else {
						result.next();
						examSession.setDateTime(result.getTimestamp("DateTime"));
						
						CourseDAO courseDAO = new CourseDAO(connection);
						examSession.setCourse(courseDAO.getCourseByCourseId(result.getInt("CourseId")));
						
						examSession.setRoom(result.getString("Room"));
		
					}
				}
				return examSession;
			}
	}
	
	public List<ExamResult> getRegisteredStudentsResults(int courseId, Timestamp datetime) throws SQLException {
		String query = "SELECT * "
				+ "FROM examresult AS E "
				+ "WHERE E.CourseId = ? AND E.ExamSessionDateTime=?;";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseId);
			pstatement.setTimestamp(2, datetime);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) 
					return new ArrayList<>();
				else {
					List<ExamResult> examResults = new ArrayList<>();
					while(result.next()) {				 
						ExamResult examResult= new ExamResult();
						
						StudentDAO student=new StudentDAO(connection);
						examResult.setStudent(student.getStudentByPersonCode(result.getInt("StudentPersonCode")));

						examResult.setGrade(result.getInt("Grade"));
						examResult.setLaude(result.getBoolean("Laude"));
						examResult.setGradeStatus(result.getString("GradeStatus"));
						
						ExamSessionDAO examSessionDAO=new ExamSessionDAO(connection);
						examResult.setExamSession(examSessionDAO.getExamSessionByCourseIdDateTime(courseId, datetime));
						
						//Add to the list
						examResults.add(examResult);
					}
					return examResults;
				}
			}
		}
	}
	
	public ExamResult getStudentExamResult(int personCode, int courseId, Timestamp datetime) throws SQLException {
		String query = "SELECT * "
				+ "FROM examresult AS E, Student AS S, Person AS P "
				+ "WHERE E.StudentPersonCode = S.PersonCode AND S.PersonCode = P.PersonCode "
				+ "AND E.CourseId = ? AND E.ExamSessionDateTime = ? AND P.PersonCode = ?;";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseId);
			pstatement.setTimestamp(2, datetime);
			pstatement.setInt(3, personCode);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();				 
					//Set exam result bean
					ExamResult examResult= new ExamResult();
					StudentDAO student=new StudentDAO(connection);
					examResult.setStudent(student.getStudentByPersonCode(result.getInt("StudentPersonCode")));
					
					examResult.setGrade(result.getInt("Grade"));
					examResult.setLaude(result.getBoolean("Laude"));
					examResult.setGradeStatus(result.getString("GradeStatus"));
					
					ExamSessionDAO examSessionDAO=new ExamSessionDAO(connection);
					examResult.setExamSession(examSessionDAO.getExamSessionByCourseIdDateTime(courseId, datetime));
					
					return examResult;
				}
			}
		}
	}
	
	public boolean rejectExamResult(int personCode, int courseId, Timestamp datetime) throws SQLException {
		//Get the student exam result
		ExamResult result = this.getStudentExamResult(personCode, courseId, datetime);
		//Check if it exists and it is published
		if(result == null || !result.getGradeStatus().equals("PUBBLICATO"))
			return false;
		else {
			String query = "UPDATE examresult "
					+ "SET GradeStatus = 'RIFIUTATO' "
					+ "WHERE StudentPersonCode = ? AND CourseId = ? AND ExamSessionDateTime = ? AND GradeStatus='PUBBLICATO';";
			try (PreparedStatement pstatement = connection.prepareStatement(query);) {
				pstatement.setInt(1, personCode);
				pstatement.setInt(2, courseId);
				pstatement.setTimestamp(3, datetime);
				int n = pstatement.executeUpdate();
				return (n == 1);
			}
		}
	}
	
	public boolean publishExamSessionGrades(int courseId, Timestamp datetime) throws SQLException {
		String query = "UPDATE examresult "
				+ "SET GradeStatus = 'PUBBLICATO' "
				+ "WHERE GradeStatus = 'INSERITO' AND CourseId = ? AND ExamSessionDateTime = ? ;";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseId);
			pstatement.setTimestamp(2, datetime);
			int n = pstatement.executeUpdate();
			return (n > 0);
		}
		
	}
	
	public boolean updateExamResult(int personCode, int courseId, Timestamp datetime, int grade, boolean laude) throws SQLException {
		//Get the student exam result
		ExamResult result = this.getStudentExamResult(personCode, courseId, datetime);
		//Check if it exists and it is not in state "VERBALIZZATO" and check that if laude is true than grade must be 30
		if(result == null || result.getGradeStatus().equals("VERBALIZZATO") || (laude && grade!=30))
			return false;
		else {
			String query = " ";
			if(result.getGradeStatus().equals("NON INSERITO")) {
				//Exam grade is not inserted yet
				query = "UPDATE examresult "
					+ "SET GradeStatus = 'INSERITO', Grade = ?, Laude = ? "
					+ "WHERE StudentPersonCode = ? AND CourseId = ? AND ExamSessionDateTime = ? ;";
			}else if(result.getGradeStatus().equals("INSERITO") || result.getGradeStatus().equals("PUBBLICATO")) { 
				//Exam report is already inserted but it is not "VERBALIZZATO" or "RIFIUTATO"
				query = "UPDATE examresult "
						+ "SET Grade = ?, Laude = ? "
						+ "WHERE StudentPersonCode = ? AND CourseId = ? AND ExamSessionDateTime = ? ;";
			}else { 
				//Error case, when the grade is "RIFIUTATO" or already "VERBALIZZATO"
				return false;
			}
			
			try (PreparedStatement pstatement = connection.prepareStatement(query);) {
				pstatement.setInt(1, grade);
				pstatement.setBoolean(2, laude);
				pstatement.setInt(3, personCode);
				pstatement.setInt(4, courseId);
				pstatement.setTimestamp(5, datetime);
				int n = pstatement.executeUpdate();
				return (n == 1);
			}
		}
	}
	
	public List<ExamResult> getReportedGrades(int courseId, Timestamp datetime) throws SQLException {
		String query = "SELECT * "
				+ "FROM examresult "
				+ "WHERE E.GradeStatus = 'VERBALIZZATO' "
				+ "AND E.CourseId = ? AND E.ExamSessionDateTime=?;";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseId);
			pstatement.setTimestamp(2, datetime);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return new ArrayList<>();
				else {
					List<ExamResult> examResults = new ArrayList<>();
					while(result.next()) {				 
						ExamResult examResult= new ExamResult();
						StudentDAO student=new StudentDAO(connection);
						examResult.setStudent(student.getStudentByPersonCode(result.getInt("StudentPersonCode")));
	
						examResult.setGrade(result.getInt("Grade"));
						examResult.setLaude(result.getBoolean("Laude"));
						examResult.setGradeStatus(result.getString("GradeStatus"));						

						ExamSessionDAO examSessionDAO=new ExamSessionDAO(connection);
						examResult.setExamSession(examSessionDAO.getExamSessionByCourseIdDateTime(courseId, datetime));
				
						//Add to the list
						examResults.add(examResult);
					}
					return examResults;
				}
			}
		}
	}
		
}
