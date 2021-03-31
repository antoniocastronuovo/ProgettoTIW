package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Course;
import it.polimi.tiw.beans.ExamSession;

public class CourseDAO {
	
	private Connection connection;
	
	public CourseDAO(Connection connection) {
		super();
		this.connection = connection;
	}
	
	public Course getCourseByCourseId(int courseId) throws SQLException {
		String query = "SELECT * from course where CourseId =?;";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseId);
			Course course = new Course();
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return course;
				else {
					result.next();
					course.setCourseID(result.getInt("CourseId"));
					course.setName(result.getString("Name"));
					course.setDescription("Description");
					TeacherDAO teacher=new TeacherDAO(connection);
					course.setTeacher(teacher.getTeacherByPersonCode(result.getInt("TeacherPersonCode")));
					}
				}
			return course;	
		}	
	}
	



	/* Return a list of ExamSession but note that the course beans inside
	 * the exam session beans in the list is not fulfilled
	 */
	public List<ExamSession> getExamSessionByCourseId(int courseId) throws SQLException {
		String query = "SELECT S.DateTime AS SD, S.Room, C.CourseId, C.Name, C.Description FROM examsession AS S JOIN Course AS C ON C.CourseId = S.CourseId WHERE C.CourseId = ?;";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseId);
			List<ExamSession> examSessions = new ArrayList<>();
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					while(result.next()) {
						ExamSession examSession = new ExamSession();
						examSession.setDateTime(result.getTimestamp("SD"));
						examSession.setRoom(result.getString("Room"));
						Course course = new Course();
						course.setCourseID(result.getInt("CourseId"));
						course.setName(result.getString("Name"));
						course.setDescription("Description");
						examSession.setCourse(course);
						examSessions.add(examSession);
					}
				}
			}
			return examSessions;
		}
	}
}
