package it.polimi.tiw.beans.formbeans;

import java.sql.Timestamp;

public class GradeForm {
	private String grade;
	private String personCode;
	private String courseId;
	private String dateTime;
	private String errorString;
	private String successString;
	
	public GradeForm() {
		super();
		this.errorString = null;
		this.successString = null;
	}

	public GradeForm(String grade, String personCode, String courseId, String dateTime) {
		super();
		this.errorString = null;
		this.successString = null;
		
		this.setGrade(grade);
		this.setPersonCode(personCode);
		this.setCourseId(courseId);
		this.setDateTime(dateTime);
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
		if(grade == null || grade.isEmpty()) {
			this.errorString = "Parameters are wrong. Please correct them.";
			this.successString = null;
		}else {
			try {
				Integer.parseInt(grade);
			} catch (NumberFormatException e) {
				this.errorString = "Parameters are wrong. Please correct them.";
				this.successString = null;
			}
		}
	}

	public String getPersonCode() {
		return personCode;
	}

	public void setPersonCode(String personCode) {
		this.personCode = personCode;
		if(personCode == null || personCode.isEmpty()) {
			this.errorString = "Parameters are wrong. Please correct them.";
			this.successString = null;
		}else {
			try {
				Integer.parseInt(personCode);
			} catch (NumberFormatException e) {
				this.errorString = "Parameters are wrong. Please correct them.";
				this.successString = null;
			}
		}
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
		if(courseId == null || courseId.isEmpty()) {
			this.errorString = "Parameters are wrong. Please correct them.";
			this.successString = null;
		}else {
			try {
				Integer.parseInt(courseId);
			} catch (NumberFormatException e) {
				this.errorString = "Parameters are wrong. Please correct them.";
				this.successString = null;
			}
		}
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
		if(dateTime == null || dateTime.isEmpty()) {
			this.errorString = "Parameters are wrong. Please correct them.";
			this.successString = null;
		}else {
			try {
				Timestamp.valueOf(dateTime);
			} catch (Exception e) {
				this.errorString = "Parameters are wrong. Please correct them.";
				this.successString = null;
			}
		}
	}

	public String getErrorString() {
		return errorString;
	}
	
	public String getSuccessString() {
		return this.successString;
	}
	
	public boolean isValid() {
		if(this.errorString == null) {
			this.successString = "Voto aggiornato correttamente a " + grade;
			return true;
		}
		return false;
	}
	
}
