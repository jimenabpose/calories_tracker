package com.jpose.caloriestracker;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.jpose.caloriestracker.entity.AppUser;
import com.jpose.caloriestracker.web.dto.LoginResponseDto;

public class AuthenticationTest extends CaloriesTrackerApplicationTests {
	
	@Test
	public void testAuthenticationSuccessfull() {
		AppUser adminUser = userRepository.findByUsername("admin");
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("username", adminUser.getUsername());
		parameters.put("password", "admin");
		ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity(getAuthenticationUrl(), parameters, LoginResponseDto.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		LoginResponseDto responseBody = response.getBody();
		assertThat(responseBody.getToken(), notNullValue());
		assertThat(responseBody.getExpiration(), greaterThan(0L));
		assertThat(responseBody.getUserId(), equalTo(adminUser.getId()));
		assertThat(responseBody.getUsername(), equalTo(adminUser.getUsername()));
	}
	
	@Test
	public void testInvalidPasswordFails() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("username", "admin");
		parameters.put("password", "wrongPassword");
		ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity(getAuthenticationUrl(), parameters, LoginResponseDto.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
		assertThat(response.getBody().getToken(), nullValue());
	}
	
	private String getAuthenticationUrl() {
		return getApiUrl() + "/authentication";
	}
}
