package org.example.shortlinkgenerator.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.example.shortlinkgenerator.exceptions.RegistrationException;
import org.example.shortlinkgenerator.models.LoginRequest;
import org.example.shortlinkgenerator.models.LoginResponse;
import org.example.shortlinkgenerator.models.RegistrationRequest;
import org.example.shortlinkgenerator.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Validated LoginRequest request) {
        return authService.attemptLogin(request.getEmail(), request.getPassword());
    }

    @PostMapping("/registration")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> registration(@RequestBody @Valid RegistrationRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new RegistrationException(bindingResult.getAllErrors());

        authService.registry(request);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> delete(@RequestBody @Valid @Email @NotEmpty String email, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new RegistrationException(bindingResult.getAllErrors());

        authService.delete(email);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
