package com.example.theultimateuser.dto;

import java.time.LocalDateTime;

//in a traditional class you have to manually write or have intellij getters, a constructor
//record does this all in one line
//data only carrier
public record UserDto(
        Long id,
        String firstName,
        String lastName,
        String profession,
        LocalDateTime createdBy
) {}
