package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.Student;

public class StudentDAO {
	private Connection connection;

	public StudentDAO(Connection connection) {
		super();
		this.connection = connection;
	}
	
	public Student checkCredentials(String email, String pwd) throws SQLException {
		String query = "SELECT PersonCode, Email, Matricola, FirstName, LastName, DegreeCourseId FROM student WHERE Email = ? AND Password =?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, email);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					Student student = new Student();
					student.setPersonCode(result.getString("PersonCode"));
					student.setEmail(result.getString("Email"));
					student.setMatricola(result.getInt("Matricola"));
					student.setFirstName(result.getString("FirstName"));
					student.setLastName(result.getString("LastName"));
					student.setCourseId(result.getInt("DegreeCourseId"));
					return student;
				}
			}
		}
	}
}
