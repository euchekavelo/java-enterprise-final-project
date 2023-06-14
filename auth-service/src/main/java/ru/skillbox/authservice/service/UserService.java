package ru.skillbox.authservice.service;

import ru.skillbox.authservice.dto.UserDto;
import ru.skillbox.authservice.exception.UserNotFoundException;
import ru.skillbox.authservice.model.User;

import java.util.List;

public interface UserService {

    User createUser(UserDto userDto);

    List<User> getAllUsers();

    User getUserByName(String username) throws UserNotFoundException;

    void deleteUserByName(String username);
}
