package by.doni.core.web.controller;


import by.doni.core.entity.RoleType;
import by.doni.core.web.dto.CreateUserRequest;
import com.github.tomakehurst.wiremock.client.BasicCredentials;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestUserController extends AbstractWebTest {

    @Test
    public void whenCreateUserThenCreateNewUserInDb() throws Exception {
        var createUserBody = new CreateUserRequest("test_username", "12345", RoleType.ROLE_USER);
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(createUserBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.username").value(equalTo("test_username")));

        assertTrue(userRepository.findByUsername("test_username").isPresent());
    }

    @Test
    @WithUserDetails("admin")
    public void whenSendRequestForUserByIdThenReturnUserById() throws Exception {
        mockMvc.perform(get("/api/v1/users/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(equalTo(1)))
                .andExpect(jsonPath("$.username").value(equalTo("test_user1")));
    }

    @Test
    @WithUserDetails("admin")
    public void whenDeleteUserByIdThenDeleteUserSuccessAndSendRequestToRemoveSubscription() throws Exception {
        assertTrue(userRepository.findById(1L).isPresent());
        mockMvc.perform(delete("/api/v1/users/{userId}", 1))
                .andExpect(status().isNoContent());
        assertFalse(userRepository.findById(1L).isPresent());

        wireMockServer.verify(WireMock.exactly(1), WireMock.deleteRequestedFor(
                        WireMock.urlEqualTo("/api/v1/subscriptions/1"))
                .withBasicAuth(new BasicCredentials(clientUser, clientPassword))
        );
    }

    @Test
    @WithUserDetails("test_user2")
    public void whenTryToDeleteUserByIdThenForbiddenResponse() throws Exception {
        assertTrue(userRepository.findById(1L).isPresent());
        mockMvc.perform(delete("/api/v1/users/{userId}", 1))
                .andExpect(status().isForbidden());
        assertTrue(userRepository.findById(1L).isPresent());
        wireMockServer.verify(WireMock.exactly(0), WireMock.anyRequestedFor(WireMock.anyUrl()));
    }
}