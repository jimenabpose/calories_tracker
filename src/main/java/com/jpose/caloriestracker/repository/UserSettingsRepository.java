package com.jpose.caloriestracker.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.jpose.caloriestracker.entity.UserSettings;

@Transactional
public interface UserSettingsRepository extends CrudRepository<UserSettings, Long> {
}