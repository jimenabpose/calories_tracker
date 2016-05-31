package com.jpose.caloriestracker.config;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.datatype.joda.JodaModule;

@Configuration
public class JacksonConfiguration {
	
	public static final String DATE_FORMAT = "YYYY-MM-dd";
	public static final String TIME_FORMAT = "HH:mm";
	static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(DATE_FORMAT);
	static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern(TIME_FORMAT);
	
    @Bean
    public JodaModule jacksonJodaModule() {
        JodaModule module = new JodaModule();
        module.addSerializer(LocalDate.class, new JsonJodaLocalDateSerializer());
        module.addDeserializer(LocalDate.class, new JsonJodaLocalDateDeserializer());
        module.addSerializer(LocalTime.class, new JsonJodaLocalTimeSerializer());
        module.addDeserializer(LocalTime.class, new JsonJodaLocalTimeDeserializer());
        return module;
    }
}