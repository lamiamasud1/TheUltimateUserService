package com.example.theultimateuser.service;

import com.example.theultimateuser.dto.UserDto;
import com.example.theultimateuser.dto.UserListDto;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private List<UserDto> mockUsers = new ArrayList<>();

    @PostConstruct
    public void init() {
        loadDataFromCsv();
    }


    public List<UserDto> findUserByUserId(Long id) {
        return mockUsers.stream()
                .filter(user -> user.id().equals(id))
                .toList();
    }

    public List<UserListDto> getUsersInDateRange(LocalDateTime start, LocalDateTime end) {
        return mockUsers.stream()
                .filter(u -> !u.createdBy().isBefore(start) && !u.createdBy().isAfter(end))
                .map(u -> new UserListDto(u.firstName(), u.lastName(), u.profession()))
                .toList();
    }


    public List<UserListDto> getUsersByProfession(String profession) {
        return mockUsers.stream()
                .filter(u -> u.profession().equalsIgnoreCase(profession))
                .map(u -> new UserListDto(u.firstName(), u.lastName(), u.profession()))
                .toList();
    }

    private void loadDataFromCsv() {
        try {
            ClassPathResource resource = new ClassPathResource("userdata.csv");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String line;
                br.readLine();
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");

                    UserDto user = new UserDto(
                            Long.parseLong(values[0].trim()),
                            values[1].trim(),
                            values[2].trim(),
                            values[3].trim(),
                            LocalDateTime.parse(values[4].trim().replace(" ", "T"))
                    );
                    mockUsers.add(user);
                }
            }

            } catch (Exception e) {
                System.err.println("failed to load csv file" + e.getMessage());
            }
        }
    }

