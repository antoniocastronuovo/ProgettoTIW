package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.Teacher;

public class TeacherDAO {
	private Connection connection;

	public TeacherDAO(Connection connection) {
		super();
		this.connection = connection;
	}
	
	public Teacher checkCredentials(String personCode, String pwd) throws SQLException{
		String query = "SELECT P.PersonCode, P.Email, P.FirstName, P.LastName, T.Department\r\n"
				+ "FROM teacher AS T JOIN person AS P ON T.PersonCode = P.PersonCode"
				+ "WHERE P.PersonCode = ? AND Password = ?;";
		try(PreparedStatement pStatement = connection.prepareStatement(query);) {
			pStatement.setString(1, personCode);
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
}
