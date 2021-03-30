package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.DegreeCourse;
import it.polimi.tiw.beans.ExamReport;
import it.polimi.tiw.beans.ExamSession;
import it.polimi.tiw.beans.Student;

public class ExamSessionDAO {
	private Connection connection;
	
	public ExamSessionDAO(Connection connection) {
		super();
		this.connection = connection;
	}
	
	
}
