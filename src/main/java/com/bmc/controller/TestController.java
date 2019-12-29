package com.bmc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

// Test controller to confirm that URLs behind /secured/ are not accessible without the correct credentials from mySQL

@CrossOrigin(origins = "http://localhost:3000")
@Controller
public class TestController {
	

	@GetMapping("secured/test")
	public ResponseEntity<?> testGetController() {
			return ResponseEntity.ok(HttpStatus.OK);
	}
}
