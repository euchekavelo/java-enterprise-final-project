package ru.skillbox.authservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skillbox.authservice.repository.UserRepository;
import ru.skillbox.authservice.service.UserService;
import ru.skillbox.authservice.service.UserServiceImpl;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    public UserRepository userRepository() {
        return mock(UserRepository.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return mock(PasswordEncoder.class);
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl(userRepository(), passwordEncoder());
    }
}
