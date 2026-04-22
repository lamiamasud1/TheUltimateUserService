package com.example.theultimateuser.controller;


import com.example.theultimateuser.dto.UserDto;
import com.example.theultimateuser.service.UserService;
import org.springframework.web.bind.annotation.*;

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
}
