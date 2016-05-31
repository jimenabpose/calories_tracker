package com.jpose.caloriestracker.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
	
	public NotFoundException() {
		super();
	}
	
	public NotFoundException(Enum<?> errorCode) {
		super(EnumExceptionFormatter.formatEnum(errorCode));
	}
}