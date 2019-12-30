package com.bmc.controller;

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

import com.bmc.model.User;
import com.bmc.service.EmailService;
import com.bmc.service.UserService;

// Controller for forgotten password. Requesting /forgot will add a new confirm token to the database user who's email matches
// 	and it will send that token to the provided email address.
// NOTE::::: copy the token directly from DB for testing. uncomment out the email section when Postman testing is completed

@CrossOrigin(origins = "http://localhost:3000")
@Controller
public class ForgotPasswordController {

	private UserService userService;
	private EmailService emailService;
	
	@Autowired
	public ForgotPasswordController(BCryptPasswordEncoder bCryptPasswordEncoder,
			UserService userService, EmailService emailService) {

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
//		registrationEmail.setTo(checkUser.getEmail());
//		registrationEmail.setSubject("Registration Confirmation");
//		registrationEmail.setText("To confirm your e-mail address, please click the link below:\n"
//				+ appUrl + "/confirm?token=" + checkUser.getConfirmationToken());
		
//		emailService.sendEmail(registrationEmail);
		
	    // save confirmation token to database
		userService.saveUser(checkUser);
		
		// TODO::::::::: make token expire
		
		// return OK
		return ResponseEntity.ok(HttpStatus.OK);
	}
}