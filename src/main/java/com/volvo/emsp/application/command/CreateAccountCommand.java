package com.volvo.emsp.application.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Create Account Command")
public class CreateAccountCommand {

    @Schema(description = "Email address for the new account",
            example = "user@example.com"
    )
    @NotBlank(message = "email is required")
    @Size(max = 255, message = "email must be less than 255 characters")
    @Email(message = "email must be a valid email address")
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
