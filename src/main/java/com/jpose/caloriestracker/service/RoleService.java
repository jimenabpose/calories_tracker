package com.jpose.caloriestracker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jpose.caloriestracker.entity.ComboData;
import com.jpose.caloriestracker.entity.Role;
import com.jpose.caloriestracker.repository.RoleRepository;

@Service
public class RoleService {
	
	@Autowired
	private RoleRepository roleRepository;
	
	public Role findByName(String name) {
		return roleRepository.findByName(name);
	}
	
	public Role findOne(Long id) {
		return roleRepository.findOne(id);
	}
	
	public List<ComboData> findAll() {
		return roleRepository.findAllForCombo();
	}
	
}
