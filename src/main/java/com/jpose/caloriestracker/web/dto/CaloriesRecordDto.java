package com.jpose.caloriestracker.web.dto;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jpose.caloriestracker.config.JsonJodaLocalDateDeserializer;
import com.jpose.caloriestracker.config.JsonJodaLocalDateSerializer;
import com.jpose.caloriestracker.config.JsonJodaLocalTimeDeserializer;
import com.jpose.caloriestracker.config.JsonJodaLocalTimeSerializer;

import lombok.Data;

@Data
public class CaloriesRecordDto {

	private Long id;
	private String text;
	private Long caloriesQuantity;
	@JsonSerialize(using=JsonJodaLocalDateSerializer.class)
	@JsonDeserialize(using=JsonJodaLocalDateDeserializer.class)
	private LocalDate date;
	@JsonSerialize(using=JsonJodaLocalTimeSerializer.class)
	@JsonDeserialize(using=JsonJodaLocalTimeDeserializer.class)
	private LocalTime time;
	private AppUserDto user;
}
