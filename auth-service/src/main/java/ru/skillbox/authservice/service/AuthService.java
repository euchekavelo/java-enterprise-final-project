package ru.skillbox.authservice.service;

import org.springframework.security.core.AuthenticationException;
import ru.skillbox.authservice.dto.UserDto;

public interface AuthService {

    String generateTokenForAuthUser(UserDto loginUser) throws AuthenticationException;
}
