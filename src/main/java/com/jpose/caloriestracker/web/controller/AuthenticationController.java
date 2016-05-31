package com.jpose.caloriestracker.web.controller;

import javax.servlet.ServletException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jpose.caloriestracker.service.AuthenticationService;
import com.jpose.caloriestracker.web.dto.LoginRequestDto;
import com.jpose.caloriestracker.web.dto.LoginResponseDto;

@RestController
@RequestMapping("/api/authentication")
public class AuthenticationController {
	
	@Autowired
	private AuthenticationService authenticationService;
	@Autowired
	private ModelMapper mapper;
	
	@RequestMapping(method = RequestMethod.POST)
	public LoginResponseDto login(@RequestBody final LoginRequestDto login) throws ServletException {
        return mapper.map(authenticationService.validateLoginAndGetAuthenticationDetails(login.getUsername(), login.getPassword()), LoginResponseDto.class);
    }
}
