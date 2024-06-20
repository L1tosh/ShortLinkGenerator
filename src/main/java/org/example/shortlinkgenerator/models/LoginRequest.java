package org.example.shortlinkgenerator.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @Email
    @NotEmpty(message = "email can't be empty")
    private String email;

    @NotEmpty(message = "password can't be empty")
    private String password;
}
