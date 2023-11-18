package ru.skillbox.authservice.service;

import ru.skillbox.authservice.dto.UserDto;
import ru.skillbox.authservice.model.User;

public interface UserService {

    User createUser(UserDto userDto);
}
