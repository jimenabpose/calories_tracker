package com.jpose.caloriestracker.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;

@Entity
@Data
public class UserSettings {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
    private Long quantityPerDay = 0L;
    
    @OneToOne(mappedBy = "userSettings")
	private AppUser user;
}
