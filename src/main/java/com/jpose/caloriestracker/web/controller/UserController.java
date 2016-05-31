package com.jpose.caloriestracker.web.controller;

import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jpose.caloriestracker.entity.AppUser;
import com.jpose.caloriestracker.service.RoleService;
import com.jpose.caloriestracker.service.UserService;
import com.jpose.caloriestracker.web.dto.AppUserDto;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private ModelMapper mapper;
	
	@PreAuthorize("hasAuthority('USER_CRUD_OTHERS')")
	@RequestMapping(method = RequestMethod.GET)
	@Transactional(readOnly = true)
	@ResponseBody
	public Page<AppUserDto> getUsers(
			@RequestParam(value = "pageNumber", required = true) Integer pageNumber,
			@RequestParam(value = "pageSize", required = true) Integer pageSize,
			@RequestParam(value = "usernameContent", required = false) String usernameContent,
			@RequestParam(value = "showOnlyEnabledUsers", required = false) Boolean showOnlyEnabledUsers
		) {
		return userService.findAllByUsernameContent(pageNumber, pageSize, usernameContent, showOnlyEnabledUsers).map(
			record -> {return getDtoFromAppUser(record);}
		);
	}
	
	@PreAuthorize("hasAuthority('USER_CRUD_OTHERS')")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	@ResponseBody
	public AppUserDto getUser(@PathVariable("id") Long id) {
		return getDtoFromAppUser(userService.findOne(id));
	}
	
	@PreAuthorize("hasAuthority('USER_CRUD_OTHERS')")
	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	@ResponseBody
	public AppUserDto create(@RequestBody(required = true) AppUserDto user) {
		return getDtoFromAppUser(userService.create(getAppUserFromDto(user), user.getPasswordConfirmation()));
	}
	
	@RequestMapping(value="register", method = RequestMethod.POST)
	@Transactional
	@ResponseBody
	public AppUserDto register(@RequestBody(required = true) AppUserDto user) {
		return getDtoFromAppUser(userService.create(getAppUserFromDto(user), user.getPasswordConfirmation()));
	}

	@PreAuthorize("hasAuthority('USER_CRUD_OTHERS')")
	@RequestMapping(method = RequestMethod.PUT)
	@Transactional
	@ResponseBody
	public AppUserDto update(@RequestBody(required = true) AppUserDto user) {
		return getDtoFromAppUser(userService.update(getAppUserFromDto(user), user.getPasswordConfirmation()));
	}
	
	@PreAuthorize("hasAuthority('USER_CRUD_OTHERS')")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@Transactional
	@ResponseBody
	public boolean delete(@PathVariable("id") Long id) {
		return userService.delete(id);
	}
	
	private AppUser getAppUserFromDto(AppUserDto dto) {
		AppUser appUser = mapper.map(dto, AppUser.class);
		if (dto.getRolesList() != null) {
			appUser.setRoles(dto.getRolesList().stream().map(
				record -> {return roleService.findOne(record);}
			).collect(Collectors.toList()));
		}
		return appUser;
	}
	
	private AppUserDto getDtoFromAppUser(AppUser appUser) {
		AppUserDto dto = mapper.map(appUser, AppUserDto.class);
		dto.setRolesList(appUser.getRoles().stream().map(
			record -> {return record.getId();}
		).collect(Collectors.toList()));
		return dto;
	}
}
