package com.employeeportal.service.registration;

import com.employeeportal.dto.registration.UserDto;

import java.util.List;

public interface UsersService {
    UserDto saveUsers(UserDto user);

    List<UserDto> getAllUsers();

    UserDto updateUsersById(Long usersId, UserDto users);
}
