package com.example.theultimateuser.controller;


import com.example.theultimateuser.dto.UserDto;
import com.example.theultimateuser.dto.UserListDto;
import com.example.theultimateuser.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public List<UserDto> getUserById(@PathVariable Long id) {
        return userService.findUserByUserId(id);
    }

    @GetMapping("/profession?{profession}")
    public List<UserListDto> getUserByProfession(@PathVariable String profession) {
        return userService.getUsersByProfession(profession);
    }

    @GetMapping("/userList")
    public List<UserListDto> getUserList(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime start,
                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime end) {
        return userService.getUsersInDateRange(start, end);
    }
}
