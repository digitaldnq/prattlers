package by.doni.core.web.controller;


import by.doni.core.entity.RoleType;
import by.doni.core.web.dto.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.equalTo;
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
}