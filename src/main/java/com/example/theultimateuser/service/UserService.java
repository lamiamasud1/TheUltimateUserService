package com.example.theultimateuser.service;

import com.example.theultimateuser.dto.UserDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class UserService {
    private final List<UserDTO> magMutualUserData = new ArrayList<>();

    @PostConstruct
    public void init() {
        loadCsvData();
    }

    public List<UserDTO> findUserInfoById(Long id) {
        return magMutualUserData.stream()
                .filter(user -> user.id().equals(id))
                .toList();
    }

    public List<UserDTO> findUserInfoInDateRange(String start, String end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(start, formatter);
        LocalDate endDate = LocalDate.parse(end, formatter);
        return magMutualUserData.stream()
                .filter(user -> {
                    LocalDate userDate = user.dateCreated();
                    if(startDate.isAfter(endDate)) {
                        throw new IllegalArgumentException("Start date should be before or equal to end date");
                    }
                    return (userDate.isEqual(startDate) || userDate.isAfter(startDate)) &&
                            (userDate.isEqual(endDate) || userDate.isBefore(endDate));
                })
                .sorted(Comparator.comparing(UserDTO::dateCreated))
                .toList();
    }

    public List<UserDTO> fullTextSearch(Map<String, String> searchFilters) {
        return magMutualUserData.stream()
                .filter(user -> matchesAllChecks(user, searchFilters))
                .toList();
    }

    private void loadCsvData() {
        try {
            ClassPathResource resource = new ClassPathResource("MagMutual User Information.csv");
            try (BufferedReader input = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                input.readLine();
                String line;
                while ((line = input.readLine()) != null) {
                    String[] userInfo = line.split(",");
                    DateTimeFormatter csvDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    UserDTO user = new UserDTO(
                            Long.parseLong(userInfo[0].trim()),
                            userInfo[1].trim().replace("\"", ""),
                            userInfo[2].trim().replace("\"", ""),
                            userInfo[3].trim().replace("\"", ""),
                            userInfo[4].trim().replace("\"", ""),
                            LocalDate.parse(userInfo[5].trim().replace("\"", ""), csvDateTimeFormatter),
                            userInfo[6].trim().replace("\"", ""),
                            userInfo[7].trim().replace("\"", "")
                    );
                    magMutualUserData.add(user);
                }
            }
            } catch (Exception e) {
                System.err.println("Failed to load file" + e.getMessage());
            }
        }

        private boolean matchesAllChecks(UserDTO user, Map<String, String> searchFilters) {
        return searchFilters.entrySet().stream()
                .filter(entry -> entry.getValue() !=null
                && !entry.getValue().isBlank())
                .allMatch(entry -> matchesSearchField(user, entry.getKey(), entry.getValue()));
        }

        private boolean matchesSearchField(UserDTO user, String key, String value) {
        String searchField = value.toLowerCase();
            return switch (key.toLowerCase()) {
                case "id" -> user.id().equals(Long.parseLong(value));
                case "firstname" -> user.firstname().toLowerCase().contains(searchField);
                case "lastname" -> user.lastname().toLowerCase().contains(searchField);
                case "email" -> user.email().toLowerCase().contains(searchField);
                case "profession" -> user.profession().toLowerCase().contains(searchField);
                case "datecreated" -> user.dateCreated().equals(LocalDate.parse(value));
                case "country" -> user.country().toLowerCase(Locale.ROOT).contains(searchField);
                case "city" -> user.city(). toLowerCase(Locale.ROOT).contains(searchField);
                default -> throw new IllegalStateException("Unexpected search value: " + key.toLowerCase());
            };
        }
    }

