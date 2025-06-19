package com.volvo.emsp.domain.model;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Emaid {

    private static final String REGEX_PATTERN = "^[A-Z]{2}[\\dA-Z]{3}[\\dA-Z]{9}$";
    private static final Pattern pattern = Pattern.compile(REGEX_PATTERN, Pattern.CASE_INSENSITIVE);

    public static boolean validate(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    private final String value;

    public Emaid(String value) {
        if(!validate(value))
            throw new IllegalArgumentException("Invalid Emaid " + value);
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Emaid emaid = (Emaid) o;
        return Objects.equals(value, emaid.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}

