package com.bmc.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.bmc.configuration.PasswordValidator;
import com.bmc.model.User;
import com.bmc.service.EmailService;
import com.bmc.service.UserService;

//Controller for new password.
//When /confirm is requested with the adequate token as a parameter, the request will accept a new password
// and activate the user. Or in the case of a forgotten password, an updated password.

@CrossOrigin(origins = "http://localhost:3000")
@Controller
public class NewPasswordController {
	
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	private PasswordValidator passwordValidator;
	private UserService userService;
	
	@Autowired
	public NewPasswordController(BCryptPasswordEncoder bCryptPasswordEncoder,
			UserService userService, EmailService emailService) {
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userService = userService;
		this.passwordValidator = new PasswordValidator(8, false);
	}

	@PostMapping("confirm")
	public ResponseEntity<?> confirmPostController(
			@RequestParam("token") String token,
			@RequestBody Map<String, String> requestParams) {
		
		// Find the user associated with the reset token
		User user = userService.findByConfirmationToken(token);
		
		// If the token isn't valid, return not acceptable
		if (user == null) { // No token found in DB
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("invalid token");
		} 
		
		// Password validation. length => 8, upper/lowercase, one numeral
		if (!passwordValidator.checkPassword(requestParams.get("p1"))) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("bad password. must contain at least one uppercase letter, one lowercase letter, one number, and be at least 8 characters");
		}
		
		// Passwords match validation
		if (!requestParams.get("p1").equals(requestParams.get("p2"))){
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("passwords do not match");
		}

		// Set new password
		user.setPassword(bCryptPasswordEncoder.encode(requestParams.get("p1")));

		// Set user to enabled (no change if already enabled)
		user.setEnabled(1);
		
		// Invalidate further use of token
		user.setConfirmationToken(null);
		
		// Save user
		userService.saveUser(user);
		
		//	RETURN OK
		return ResponseEntity.ok(HttpStatus.OK);
	}
}
