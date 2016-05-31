package com.jpose.caloriestracker.service;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.jpose.caloriestracker.entity.AppUser;
import com.jpose.caloriestracker.entity.CaloriesRecord;
import com.jpose.caloriestracker.repository.CaloriesRecordRepository;
import com.jpose.caloriestracker.service.exceptions.BadRequestException;
import com.jpose.caloriestracker.service.exceptions.ForbiddenException;
import com.jpose.caloriestracker.service.exceptions.NotFoundException;

@Service
public class CaloriesRecordService {
	
	public enum CaloriesRecordError {
		PAGE_SIZE_INVALID,
		UNAUTHORIZED_REQUEST,
		DATE_RANGE_INVALID,
		CALORIES_RECORD_NOT_FOUND,
		CALORIES_RECORD_CREATION_NO_ID,
		CALORIES_RECORD_QUANTITY_CAN_NOT_BE_EMPTY,
		CALORIES_RECORD_DATE_CAN_NOT_BE_EMPTY,
		CALORIES_RECORD_TIME_CAN_NOT_BE_EMPTY,
		CALORIES_RECORD_TEXT_CAN_NOT_BE_EMPTY
		;
	}
	
	private static final int MAX_PAGE_SIZE = 50;

	@Autowired
	private CaloriesRecordRepository caloriesRecordRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private AuthenticationService authenticationService;
	
	public Page<CaloriesRecord> findAll(Integer pageNumber, Integer pageSize, Long userId, LocalDate dateFrom, LocalDate dateTo, LocalTime timeFrom, LocalTime timeTo) {
		if (pageSize > MAX_PAGE_SIZE) {
			throw new BadRequestException(CaloriesRecordError.PAGE_SIZE_INVALID);
		}
		if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
			throw new BadRequestException(CaloriesRecordError.DATE_RANGE_INVALID);
		}
		AppUser appUser = null;
		if (userId != null) {
			appUser = userService.findOne(userId);
		}
		return caloriesRecordRepository.findAllByUser(appUser, dateFrom, dateTo, timeFrom, timeTo, new PageRequest(pageNumber - 1, pageSize));
	}
	
	public CaloriesRecord findOne(Long id) {
		CaloriesRecord caloriesRecord = caloriesRecordRepository.findOne(id);
		if (caloriesRecord == null) {
			throw new NotFoundException(CaloriesRecordError.CALORIES_RECORD_NOT_FOUND);
		}
		return caloriesRecord;
	}
	
	public CaloriesRecord update(CaloriesRecord caloriesRecord) {
		if (caloriesRecord.getId() == null) {
			throw new BadRequestException(CaloriesRecordError.CALORIES_RECORD_NOT_FOUND);
		}
		return this.save(caloriesRecord);
	}
	
	public CaloriesRecord create(CaloriesRecord caloriesRecord) {
		if (caloriesRecord.getId() != null) {
			throw new BadRequestException(CaloriesRecordError.CALORIES_RECORD_CREATION_NO_ID);
		}
		return this.save(caloriesRecord);
	}

	private CaloriesRecord save(CaloriesRecord caloriesRecord) {
		if (caloriesRecord.getUser() == null) {
			caloriesRecord.setUser(userService.getLoggedUser());
		}
		if (caloriesRecord.getCaloriesQuantity() == null) {
			throw new BadRequestException(CaloriesRecordError.CALORIES_RECORD_QUANTITY_CAN_NOT_BE_EMPTY);
		}
		if (caloriesRecord.getDate() == null) {
			throw new BadRequestException(CaloriesRecordError.CALORIES_RECORD_DATE_CAN_NOT_BE_EMPTY);
		}
		if (caloriesRecord.getText() == null) {
			throw new BadRequestException(CaloriesRecordError.CALORIES_RECORD_TEXT_CAN_NOT_BE_EMPTY);
		}
		if (caloriesRecord.getTime() == null) {
			throw new BadRequestException(CaloriesRecordError.CALORIES_RECORD_TIME_CAN_NOT_BE_EMPTY);
		}
		return caloriesRecordRepository.save(caloriesRecord);
	}

	public Boolean delete(Long id) {
		CaloriesRecord caloriesRecord = this.findOne(id);
		if (caloriesRecord == null) {
			throw new NotFoundException(CaloriesRecordError.CALORIES_RECORD_NOT_FOUND);
		}
		if (!authenticationService.canCrudOthersCaloriesRecords() && !caloriesRecord.getUser().getId().equals(userService.getLoggedUser().getId())) {
			throw new ForbiddenException(CaloriesRecordError.UNAUTHORIZED_REQUEST);
		}
		caloriesRecordRepository.delete(caloriesRecord);
		return true;
	}
	
	public Long getQuantityForDate(LocalDate date, AppUser user) {
		return caloriesRecordRepository.sumCalories(date, user);
	}
}
