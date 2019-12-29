package com.bmc.configuration;

// This password validator has variable length and number of special characters,
// and checks that passwords contain at least one number, one uppercase, and one lowercase letter

public class PasswordValidator {
	
	private int validLength;
	private boolean checkSpecialCharacters;
	
	public PasswordValidator() {}
	
	public PasswordValidator(int validLength, boolean checkSpecialCharacters){
		this.validLength = validLength;
		this.checkSpecialCharacters = checkSpecialCharacters;
	}
	
	public boolean checkPassword(String password) {
		
		// if the password doesn't equal itself in full lowercase, it must have an uppercase char
		boolean hasUppercase = !password.equals(password.toLowerCase());
		// and vice versa
		boolean hasLowercase = !password.equals(password.toUpperCase());
		
		// check for at least one number
		boolean hasNumber = password.matches(".*\\d.*");
		
		boolean isLongEnough = password.length() >= validLength;
		
		// default this to true to pass the check if no special characters are required
		boolean hasSpecialCharacters = true;
	
		if (checkSpecialCharacters) {
			//Checks at least one char is not alphanumeric
			hasSpecialCharacters = !password.matches("[A-Za-z0-9 ]*"); 
		}
		
		return hasUppercase && hasLowercase && isLongEnough && hasSpecialCharacters && hasNumber;
	}
}
