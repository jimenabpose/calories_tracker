package com.jpose.caloriestracker.web.dto;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Data;

@Data
public class AppUserDto {

    private Long id;
    private String username;
    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;
    @JsonProperty(access = Access.WRITE_ONLY)
    private String passwordConfirmation;
    private Boolean enabled;
    private Collection<Long> rolesList;
}
