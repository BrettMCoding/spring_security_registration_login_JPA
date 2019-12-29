package com.bmc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bmc.model.User;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Integer> {
	 Optional<User> findByUserName(String userName);
	 User findByEmail(String email);
	 User findByConfirmationToken(String confirmationToken);
}