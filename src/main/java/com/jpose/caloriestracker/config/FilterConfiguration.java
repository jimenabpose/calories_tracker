package com.jpose.caloriestracker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class FilterConfiguration {
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Bean
    public FilterRegistrationBean jwtFilter() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new JwtFilter(userDetailsService));
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }
}