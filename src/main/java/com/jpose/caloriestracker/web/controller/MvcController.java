package com.jpose.caloriestracker.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MvcController {
	
	@RequestMapping(value = {"/{path:[^\\.]*}", "/{.*}/{path:[^\\.]*}", "/{.*}/{.*}/{path:[^\\.]*}"})
	public String redirect() {
	  return "forward:/";
	}
}