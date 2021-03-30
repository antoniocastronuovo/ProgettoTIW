package it.polimi.tiw.beans;

import java.sql.Timestamp;

public class ExamSession {
	private Course course;
	private Timestamp dateTime;
	private String room;
	
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

	public Timestamp getDateTime() {
		return dateTime;
	}

	public void setDateTime(Timestamp dateTime) {
		this.dateTime = dateTime;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}
	
	
}
