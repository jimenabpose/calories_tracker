package com.jpose.caloriestracker.utils;

import com.jpose.caloriestracker.web.dto.AppUserDto;

public class AppUserDtoWithVisiblePassword extends AppUserDto {

	@SuppressWarnings("unused")
    private String password;
    @SuppressWarnings("unused")
	private String passwordConfirmation;
}
