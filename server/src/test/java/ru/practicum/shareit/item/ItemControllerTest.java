package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.exception.errors.ErrorMessage.HEADER_USER_ID;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService service;

    @Autowired
    private MockMvc mvc;

    private ItemInputDto request;
    private ItemResponseDto response;

    @BeforeEach
    void setUp() {
        request = ItemInputDto.builder()
                .name("Test name item")
                .description("Test description")
                .available(true)
                .requestId(null)
                .build();
        response = ItemResponseDto.builder()
                .id(1L)
                .name("Test name item")
                .description("Test description")
                .available(true)
                .ownerId(1L)
                .requestId(null)
                .build();
    }

    @Test
    @SneakyThrows
    void create() {
        long userId = 1L;
        when(service.create(userId, request)).thenReturn(response);

        mvc.perform(post("/items")
                        .header(HEADER_USER_ID, userId)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(response.getName())))
                .andExpect(jsonPath("$.description", is(response.getDescription())))
                .andExpect(jsonPath("$.available", is(response.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.ownerId", is(response.getOwnerId()), Long.class));
    }

    @Test
    @SneakyThrows
    void update() {
        Long itemId = 1L;
        long userId = 1L;
        when(service.update(itemId, userId, request)).thenReturn(response);

        mvc.perform(patch("/items/" + itemId)
                        .header(HEADER_USER_ID, userId)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(response.getName())))
                .andExpect(jsonPath("$.description", is(response.getDescription())))
                .andExpect(jsonPath("$.available", is(response.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.ownerId", is(response.getOwnerId()), Long.class));
    }

    @Test
    @SneakyThrows
    void getById() {
        Long itemId = 1L;
        long userId = 1L;
        ItemResponseDetailsDto responseDetailsDto = ItemResponseDetailsDto.builder()
                .id(1L)
                .name("Test name item")
                .description("Test description")
                .available(true)
                .ownerId(1L)
                .requestId(null)
                .lastBooking(null)
                .nextBooking(null)
                .comments(Collections.emptyList())
                .build();
        when(service.getById(itemId, userId)).thenReturn(responseDetailsDto);

        mvc.perform(get("/items/" + itemId)
                        .header(HEADER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDetailsDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(responseDetailsDto.getName())))
                .andExpect(jsonPath("$.description", is(responseDetailsDto.getDescription())))
                .andExpect(jsonPath("$.available", is(responseDetailsDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.ownerId", is(responseDetailsDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.lastBooking", is(responseDetailsDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(responseDetailsDto.getNextBooking())))
                .andExpect(jsonPath("$.comments", is(responseDetailsDto.getComments())));
    }

    @Test
    @SneakyThrows
    void getAllByUserId() {
        long userId = 1L;
        when(service.getAllById(userId, 0, 10)).thenReturn(List.of(response));

        mvc.perform(get("/items")
                        .header(HEADER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(response.getName())));
    }

    @Test
    @SneakyThrows
    void search() {
        long userId = 1L;
        String text = "desc";
        when(service.search(userId, text, 0, 10)).thenReturn(List.of(response));

        mvc.perform(get("/items/search")
                        .header(HEADER_USER_ID, userId)
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(response.getName())));
    }

    @Test
    @SneakyThrows
    void addComment() {
        long userId = 1L;
        Long itemId = 1L;
        CommentInputDto commentInput = CommentInputDto.builder()
                .text("test comment")
                .build();
        CommentResponseDto commentResponse = CommentResponseDto.builder()
                .id(1L)
                .text("test comment")
                .authorName("author")
                .created(LocalDateTime.of(2025, 7, 25, 11, 11, 00))
                .build();
        when(service.addComment(itemId, userId, commentInput)).thenReturn(commentResponse);

        mvc.perform(post("/items/" + itemId + "/comment")
                        .header(HEADER_USER_ID, userId)
                        .content(mapper.writeValueAsString(commentInput))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentResponse.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentResponse.getText())))
                .andExpect(jsonPath("$.authorName", is(commentResponse.getAuthorName())));
    }
}
