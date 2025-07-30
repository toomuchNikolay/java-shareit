package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.exception.errors.ErrorMessage.HEADER_USER_ID;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService service;

    @Autowired
    private MockMvc mvc;

    private BookingInputDto request;
    private BookingResponseDto response;

    @BeforeEach
    void setUp() {
        request = BookingInputDto.builder()
                .start(LocalDateTime.of(2025, 7, 1, 0, 0))
                .end(LocalDateTime.of(2025, 8, 1, 0, 0))
                .itemId(1L)
                .build();
        response = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 8, 1, 0, 0))
                .end(LocalDateTime.of(2025, 8, 10, 0, 0))
                .status(BookingStatus.WAITING)
                .booker(UserResponseDto.builder()
                        .id(2L)
                        .name("User name")
                        .email("mail@box.com")
                        .build())
                .item(ItemResponseDto.builder()
                        .id(1L)
                        .name("Item name")
                        .description("Item description")
                        .available(true)
                        .ownerId(1L)
                        .requestId(null)
                        .build())
                .build();
    }

    @Test
    @SneakyThrows
    void create() {
        long userId = 2L;
        when(service.create(userId, request)).thenReturn(response);

        mvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, userId)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.status", is(response.getStatus().name())))
                .andExpect(jsonPath("$.booker").isNotEmpty())
                .andExpect(jsonPath("$.item").isNotEmpty());
    }

    @Test
    @SneakyThrows
    void approve() {
        Long bookingId = 1L;
        long userid = 1L;
        response.setStatus(BookingStatus.APPROVED);
        when(service.approve(bookingId, userid, true)).thenReturn(response);

        mvc.perform(patch("/bookings/" + bookingId)
                        .header(HEADER_USER_ID, userid)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.status", is(response.getStatus().name())))
                .andExpect(jsonPath("$.booker").isNotEmpty())
                .andExpect(jsonPath("$.item").isNotEmpty());
    }

    @Test
    @SneakyThrows
    void getById() {
        Long bookingId = 1L;
        long userId = 2L;
        when(service.getById(bookingId, userId)).thenReturn(response);

        mvc.perform(get("/bookings/" + bookingId)
                        .header(HEADER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(response.getId()), Long.class))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.status", is(response.getStatus().name())))
                .andExpect(jsonPath("$.booker").isNotEmpty())
                .andExpect(jsonPath("$.item").isNotEmpty());
    }

    @Test
    @SneakyThrows
    void getBookingsByUser() {
        long userId = 2L;
        String state = "current";
        response.setStatus(BookingStatus.APPROVED);
        when(service.getBookingsByUser(userId, state, 0, 10)).thenReturn(List.of(response));

        mvc.perform(get("/bookings")
                        .header(HEADER_USER_ID, userId)
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(response.getId()), Long.class));
    }

    @Test
    @SneakyThrows
    void getBookingsByOwner() {
        long userId = 1L;
        String state = "WAITING";
        when(service.getBookingsByOwner(userId, state, 0, 10)).thenReturn(List.of(response));

        mvc.perform(get("/bookings/owner")
                        .header(HEADER_USER_ID, userId)
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(response.getId()), Long.class));
    }
}
