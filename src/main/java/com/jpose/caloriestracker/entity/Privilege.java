package com.jpose.caloriestracker.entity;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.Data;

@Entity
@Data
public class Privilege {
	public static final String USER_CRUD_OTHERS = "USER_CRUD_OTHERS";
	public static final String USER_CRUD_OWN = "USER_CRUD_OWN";
	public static final String CALORIES_RECORD_CRUD_OTHERS = "CALORIES_RECORD_CRUD_OTHERS";
	public static final String CALORIES_RECORD_CRUD_OWN = "CALORIES_RECORD_CRUD_OWN";
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @ManyToMany(mappedBy = "privileges")
    private Collection<Role> roles;
    
    public Privilege() {
    }
    
    public Privilege (String name) {
    	this.name = name;
    }
}