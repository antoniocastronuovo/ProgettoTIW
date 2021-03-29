package it.polimi.tiw.beans;

public class Teacher extends Person {
	
	private String department;
	
	public Teacher(String department) {
		super();
		this.department=department;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}
	
}
