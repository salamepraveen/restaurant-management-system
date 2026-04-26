package com.prav.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prav.auth.dto.*;
import com.prav.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void signin_success() throws Exception {
        AuthResponse response = new AuthResponse();
        response.setToken("fake-token");
        response.setUsername("testuser");

        when(authService.signin(any(AuthRequest.class))).thenReturn(response);

        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("fake-token"))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    void signin_validationFailure() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername(""); // Blank username
        request.setPassword("123"); // Too short

        mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signupDirect_success() throws Exception {
        AuthResponse response = new AuthResponse();
        response.setToken("new-token");

        when(authService.signupDirect(any(AuthRequest.class))).thenReturn(response);

        AuthRequest request = new AuthRequest();
        request.setUsername("newuser");
        request.setPassword("password123");

        mockMvc.perform(post("/auth/signup/direct")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.token").value("new-token"));
    }

    @Test
    void requestSignupOtp_success() throws Exception {
        OtpRequestDTO request = new OtpRequestDTO();
        request.setUsername("user");
        request.setEmail("test@test.com");
        request.setPassword("password123");

        mockMvc.perform(post("/auth/signup/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP sent to your email. Please verify to complete signup."));
    }

    @Test
    void verifySignup_success() throws Exception {
        AuthResponse response = new AuthResponse();
        response.setToken("verified-token");

        when(authService.verifySignupAndCreate(any(OtpVerifyDTO.class))).thenReturn(response);

        OtpVerifyDTO request = new OtpVerifyDTO();
        request.setEmail("test@test.com");
        request.setOtp("123456");

        mockMvc.perform(post("/auth/signup/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.token").value("verified-token"));
    }

    @Test
    void requestLoginOtp_success() throws Exception {
        OtpLoginRequestDTO request = new OtpLoginRequestDTO();
        request.setUsername("testuser");

        mockMvc.perform(post("/auth/signin/otp/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP sent to your registered email."));
    }

    @Test
    void verifyLoginOtp_success() throws Exception {
        AuthResponse response = new AuthResponse();
        response.setToken("otp-login-token");

        when(authService.verifyLoginOtp(any(OtpVerifyDTO.class))).thenReturn(response);

        OtpVerifyDTO request = new OtpVerifyDTO();
        request.setEmail("test@test.com");
        request.setOtp("123456");

        mockMvc.perform(post("/auth/signin/otp/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("otp-login-token"));
    }
}
