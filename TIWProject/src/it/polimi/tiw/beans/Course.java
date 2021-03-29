package it.polimi.tiw.beans;

public class Course {
	private int courseID;
	private String name;
	private String description;
	private Teacher teacher;
	
	public Course() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Course(int courseID, String name, String description, Teacher teacher) {
		super();
		this.courseID = courseID;
		this.name = name;
		this.description = description;
		this.teacher = teacher;
	}

	public int getCourseID() {
		return courseID;
	}

	public void setCourseID(int courseID) {
		this.courseID = courseID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}
	
	
}
