package com.example.theultimateuser.repository;

import com.example.theultimateuser.dto.UserDTO;

import java.io.IOException;
import java.util.List;

/**
 * Repository interface for managing persistent user data
 * This is the abstraction layer between business logic and the CSV storage
 */
public interface UserRepository {
    List<UserDTO> readAllUsers();
    void saveAllUsers(List<UserDTO> userDTOs) throws IOException;
}
