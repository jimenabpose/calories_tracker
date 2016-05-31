package com.jpose.caloriestracker;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
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
import com.jpose.caloriestracker.entity.CaloriesRecord;
import com.jpose.caloriestracker.web.dto.AppUserDto;
import com.jpose.caloriestracker.web.dto.CaloriesRecordDto;
import com.jpose.caloriestracker.web.dto.LoginResponseDto;

public class CaloriesRecordPermissionsAsRoleUserTest extends CaloriesTrackerApplicationTests {
	
	private LoginResponseDto loginData;
	private HttpHeaders requestHeaders = new HttpHeaders();
	
	@Before
    public void setUp() {
		this.loginData = loginAndGetData("user2", "user2");
		this.requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		this.requestHeaders.set("Authorization", "Bearer " + loginData.getToken());
    }
	
	@Test
	public void testGetCaloriesRecordsOfAnotherUserFails() {
		AppUser user2 = userRepository.findByUsername("user");
		HttpEntity<String> entity = new HttpEntity<String>("parameters", requestHeaders);
		ResponseEntity<String> response = restTemplate.exchange(getCaloriesRecordUrl() + getDefaultPageParameters() + "&user=" + user2.getId(), HttpMethod.GET, entity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
	}
	
	@Test
	public void testGetCaloriesRecordOfAnotherUserFails() {
		CaloriesRecord caloriesRecord = caloriesRecordRepository.findOne(1L);
		HttpEntity<String> entity = new HttpEntity<String>("parameters", requestHeaders);
		ResponseEntity<CaloriesRecordDto> response = restTemplate.exchange(getCaloriesRecordUrl() + "/" + caloriesRecord.getId(), HttpMethod.GET, entity, CaloriesRecordDto.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
	}

	@Test
	public void testAddCaloriesRecordForAnotherUserFails() throws JsonProcessingException {
		AppUser anotherUser = userRepository.findByUsername("user");
		AppUserDto appUser = new AppUserDto();
		appUser.setUsername(anotherUser.getUsername());
		appUser.setId(anotherUser.getId());
		CaloriesRecordDto caloriesRecord = new CaloriesRecordDto();
		caloriesRecord.setText("newCaloriesRecord");
		caloriesRecord.setCaloriesQuantity(2000L);
		caloriesRecord.setDate(new LocalDate(2016, 5, 26));
		caloriesRecord.setTime(new LocalTime(13, 30));
		caloriesRecord.setUser(appUser);
		String caloriesRecordString = objectMapper.writeValueAsString(caloriesRecord);
		HttpEntity<String> entity = new HttpEntity<String>(caloriesRecordString, requestHeaders);
		ResponseEntity<CaloriesRecordDto> response = restTemplate.exchange(getCaloriesRecordUrl(), HttpMethod.POST, entity, CaloriesRecordDto.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
	}
	
	@Test
	public void testEditCaloriesRecordForAnotherUserFails() throws JsonProcessingException {
		AppUser anotherUser = userRepository.findByUsername("user");
		AppUserDto appUser = new AppUserDto();
		appUser.setUsername(anotherUser.getUsername());
		appUser.setId(anotherUser.getId());
		CaloriesRecordDto caloriesRecord = new CaloriesRecordDto();
		caloriesRecord.setId(caloriesRecordRepository.findAll().get(0).getId());
		caloriesRecord.setText("editedCaloriesRecord");
		caloriesRecord.setUser(appUser);
		String caloriesRecordString = objectMapper.writeValueAsString(caloriesRecord);
		HttpEntity<String> entity = new HttpEntity<String>(caloriesRecordString, requestHeaders);
		ResponseEntity<CaloriesRecordDto> response = restTemplate.exchange(getCaloriesRecordUrl(), HttpMethod.PUT, entity, CaloriesRecordDto.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
	}
	
	@Test
	public void testDeleteCaloriesRecordForAnotherUserFails() {
		CaloriesRecord caloriesRecordInDatabase = caloriesRecordRepository.findAll().get(0);
		HttpEntity<String> entity = new HttpEntity<String>("parameters", requestHeaders);
		ResponseEntity<String> response = restTemplate.exchange(getCaloriesRecordUrl() + "/" + caloriesRecordInDatabase.getId(), HttpMethod.DELETE, entity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
	}
	
	private String getCaloriesRecordUrl() {
		return getApiUrl() + "/caloriesRecord";
	}
}
