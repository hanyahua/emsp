package com.volvo.emsp.domain.model;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Objects;

public class Email {

    /**
     * Validates email address format without network checks
     * @param email the email to validate
     * @return true if valid format, false otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isValid(String email) {
        if(email == null) return false;
        if(email.length() > 255) return false;
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }

    public static Email of(String email) {
        return new Email(email);
    }

    private final String email;

    public Email(String email) {
        if(!isValid(email)) throw new IllegalArgumentException("Invalid email format: " + email);
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Email email1 = (Email) o;
        return Objects.equals(email, email1.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }

    public String toString() {
        return email;
    }
}
