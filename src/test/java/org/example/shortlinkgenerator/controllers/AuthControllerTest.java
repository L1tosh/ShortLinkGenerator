package org.example.shortlinkgenerator.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.shortlinkgenerator.models.LoginRequest;
import org.example.shortlinkgenerator.models.LoginResponse;
import org.example.shortlinkgenerator.models.RegistrationRequest;
import org.example.shortlinkgenerator.services.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void login_ValidRequest_ReturnsJwtToken() throws Exception {
        // given
        final var request = LoginRequest.builder()
                .email("test@gmail.com")
                .password("12345678")
                .build();

        final var token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwiZXhwIjoxNzE4OTY3NjMyLCJlIjoidGVzdEBnbWFpbC5jb20iLCJhIjpbIlJPTEVfVVNFUiJdfQ.jQ3SMdbJNzWgBFkONBJEjqGjysEtGVlHZWGCq4MxxxI";
        final var response = LoginResponse.builder()
                .accessToken(token)
                .build();

        // when
        when(authService.attemptLogin(request.getEmail(), request.getPassword()))
                .thenReturn(response);
        // then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void login_BadValidRequest_ThrowsConstraintViolationException() throws Exception {
        // given
        final var request = LoginRequest.builder()
                .email("badEmail")
                .password("what?")
                .build();

        // when

        // then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void registration_ValidRequest_ReturnsStatusCreated() throws Exception {
        // given
        final var request = RegistrationRequest.builder()
                .email("test@gmail.com")
                .password("12345678")
                .role("ROLE_USER")
                .build();
        // when

        // then
        mockMvc.perform(post("/api/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

    }

    @Test
    @WithMockUser
    void registration_WithLessRole_ReturnsStatusForbidden() throws Exception {
        // given
        final var request = RegistrationRequest.builder()
                .email("test@gmail.com")
                .password("12345678")
                .role("ROLE_USER")
                .build();
        // when

        // then
        mockMvc.perform(post("/api/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void registration_BadValidRequest_ReturnsStatusBadRequest() throws Exception {
        // given
        final var request = RegistrationRequest.builder()
                .email("test@gmailcom")
                .password("1234567")
                .role("ROLE_USER")
                .build();
        // when

        // then
        mockMvc.perform(post("/api/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void delete_ValidRequest_ReturnsStatusOk() throws Exception {
        // given
        final var email = "test@gmail.com";
        // when

        // then
        mockMvc.perform(post("/api/auth/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(email))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void delete_WithLessRole_ReturnsStatusForbidden() throws Exception {
        // given
        final var email = "test@gmail.com";
        // when

        // then
        mockMvc.perform(post("/api/auth/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(email)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void delete_BadValidRequest_ReturnsStatusBadRequest() throws Exception {
        // given
        final var email = "testgmail.com";
        // when

        // then
        mockMvc.perform(post("/api/auth/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(email)))
                .andExpect(status().isBadRequest());
    }
}