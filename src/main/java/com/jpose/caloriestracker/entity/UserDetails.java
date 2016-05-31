package com.jpose.caloriestracker.entity;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

@SuppressWarnings("serial")
public class UserDetails extends User {

	private Long id;

	public UserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Long id) {
		super(username, password, authorities);
		this.id = id;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
}
