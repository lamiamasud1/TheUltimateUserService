package com.example.theultimateuser.controller;
import com.example.theultimateuser.dto.UserDTO;
import com.example.theultimateuser.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST controller for accessing user search endpoints.
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
       return userService.findUserInfoById(id);
    }

    @GetMapping("/dateRange")
    public List<UserDTO> getUserInfoByDateRange(@RequestParam LocalDate startDate, LocalDate endDate) {
        return userService.findUserInfoInDateRange(startDate, endDate);
    }

    @GetMapping("/searchByField")
    public List<UserDTO> getStudentsByField(@RequestParam Map<String,String> userSearchParams)  {
        return userService.fullTextSearch(userSearchParams);
    }

    @PatchMapping("/{id}")
    public UserDTO updateUserInfo(@PathVariable Long id, @RequestBody Map<String, String> fieldsToUpdate) {
       return  userService.updateMultipleUserSearchFields(id, fieldsToUpdate);
    }

}

