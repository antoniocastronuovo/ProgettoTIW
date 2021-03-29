package it.polimi.tiw.beans;

import java.util.Date;

public class ExamReport {
	private int examReportId;
	private Date dateTime;
	
	public ExamReport(int examReportId, Date dateTime) {
		super();
		this.examReportId = examReportId;
		this.dateTime=dateTime;
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
	
}
