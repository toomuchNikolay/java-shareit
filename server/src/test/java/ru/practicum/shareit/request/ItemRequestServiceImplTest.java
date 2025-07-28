package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestInputDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDetailsDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private ItemRequestServiceImpl service;

    private User firstUser;
    private ItemRequest firstRequestFirstUser;
    private ItemRequest firstRequestSecondUser;
    private ItemRequest secondRequestFirstUser;
    private ItemRequest secondRequestSecondUser;

    @BeforeEach
    void setUp() {
        firstUser = User.builder()
                .id(1L)
                .name("First user")
                .email("firstmail@box.com")
                .build();
        User secondUser = User.builder()
                .id(2L)
                .name("Second user")
                .email("secondmail@box.com")
                .build();

        firstRequestFirstUser = ItemRequest.builder()
                .id(1L)
                .description("specification first request firstUser")
                .requestor(firstUser)
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        firstRequestSecondUser = ItemRequest.builder()
                .id(2L)
                .description("specification first request secondUser")
                .requestor(secondUser)
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        secondRequestFirstUser = ItemRequest.builder()
                .id(3L)
                .description("specification second request firstUser")
                .requestor(firstUser)
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        secondRequestSecondUser = ItemRequest.builder()
                .id(4L)
                .description("specification second request secondUser")
                .requestor(secondUser)
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
    }

    @Test
    void create() {
        ItemRequestInputDto dto = ItemRequestInputDto.builder()
                .description("test")
                .build();
        when(userService.findUserOrThrow(1L)).thenReturn(firstUser);
        ItemRequest request = ItemRequestMapper.toEntity(dto, firstUser);
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestResponseDto response = service.create(firstUser.getId(), dto);

        assertThat(response, notNullValue());
        assertThat(response.getDescription(), equalTo(dto.getDescription()));
        verify(userService).findUserOrThrow(anyLong());
        verify(requestRepository).save(any(ItemRequest.class));
    }

    @Test
    void getOwnItemRequests() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ItemRequest> requests = List.of(secondRequestFirstUser, firstRequestFirstUser);
        Page<ItemRequest> page = new PageImpl<>(requests, pageable, requests.size());
        when(userService.findUserOrThrow(firstUser.getId())).thenReturn(firstUser);
        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(firstUser.getId(), pageable))
                .thenReturn(page);

        List<ItemRequestResponseDetailsDto> response = service.getOwnItemRequests(firstUser.getId(), 0, 10);

        assertThat(response, hasSize(2));
        assertThat(response, hasItem(ItemRequestMapper.toDetailsDto(firstRequestFirstUser)));
        assertThat(response, hasItem(ItemRequestMapper.toDetailsDto(secondRequestFirstUser)));
        assertThat(response.getFirst().getId(), is(secondRequestFirstUser.getId()));
        assertThat(response.getFirst().getItems(), is(secondRequestFirstUser.getItems()));
        verify(requestRepository, times(1))
                .findAllByRequestorIdOrderByCreatedDesc(anyLong(), any());
    }

    @Test
    void getOthersItemRequests() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ItemRequest> requests = List.of(secondRequestSecondUser, firstRequestSecondUser);
        Page<ItemRequest> page = new PageImpl<>(requests, pageable, requests.size());
        when(userService.findUserOrThrow(firstUser.getId())).thenReturn(firstUser);
        when(requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(firstUser.getId(), pageable))
                .thenReturn(page);

        List<ItemRequestResponseDto> response = service.getOthersItemRequests(firstUser.getId(), 0, 10);

        assertThat(response, hasSize(2));
        assertThat(response, not(hasItem(ItemRequestMapper.toDto(firstRequestFirstUser))));
        assertThat(response, not(hasItem(ItemRequestMapper.toDto(secondRequestFirstUser))));
        assertThat(ItemRequestMapper.toDto(secondRequestSecondUser), is(response.getFirst()));
        verify(requestRepository, times(1))
                .findAllByRequestorIdNotOrderByCreatedDesc(anyLong(), any());
    }

    @Test
    void getById_whenRequestFound_thenReturnedRequest() {
        when(requestRepository.findById(firstRequestSecondUser.getId()))
                .thenReturn(Optional.of(firstRequestSecondUser));

        ItemRequestResponseDetailsDto response = service.getById(firstRequestSecondUser.getId());

        assertThat(response, notNullValue());
        assertThat(response.getId(), equalTo(firstRequestSecondUser.getId()));
        assertThat(response.getDescription(), equalTo(firstRequestSecondUser.getDescription()));
        assertThat(response.getItems(), is(firstRequestSecondUser.getItems()));
        verify(requestRepository, times(1)).findById(anyLong());
    }

    @Test
    void getById_whenRequestNotFound_thenNotFoundExceptionThrown() {
        Long requestId = 0L;
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getById(requestId));
        verify(requestRepository).findById(anyLong());
    }
}
