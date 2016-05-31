package com.jpose.caloriestracker;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIn.isIn;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jpose.caloriestracker.service.UserService.UserError;
import com.jpose.caloriestracker.service.exceptions.EnumExceptionFormatter;
import com.jpose.caloriestracker.utils.AppUserDtoWithVisiblePassword;
import com.jpose.caloriestracker.web.dto.LoginResponseDto;

public class UserWrongParametersTest extends CaloriesTrackerApplicationTests {
	
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
	public void testGetUsersPageParameters() {
		HttpEntity<String> entity = new HttpEntity<String>("parameters", requestHeaders);
		ResponseEntity<Map> response = restTemplate.exchange(getUserUrl() + "?pageNumber=1&pageSize=100", HttpMethod.GET, entity, Map.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
		Map<String, String> content = (Map<String, String>) response.getBody();
		assertThat(content.get("message"), equalTo(EnumExceptionFormatter.formatEnum(UserError.PAGE_SIZE_INVALID)));
		ResponseEntity<Map> responseOk = restTemplate.exchange(getUserUrl() + "?pageNumber=100&pageSize=10", HttpMethod.GET, entity, Map.class);
		assertThat(responseOk.getStatusCode(), equalTo(HttpStatus.OK));
		List<Map<String, String>> contentOk = (List<Map<String, String>>) responseOk.getBody().get("content");
		assertThat(contentOk.size(), equalTo(0));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testGetInexistentUser() {
		HttpEntity<String> entity = new HttpEntity<String>("parameters", requestHeaders);
		ResponseEntity<Map> response = restTemplate.exchange(getUserUrl() + "/500", HttpMethod.GET, entity, Map.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
		Map<String, String> content = (Map<String, String>) response.getBody();
		assertThat(content.get("message"), equalTo(EnumExceptionFormatter.formatEnum(UserError.USER_NOT_FOUND)));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testAddEmptyUser() throws JsonProcessingException {
		List<String> userCreationErrors = Arrays.asList(new String[] {
			EnumExceptionFormatter.formatEnum(UserError.PASSWORD_CAN_NOT_BE_EMPTY),
			EnumExceptionFormatter.formatEnum(UserError.PASSWORD_INVALID_MATCHING),
			EnumExceptionFormatter.formatEnum(UserError.ROLE_UNAUTHORIZED),
			EnumExceptionFormatter.formatEnum(UserError.USER_CREATION_NO_ID),
			EnumExceptionFormatter.formatEnum(UserError.USERNAME_ALREADY_TAKEN),
			EnumExceptionFormatter.formatEnum(UserError.USERNAME_CAN_NOT_BE_EMPTY)
		});
		AppUserDtoWithVisiblePassword appUser = new AppUserDtoWithVisiblePassword();
		String appUserString = objectMapper.writeValueAsString(appUser);
		HttpEntity<String> entity = new HttpEntity<String>(appUserString, requestHeaders);
		ResponseEntity<Map> response = restTemplate.exchange(getUserUrl(), HttpMethod.POST, entity, Map.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
		Map<String, String> content = (Map<String, String>) response.getBody();
		assertThat(content.get("message"), isIn(userCreationErrors));
	}
	
	@Test
	public void testAddOrEditUserWithNoUsername() throws JsonProcessingException {
		AppUserDtoWithVisiblePassword appUser = getBaseUserDto();
		appUser.setUsername(null);
		assertBadUserRequest(appUser, HttpMethod.POST, UserError.USERNAME_CAN_NOT_BE_EMPTY);
		appUser.setId(2L);
		assertBadUserRequest(appUser, HttpMethod.PUT, UserError.USERNAME_CAN_NOT_BE_EMPTY);
	}
	
	@Test
	public void testAddOrEditUserWithNoOrWrongPassword() throws JsonProcessingException {
		AppUserDtoWithVisiblePassword appUser = getBaseUserDto();
		appUser.setPassword(null);
		assertBadUserRequest(appUser, HttpMethod.POST, UserError.PASSWORD_CAN_NOT_BE_EMPTY);
		appUser.setPassword("test");
		appUser.setPassword("test2");
		assertBadUserRequest(appUser, HttpMethod.POST, UserError.PASSWORD_INVALID_MATCHING);
		appUser.setId(2L);
		assertBadUserRequest(appUser, HttpMethod.PUT, UserError.PASSWORD_INVALID_MATCHING);
	}
	
	@Test
	public void testEditUserNotSpecifyingEnabled() throws JsonProcessingException {
		AppUserDtoWithVisiblePassword appUser = getBaseUserDto();
		appUser.setEnabled(null);
		appUser.setId(1L);
		assertBadUserRequest(appUser, HttpMethod.PUT, UserError.USER_SPECIFY_ENABLED);
	}
	
	@Test
	public void testDeleteInexistentUserFails() {
		HttpEntity<String> entity = new HttpEntity<String>("parameters", requestHeaders);
		ResponseEntity<String> response = restTemplate.exchange(getUserUrl() + "/500", HttpMethod.DELETE, entity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
	}
	
	private String getUserUrl() {
		return getApiUrl() + "/user";
	}
	
	private AppUserDtoWithVisiblePassword getBaseUserDto() {
		AppUserDtoWithVisiblePassword appUser = new AppUserDtoWithVisiblePassword();
		appUser.setUsername("testUserParameters");
		appUser.setPassword("testPassword");
		appUser.setPasswordConfirmation("testPassword");
		appUser.setRolesList(Arrays.asList(new Long[]{2L}));
		appUser.setEnabled(true);
		return appUser;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void assertBadUserRequest(AppUserDtoWithVisiblePassword appUser, HttpMethod method, Enum<?> expectedError) throws JsonProcessingException {
		String appUserString = objectMapper.writeValueAsString(appUser);
		HttpEntity<String> entity = new HttpEntity<String>(appUserString, requestHeaders);
		ResponseEntity<Map> response = restTemplate.exchange(getUserUrl(), method, entity, Map.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
		Map<String, String> content = (Map<String, String>) response.getBody();
		assertThat(content.get("message"), equalTo(EnumExceptionFormatter.formatEnum(expectedError)));
	}
}
