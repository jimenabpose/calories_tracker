package com.jpose.caloriestracker.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jpose.caloriestracker.entity.AppUser;
import com.jpose.caloriestracker.entity.ComboData;

public interface UserRepository extends JpaRepository<AppUser, Long> {
	
	public AppUser findByUsername(String username);
	
	public AppUser findByUsernameAndEnabled(String username, Boolean enabled);
	
	public Page<AppUser> findByUsernameContaining(String username, Pageable pageable);
	
	public Page<AppUser> findByUsernameContainingAndEnabled(String username, Boolean enabled, Pageable pageable);
	
	@Query("select new com.jpose.caloriestracker.entity.ComboData(user.id, user.username) from AppUser user")
	public List<ComboData> findIdsAndUsernames();
}