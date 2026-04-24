package com.example.theultimateuser.dto;

import java.time.LocalDate;

/**
 * Record representing user information that is searchable within the system.
 */
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
