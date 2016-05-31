package com.jpose.caloriestracker.service;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jpose.caloriestracker.entity.AppUser;
import com.jpose.caloriestracker.entity.ComboData;
import com.jpose.caloriestracker.entity.Role;
import com.jpose.caloriestracker.repository.UserRepository;
import com.jpose.caloriestracker.service.exceptions.BadRequestException;
import com.jpose.caloriestracker.service.exceptions.ForbiddenException;
import com.jpose.caloriestracker.service.exceptions.NotFoundException;

@Service
public class UserService {
	
	public enum UserError {
		PAGE_SIZE_INVALID,
		USER_CREATION_NO_ID,
		USERNAME_ALREADY_TAKEN,
		USERNAME_CAN_NOT_BE_EMPTY,
		PASSWORD_CAN_NOT_BE_EMPTY,
		PASSWORD_INVALID_MATCHING,
		USER_NOT_FOUND,
		ROLE_UNAUTHORIZED,
		USER_SPECIFY_ENABLED,
		CAN_NOT_DISABLE_CURRENT_USER,
		CAN_NOT_CHANGE_CURRENT_USER_ROLES
		;
	}
	
	private static final int MAX_PAGE_SIZE = 50;
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleService roleService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationService authenticationService;
	
	public Page<AppUser> findAllByUsernameContent(Integer pageNumber, Integer pageSize, String usernameContent, Boolean showOnlyEnabledUsers) {
		if (pageSize > MAX_PAGE_SIZE) {
			throw new BadRequestException(UserError.PAGE_SIZE_INVALID);
		}
		Pageable pageable = new PageRequest(pageNumber - 1, pageSize);
		String usernameToFind = usernameContent == null ? "" : usernameContent;
		if (showOnlyEnabledUsers == null || showOnlyEnabledUsers == false) {
			return userRepository.findByUsernameContaining(usernameToFind, pageable);
		}
		return userRepository.findByUsernameContainingAndEnabled(usernameToFind, true, pageable);
	}
	
	public AppUser findOne(Long id) {
		AppUser user = userRepository.findOne(id);
		if (user == null) {
			throw new NotFoundException(UserError.USER_NOT_FOUND);
		}
		return user;
	}
	
	public AppUser create(AppUser user, String passwordConfirmation) {
		if (user.getId() != null) {
			throw new BadRequestException(UserError.USER_CREATION_NO_ID);
		}
		if (!authenticationService.canCrudOtherUsers()) {
			Collection<Role> roles = user.getRoles();
			if (!roles.isEmpty() && (roles.size() > 1 || !roles.contains(roleService.findByName(Role.ROLE_USER)))) {
				throw new ForbiddenException(UserError.ROLE_UNAUTHORIZED);
			}
		}
		if (userRepository.findByUsername(user.getUsername()) != null) {
			throw new BadRequestException(UserError.USERNAME_ALREADY_TAKEN);
		}
		if (StringUtils.isBlank(user.getPassword()) || StringUtils.isBlank(passwordConfirmation)) {
			throw new BadRequestException(UserError.PASSWORD_CAN_NOT_BE_EMPTY);
		}
		if (!user.getPassword().equals(passwordConfirmation)) {
			throw new BadRequestException(UserError.PASSWORD_INVALID_MATCHING);
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return this.save(user);
	}
	
	public AppUser update(AppUser user, String passwordConfirmation) {
		if (user.getId() == null) {
			throw new NotFoundException(UserError.USER_NOT_FOUND);
		}
		AppUser appUser = userRepository.findOne(user.getId());
		if (appUser == null) {
			throw new NotFoundException(UserError.USER_NOT_FOUND);
		}
		AppUser userWithUsername = userRepository.findByUsername(user.getUsername());
		if (userWithUsername != null && !userWithUsername.getId().equals(appUser.getId())) {
			throw new BadRequestException(UserError.USERNAME_ALREADY_TAKEN);
		}
		if (user.getEnabled() == null) {
			throw new BadRequestException(UserError.USER_SPECIFY_ENABLED);
		}
		Long loggedUserId = authenticationService.getLoggedUserId();
		if (loggedUserId.equals(user.getId())) {
			if (!user.getEnabled()) {
				throw new BadRequestException(UserError.CAN_NOT_DISABLE_CURRENT_USER);
			}
			if (!user.getRoles().equals(appUser.getRoles())) {
				throw new BadRequestException(UserError.CAN_NOT_CHANGE_CURRENT_USER_ROLES);
			}
		}
		appUser.setUsername(user.getUsername());
		appUser.setEnabled(user.getEnabled());
		if (user.getPassword() != null) {
			if (!user.getPassword().equals(passwordConfirmation)) {
				throw new BadRequestException(UserError.PASSWORD_INVALID_MATCHING);
			}
			appUser.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		appUser.setRoles(user.getRoles());
		return this.save(appUser);
	}

	public AppUser save(AppUser user) {
		if (StringUtils.isBlank(user.getUsername())) {
			throw new BadRequestException(UserError.USERNAME_CAN_NOT_BE_EMPTY);
		}
		return userRepository.save(user);
	}

	public boolean delete(Long id) {
		AppUser user = this.findOne(id);
		if (user == null) {
			throw new NotFoundException(UserError.USER_NOT_FOUND);
		}
		if (authenticationService.getLoggedUserId() == user.getId()) {
			throw new BadRequestException(UserError.CAN_NOT_DISABLE_CURRENT_USER);
		}
		user.setEnabled(false);
		userRepository.save(user);
		return true;
	}
	
	public AppUser getLoggedUser() {
		return this.findOne(authenticationService.getLoggedUserId());
	}
	
	public AppUser getUserOrLogged(Long userId) {
		return userId == null ? this.getLoggedUser() : this.findOne(userId);
	}
	
	public List<ComboData> findUsersComboData() {
		return userRepository.findIdsAndUsernames();
	}
}
