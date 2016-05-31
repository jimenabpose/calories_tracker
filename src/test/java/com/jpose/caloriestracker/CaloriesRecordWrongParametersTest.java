package com.jpose.caloriestracker;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIn.isIn;

import java.util.Arrays;
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
import com.jpose.caloriestracker.service.CaloriesRecordService.CaloriesRecordError;
import com.jpose.caloriestracker.service.exceptions.EnumExceptionFormatter;
import com.jpose.caloriestracker.web.dto.AppUserDto;
import com.jpose.caloriestracker.web.dto.CaloriesRecordDto;
import com.jpose.caloriestracker.web.dto.LoginResponseDto;

public class CaloriesRecordWrongParametersTest extends CaloriesTrackerApplicationTests {
	
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
	public void testGetCaloriesRecordsPageParameters() {
		HttpEntity<String> entity = new HttpEntity<String>("parameters", requestHeaders);
		ResponseEntity<Map> response = restTemplate.exchange(getCaloriesRecordUrl() + "?pageNumber=1&pageSize=100", HttpMethod.GET, entity, Map.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
		Map<String, String> content = (Map<String, String>) response.getBody();
		assertThat(content.get("message"), equalTo(EnumExceptionFormatter.formatEnum(CaloriesRecordError.PAGE_SIZE_INVALID)));
		ResponseEntity<Map> responseOk = restTemplate.exchange(getCaloriesRecordUrl() + "?pageNumber=100&pageSize=10", HttpMethod.GET, entity, Map.class);
		assertThat(responseOk.getStatusCode(), equalTo(HttpStatus.OK));
		List<Map<String, String>> contentOk = (List<Map<String, String>>) responseOk.getBody().get("content");
		assertThat(contentOk.size(), equalTo(0));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testGetInexistentCaloriesRecordFails() {
		HttpEntity<String> entity = new HttpEntity<String>("parameters", requestHeaders);
		ResponseEntity<Map> response = restTemplate.exchange(getCaloriesRecordUrl() + "/500", HttpMethod.GET, entity, Map.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
		Map<String, String> content = (Map<String, String>) response.getBody();
		assertThat(content.get("message"), equalTo(EnumExceptionFormatter.formatEnum(CaloriesRecordError.CALORIES_RECORD_NOT_FOUND)));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testAddEmptyCaloriesRecordFails() throws JsonProcessingException {
		List<String> caloriesRecordCreationErrors = Arrays.asList(new String[] {
			EnumExceptionFormatter.formatEnum(CaloriesRecordError.CALORIES_RECORD_NOT_FOUND),
			EnumExceptionFormatter.formatEnum(CaloriesRecordError.CALORIES_RECORD_QUANTITY_CAN_NOT_BE_EMPTY),
			EnumExceptionFormatter.formatEnum(CaloriesRecordError.CALORIES_RECORD_DATE_CAN_NOT_BE_EMPTY),
			EnumExceptionFormatter.formatEnum(CaloriesRecordError.CALORIES_RECORD_TIME_CAN_NOT_BE_EMPTY),
			EnumExceptionFormatter.formatEnum(CaloriesRecordError.CALORIES_RECORD_TEXT_CAN_NOT_BE_EMPTY)
		});
		CaloriesRecordDto caloriesRecordDto = new CaloriesRecordDto();
		String caloriesRecordDtoString = objectMapper.writeValueAsString(caloriesRecordDto);
		HttpEntity<String> entity = new HttpEntity<String>(caloriesRecordDtoString, requestHeaders);
		ResponseEntity<Map> response = restTemplate.exchange(getCaloriesRecordUrl(), HttpMethod.POST, entity, Map.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
		Map<String, String> content = (Map<String, String>) response.getBody();
		assertThat(content.get("message"), isIn(caloriesRecordCreationErrors));
	}
	
	@Test
	public void testAddOrEditCaloriesRecordWitNoTextFails() throws JsonProcessingException {
		CaloriesRecordDto caloriesRecord = getBaseCaloriesRecordDto();
		caloriesRecord.setText(null);
		assertBadCaloriesRecordRequest(caloriesRecord, HttpMethod.POST, CaloriesRecordError.CALORIES_RECORD_TEXT_CAN_NOT_BE_EMPTY);
		caloriesRecord.setId(1L);
		assertBadCaloriesRecordRequest(caloriesRecord, HttpMethod.PUT, CaloriesRecordError.CALORIES_RECORD_TEXT_CAN_NOT_BE_EMPTY);
	}
	
	@Test
	public void testAddOrEditCaloriesRecordWitNoCaloriesQuantityFails() throws JsonProcessingException {
		CaloriesRecordDto caloriesRecord = getBaseCaloriesRecordDto();
		caloriesRecord.setCaloriesQuantity(null);
		assertBadCaloriesRecordRequest(caloriesRecord, HttpMethod.POST, CaloriesRecordError.CALORIES_RECORD_QUANTITY_CAN_NOT_BE_EMPTY);
		caloriesRecord.setId(1L);
		assertBadCaloriesRecordRequest(caloriesRecord, HttpMethod.PUT, CaloriesRecordError.CALORIES_RECORD_QUANTITY_CAN_NOT_BE_EMPTY);
	}
	
	@Test
	public void testAddOrEditCaloriesRecordWitNoDateFails() throws JsonProcessingException {
		CaloriesRecordDto caloriesRecord = getBaseCaloriesRecordDto();
		caloriesRecord.setDate(null);
		assertBadCaloriesRecordRequest(caloriesRecord, HttpMethod.POST, CaloriesRecordError.CALORIES_RECORD_DATE_CAN_NOT_BE_EMPTY);
		caloriesRecord.setId(1L);
		assertBadCaloriesRecordRequest(caloriesRecord, HttpMethod.PUT, CaloriesRecordError.CALORIES_RECORD_DATE_CAN_NOT_BE_EMPTY);
	}
	
	@Test
	public void testAddOrEditCaloriesRecordWitNoTimeFails() throws JsonProcessingException {
		CaloriesRecordDto caloriesRecord = getBaseCaloriesRecordDto();
		caloriesRecord.setTime(null);
		assertBadCaloriesRecordRequest(caloriesRecord, HttpMethod.POST, CaloriesRecordError.CALORIES_RECORD_TIME_CAN_NOT_BE_EMPTY);
		caloriesRecord.setId(1L);
		assertBadCaloriesRecordRequest(caloriesRecord, HttpMethod.PUT, CaloriesRecordError.CALORIES_RECORD_TIME_CAN_NOT_BE_EMPTY);
	}
	
	@Test
	public void testDeleteInexistentCaloriesRecordFails() {
		HttpEntity<String> entity = new HttpEntity<String>("parameters", requestHeaders);
		ResponseEntity<String> response = restTemplate.exchange(getCaloriesRecordUrl() + "/500", HttpMethod.DELETE, entity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
	}

	private String getCaloriesRecordUrl() {
		return getApiUrl() + "/caloriesRecord";
	}
	
	private CaloriesRecordDto getBaseCaloriesRecordDto() {
		AppUserDto appUser = new AppUserDto();
		appUser.setUsername(loginData.getUsername());
		appUser.setId(loginData.getUserId());
		CaloriesRecordDto caloriesRecord = new CaloriesRecordDto();
		caloriesRecord.setText("newCaloriesRecord");
		caloriesRecord.setCaloriesQuantity(2000L);
		caloriesRecord.setDate(new LocalDate(2016, 5, 26));
		caloriesRecord.setTime(new LocalTime(13, 30));
		caloriesRecord.setUser(appUser);
		return caloriesRecord;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void assertBadCaloriesRecordRequest(CaloriesRecordDto caloriesRecordDto, HttpMethod method, Enum<?> expectedError) throws JsonProcessingException {
		String caloriesRecordString = objectMapper.writeValueAsString(caloriesRecordDto);
		HttpEntity<String> entity = new HttpEntity<String>(caloriesRecordString, requestHeaders);
		ResponseEntity<Map> response = restTemplate.exchange(getCaloriesRecordUrl(), method, entity, Map.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
		Map<String, String> content = (Map<String, String>) response.getBody();
		assertThat(content.get("message"), equalTo(EnumExceptionFormatter.formatEnum(expectedError)));
	}
}
