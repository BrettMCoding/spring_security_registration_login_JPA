package com.bmc.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


// The controller we use to confirm a good login

@CrossOrigin(origins= "http://localhost:3000" )
@RestController
public class AuthenticationController {

  @GetMapping(path = "/auth")
  public String authString() {
      return ("successful authorization");
  }   
}