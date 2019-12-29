package com.bmc.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bmc.model.CustomUserDetails;
import com.bmc.model.User;
import com.bmc.repository.UserRepository;

@Service("userService")
public class UserService implements UserDetailsService {

	private UserRepository userRepository;

	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	public Optional<User> findByUsername(String username) {
		return userRepository.findByUserName(username);
	}
	
	public User findByConfirmationToken(String confirmationToken) {
		return userRepository.findByConfirmationToken(confirmationToken);
	}
	
	public void saveUser(User user) {
		userRepository.save(user);
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> optionalUsers = findByUsername(username);
		
		optionalUsers
			.orElseThrow(() -> new UsernameNotFoundException("Username not found"));
		return optionalUsers
			.map(CustomUserDetails::new).get();
		
	}

}