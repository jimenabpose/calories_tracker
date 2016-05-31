package com.jpose.caloriestracker.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import lombok.Data;

@Entity
@Data
public class CaloriesRecord {

	private @Id @GeneratedValue Long id;
	private String text;
	private Long caloriesQuantity;
	private LocalDate date;
	private LocalTime time;
	
	@ManyToOne(optional = false)
	private AppUser user;
	
	public CaloriesRecord() {
    }
    
    public CaloriesRecord(Long id) {
    	this.id = id;
    }
}
