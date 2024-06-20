package org.example.shortlinkgenerator.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    @Size(min = 8, max = 32)
    private String password;

    @NotEmpty
    private String role;
}
