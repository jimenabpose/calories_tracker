package com.jpose.caloriestracker.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
	
	public BadRequestException() {
		super();
	}
	
	public BadRequestException(Enum<?> errorCode) {
		super(EnumExceptionFormatter.formatEnum(errorCode));
	}
}