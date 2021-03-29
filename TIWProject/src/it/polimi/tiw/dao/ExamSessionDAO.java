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
	
	/* Return a list of ExamSession but note that the course beans inside
	 * the exam session beans in the list is not fulfilled
	 */
	public List<ExamSession> getExamSessionByCourseId(int courseId) throws SQLException {
		String query = "SELECT S.DateTime AS SD, S.Room, R.ExamReportId, R.DateTime AS RD FROM examsession AS S JOIN examreport AS R WHERE CourseId = ?;";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, courseId);
			List<ExamSession> examSessions = new ArrayList<>();
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					while(result.next()) {
						ExamSession examSession = new ExamSession();
						examSession.setDateTime(result.getDate("SD"));
						examSession.setRoom(result.getString("Room"));
						ExamReport examReport = new ExamReport();
						examReport.setExamReportId(result.getInt("ExamReportId"));
						examReport.setDateTime(result.getDate("RD"));
						examSession.setReport(examReport);
						examSessions.add(examSession);
					}
				}
			}
			return examSessions;
		}
	}
}
