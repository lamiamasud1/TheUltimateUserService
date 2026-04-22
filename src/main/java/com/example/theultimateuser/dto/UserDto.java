package com.example.theultimateuser.dto;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String firstName,
        String lastName,
        String profession,
        LocalDateTime createdBy
) {}
