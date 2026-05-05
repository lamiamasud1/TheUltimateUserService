package com.example.theultimateuser.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;

/**
 * Record representing user information that is searchable within the system.
 */
@JsonPropertyOrder({"id", "firstname", "lastname", "email", "profession", "dateCreated", "country", "city"})
public record UserDTO(
        Long id,
        String firstname,
        String lastname,
        String email,
        String profession,
        @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dateCreated,
        String country,
        String city
) {}
