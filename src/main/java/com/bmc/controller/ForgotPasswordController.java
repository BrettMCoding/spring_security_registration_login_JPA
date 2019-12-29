package com.bmc.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
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

// Controller for forgotten password. Requesting /forgot will add a new confirm token to the database user who's email matches.
// When /forgotconfirm is requested with the adequate token as a parameter, the request will accept a password update
// NOTE::::: copy the token directly from DB for testing. uncomment out the email section when Postman testing is completed

@CrossOrigin(origins = "http://localhost:3000")
@Controller
public class ForgotPasswordController {
	
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	private PasswordValidator passwordValidator;
	private UserService userService;
	private EmailService emailService;
	
	@Autowired
	public ForgotPasswordController(BCryptPasswordEncoder bCryptPasswordEncoder,
			UserService userService, EmailService emailService) {
		
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userService = userService;
		this.emailService = emailService;
	}
	
	@PostMapping("forgot")
	public ResponseEntity<?> forgotPasswordPostController(
			@RequestBody User checkEmail) {

		User checkUser = userService.findByEmail(checkEmail.getEmail());
		
		// check to see that the email address is valid
		if (checkUser == null) {
			//	return user doesn't exist / not acceptable
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("email doesn't exist");
		}
		
		// check to see that user doesn't have a token already (should never happen with the correct front-end, but could be exploitable if unchecked?)
		if (checkUser.getConfirmationToken() != null) {
			// return user has a token already
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("user already has a confirmation/forgotpassword token");
		}
	      
	    // Generate random 36-character string token for confirmation link
	    checkUser.setConfirmationToken(UUID.randomUUID().toString());
	    
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
		
	    // save confirmation token to database
		userService.saveUser(checkUser);
		
		// TODO::::::::: make token expire
		
		// return OK
		return ResponseEntity.ok(HttpStatus.OK);
	}
	
	// Process confirmation link
	@PostMapping("forgotconfirm")
	public ResponseEntity<?> forgotPasswordConfirmPostController(
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
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("bad password");
		}

		// Set new password
		user.setPassword(bCryptPasswordEncoder.encode(requestParams.get("p1")));
		
		// Invalidate further use of token
		user.setConfirmationToken(null);
		
		// Save user
		userService.saveUser(user);
		
		//	RETURN OK
		return ResponseEntity.ok(HttpStatus.OK);
	}
}