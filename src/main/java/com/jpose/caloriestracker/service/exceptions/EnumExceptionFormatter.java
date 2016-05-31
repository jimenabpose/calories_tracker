package com.jpose.caloriestracker.service.exceptions;

public class EnumExceptionFormatter {

	public static String formatEnum(Enum<?> enumValue) {
		return String.format("%s_%s", enumValue.getClass().getSimpleName(), enumValue.name());
	}
}
