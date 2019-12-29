package com.bmc.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

public class EnvConfig {
	
	public EnvConfig(){}
	
	@Autowired
	private Environment env;
	
	@Value("${emailpassword}")
	private String emailpassword = env.getProperty("smtpemailpassword");
	

}
