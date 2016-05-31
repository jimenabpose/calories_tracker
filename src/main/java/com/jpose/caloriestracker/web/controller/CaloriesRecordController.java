package com.jpose.caloriestracker.web.controller;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jpose.caloriestracker.config.JacksonConfiguration;
import com.jpose.caloriestracker.entity.CaloriesRecord;
import com.jpose.caloriestracker.service.CaloriesRecordService;
import com.jpose.caloriestracker.web.dto.CaloriesRecordDto;

@RestController
@RequestMapping("/api/caloriesRecord")
public class CaloriesRecordController {

	@Autowired
	private CaloriesRecordService caloriesRecordService;
	@Autowired
	private ModelMapper mapper;

	@PreAuthorize("#user == principal.id or hasAuthority('CALORIES_RECORD_CRUD_OTHERS')")
	@RequestMapping(method = RequestMethod.GET)
	@Transactional(readOnly = true)
	@ResponseBody
	public Page<CaloriesRecordDto> getCaloriesRecords(
			@RequestParam(value = "pageNumber", required = true) Integer pageNumber,
			@RequestParam(value = "pageSize", required = true) Integer pageSize,
			@RequestParam(value = "user", required = false) Long user,
			@RequestParam(value = "dateFrom", required = false) @DateTimeFormat(pattern = JacksonConfiguration.DATE_FORMAT) LocalDate dateFrom,
			@RequestParam(value = "dateTo", required = false) @DateTimeFormat(pattern = JacksonConfiguration.DATE_FORMAT) LocalDate dateTo,
			@RequestParam(value = "timeFrom", required = false) @DateTimeFormat(pattern = JacksonConfiguration.TIME_FORMAT) LocalTime timeFrom,
			@RequestParam(value = "timeTo", required = false) @DateTimeFormat(pattern = JacksonConfiguration.TIME_FORMAT) LocalTime timeTo
		) {
		return caloriesRecordService.findAll(pageNumber, pageSize, user, dateFrom, dateTo, timeFrom, timeTo).map(
			record -> {return mapper.map(record, CaloriesRecordDto.class);}
		);
	}
	
	@PreAuthorize("hasAuthority('CALORIES_RECORD_CRUD_OWN') or hasAuthority('CALORIES_RECORD_CRUD_OTHERS')")
	@PostAuthorize("hasAuthority('CALORIES_RECORD_CRUD_OTHERS') or returnObject.user.id == principal.id")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	@ResponseBody
	public CaloriesRecordDto getCaloriesRecord(@PathVariable("id") Long id) {
		return mapper.map(caloriesRecordService.findOne(id), CaloriesRecordDto.class);
	}

	@PreAuthorize("hasAuthority('CALORIES_RECORD_CRUD_OTHERS') or (hasAuthority('CALORIES_RECORD_CRUD_OWN') AND #caloriesRecord.user.id == principal.id)")
	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	@ResponseBody
	public CaloriesRecordDto create(@RequestBody CaloriesRecordDto caloriesRecord) {
		return mapper.map(caloriesRecordService.create(mapper.map(caloriesRecord, CaloriesRecord.class)), CaloriesRecordDto.class);
	}

	@PreAuthorize("hasAuthority('CALORIES_RECORD_CRUD_OTHERS') or (hasAuthority('CALORIES_RECORD_CRUD_OWN') AND #caloriesRecord.user.id == principal.id)")
	@RequestMapping(method = RequestMethod.PUT)
	@Transactional
	@ResponseBody
	public CaloriesRecordDto update(@RequestBody CaloriesRecordDto caloriesRecord) {
		return mapper.map(caloriesRecordService.update(mapper.map(caloriesRecord, CaloriesRecord.class)), CaloriesRecordDto.class);
	}
	
	@PreAuthorize("hasAuthority('CALORIES_RECORD_CRUD_OWN') or hasAuthority('CALORIES_RECORD_CRUD_OTHERS')")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@Transactional
	@ResponseBody
	public Boolean delete(@PathVariable("id") Long id) {
		return caloriesRecordService.delete(id);
	}
}