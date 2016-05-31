package com.jpose.caloriestracker.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jpose.caloriestracker.entity.Role;
import com.jpose.caloriestracker.service.RoleService;
import com.jpose.caloriestracker.service.UserService;
import com.jpose.caloriestracker.web.dto.ComboDataDto;

@RestController
@RequestMapping("/api/combos")
public class CombosController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private ModelMapper mapper;
	
	@PreAuthorize("hasAuthority('CALORIES_RECORD_CRUD_OTHERS')")
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	@ResponseBody
	public List<ComboDataDto> getUsersData() {
		return userService.findUsersComboData().stream().map(
			record -> {return mapper.map(record, ComboDataDto.class);}
		).collect(Collectors.toList());
	}
	
	@PreAuthorize("hasAuthority('USER_CRUD_OTHERS')")
	@RequestMapping(value = "/roles", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	@ResponseBody
	public List<ComboDataDto> getRoles() {
		return roleService.findAll().stream().map(
			record -> {return mapper.map(record, ComboDataDto.class);}
		).collect(Collectors.toList());
	}
	
	@RequestMapping(value = "/userRole", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	@ResponseBody
	public Long getUserRole() {
		return roleService.findByName(Role.ROLE_USER).getId();
	}
}
