package com.bmc.controller;

import java.util.Map;
import java.util.UUID;

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

// Controller for registration. Requesting /register with email, username
// will add a new user to the database and send a token email to the email address.
// When /confirm is requested with the adequate token as a parameter, the request will accept a new password and activate the user
// NOTE::::: copy the token directly from DB for testing. uncomment out the email section when Postman testing is completed

@CrossOrigin(origins = "http://localhost:3000")
@Controller
public class RegisterController {
	
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	private PasswordValidator passwordValidator;
	private UserService userService;
	private EmailService emailService;
	
	@Autowired
	public RegisterController(BCryptPasswordEncoder bCryptPasswordEncoder,
			UserService userService, EmailService emailService) {
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userService = userService;
		this.emailService = emailService;
		this.passwordValidator = new PasswordValidator(8, false);
	}
	
	@PostMapping("register")
	public ResponseEntity<?> registerPostController(
			@RequestBody User newUserRequest) {
		
		// We pass valid parameters to a new User to filter any unauthorized data in the request body
		// We'll pass the name's after validating the email
		User newUser = new User();
		newUser.setEmail(newUserRequest.getEmail());
		newUser.setUserName(newUserRequest.getUserName());

		if (newUser.getEmail() == null || newUser.getUserName() == null) {
			// return user needs a name and an email ya dingus
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email and username required");
		}
		
		newUser.setFirstName(newUserRequest.getFirstName());
		newUser.setLastName(newUserRequest.getLastName());
		
		// check to see that the email address isn't being used
		if (userService.findByEmail(newUser.getEmail()) != null) {
			//	return user already exists with that email
			return ResponseEntity.status(HttpStatus.CONFLICT).body("email already exists");
		}

		// Disable user until they click on confirmation link in email
	    newUser.setEnabled(0);
	      
	    // Generate random 36-character string token for confirmation link
	    newUser.setConfirmationToken(UUID.randomUUID().toString());
	    
	    // appUrl for email
	    String appUrl = "http://localhost:8080";
	    
	    // SIMPLE MAIL TOKEN
//		SimpleMailMessage registrationEmail = new SimpleMailMessage();
//		registrationEmail.setTo(newUser.getEmail());
//		registrationEmail.setSubject("Registration Confirmation");
//		registrationEmail.setText("To confirm your e-mail address, please click the link below:\n"
//				+ appUrl + "/confirm?token=" + newUser.getConfirmationToken());
//		
//		emailService.sendEmail(registrationEmail);
		
	    // save user to database
		userService.saveUser(newUser);
		
		// return OK
		return ResponseEntity.ok(HttpStatus.OK);
	}
	
	// Process confirmation link
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
		
		if (!requestParams.get("p1").equals(requestParams.get("p2"))){
			// password strength serverside validation
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("passwords do not match");
		}

		// Set new password BCRYPT THAT BITCH
		user.setPassword(bCryptPasswordEncoder.encode(requestParams.get("p1")));

		// Set user to enabled
		user.setEnabled(1);
		
		// Invalidate further use of token
		user.setConfirmationToken(null);
		
		// Save user
		userService.saveUser(user);
		
		//	RETURN OK
		return ResponseEntity.ok(HttpStatus.OK);
	}
}