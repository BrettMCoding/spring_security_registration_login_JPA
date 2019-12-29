package com.bmc.model;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@SuppressWarnings("serial")
public class CustomUserDetails extends User implements UserDetails {

    public CustomUserDetails(final User user) {
        super(user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
    	Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
    	authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    	
    	return authorities;
    
		//      return getRoles()
				//                .stream()
				//                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoles()))
				//                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    // MUST CHANGE USERNAME TO EITHER EMAIL OR FIRST LAST NAME TO USERNAME
    @Override
    public String getUsername() {
        return super.getFirstName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
