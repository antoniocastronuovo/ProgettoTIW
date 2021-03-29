package it.polimi.tiw.beans;

public abstract class Person {
	protected int personCode;
	protected String email;
	protected String password;
	protected String firstName;
	protected String lastName;
	

	public int getPersonCode() {
		return personCode;
	}
	public void setPersonCode(int personCode) {
		this.personCode = personCode;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
}
