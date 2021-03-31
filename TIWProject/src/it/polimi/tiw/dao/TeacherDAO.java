package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Course;
import it.polimi.tiw.beans.Student;
import it.polimi.tiw.beans.Teacher;

public class TeacherDAO {
	private Connection connection;

	public TeacherDAO(Connection connection) {
		super();
		this.connection = connection;
	}
	
	public Teacher getTeacherByPersonCode(int personCode) throws SQLException {
		String query = "select * from person as P, teacher as T where P.PersonCode=T.PersonCode and T.PersonCode=?;";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, personCode);
			Teacher teacher = new Teacher();
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return teacher;
				else {
					result.next();
					
					teacher.setPersonCode(result.getInt("PersonCode"));
					teacher.setEmail(result.getString("Email"));
					teacher.setFirstName(result.getString("FirstName"));
					teacher.setLastName(result.getString("LastName"));
					teacher.setDepartment(result.getString("Department"));
					return teacher;
				}
			}
		}
	}
	
	public Teacher checkCredentials(int personCode, String pwd) throws SQLException{
		String query = "SELECT P.PersonCode, P.Email, P.FirstName, P.LastName, T.Department "
				+ "FROM teacher AS T JOIN person AS P ON T.PersonCode = P.PersonCode "
				+ "WHERE P.PersonCode = ? AND P.Password = ?;";
		try(PreparedStatement pStatement = connection.prepareStatement(query);) {
			pStatement.setInt(1, personCode);
			pStatement.setString(2, pwd);
			try(ResultSet result = pStatement.executeQuery();) {
				if(!result.isBeforeFirst())
					return null;
				else {
					result.next();
					Teacher teacher = new Teacher();
					teacher.setPersonCode(result.getInt("PersonCode"));
					teacher.setEmail(result.getString("Email"));
					teacher.setFirstName(result.getString("FirstName"));
					teacher.setLastName(result.getString("LastName"));
					teacher.setDepartment(result.getString("Department"));
					return teacher;
				}
			}
		}
	}
	
	public List<Course> getTaughtCoursesDesc(int personCode) throws SQLException{
		String query = "SELECT C.CourseId, C.Name, C.Description, C.TeacherPersonCode, T.Department, P.Email, P.FirstName, P.Lastname "
				+ "FROM course AS C, teacher AS T, person AS P "
				+ "WHERE  C.TeacherPersonCode=T.PersonCode AND T.PersonCode = P.PersonCode AND T.PersonCode = ? "
				+ "ORDER BY C.Name DESC; ";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, personCode);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return new ArrayList<>();
				else {
					List<Course> courses = new ArrayList<>();
					while(result.next()) {				 
						Course tempCourse= new Course();
						tempCourse.setCourseID(result.getInt("CourseId"));;
						tempCourse.setTeacher(new Teacher());
						tempCourse.getTeacher().setDepartment(result.getString("Department"));
						tempCourse.getTeacher().setEmail(result.getString("Email"));
						tempCourse.getTeacher().setFirstName(result.getString("FirstName"));
						tempCourse.getTeacher().setLastName(result.getString("LastName"));
						tempCourse.getTeacher().setPersonCode(result.getInt("TeacherPersonCode"));
						tempCourse.setDescription(result.getString("Description"));
						tempCourse.setName(result.getString("Name"));
						courses.add(tempCourse);
					}
					return courses;
				}
			}
		}
	}
	
	public Teacher getTeacherByPersonCode(int teacherPersonCode) throws SQLException {
		String query = "SELECT T.PersonCode, Email, FirstName, LastName, Department "
				+ "FROM teacher AS T JOIN person AS P ON T.PersonCode = P.PersonCode "
				+ "WHERE T.PersonCode = ?;";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, teacherPersonCode);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					Teacher teacher = new Teacher();
					teacher.setPersonCode(result.getInt("PersonCode"));
					teacher.setEmail(result.getString("Email"));
					teacher.setFirstName(result.getString("FirstName"));
					teacher.setLastName(result.getString("LastName"));
					teacher.setDepartment(result.getString("Department"));
					return teacher;
				}
			}
		}
	}
}
