package com.example.CS330_PZ.black_box;

import com.example.CS330_PZ.service.ValidatorService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class EmailValidatorServiceIntegrationTests {

    @Autowired
    private ValidatorService emailValidatorService;

    @ParameterizedTest
    @ValueSource(strings = {
            "anja.popovic@example.com",
            "marko_123@domain.org",
            "pera@domain.rs"
    })
    void testValidEmails(String email) {
        assertTrue(emailValidatorService.isValidEmail(email),
                "Valid email should pass: " + email);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "@example.com",
            "!anja@example.com"
    })
    void testInvalidLocalPart(String email) {
        assertFalse(emailValidatorService.isValidEmail(email),
                "Invalid local part should fail: " + email);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "anja.example.com",
            "anja@@example.com"
    })
    void testInvalidAtSymbol(String email) {
        assertFalse(emailValidatorService.isValidEmail(email),
                "Invalid @ usage should fail: " + email);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "anja@exa!mple.com",
            "anja@.com"
    })
    void testInvalidDomainName(String email) {
        assertFalse(emailValidatorService.isValidEmail(email),
                "Invalid domain name should fail: " + email);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "anja@examplecom",
            "anja@example..com"
    })
    void testInvalidDotBeforeExtension(String email) {
        assertFalse(emailValidatorService.isValidEmail(email),
                "Invalid dot placement should fail: " + email);
    }
    @ParameterizedTest
    @ValueSource(strings = {
            "anja@example.c",
            "anja@example.comm"
    })
    void testInvalidExtension(String email) {
        assertFalse(emailValidatorService.isValidEmail(email),
                "Invalid extension length should fail: " + email);
    }
}
