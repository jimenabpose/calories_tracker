package com.jpose.caloriestracker.web.dto;

import lombok.Data;

@Data
public class UserSettingsDto {

    private Long quantityPerDay;
    private Long quantityForToday;
    
    public UserSettingsDto() {
    }
    
    public UserSettingsDto(Long quantityPerDay, Long quantityForToday) {
    	this.quantityPerDay = quantityPerDay;
    	this.quantityForToday = quantityForToday;
    }
}
