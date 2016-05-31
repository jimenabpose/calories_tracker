package com.jpose.caloriestracker.config;

import java.io.IOException;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class JsonJodaLocalDateSerializer extends JsonSerializer<LocalDate> {

	@Override
	public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider arg2) throws IOException, JsonProcessingException {
		gen.writeString(JacksonConfiguration.DATE_FORMATTER.print(value));
	}
}