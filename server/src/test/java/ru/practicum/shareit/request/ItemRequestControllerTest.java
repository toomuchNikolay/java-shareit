package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDetailsDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.exception.errors.ErrorMessage.HEADER_USER_ID;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService service;

    @Autowired
    private MockMvc mvc;

    private ItemRequestInputDto request;

    @BeforeEach
    void setUp() {
        request = ItemRequestInputDto.builder()
                .description("test")
                .build();
    }

    @Test
    @SneakyThrows
    void create() {
        long userId = 1L;
        ItemRequestResponseDto response = ItemRequestResponseDto.builder()
                .id(1L)
                .description("test")
                .created(LocalDateTime.now())
                .build();
        when(service.create(userId, request)).thenReturn(response);

        mvc.perform(post("/requests")
                        .header(HEADER_USER_ID, userId)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(response.getDescription())));
    }

    @Test
    @SneakyThrows
    void getOwn() {
        long userId = 1L;
        List<ItemRequestResponseDetailsDto> response = List.of(
                ItemRequestResponseDetailsDto.builder()
                        .id(2L)
                        .description("second")
                        .created(LocalDateTime.now())
                        .items(Collections.emptyList())
                        .build(),
                ItemRequestResponseDetailsDto.builder()
                        .id(1L)
                        .description("first")
                        .created(LocalDateTime.now())
                        .items(Collections.emptyList())
                        .build()
        );
        when(service.getOwnItemRequests(userId, 0, 10)).thenReturn(response);

        mvc.perform(get("/requests")
                        .header(HEADER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(2L), Long.class))
                .andExpect(jsonPath("$[0].description", is("second"), String.class))
                .andExpect(jsonPath("$[1].id", is(1L), Long.class))
                .andExpect(jsonPath("$[1].description", is("first"), String.class));
    }

    @Test
    @SneakyThrows
    void getOthers() {
        long userId = 1L;
        List<ItemRequestResponseDto> response = List.of(
                ItemRequestResponseDto.builder()
                        .id(2L)
                        .description("second")
                        .created(LocalDateTime.now())
                        .build(),
                ItemRequestResponseDto.builder()
                        .id(1L)
                        .description("first")
                        .created(LocalDateTime.now())
                        .build()
        );
        when(service.getOthersItemRequests(userId, 0, 10)).thenReturn(response);

        mvc.perform(get("/requests/all")
                        .header(HEADER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(2L), Long.class))
                .andExpect(jsonPath("$[0].description", is("second"), String.class))
                .andExpect(jsonPath("$[1].id", is(1L), Long.class))
                .andExpect(jsonPath("$[1].description", is("first"), String.class));
    }

    @Test
    @SneakyThrows
    void getById() {
        long userId = 2L;
        Long requestId = 1L;
        ItemRequestResponseDetailsDto response = ItemRequestResponseDetailsDto.builder()
                .id(requestId)
                .description("test")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();
        when(service.getById(1L)).thenReturn(response);

        mvc.perform(get("/requests/" + requestId)
                        .header(HEADER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(response.getDescription())));
    }
}
