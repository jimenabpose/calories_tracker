package com.jpose.caloriestracker;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpose.caloriestracker.repository.CaloriesRecordRepository;
import com.jpose.caloriestracker.repository.UserRepository;
import com.jpose.caloriestracker.web.dto.LoginResponseDto;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CaloriesTrackerApplication.class)
@WebIntegrationTest(randomPort = true)
@TestPropertySource("/test.properties")
@Ignore
public class CaloriesTrackerApplicationTests {
	
	@Value("${local.server.port}")
    private int port;
	RestTemplate restTemplate = new TestRestTemplate();
	
	@Autowired
	protected UserRepository userRepository;
	@Autowired
	protected CaloriesRecordRepository caloriesRecordRepository;
	@Autowired
	protected PasswordEncoder passwordEncoder;
	protected ObjectMapper objectMapper = new ObjectMapper();
	
	protected String getApiUrl() {
		return "http://localhost:" + port + "/api";
	}
	
	protected LoginResponseDto loginAndGetData(String username, String password) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("username", username);
		parameters.put("password", password);
		ResponseEntity<LoginResponseDto> response = restTemplate.postForEntity(getApiUrl() + "/authentication", parameters, LoginResponseDto.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		return response.getBody();
	}
	
	protected String getDefaultPageParameters() {
		return "?pageNumber=1&pageSize=10";
	}
}
