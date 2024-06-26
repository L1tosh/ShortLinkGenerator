package org.example.shortlinkgenerator.controllers;

import org.example.shortlinkgenerator.entity.ShortLinkManager;
import org.example.shortlinkgenerator.exceptions.ShortUrlNotFoundException;
import org.example.shortlinkgenerator.services.ShortLinkManagerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShortLinkManagerControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ShortLinkManagerService shortLinkManagerService;

    @Test
    @DisplayName("createShortLink returns Unauthorized 401")
    void createShortUrl_UnauthenticatedUser_ShouldNotSeeEndpoint() throws Exception {
        mockMvc.perform(post("/api/create"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("getPostByShortUrl returns Unauthorized 401")
    void getPostByShortUrl_UnauthenticatedUser_ShouldNotSeeEndpoint() throws Exception {
        String shortUrl = "abcde123";
        mockMvc.perform(get("/api/get?shortUrl=" + shortUrl))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("deleteShortUrl returns Unauthorized 401")
    void deleteShortUrl_UnauthenticatedUser_ShouldNotSeeEndpoint() throws Exception {
        mockMvc.perform(post("/api/delete"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("createShortLink creates a new unique ShortUrl and returns it")
    void createShortUrl_RequestIsValid_ReturnsUniqueShortUrl() throws Exception {
        String url = "a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146a";
        String shortUrl = "abcde123";

        Mockito.when(shortLinkManagerService.generateAndSaveShortUrl(url)).thenReturn(shortUrl);

        mockMvc.perform(post("/api/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(url))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(shortUrl)));

        Mockito.verify(shortLinkManagerService).generateAndSaveShortUrl(url);
        Mockito.verifyNoMoreInteractions(shortLinkManagerService);
    }

    @Test
    @WithMockUser
    @DisplayName("createShortUrl with invalid request body should return 400 Bad Request")
    void createShortUrl_RequestIsInvalid_TrowsBadRequestException() throws Exception {
        mockMvc.perform(post("/api/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("getPostByShortUrl returns url")
    void getPostByShortUrl_RequestWithExistShortUrl_ReturnsUrl() throws Exception {
        String url = "a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146a";
        String shortUrl = "abcde123";

        ShortLinkManager shortLinkManagerObj = ShortLinkManager.builder()
                .id(1L)
                .shortUrl(shortUrl)
                .url(url)
                .createdAt(LocalDateTime.now()).build();

        Mockito.when(shortLinkManagerService.getUrlByShortUrl(shortUrl))
                .thenReturn(Optional.of(shortLinkManagerObj));

        mockMvc.perform(get("/api/get?shortUrl=" + shortUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(url)));

        Mockito.verify(shortLinkManagerService).getUrlByShortUrl(shortUrl);
        Mockito.verifyNoMoreInteractions(shortLinkManagerService);
    }

    @Test
    @WithMockUser
    @DisplayName("getPostByShortUrl throw ShortUrlNotFoundException")
    void getPostByShortUrl_RequestWithNotExistShortUrl_ThrowShortUrlNotFoundException() throws Exception {
        String shortUrl = "nonexist";

        Mockito.when(shortLinkManagerService.getUrlByShortUrl(shortUrl))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/get?shortUrl=" + shortUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ShortUrlNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(ShortUrlNotFoundException.DEFAULT_MSG,
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

        Mockito.verify(shortLinkManagerService).getUrlByShortUrl(shortUrl);
        Mockito.verifyNoMoreInteractions(shortLinkManagerService);
    }

    @Test
    @WithMockUser
    @DisplayName("getPostByShortUrl returns status Ok")
    void deleteShortUrl_RequestWithExistUrl_ReturnsStatusOk() throws Exception {
        String url = "valid_url";

        mockMvc.perform(post("/api/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(url))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("getPostByShortUrl returns status NotFound")
    void deleteShortUrl_RequestWithNotExistUrl_ReturnsNotFound() throws Exception {
        String url = "valid_url";

        Mockito.doThrow(new ShortUrlNotFoundException())
                .when(shortLinkManagerService).deleteShortLinkManager(url);

        mockMvc.perform(post("/api/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(url))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals(ShortUrlNotFoundException.DEFAULT_MSG,
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

        Mockito.verify(shortLinkManagerService).deleteShortLinkManager(url);
        Mockito.verifyNoMoreInteractions(shortLinkManagerService);
    }
}