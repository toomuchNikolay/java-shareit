package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserInputDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService service;

    @Autowired
    private MockMvc mvc;

    private UserInputDto request;
    private UserResponseDto response;

    @BeforeEach
    void setUp() {
        request = UserInputDto.builder()
                .name("Test")
                .email("Test@box.com")
                .build();
        response = UserResponseDto.builder()
                .id(1L)
                .name("Test")
                .email("Test@box.com")
                .build();
    }

    @Test
    @SneakyThrows
    void create() {
        when(service.create(any())).thenReturn(response);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(response.getName())))
                .andExpect(jsonPath("$.email", is(response.getEmail())));
    }

    @Test
    @SneakyThrows
    void update() {
        Long userId = 1L;
        when(service.update(eq(userId), any())).thenReturn(response);

        mvc.perform(patch("/users/" + userId)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(response.getName())))
                .andExpect(jsonPath("$.email", is(response.getEmail())));
    }

    @Test
    @SneakyThrows
    void getById() {
        Long userId = 1L;
        when(service.getById(userId)).thenReturn(response);

        mvc.perform(get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(response.getName())))
                .andExpect(jsonPath("$.email", is(response.getEmail())));
    }

    @Test
    @SneakyThrows
    void delete() {
        Long userId = 1L;
        doNothing().when(service).delete(userId);

        mvc.perform(MockMvcRequestBuilders.delete("/users/" + userId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(userId);
    }
}
