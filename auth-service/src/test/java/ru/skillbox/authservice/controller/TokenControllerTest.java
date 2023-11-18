package ru.skillbox.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.skillbox.authservice.dto.UserDto;
import ru.skillbox.authservice.model.User;
import ru.skillbox.authservice.repository.UserRepository;
import ru.skillbox.authservice.security.SecurityConfiguration;
import ru.skillbox.authservice.service.AuthService;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(TokenController.class)
class TokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @Configuration
    @ComponentScan(basePackageClasses = {TokenController.class, SecurityConfiguration.class})
    public static class TestConf {
    }

    @Test
    void generateTokenSuccessTest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Test name");
        userDto.setPassword("Test password");

        User user = new User();
        user.setId(1L);
        user.setName(userDto.getName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        when(userRepository.findByName(userDto.getName())).thenReturn(Optional.of(user));
        when(authService.generateTokenForAuthUser(userDto)).thenReturn("test token");
        mockMvc.perform(
                        post("/token/generate")
                                .with(csrf())
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }
}