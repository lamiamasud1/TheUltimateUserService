package com.example.theultimateuser.service;

import com.example.theultimateuser.dto.UserDTO;
import com.example.theultimateuser.repository.CsvUserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;


/**
 * Service handles user search operations, including
 * finding user info by certain criteria and full-text search.
 */
@Service
public class UserService {

    private final CsvUserRepository csvUserRepository;

    public UserService(CsvUserRepository csvUserRepository) {
        this.csvUserRepository = csvUserRepository;
    }

    public UserDTO findUserInfoById(Long id) {
        return csvUserRepository.readAllUsers().stream()
                .filter(user -> user.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public List<UserDTO> findUserInfoInDateRange(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new InvalidSearchCriteriaException("The start date (" + start + ") should be before or equal to the end date (" + end + ")");
        }
        return csvUserRepository.readAllUsers().stream()
                .filter(user -> !user.dateCreated().isBefore(start) && !user.dateCreated().isAfter(end))
                .sorted(Comparator.comparing(UserDTO::dateCreated))
                .toList();
    }

    public List<UserDTO> fullTextSearch(Map<String, String> searchFilters) {
        Map<String, String> sanitizedFilters = new HashMap<>();
        searchFilters.forEach((key, value) -> {
            if (value != null && !value.isBlank()) {
                sanitizedFilters.put(key.toLowerCase(), value.trim());
            }
        });
        searchFilterValidation(sanitizedFilters);
        return csvUserRepository.readAllUsers().stream()
                .filter(user -> matchesAllChecks(user, sanitizedFilters))
                .toList();
    }

    public UserDTO updateMultipleUserSearchFields(Long id, Map<String, String> fieldsToUpdate) {
        Map<String, String> sanitizedFields = new HashMap<>();
        fieldsToUpdate.forEach((key, value) -> {
            if (value != null && !value.isBlank()) {
                sanitizedFields.put(key.toLowerCase(), value.trim());
            }
        });

        if (sanitizedFields.containsKey("id")) {
            throw new ImmutableFieldUpdateException("ID cannot be modified.");
        } else if (sanitizedFields.containsKey("datecreated")) {
            throw new ImmutableFieldUpdateException("Date cannot be modified.");
        }
        UserDTO existingUserInfo = findUserInfoById(id);
        UserDTO updatedUserRecord = new UserDTO(
                existingUserInfo.id(),
                fieldsToUpdate.getOrDefault("firstname", existingUserInfo.firstname()),
                fieldsToUpdate.getOrDefault("lastname", existingUserInfo.lastname()),
                fieldsToUpdate.getOrDefault("email", existingUserInfo.email()),
                fieldsToUpdate.getOrDefault("profession", existingUserInfo.profession()),
                existingUserInfo.dateCreated(),
                fieldsToUpdate.getOrDefault("country", existingUserInfo.country()),
                fieldsToUpdate.getOrDefault("city", existingUserInfo.city())
        );
        applyUpdateToModifiedCsv(id, updatedUserRecord);
        return updatedUserRecord;
    }

    private void searchFilterValidation(Map<String, String> searchFilters) {
        List<String> allowedKeys = List.of("id", "datecreated", "firstname", "lastname", "email", "profession", "country", "city");
        for (String key : searchFilters.keySet()) {
            if (!allowedKeys.contains(key)) {
                throw new InvalidSearchCriteriaException("Unknown search filter: " + key);
            }
        }
    }

    private boolean matchesAllChecks(UserDTO user, Map<String, String> searchFilters) {
        return searchFilters.entrySet().stream()
                .allMatch(entry -> matchesSearchField(user, entry.getKey(), entry.getValue()));
    }

    private boolean matchesSearchField(UserDTO user, String key, String value) {
        String searchField = value.toLowerCase();
        return switch (key) {
            case "id" -> user.id().toString().equals(value);
            case "firstname" -> user.firstname().toLowerCase().contains(searchField);
            case "lastname" -> user.lastname().toLowerCase().contains(searchField);
            case "email" -> user.email().toLowerCase().contains(searchField);
            case "profession" -> user.profession().toLowerCase().contains(searchField);
            case "datecreated" -> user.dateCreated().toString().equals(value);
            case "country" -> user.country().toLowerCase(Locale.ROOT).contains(searchField);
            case "city" -> user.city().toLowerCase(Locale.ROOT).contains(searchField);
            default -> true;
        };
    }

    private void applyUpdateToModifiedCsv(Long id, UserDTO updatedUserInfo) {
        List<UserDTO> users = new ArrayList<>(csvUserRepository.readAllUsers());
        boolean userRemoved = users.removeIf(user -> user.id().equals(id));
        if (!userRemoved) {
            throw new UserNotFoundException(id);
        }
        users.add(updatedUserInfo);
        csvUserRepository.saveAllUsers(users);
    }

}

