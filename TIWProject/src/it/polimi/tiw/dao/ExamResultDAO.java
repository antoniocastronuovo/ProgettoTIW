package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Course;
import it.polimi.tiw.beans.ExamResult;
import it.polimi.tiw.beans.ExamSession;
import it.polimi.tiw.beans.Student;
import it.polimi.tiw.beans.Teacher;

public class ExamResultDAO {
	private Connection connection;
	
	public ExamResultDAO (Connection connection) {
		super();
		this.connection = connection;	
	}
	
	public ExamResult getExamResultByStudent(int personCode, int courseId, Timestamp dateTime) throws SQLException {
		String query = "Select * from examresult as ER, person as P, course as C, examsession as ES, student as S"
				+ " where ES.CourseId=C.CourseId and (ER.CourseId,ER.ExamSessionDateTime)=(ES.CourseId,ES.DateTime) "
				+ "and ER.StudentPersonCode=P.PersonCode and P.PersonCode=S.PersonCode and S.PersonCode=? "
				+ "and (ER.CourseId,ER.ExamSessionDateTime)=(?,?) ;";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, personCode);
			pstatement.setInt(2, courseId);
			pstatement.setTimestamp(3, dateTime);
			ExamResult examResult = new ExamResult();
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return examResult;
				else {
					List<Course> courses = new ArrayList<>();
						result.next();
						
						
						examResult.setGrade(result.getInt("Grade"));
						examResult.setLaude(result.getBoolean("Laude"));
						examResult.setGradeStatus(result.getString("GradeStatus"));
						
						ExamSession examSession = new ExamSession();
						examSession.setDateTime(result.getTimestamp("DateTime"));
						
						Course course = new Course();
						course.setCourseID(result.getInt("CourseId"));
						course.setDescription(result.getString("Description"));
						course.setName(result.getString("Name"));
						
						examSession.setCourse(course);
						examSession.setRoom(result.getString("Room"));
						examResult.setExamSession(examSession);
						
						StudentDAO student=new StudentDAO(connection);
						examResult.setStudent(student.getStudentByPersonCode(result.getInt("PersonCode")));
						
				
					}
					return examResult;
				}
			}
		}
	}