package com.jpose.caloriestracker;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jpose.caloriestracker.entity.AppUser;
import com.jpose.caloriestracker.entity.Role;
import com.jpose.caloriestracker.repository.RoleRepository;
import com.jpose.caloriestracker.utils.AppUserDtoWithVisiblePassword;
import com.jpose.caloriestracker.web.dto.AppUserDto;
import com.jpose.caloriestracker.web.dto.LoginResponseDto;

public class UserPermissionsAsRoleUserTest extends CaloriesTrackerApplicationTests {
	
	@Autowired
	protected RoleRepository roleRepository;
	private LoginResponseDto loginData;
	private HttpHeaders requestHeaders = new HttpHeaders();
	
	@Before
    public void setUp() {
		this.loginData = loginAndGetData("user", "user");
		this.requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		this.requestHeaders.set("Authorization", "Bearer " + loginData.getToken());
    }
	
	@Test
	public void testGetUsersIsForbidden() {
		HttpEntity<String> entity = new HttpEntity<String>("parameters", requestHeaders);
		ResponseEntity<String> response = restTemplate.exchange(getUserUrl() + getDefaultPageParameters(), HttpMethod.GET, entity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
	}
	
	@Test
	public void testGetUserIsForbidden() {
		AppUser appUser = userRepository.findByUsername("admin");
		HttpEntity<String> entity = new HttpEntity<String>("parameters", requestHeaders);
		ResponseEntity<AppUserDto> response = restTemplate.exchange(getUserUrl() + "/" + appUser.getId(), HttpMethod.GET, entity, AppUserDto.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
	}
	
	@Test
	public void testAddUserWithUserRoles() throws JsonProcessingException {
		AppUserDtoWithVisiblePassword appUser = new AppUserDtoWithVisiblePassword();
		appUser.setUsername("testAddUserWithUserRoles");
		appUser.setPassword("testPassword");
		appUser.setPasswordConfirmation("testPassword");
		appUser.setRolesList(Arrays.asList(new Long[]{roleRepository.findByName(Role.ROLE_USER).getId()}));
		appUser.setEnabled(true);
		String appUserString = objectMapper.writeValueAsString(appUser);
		HttpEntity<String> entity = new HttpEntity<String>(appUserString, requestHeaders);
		ResponseEntity<AppUserDto> response = restTemplate.exchange(getUserUrl() + "/register", HttpMethod.POST, entity, AppUserDto.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		AppUser userInDatabase = userRepository.findByUsername("testAddUserWithUserRoles");
		assertThat(userInDatabase.getUsername(), equalTo(appUser.getUsername()));
		assertThat(userInDatabase.getUsername(), equalTo(appUser.getUsername()));
		assertThat(userInDatabase.getEnabled(), equalTo(appUser.getEnabled()));
		assertThat(getRolesIds(userInDatabase.getRoles()), equalTo(appUser.getRolesList()));
	}
	
	@Test
	public void testRegisterUserWithOtherThanUserRolesIsForbidden() throws JsonProcessingException {
		AppUserDtoWithVisiblePassword appUser = new AppUserDtoWithVisiblePassword();
		appUser.setUsername("testRegisterUserWithOtherThanUserRolesIsForbidden");
		appUser.setPassword("testPassword");
		appUser.setPasswordConfirmation("testPassword");
		appUser.setRolesList(Arrays.asList(new Long[]{roleRepository.findByName(Role.ROLE_ADMIN).getId()}));
		appUser.setEnabled(true);
		String appUserString = objectMapper.writeValueAsString(appUser);
		HttpEntity<String> entity = new HttpEntity<String>(appUserString, requestHeaders);
		ResponseEntity<AppUserDto> response = restTemplate.exchange(getUserUrl() + "/register", HttpMethod.POST, entity, AppUserDto.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
	}
	
	@Test
	public void testAddUserIsForbidden() throws JsonProcessingException {
		AppUserDtoWithVisiblePassword appUser = new AppUserDtoWithVisiblePassword();
		appUser.setUsername("testAddUserIsForbidden");
		appUser.setPassword("testPassword");
		appUser.setPasswordConfirmation("testPassword");
		appUser.setRolesList(Arrays.asList(roleRepository.findByName(Role.ROLE_ADMIN).getId()));
		appUser.setEnabled(true);
		String appUserString = objectMapper.writeValueAsString(appUser);
		HttpEntity<String> entity = new HttpEntity<String>(appUserString, requestHeaders);
		ResponseEntity<AppUserDto> response = restTemplate.exchange(getUserUrl(), HttpMethod.POST, entity, AppUserDto.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
	}
	
	@Test
	public void testEditUserIsForbidden() throws JsonProcessingException {
		AppUser userInDatabase = userRepository.findByUsername("user");
		AppUserDtoWithVisiblePassword appUser = new AppUserDtoWithVisiblePassword();
		appUser.setId(userInDatabase.getId());
		appUser.setUsername("testEditUserIsForbidden");
		appUser.setPassword("modifiedPassword");
		appUser.setPasswordConfirmation("modifiedPassword");
		appUser.setRolesList(Arrays.asList(new Long[]{1L}));
		appUser.setEnabled(false);
		String appUserString = objectMapper.writeValueAsString(appUser);
		HttpEntity<String> entity = new HttpEntity<String>(appUserString, requestHeaders);
		ResponseEntity<AppUserDto> response = restTemplate.exchange(getUserUrl(), HttpMethod.PUT, entity, AppUserDto.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
	}
	
	@Test
	public void testDeleteUserIsForbidden() {
		AppUser userInDatabase = userRepository.findByUsername("usermanager");
		HttpEntity<String> entity = new HttpEntity<String>(null, requestHeaders);
		ResponseEntity<String> response = restTemplate.exchange(getUserUrl() + "/" + userInDatabase.getId(), HttpMethod.DELETE, entity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
	}
	
	private List<Long> getRolesIds(Collection<Role> roles) {
		return roles.stream().map(role -> role.getId()).collect(Collectors.toList());
	}
	
	private String getUserUrl() {
		return getApiUrl() + "/user";
	}
}
