package it.polimi.tiw.beans;

import java.util.Date;

public class ExamSession {
	private Course course;
	private Date dateTime;
	private String room;
	private ExamReport report;
	
	public ExamSession() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public ExamReport getReport() {
		return report;
	}

	public void setReport(ExamReport report) {
		this.report = report;
	}
	
	
}
