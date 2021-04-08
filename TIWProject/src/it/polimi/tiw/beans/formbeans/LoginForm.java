package it.polimi.tiw.beans.formbeans;

public class LoginForm {
	private Integer personCode;
	private String password;
	private String personCodeError;
	private String passwordError;
	private String credentialError;
	private boolean loginOk;
	
	public LoginForm() {
		super();
		passwordError = null;
		personCodeError = null;
		credentialError = null;
	}

	public int getPersonCode() {
		return personCode;
	}

	public void setPersonCode(String personCode) {
		if(personCode == null || personCode.isEmpty()) {
			this.personCodeError = "Person code is required";
		}else {
			try {
				this.personCode = Integer.parseInt(personCode);
				this.personCodeError = null;
			} catch (NullPointerException | IllegalArgumentException e ) {
				this.personCodeError = "Person code must contain only numbers.";
			}
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
		if(password == null || password.isEmpty()) {
			this.passwordError = "Password field is required.";
		}else if(password.length() < 3 || password.length() > 12) {
			this.passwordError = "Password must be between 3 and 12 characters.";
		}else {
			this.passwordError = null;
		}
	}

	public String getPersonCodeError() {
		return personCodeError;
	}


	public String getPasswordError() {
		return passwordError;
	}

	public void setLoginOk(boolean valid) {
		if(valid) {
			this.loginOk = true;
			this.credentialError = null;
		}else {
			this.loginOk = false;
			this.credentialError = "Username and password are not correct.";
		}
	}
	
	public boolean areCredetialsOk() {
		return this.passwordError == null && this.personCodeError == null;
	}
	
	public boolean isLoginOk() {
		return loginOk;
	}

	public String getCredentialError() {
		return credentialError;
	}
	
	
}
