package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.DegreeCourse;
import it.polimi.tiw.beans.Student;

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
}
