package com.jpose.caloriestracker.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.jpose.caloriestracker.entity.AuthenticationDetails;
import com.jpose.caloriestracker.entity.Privilege;
import com.jpose.caloriestracker.entity.UserDetails;
import com.jpose.caloriestracker.service.UserService.UserError;
import com.jpose.caloriestracker.service.exceptions.BadRequestException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class AuthenticationService {
	
	// The secret key could be generated randomly on app startup.
	private static final String SECRET_KEY = "ef0X4BYuHXTJBYSJ5b0ceOL7PZMugOymskdslkDS7m8POeWc5b7IlUdAgCWbGQw";
	private static final long TOKEN_TIME_VALIDITY_MS = 1000L * 60 * 60;	// 1 hour
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	public AuthenticationDetails validateLoginAndGetAuthenticationDetails(String username, String password) {
		if (username == null) {
			throw new BadRequestException(UserError.USERNAME_CAN_NOT_BE_EMPTY);
		}
		if (password == null) {
			throw new BadRequestException(UserError.PASSWORD_CAN_NOT_BE_EMPTY);
		}
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        long expires = System.currentTimeMillis() + TOKEN_TIME_VALIDITY_MS;
        String token = Jwts.builder().setSubject(userDetails.getUsername())
            .claim("roles", userDetails.getAuthorities())
            .setIssuedAt(new Date())
            .setExpiration(new Date(expires))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();
        return new AuthenticationDetails(userDetails.getId(), userDetails.getUsername(), token, expires, userDetails.getAuthorities());
	}
	
	public boolean canCrudOtherUsers() {
		return !getPrincipal().equals("anonymousUser")
			&& getUserDetails().getAuthorities().contains(new SimpleGrantedAuthority(Privilege.USER_CRUD_OTHERS));
	}
	
	public boolean canCrudOthersCaloriesRecords() {
		return !getPrincipal().equals("anonymousUser")
			&& getUserDetails().getAuthorities().contains(new SimpleGrantedAuthority(Privilege.CALORIES_RECORD_CRUD_OTHERS));
	}
	
	public Long getLoggedUserId() {
		return getUserDetails().getId();
	}
	
	private Object getPrincipal() {
		return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	
	private UserDetails getUserDetails() {
		return (com.jpose.caloriestracker.entity.UserDetails) getPrincipal();
	}
}
