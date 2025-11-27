package com.phegondev.InventoryMgtSystem.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phegondev.InventoryMgtSystem.dtos.LoginRequest;
import com.phegondev.InventoryMgtSystem.dtos.RegisterRequest;
import com.phegondev.InventoryMgtSystem.dtos.Response;
import com.phegondev.InventoryMgtSystem.enums.UserRole;
import com.phegondev.InventoryMgtSystem.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)  // 🔥 DISABLE ALL SECURITY FILTERS
@Import(AuthController.class)              // 🔥 ONLY LOAD THIS CONTROLLER
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // 🔥 Mock JwtUtils so security does NOT load the real bean
    @MockBean
    private com.phegondev.InventoryMgtSystem.security.JwtUtils jwtUtils;

    // 🔥 Mock CustomUserDetailsService to avoid loading AuthFilter
    @MockBean
    private com.phegondev.InventoryMgtSystem.security.CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_success() throws Exception {

        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("12345678");
        request.setPhoneNumber("9876543210");
        request.setRole(UserRole.ADMIN);

        Response response = Response.builder()
                .status(200)
                .message("User registered successfully")
                .build();

        Mockito.when(userService.registerUser(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void loginUser_success() throws Exception {

        LoginRequest login = new LoginRequest();
        login.setEmail("test@example.com");
        login.setPassword("12345678");

        Response response = Response.builder()
                .status(200)
                .message("Login successful")
                .token("fake-token")
                .role(UserRole.ADMIN)
                .build();

        Mockito.when(userService.loginUser(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.token").value("fake-token"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.status").value(200));
    }
}
