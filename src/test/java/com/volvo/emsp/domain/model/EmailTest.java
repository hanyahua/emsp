package com.volvo.emsp.domain.model;
import com.volvo.emsp.testmodel.Emails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void testValidEmail() {
        // given
        String validEmail = Emails.EMAIL1;
        // when & then
        assertDoesNotThrow(() -> new Email(validEmail));
        assertEquals(validEmail, new Email(validEmail).toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            Emails.INVALID_EMAIL,
            Emails.INVALID_EMAIL2,
            Emails.INVALID_EMAIL3,
            Emails.INVALID_EMAIL4,
            Emails.INVALID_EMAIL5,
            Emails.INVALID_EMAIL6,
            Emails.INVALID_EMAIL7,
            Emails.INVALID_EMAIL8,
            Emails.INVALID_EMAIL9,
            Emails.INVALID_EMAIL10,
            Emails.INVALID_EMAIL11,
            Emails.INVALID_EMAIL12,
            Emails.INVALID_EMAIL13,
            Emails.INVALID_EMAIL14,
            Emails.INVALID_EMAIL15,
            Emails.TOO_LONG_EMAIL
    })
    void testInvalidEmailFormat(String invalidEmail) {
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> new Email(invalidEmail),
            "Should throw exception for invalid email: " + invalidEmail);
    }

    @Test
    void testNullEmail() {
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> new Email(null),
            "Should throw exception for null email");
    }

    @Test
    void testEmailEquality() {
        // given
        String emailStr = Emails.EMAIL1;
        Email email1 = new Email(emailStr);
        Email email2 = new Email(emailStr);
        Email differentEmail = new Email(Emails.EMAIL2);
        // when & then
        assertEquals(email1, email2, "Equal emails should be equal");
        assertNotEquals(email1, differentEmail, "Different emails should not be equal");
        assertEquals(emailStr, email1.toString(), "Email toString should return original string");
    }
}