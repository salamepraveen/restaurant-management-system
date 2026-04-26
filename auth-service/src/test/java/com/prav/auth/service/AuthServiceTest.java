package com.prav.auth.service;

import com.prav.auth.client.UserClient;
import com.prav.auth.dto.*;
import com.prav.auth.exception.InvalidCredentialsException;
import com.prav.auth.util.JwtUtil;
import com.prav.common.exception.ConflictException;
import com.prav.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserClient userClient;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private OtpService otpService;

    @InjectMocks
    private AuthService authService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private UserDTO testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserDTO();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword(encoder.encode("password123"));
        testUser.setRole("USER");
    }

    @Test
    void signin_success() {
        when(userClient.getUserByUsername("testuser")).thenReturn(testUser);
        when(jwtUtil.generateToken(testUser)).thenReturn("fake-jwt-token");

        AuthRequest req = new AuthRequest();
        req.setUsername("testuser");
        req.setPassword("password123");

        AuthResponse response = authService.signin(req);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
        assertEquals("testuser", response.getUsername());
        verify(userClient).getUserByUsername("testuser");
    }

    @Test
    void signin_invalidUser_throwsException() {
        when(userClient.getUserByUsername("ghost")).thenThrow(new RuntimeException("Not found"));

        AuthRequest req = new AuthRequest();
        req.setUsername("ghost");
        req.setPassword("password123");

        assertThrows(InvalidCredentialsException.class, () -> authService.signin(req));
    }

    @Test
    void signin_wrongPassword_throwsException() {
        when(userClient.getUserByUsername("testuser")).thenReturn(testUser);

        AuthRequest req = new AuthRequest();
        req.setUsername("testuser");
        req.setPassword("wrong-pass");

        assertThrows(InvalidCredentialsException.class, () -> authService.signin(req));
    }

    @Test
    void signupDirect_success() {
        when(userClient.getUserByUsername(anyString())).thenThrow(new RuntimeException("404 Not Found"));
        when(userClient.createUser(any(UserDTO.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(any(UserDTO.class))).thenReturn("new-token");

        AuthRequest req = new AuthRequest();
        req.setUsername("newuser");
        req.setPassword("password123");
        req.setEmail("new@test.com");

        AuthResponse response = authService.signupDirect(req);

        assertNotNull(response);
        assertEquals("new-token", response.getToken());
        verify(userClient).createUser(any(UserDTO.class));
    }

    @Test
    void requestSignupOtp_success() {
        when(userClient.getUserByUsername("newuser")).thenThrow(new RuntimeException("404"));
        
        OtpRequestDTO req = new OtpRequestDTO();
        req.setUsername("newuser");
        req.setEmail("new@test.com");
        req.setPassword("pass123");

        authService.requestSignupOtp(req);

        verify(otpService).generateSignupOtp("new@test.com", "newuser", "pass123");
    }

    @Test
    void requestSignupOtp_conflict() {
        when(userClient.getUserByUsername("existing")).thenReturn(testUser);
        
        OtpRequestDTO req = new OtpRequestDTO();
        req.setUsername("existing");

        assertThrows(ConflictException.class, () -> authService.requestSignupOtp(req));
    }

    @Test
    void verifySignupAndCreate_success() {
        OtpService.SignupData signupData = new OtpService.SignupData("user", "pass", "email@test.com");
        when(otpService.verifySignupOtp("email@test.com", "123456")).thenReturn(signupData);
        when(userClient.createUser(any(UserDTO.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(testUser)).thenReturn("tk");

        OtpVerifyDTO req = new OtpVerifyDTO();
        req.setEmail("email@test.com");
        req.setOtp("123456");

        AuthResponse response = authService.verifySignupAndCreate(req);

        assertNotNull(response);
        assertEquals("tk", response.getToken());
    }

    @Test
    void requestLoginOtp_success() {
        when(userClient.getUserByUsername("testuser")).thenReturn(testUser);
        testUser.setEmail("test@test.com");

        OtpLoginRequestDTO req = new OtpLoginRequestDTO();
        req.setUsername("testuser");

        authService.requestLoginOtp(req);

        verify(otpService).generateLoginOtp("test@test.com");
    }

    @Test
    void requestLoginOtp_userNotFound() {
        when(userClient.getUserByUsername("ghost")).thenThrow(new RuntimeException("404"));
        OtpLoginRequestDTO req = new OtpLoginRequestDTO();
        req.setUsername("ghost");
        assertThrows(ResourceNotFoundException.class, () -> authService.requestLoginOtp(req));
    }

    @Test
    void requestLoginOtp_noEmail() {
        testUser.setEmail(null);
        when(userClient.getUserByUsername("testuser")).thenReturn(testUser);
        OtpLoginRequestDTO req = new OtpLoginRequestDTO();
        req.setUsername("testuser");
        assertThrows(RuntimeException.class, () -> authService.requestLoginOtp(req));
    }

    @Test
    void verifyLoginOtp_success() {
        when(userClient.getUserByEmail("test@test.com")).thenReturn(testUser);
        when(jwtUtil.generateToken(testUser)).thenReturn("tk");

        OtpVerifyDTO req = new OtpVerifyDTO();
        req.setEmail("test@test.com");
        req.setOtp("123456");

        AuthResponse response = authService.verifyLoginOtp(req);

        assertNotNull(response);
        assertEquals("tk", response.getToken());
        verify(otpService).verifyLoginOtp("test@test.com", "123456");
    }

    @Test
    void verifyLoginOtp_invalidEmail() {
        when(userClient.getUserByEmail("bad@test.com")).thenThrow(new RuntimeException("404"));
        OtpVerifyDTO req = new OtpVerifyDTO();
        req.setEmail("bad@test.com");
        req.setOtp("123456");
        assertThrows(InvalidCredentialsException.class, () -> authService.verifyLoginOtp(req));
    }
}
