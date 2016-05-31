package com.jpose.caloriestracker;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;

import java.util.List;
import java.util.Map;

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
import com.jpose.caloriestracker.entity.CaloriesRecord;
import com.jpose.caloriestracker.web.dto.AppUserDto;
import com.jpose.caloriestracker.web.dto.CaloriesRecordDto;
import com.jpose.caloriestracker.web.dto.LoginResponseDto;

public class CaloriesRecordTest extends CaloriesTrackerApplicationTests {
	
	private LoginResponseDto loginData;
	private HttpHeaders requestHeaders = new HttpHeaders();
	
	@Before
    public void setUp() {
		this.loginData = loginAndGetData("user", "user");
		this.requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		this.requestHeaders.set("Authorization", "Bearer " + loginData.getToken());
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testGetCaloriesRecords() {
		HttpEntity<String> entity = new HttpEntity<String>("parameters", requestHeaders);
		ResponseEntity<Map> response = restTemplate.exchange(getCaloriesRecordUrl() + getDefaultPageParameters() + "&user=" + loginData.getUserId(), HttpMethod.GET, entity, Map.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		List<Map<String, String>> content = (List<Map<String, String>>) response.getBody().get("content");
		assertThat(content.size(), greaterThan(0));
	}
	
	@Test
	public void testGetCaloriesRecord() {
		CaloriesRecord caloriesRecord = caloriesRecordRepository.findAll().get(0);
		HttpEntity<String> entity = new HttpEntity<String>("parameters", requestHeaders);
		ResponseEntity<CaloriesRecordDto> response = restTemplate.exchange(getCaloriesRecordUrl() + "/" + caloriesRecord.getId(), HttpMethod.GET, entity, CaloriesRecordDto.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		CaloriesRecordDto returnedCaloriesRecord = response.getBody();
		assertThat(returnedCaloriesRecord.getText(), equalTo(caloriesRecord.getText()));
		assertThat(returnedCaloriesRecord.getCaloriesQuantity(), equalTo(caloriesRecord.getCaloriesQuantity()));
		assertThat(returnedCaloriesRecord.getDate(), equalTo(caloriesRecord.getDate()));
		assertThat(returnedCaloriesRecord.getTime(), equalTo(caloriesRecord.getTime()));
		assertThat(returnedCaloriesRecord.getUser().getId(), equalTo(caloriesRecord.getUser().getId()));
	}

	@Test
	public void testAddCaloriesRecord() throws JsonProcessingException {
		AppUserDto appUser = new AppUserDto();
		appUser.setUsername(loginData.getUsername());
		appUser.setId(loginData.getUserId());
		CaloriesRecordDto caloriesRecord = new CaloriesRecordDto();
		caloriesRecord.setText("newCaloriesRecord");
		caloriesRecord.setCaloriesQuantity(2000L);
		caloriesRecord.setDate(new LocalDate(2016, 5, 26));
		caloriesRecord.setTime(new LocalTime(13, 30));
		caloriesRecord.setUser(appUser);
		String caloriesRecordString = objectMapper.writeValueAsString(caloriesRecord);
		HttpEntity<String> entity = new HttpEntity<String>(caloriesRecordString, requestHeaders);
		ResponseEntity<CaloriesRecordDto> response = restTemplate.exchange(getCaloriesRecordUrl(), HttpMethod.POST, entity, CaloriesRecordDto.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		CaloriesRecord caloriesRecordInDatabase = caloriesRecordRepository.findOne(response.getBody().getId());
		assertThat(caloriesRecordInDatabase.getText(), equalTo(caloriesRecord.getText()));
		assertThat(caloriesRecordInDatabase.getCaloriesQuantity(), equalTo(caloriesRecord.getCaloriesQuantity()));
		assertThat(caloriesRecordInDatabase.getDate(), equalTo(caloriesRecord.getDate()));
		assertThat(caloriesRecordInDatabase.getTime(), equalTo(caloriesRecord.getTime()));
		assertThat(caloriesRecordInDatabase.getUser().getId(), equalTo(caloriesRecord.getUser().getId()));
	}
	
	@Test
	public void testEditCaloriesRecord() throws JsonProcessingException {
		LocalDate expectedDate = new LocalDate(2016, 5, 27);
		LocalTime expectedTime = new LocalTime(21, 00);
		AppUserDto appUser = new AppUserDto();
		appUser.setUsername(loginData.getUsername());
		appUser.setId(loginData.getUserId());
		CaloriesRecordDto caloriesRecord = new CaloriesRecordDto();
		caloriesRecord.setId(caloriesRecordRepository.findAll().get(0).getId());
		caloriesRecord.setText("editedCaloriesRecord");
		caloriesRecord.setCaloriesQuantity(1500L);
		caloriesRecord.setDate(expectedDate);
		caloriesRecord.setTime(expectedTime);
		caloriesRecord.setUser(appUser);
		String caloriesRecordString = objectMapper.writeValueAsString(caloriesRecord);
		HttpEntity<String> entity = new HttpEntity<String>(caloriesRecordString, requestHeaders);
		ResponseEntity<CaloriesRecordDto> response = restTemplate.exchange(getCaloriesRecordUrl(), HttpMethod.PUT, entity, CaloriesRecordDto.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		CaloriesRecord caloriesRecordInDatabase = caloriesRecordRepository.findOne(caloriesRecord.getId());
		assertThat(caloriesRecordInDatabase.getText(), equalTo(caloriesRecord.getText()));
		assertThat(caloriesRecordInDatabase.getCaloriesQuantity(), equalTo(caloriesRecord.getCaloriesQuantity()));
		assertThat(caloriesRecordInDatabase.getDate(), equalTo(caloriesRecord.getDate()));
		assertThat(caloriesRecordInDatabase.getTime(), equalTo(caloriesRecord.getTime()));
		assertThat(caloriesRecordInDatabase.getUser().getId(), equalTo(caloriesRecord.getUser().getId()));
	}
	
	@Test
	public void testDeleteCaloriesRecord() {
		CaloriesRecord caloriesRecordInDatabase = caloriesRecordRepository.findAll().get(0);
		HttpEntity<String> entity = new HttpEntity<String>("parameters", requestHeaders);
		ResponseEntity<Boolean> response = restTemplate.exchange(getCaloriesRecordUrl() + "/" + caloriesRecordInDatabase.getId(), HttpMethod.DELETE, entity, Boolean.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getBody(), equalTo(true));
		caloriesRecordInDatabase = caloriesRecordRepository.findOne(caloriesRecordInDatabase.getId());
		assertThat(caloriesRecordInDatabase, nullValue());
	}
	
	private String getCaloriesRecordUrl() {
		return getApiUrl() + "/caloriesRecord";
	}
}
