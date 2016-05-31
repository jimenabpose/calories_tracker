package com.jpose.caloriestracker.entity;

import lombok.Data;

@Data
public class ComboData {
	private Long id;
	private String description;
	
	public ComboData() {
	}
	
	public ComboData(Long id, String description) {
		this.id = id;
		this.description = description;
	}
}
