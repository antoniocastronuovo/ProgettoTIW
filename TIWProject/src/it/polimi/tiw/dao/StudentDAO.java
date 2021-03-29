package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Course;
import it.polimi.tiw.beans.DegreeCourse;
import it.polimi.tiw.beans.Student;
import it.polimi.tiw.beans.Teacher;

public class StudentDAO {
	private Connection connection;

	public StudentDAO(Connection connection) {
		super();
		this.connection = connection;
	}
	
	public Student checkCredentials(String personCode, String pwd) throws SQLException {
		String query = "SELECT P.PersonCode, P.Email, S.Matricola, P.FirstName, P.LastName, S.DegreeCourseId, D.DegreeCourseId, D.Name, D.Description\r\n"
				+ "FROM (student AS S JOIN person AS P ON S.PersonCode = P.PersonCode) JOIN degreecourse AS D "
				+ "WHERE P.PersonCode = ? AND P.Password =?;";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, personCode);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					Student student = new Student();
					student.setPersonCode(result.getInt("PersonCode"));
					student.setEmail(result.getString("Email"));
					student.setMatricola(result.getInt("Matricola"));
					student.setFirstName(result.getString("FirstName"));
					student.setLastName(result.getString("LastName"));
					DegreeCourse degreeCourse = new DegreeCourse();
					degreeCourse.setDegreeCourseId(result.getInt("DegreeCourseId"));
					degreeCourse.setName(result.getString("Name"));
					degreeCourse.setDescription(result.getString("Description"));
					student.setDegreeCourse(degreeCourse);
					return student;
				}
			}
		}
	}
	public List<Course> getFollowedCourses(String personCode) throws SQLException {
		String query = "Select * from course as C join courseenrollment as CE join teacher as T "
				+ "where C.CourseId=CE.CourseID and C.TeacherPersonCode=T.PersonCode and StudentPersonCode = ?; ";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, personCode);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					List<Course> courses = new ArrayList<>();
					while(result.next()) {				 
						Course tempCourse= new Course();
						
						tempCourse.setCourseID(result.getInt("CourseId"));;
						tempCourse.setTeacher(new Teacher());
						tempCourse.getTeacher().setDepartment(result.getString("Department"));
						tempCourse.getTeacher().setEmail(result.getString("Email"));
						tempCourse.getTeacher().setPassword(result.getString("Password"));
						tempCourse.getTeacher().setFirstName(result.getString("FirstName"));
						tempCourse.getTeacher().setLastName(result.getString("LastName"));
						tempCourse.getTeacher().setPersonCode(result.getInt("TeacherPersonCode"));
						
						tempCourse.setDescription(result.getString("Description"));
						tempCourse.setName(result.getString("Name"));
					}
					return courses;
				}
			}
		}
	}
}
