package models;

import java.io.Serializable;

public class User implements Serializable {

	/**
	 * For code simplicity, DTO === model 
	 */
	public static final long serialVersionUID = 1L;
	public String username;
	public String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public User() {}

	@Override
	public String toString() {
		return username + "," + password;
	}

}
