package com.prav.auth.service;

import com.prav.auth.client.UserClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private OtpService otpService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(otpService, "fromEmail", "test@pizza.com");
        ReflectionTestUtils.setField(otpService, "expiryMinutes", 5);
        ReflectionTestUtils.setField(otpService, "otpLength", 6);
    }

    @Test
    void generateSignupOtp_success() {
        when(userClient.getUserByEmail(anyString())).thenThrow(new RuntimeException("404 Not Found"));
        
        String otp = otpService.generateSignupOtp("test@test.com", "user", "pass");
        
        assertNotNull(otp);
        assertEquals(6, otp.length());
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void generateSignupOtp_emailAlreadyRegistered_throwsException() {
        when(userClient.getUserByEmail("existing@test.com")).thenReturn(new com.prav.auth.dto.UserDTO());
        
        assertThrows(com.prav.common.exception.ConflictException.class, () -> otpService.generateSignupOtp("existing@test.com", "user", "pass"));
    }

    @Test
    void verifySignupOtp_success() {
        when(userClient.getUserByEmail(anyString())).thenThrow(new RuntimeException("404 Not Found"));
        String otp = otpService.generateSignupOtp("test@test.com", "user", "pass");
        
        OtpService.SignupData data = otpService.verifySignupOtp("test@test.com", otp);
        
        assertNotNull(data);
        assertEquals("user", data.getUsername());
        assertEquals("pass", data.getPassword());
    }

    @Test
    void verifySignupOtp_wrongPurpose_throwsException() {
        otpService.generateLoginOtp("test@test.com");
        java.util.Map<String, Object> store = (java.util.Map<String, Object>) ReflectionTestUtils.getField(otpService, "otpStore");
        Object entry = store.get("test@test.com");
        String otp = (String) ReflectionTestUtils.getField(entry, "otp");
        
        assertThrows(RuntimeException.class, () -> otpService.verifySignupOtp("test@test.com", otp));
    }

    @Test
    void verifySignupOtp_expired_throwsException() {
        when(userClient.getUserByEmail(anyString())).thenThrow(new RuntimeException("404 Not Found"));
        String otp = otpService.generateSignupOtp("test@test.com", "user", "pass");
        
        java.util.Map<String, Object> store = (java.util.Map<String, Object>) ReflectionTestUtils.getField(otpService, "otpStore");
        Object entry = store.get("test@test.com");
        ReflectionTestUtils.setField(entry, "expiresAt", java.time.LocalDateTime.now().minusSeconds(1));
        
        assertThrows(RuntimeException.class, () -> otpService.verifySignupOtp("test@test.com", otp));
    }

    @Test
    void verifySignupOtp_invalidOtp_throwsException() {
        when(userClient.getUserByEmail(anyString())).thenThrow(new RuntimeException("404 Not Found"));
        otpService.generateSignupOtp("test@test.com", "user", "pass");
        
        assertThrows(RuntimeException.class, () -> otpService.verifySignupOtp("test@test.com", "000000"));
    }

    @Test
    void generateLoginOtp_success() {
        otpService.generateLoginOtp("login@test.com");
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void verifyLoginOtp_success() {
        otpService.generateLoginOtp("login@test.com");
        // We can't easily get the OTP because it's random, but we can mock generateOtp or just check store
        // Actually, let's use reflection to get it from the map for testing
        java.util.Map<String, Object> store = (java.util.Map<String, Object>) ReflectionTestUtils.getField(otpService, "otpStore");
        Object entry = store.get("login@test.com");
        String otp = (String) ReflectionTestUtils.getField(entry, "otp");
        
        otpService.verifyLoginOtp("login@test.com", otp);
        assertNull(store.get("login@test.com"));
    }
}
