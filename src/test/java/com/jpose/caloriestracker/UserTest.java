package com.jpose.caloriestracker;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jpose.caloriestracker.entity.AppUser;
import com.jpose.caloriestracker.entity.Role;
import com.jpose.caloriestracker.service.UserService.UserError;
import com.jpose.caloriestracker.service.exceptions.EnumExceptionFormatter;
import com.jpose.caloriestracker.utils.AppUserDtoWithVisiblePassword;
import com.jpose.caloriestracker.web.dto.AppUserDto;
import com.jpose.caloriestracker.web.dto.LoginResponseDto;

public class UserTest extends CaloriesTrackerApplicationTests {
	
	private LoginResponseDto loginData;
	private HttpHeaders requestHeaders = new HttpHeaders();
	
	@Before
    public void setUp() {
		this.loginData = loginAndGetData("admin", "admin");
		this.requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		this.requestHeaders.set("Authorization", "Bearer " + loginData.getToken());
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testGetUsers() {
		HttpEntity<String> entity = new HttpEntity<String>("parameters", requestHeaders);
		ResponseEntity<Map> response = restTemplate.exchange(getUserUrl() + getDefaultPageParameters(), HttpMethod.GET, entity, Map.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		List<Map<String, String>> content = (List<Map<String, String>>) response.getBody().get("content");
		assertThat(content.size(), greaterThan(0));
	}
	
	@Test
	public void testGetUser() {
		AppUser appUser = userRepository.findByUsername("admin");
		HttpEntity<String> entity = new HttpEntity<String>("parameters", requestHeaders);
		ResponseEntity<AppUserDto> response = restTemplate.exchange(getUserUrl() + "/" + appUser.getId(), HttpMethod.GET, entity, AppUserDto.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		AppUserDto returnedUser = response.getBody();
		assertThat(returnedUser.getUsername(), equalTo(appUser.getUsername()));
		assertThat(returnedUser.getUsername(), equalTo(appUser.getUsername()));
		assertThat(returnedUser.getEnabled(), equalTo(appUser.getEnabled()));
		assertThat(returnedUser.getRolesList(), equalTo(getRolesIds(appUser.getRoles())));
	}
	
	@Test
	public void testAddUser() throws JsonProcessingException {
		AppUserDtoWithVisiblePassword appUser = new AppUserDtoWithVisiblePassword();
		appUser.setUsername("testUser");
		appUser.setPassword("testPassword");
		appUser.setPasswordConfirmation("testPassword");
		appUser.setRolesList(Arrays.asList(new Long[]{2L}));
		appUser.setEnabled(true);
		String appUserString = objectMapper.writeValueAsString(appUser);
		HttpEntity<String> entity = new HttpEntity<String>(appUserString, requestHeaders);
		ResponseEntity<AppUserDto> response = restTemplate.exchange(getUserUrl(), HttpMethod.POST, entity, AppUserDto.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		AppUser userInDatabase = userRepository.findByUsername("testUser");
		assertThat(userInDatabase.getUsername(), equalTo(appUser.getUsername()));
		assertThat(userInDatabase.getUsername(), equalTo(appUser.getUsername()));
		assertThat(userInDatabase.getEnabled(), equalTo(appUser.getEnabled()));
		assertThat(getRolesIds(userInDatabase.getRoles()), equalTo(appUser.getRolesList()));
	}
	
	@Test
	public void testEditUser() throws JsonProcessingException {
		AppUser userInDatabase = userRepository.findByUsername("user");
		AppUserDtoWithVisiblePassword appUser = new AppUserDtoWithVisiblePassword();
		appUser.setId(userInDatabase.getId());
		appUser.setUsername("modifiedUser");
		appUser.setPassword("modifiedPassword");
		appUser.setPasswordConfirmation("modifiedPassword");
		appUser.setRolesList(Arrays.asList(new Long[]{1L}));
		appUser.setEnabled(false);
		String appUserString = objectMapper.writeValueAsString(appUser);
		HttpEntity<String> entity = new HttpEntity<String>(appUserString, requestHeaders);
		ResponseEntity<AppUserDto> response = restTemplate.exchange(getUserUrl(), HttpMethod.PUT, entity, AppUserDto.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		AppUser modifiedUserInDatabase = userRepository.findByUsername("modifiedUser");
		assertThat(modifiedUserInDatabase.getUsername(), equalTo(appUser.getUsername()));
		assertThat(modifiedUserInDatabase.getUsername(), equalTo(appUser.getUsername()));
		assertThat(modifiedUserInDatabase.getEnabled(), equalTo(appUser.getEnabled()));
		assertThat(getRolesIds(modifiedUserInDatabase.getRoles()), equalTo(appUser.getRolesList()));
	}
	
	@Test
	public void testDeleteUser() {
		AppUser userInDatabase = userRepository.findByUsername("usermanager");
		HttpEntity<String> entity = new HttpEntity<String>("parameters", requestHeaders);
		ResponseEntity<Boolean> response = restTemplate.exchange(getUserUrl() + "/" + userInDatabase.getId(), HttpMethod.DELETE, entity, Boolean.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		AppUser modifiedUserInDatabase = userRepository.findByUsername("usermanager");
		assertThat(modifiedUserInDatabase, notNullValue());
		assertThat(response.getBody(), equalTo(true));
		assertThat(modifiedUserInDatabase.getEnabled(), equalTo(false));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testCanNotDisableCurrentUser() {
		AppUser userInDatabase = userRepository.findByUsername("admin");
		userInDatabase.setEnabled(false);
		HttpEntity<String> entity = new HttpEntity<String>("parameters", requestHeaders);
		ResponseEntity<Map> response = restTemplate.exchange(getUserUrl() + "/" + userInDatabase.getId(), HttpMethod.DELETE, entity, Map.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
		Map<String, String> content = (Map<String, String>) response.getBody();
		assertThat(content.get("message"), equalTo(EnumExceptionFormatter.formatEnum(UserError.CAN_NOT_DISABLE_CURRENT_USER)));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testCanNotEditCurrentUserRoles() {
		AppUser userInDatabase = userRepository.findByUsername("admin");
		userInDatabase.setRoles(Arrays.asList(new Role[] {new Role(Role.ROLE_USER)}));
		HttpEntity<String> entity = new HttpEntity<String>("parameters", requestHeaders);
		ResponseEntity<Map> response = restTemplate.exchange(getUserUrl() + "/" + userInDatabase.getId(), HttpMethod.DELETE, entity, Map.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
		Map<String, String> content = (Map<String, String>) response.getBody();
		assertThat(content.get("message"), equalTo(EnumExceptionFormatter.formatEnum(UserError.CAN_NOT_DISABLE_CURRENT_USER)));
	}
	
	private List<Long> getRolesIds(Collection<Role> roles) {
		return roles.stream().map(role -> role.getId()).collect(Collectors.toList());
	}
	
	private String getUserUrl() {
		return getApiUrl() + "/user";
	}
}
