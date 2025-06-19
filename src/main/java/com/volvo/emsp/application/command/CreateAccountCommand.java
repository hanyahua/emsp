package com.volvo.emsp.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateAccountCommand {

    @NotBlank(message = "email is required")
    @Size(max = 255, message = "email must be less than 255 characters")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "CreateAccountCommand{" +
                "email='" + email + '\'' +
                '}';
    }
}
