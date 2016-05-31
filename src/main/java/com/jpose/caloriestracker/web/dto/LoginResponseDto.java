package com.jpose.caloriestracker.web.dto;

import java.util.List;

import lombok.Data;

@Data
public class LoginResponseDto {

    private Long userId;
    private String username;
    private String token;
    private Long expiration;
    private List<String> authorities;
}
