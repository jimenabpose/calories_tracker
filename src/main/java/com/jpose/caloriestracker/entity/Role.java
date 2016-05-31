package com.jpose.caloriestracker.entity;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import lombok.Data;

@Entity
@Data
public class Role {
	
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_USER_MANAGER = "ROLE_USER_MANAGER";
	public static final String ROLE_USER = "ROLE_USER";
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @ManyToMany(mappedBy = "roles")
    private Collection<AppUser> users;
 
    @ManyToMany
    @JoinTable(
        name = "roles_privileges", 
        joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"), 
        inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id"))
    private Collection<Privilege> privileges;
    
    public Role() {
    }
    
    public Role(String name) {
    	this.name = name;
    }
}