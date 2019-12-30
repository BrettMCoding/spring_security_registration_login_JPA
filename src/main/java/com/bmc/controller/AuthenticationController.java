package com.bmc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


// The controller we use to confirm a good login

@CrossOrigin(origins= "http://localhost:3000" )
@RestController
public class AuthenticationController {

  @GetMapping(path = "/auth")
  public ResponseEntity<?> authString() {
      return ResponseEntity.status(HttpStatus.OK).body("Successful authorization");
  }   
}