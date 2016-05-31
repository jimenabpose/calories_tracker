package com.jpose.caloriestracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jpose.caloriestracker.entity.ComboData;
import com.jpose.caloriestracker.entity.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
	public Role findByName(String name);
	
	@Query("select new com.jpose.caloriestracker.entity.ComboData(role.id, role.name) from Role role")
	public List<ComboData> findAllForCombo();
}