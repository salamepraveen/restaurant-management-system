package com.prav.auth.service;

import com.prav.common.exception.ConflictException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.prav.auth.client.UserClient;

@Service
public class OtpService {
 
    private static final Logger log = LoggerFactory.getLogger(OtpService.class);
    private static final java.security.SecureRandom secureRandom = new java.security.SecureRandom();

    private final JavaMailSender mailSender;
    private final UserClient userClient;
    private final String fromEmail;
    private final int expiryMinutes;
    private final int otpLength;

    public OtpService(
            JavaMailSender mailSender,
            UserClient userClient,
            @Value("${spring.mail.username}") String fromEmail,
            @Value("${otp.expiry.minutes:5}") int expiryMinutes,
            @Value("${otp.length:6}") int otpLength) {
        this.mailSender = mailSender;
        this.userClient = userClient;
        this.fromEmail = fromEmail;
        this.expiryMinutes = expiryMinutes;
        this.otpLength = otpLength;
    }

    // In-memory OTP store — key: email, value: OtpEntry
    private final Map<String, OtpEntry> otpStore = new HashMap<>();

    // ========== SIGNUP OTP ==========

    public String generateSignupOtp(String email, String username, String password) {
        // ✅ Check duplicate email via Feign — user-service has the DB
        try {
            Object existing = userClient.getUserByEmail(email);
            if (existing != null) {
                throw new ConflictException("Email already registered: " + email);
            }
        } catch (ConflictException e) {
            throw e;
        } catch (Exception e) {
            // 404 means email not found — that's good, proceed
            if (e.getMessage() != null && !e.getMessage().contains("404") && !e.getMessage().contains("Not Found")) {
                // user-service might be down, log warning but allow signup
                log.info("  [WARN] Could not check duplicate email (user-service may be down): {}", e.getMessage());
            }
        }

        // Generate OTP
        String otp = generateOtp();

        // ✅ Print OTP to console
        log.info("\n╔════════════════════════════════════════════╗");
        log.info("  📧 OTP for {} : {}", email, otp);
        log.info("╚════════════════════════════════════════════╝\n");

        // ✅ Store OTP — using otpStore, not otpCache
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expiryMinutes);
        otpStore.put(email, new OtpEntry(otp, expiresAt, username, password, "SIGNUP"));

        // ✅ Send email with correct 3-arg method
        try {
            sendOtpEmail(email, otp, "Your Pizza App Signup OTP");
        } catch (Exception e) {
            log.info("  [WARN] Email send failed (OTP still valid in console): {}", e.getMessage());
        }

        return otp;
    }

    public SignupData verifySignupOtp(String email, String otp) {
        OtpEntry entry = otpStore.get(email);

        if (entry == null) {
            throw new RuntimeException("No OTP requested for this email");
        }
        if (!"SIGNUP".equals(entry.purpose)) {
            throw new RuntimeException("Invalid OTP purpose");
        }
        if (entry.expiresAt.isBefore(LocalDateTime.now())) {
            otpStore.remove(email);
            throw new RuntimeException("OTP expired. Please request a new one");
        }
        if (!entry.otp.equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        // Valid — return pending data and remove entry
        otpStore.remove(email);
        return new SignupData(entry.username, entry.password, email);
    }

    // ========== LOGIN OTP ==========

    public void generateLoginOtp(String email) {
        String otp = generateOtp();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expiryMinutes);

        otpStore.put(email, new OtpEntry(otp, expiresAt, null, null, "LOGIN"));

        // ✅ Print login OTP to console too
        log.info("\n╔════════════════════════════════════════════╗");
        log.info("  📧 LOGIN OTP for {} : {}", email, otp);
        log.info("╚════════════════════════════════════════════╝\n");

        sendOtpEmail(email, otp, "Your Pizza App Login OTP");
    }

    public void verifyLoginOtp(String email, String otp) {
        OtpEntry entry = otpStore.get(email);

        if (entry == null) {
            throw new RuntimeException("No OTP requested for this email");
        }
        if (!"LOGIN".equals(entry.purpose)) {
            throw new RuntimeException("Invalid OTP purpose");
        }
        if (entry.expiresAt.isBefore(LocalDateTime.now())) {
            otpStore.remove(email);
            throw new RuntimeException("OTP expired. Please request a new one");
        }
        if (!entry.otp.equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        // Valid — remove entry
        otpStore.remove(email);
    }

    // ========== HELPERS ==========

    private String generateOtp() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }

    private void sendOtpEmail(String toEmail, String otp, String subject) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText("Your OTP is: " + otp + "\n\n"
                + "This OTP expires in " + expiryMinutes + " minutes.\n"
                + "Do not share this OTP with anyone.");
        mailSender.send(message);
    }

    // ========== Inner classes ==========

    private static class OtpEntry {
        String otp;
        LocalDateTime expiresAt;
        String username;
        String password;
        String purpose; // SIGNUP or LOGIN

        OtpEntry(String otp, LocalDateTime expiresAt, String username, String password, String purpose) {
            this.otp = otp;
            this.expiresAt = expiresAt;
            this.username = username;
            this.password = password;
            this.purpose = purpose;
        }
    }

    public static class SignupData {
        private final String username;
        private final String password;
        private final String email;

        public SignupData(String username, String password, String email) {
            this.username = username;
            this.password = password;
            this.email = email;
        }

        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getEmail() { return email; }
    }
}
