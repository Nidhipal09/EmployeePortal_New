package com.employeeportal.controller.registration;

import com.employeeportal.dto.registration.UserDto;
import com.employeeportal.service.registration.UsersService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UsersController {
    @Autowired
    private UsersService usersService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN')")
    public ResponseEntity<UserDto> saveUsers(@RequestBody UserDto user) {
        UserDto addUsers = usersService.saveUsers(user);
        return new ResponseEntity<>(addUsers, HttpStatus.CREATED);

    }

    @GetMapping("/allUsers")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> allUsers = usersService.getAllUsers();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @PutMapping("/update/{usersId}")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_ADMIN')")
    public ResponseEntity<UserDto> updateUsersById(@PathVariable("usersId") Long usersId, @RequestBody UserDto user) {
        UserDto updatedUsers = usersService.updateUsersById(usersId, user);
        return new ResponseEntity<>(updatedUsers, HttpStatus.OK);
    }
}
