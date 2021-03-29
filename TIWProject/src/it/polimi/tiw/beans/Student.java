package it.polimi.tiw.beans;

public class Student extends Person{
	private int matricola;
	private int courseId;
	
	public Student() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public int getMatricola() {
		return matricola;
	}
	public void setMatricola(int matricola) {
		this.matricola = matricola;
	}
	public int getCourseId() {
		return courseId;
	}
	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}
	
	
}
