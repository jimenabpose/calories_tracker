package com.jpose.caloriestracker.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {
	
	public ForbiddenException() {
		super();
	}
	
	public ForbiddenException(Enum<?> errorCode) {
		super(EnumExceptionFormatter.formatEnum(errorCode));
	}
}