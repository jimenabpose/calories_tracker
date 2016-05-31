package com.jpose.caloriestracker.repository;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jpose.caloriestracker.entity.AppUser;
import com.jpose.caloriestracker.entity.CaloriesRecord;

public interface CaloriesRecordRepository extends JpaRepository<CaloriesRecord, Long>  {
	
	@Query("SELECT record FROM CaloriesRecord record "
		+ "	WHERE (:user IS NULL OR record.user = :user) "
		+ "		AND (:dateFrom IS NULL OR :dateFrom <= record.date)"
		+ "		AND (:dateTo IS NULL OR record.date <= :dateTo)"
		+ "		AND (:timeFrom IS NULL OR :timeFrom <= record.time)"
		+ "		AND (:timeTo IS NULL OR record.time <= :timeTo)"
		+ "     ORDER BY record.date DESC"
	)
	Page<CaloriesRecord> findAllByUser(@Param("user") AppUser user, 
		@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo, 
		@Param("timeFrom") LocalTime timeFrom, @Param("timeTo") LocalTime timeTo, Pageable pageable);
	
	@Query("SELECT SUM(caloriesQuantity) FROM CaloriesRecord WHERE user = :user AND date = :date")
	Long sumCalories(@Param("date") LocalDate date, @Param("user") AppUser user);
}
