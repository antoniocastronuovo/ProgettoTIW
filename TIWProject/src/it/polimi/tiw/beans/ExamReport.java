package it.polimi.tiw.beans;

import java.util.Date;

public class ExamReport {
	private int examReportId;
	private Date dateTime;
	private ExamSession examSession;
	
	public ExamReport() {
		super();
	}

	public int getExamReportId() {
		return examReportId;
	}

	public void setExamReportId(int examReportId) {
		this.examReportId = examReportId;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public ExamSession getExamSession() {
		return examSession;
	}

	public void setExamSession(ExamSession examSession) {
		this.examSession = examSession;
	}
	
	
}
