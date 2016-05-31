package com.jpose.caloriestracker.entity;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import lombok.Data;

@Data
public class AuthenticationDetails {

	private Long userId;
    private String username;
    private String token;
    private Long expiration;
    private Collection<GrantedAuthority> authorities;
    
    public AuthenticationDetails() {
    }
    
    public AuthenticationDetails(Long userId, String username, String token, Long expiration, Collection<GrantedAuthority> authorities) {
    	this.userId = userId;
    	this.username = username;
    	this.token = token;
    	this.expiration = expiration;
    	this.authorities = authorities;
    }
}
