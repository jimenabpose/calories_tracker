package com.jpose.caloriestracker.repository;

import org.springframework.data.repository.CrudRepository;

import com.jpose.caloriestracker.entity.Privilege;

public interface PrivilegeRepository extends CrudRepository<Privilege, Long> {
	public Privilege findByName(String name);
}