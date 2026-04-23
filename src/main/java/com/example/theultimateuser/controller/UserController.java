package com.example.theultimateuser.controller;
import com.example.theultimateuser.dto.UserDTO;
import com.example.theultimateuser.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity <List<UserDTO>> getUserById(@PathVariable Long id) {
        List<UserDTO> userById = userService.findUserInfoById(id);
        if(userById.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userById);
    }

    @GetMapping("/userList")
    public ResponseEntity<List<UserDTO>> getUserInfoByDateRange(@RequestParam String startDate, String endDate) {
        List<UserDTO> userResultsByDate =  userService.findUserInfoInDateRange(startDate, endDate);
        return ResponseEntity.ok(userResultsByDate);
    }

    @GetMapping("/searchByField")
    public ResponseEntity<List<UserDTO>> getStudentsByField(@RequestParam Map<String,String> userSearchParams) {
        List<UserDTO> searchResults = userService.fullTextSearch(userSearchParams);
        return ResponseEntity.ok(searchResults);
    }

}

