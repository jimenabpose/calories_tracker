package com.jpose.caloriestracker.web.controller;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jpose.caloriestracker.entity.AppUser;
import com.jpose.caloriestracker.entity.UserSettings;
import com.jpose.caloriestracker.service.CaloriesRecordService;
import com.jpose.caloriestracker.service.UserService;
import com.jpose.caloriestracker.web.dto.UserSettingsDto;

@RestController
@RequestMapping("/api/userSettings")
public class UserSettingsController {

	@Autowired
	private UserService userService;
	@Autowired
	private CaloriesRecordService caloriesRecordService;
	
	@PreAuthorize("hasAuthority('USER_CRUD_OWN') && hasAuthority('CALORIES_RECORD_CRUD_OWN')")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Long getUserSettings() {
		return userService.getLoggedUser().getUserSettings().getQuantityPerDay();
	}
	
	@PreAuthorize("hasAuthority('USER_CRUD_OWN') && hasAuthority('CALORIES_RECORD_CRUD_OWN')")
	@RequestMapping(value = "date", method = RequestMethod.GET)
	@ResponseBody
	public UserSettingsDto getUserSettingsForDate(LocalDate date) {
		Long quantityForDate = caloriesRecordService.getQuantityForDate(date, userService.getLoggedUser());
		return new UserSettingsDto(
			userService.getLoggedUser().getUserSettings().getQuantityPerDay(),
			quantityForDate != null ? quantityForDate : 0
		);
	}
	
	@PreAuthorize("hasAuthority('USER_CRUD_OWN') && hasAuthority('CALORIES_RECORD_CRUD_OWN')")
	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	public Long saveUserSettings(@RequestBody Long quantityPerDay) {
		AppUser user = userService.getLoggedUser();
		if (user.getUserSettings() == null) {
			user.setUserSettings(new UserSettings());
		}
		user.getUserSettings().setQuantityPerDay(quantityPerDay);
		user.getUserSettings().setUser(user);
		userService.save(user);
		return quantityPerDay;
	}
}