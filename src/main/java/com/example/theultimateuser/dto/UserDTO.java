package com.example.theultimateuser.dto;

import java.time.LocalDate;

public record UserDTO(
        Long id,
        String firstname,
        String lastname,
        String email,
        String profession,
        LocalDate dateCreated,
        String country,
        String city
) {}
