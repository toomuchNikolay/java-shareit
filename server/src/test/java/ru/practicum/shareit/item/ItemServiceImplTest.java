package ru.practicum.shareit.item;

import com.querydsl.core.types.dsl.BooleanExpression;
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
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ItemRequestServiceImpl requestService;

    @InjectMocks
    private ItemServiceImpl service;

    private User someUser;
    private User anotherUser;
    private Item someItem;

    @BeforeEach
    void setUp() {
        someUser = User.builder()
                .id(1L)
                .name("Name some user")
                .email("thismailsomeuser@box.com")
                .build();
        anotherUser = User.builder()
                .id(2L)
                .name("Another user")
                .email("anothermail@box.com")
                .build();

        someItem = Item.builder()
                .id(1L)
                .name("Name someItem")
                .description("specification someItem")
                .available(true)
                .owner(someUser)
                .request(null)
                .comments(Collections.emptyList())
                .build();
    }

    @Test
    void create_whenRequestWithIdRequest_thenCreateItem() {
        long ownerId = 1L;
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("specification first request firstUser")
                .requestor(someUser)
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        ItemInputDto dto = ItemInputDto.builder()
                .name("Item name")
                .description("specification")
                .available(true)
                .requestId(1L)
                .build();
        Item item = ItemMapper.toEntity(dto, someUser, request);
        when(userService.findUserOrThrow(ownerId)).thenReturn(someUser);
        when(requestService.findItemRequestOrThrow(1L)).thenReturn(request);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemResponseDto response = service.create(ownerId, dto);

        assertThat(response, notNullValue());
        assertThat(response.getName(), equalTo(dto.getName()));
        assertThat(response.getRequestId(), equalTo(request.getId()));
        verify(userService, times(1)).findUserOrThrow(anyLong());
        verify(requestService, times(1)).findItemRequestOrThrow(anyLong());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void create_whenOwnerNotFound_thenNotFoundExceptionThrown() {
        long ownerId = 10L;
        ItemInputDto dto = ItemInputDto.builder()
                .name("Item name")
                .description("specification")
                .available(true)
                .requestId(null)
                .build();
        when(userService.findUserOrThrow(ownerId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> service.create(ownerId, dto));
        verify(userService, times(1)).findUserOrThrow(anyLong());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update_whenItemFound_thenUpdateOnlyAvailableFields() {
        ItemInputDto dto = ItemInputDto.builder()
                .name("New name item")
                .description("New spec")
                .available(false)
                .requestId(1L)
                .build();
        when(itemRepository.findById(someItem.getId())).thenReturn(Optional.of(someItem));

        ItemResponseDto response = service.update(someItem.getId(), someUser.getId(), dto);

        assertThat(response.getName(), equalTo(dto.getName()));
        assertThat(response.getDescription(), equalTo(dto.getDescription()));
        assertThat(response.getAvailable(), equalTo(dto.getAvailable()));
        assertThat(response.getRequestId(), not(equalTo(dto.getRequestId())));
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void update_whenUserNotOwner_thenAccessDeniedExceptionThrown() {
        Long itemId = someItem.getId();
        long userId = 10L;
        ItemInputDto dto = ItemInputDto.builder()
                .name("New name item")
                .description("New spec")
                .available(false)
                .requestId(1L)
                .build();
        when(itemRepository.findById(someItem.getId())).thenReturn(Optional.of(someItem));

        assertThrows(AccessDeniedException.class, () -> service.update(itemId, userId, dto));
        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void getById_whenItemFound_thenReturnedItem() {
        Long itemId = 1L;
        long userId = 1L;
        Booking firstBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(someItem)
                .booker(anotherUser)
                .status(BookingStatus.APPROVED)
                .build();
        Booking secondBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(someItem)
                .booker(anotherUser)
                .status(BookingStatus.APPROVED)
                .build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(someItem));
        when(bookingRepository.findNearestBooking(itemId, false)).thenReturn(Optional.of(firstBooking));
        when(bookingRepository.findNearestBooking(itemId, true)).thenReturn(Optional.of(secondBooking));

        ItemResponseDetailsDto responseDetailsDto = service.getById(itemId, userId);

        assertThat(responseDetailsDto, notNullValue());
        assertThat(responseDetailsDto.getName(), equalTo(someItem.getName()));
        assertThat(responseDetailsDto.getLastBooking(), equalTo(firstBooking.getStart()));
        assertThat(responseDetailsDto.getNextBooking(), equalTo(secondBooking.getStart()));
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(2)).findNearestBooking(anyLong(), anyBoolean());
    }

    @Test
    void getById_whenItemNotFound_thenNotFoundExceptionThrown() {
        Long itemId = 10L;
        long userId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getById(itemId, userId));
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllById_whenItemsFound_thenReturnedCollectionItems() {
        long userId = 1L;
        Item anotherItem = Item.builder()
                .id(2L)
                .name("Name anotherItem")
                .description("specification anotherItem")
                .available(false)
                .owner(someUser)
                .request(null)
                .comments(Collections.emptyList())
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> requests = List.of(someItem, anotherItem);
        Page<Item> page = new PageImpl<>(requests, pageable, requests.size());
        when(userService.findUserOrThrow(userId)).thenReturn(someUser);
        when(itemRepository.findAllByOwner_Id(userId, pageable)).thenReturn(page);

        List<ItemResponseDto> response = service.getAllById(userId, 0, 10);

        assertThat(response, hasSize(2));
        assertThat(response, hasItem(ItemMapper.toDto(someItem)));
        assertThat(response, hasItem(ItemMapper.toDto(anotherItem)));
        verify(userService, times(1)).findUserOrThrow(anyLong());
        verify(itemRepository, times(1)).findAllByOwner_Id(anyLong(), any());
    }

    @Test
    void getAllById_whenNotItemsFound_thenReturnedEmptyList() {
        long userId = 2L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> page = new PageImpl<>(Collections.emptyList());
        when(userService.findUserOrThrow(userId)).thenReturn(anotherUser);
        when(itemRepository.findAllByOwner_Id(userId, pageable)).thenReturn(page);

        List<ItemResponseDto> response = service.getAllById(userId, 0, 10);

        assertThat(response, empty());
        verify(userService, times(1)).findUserOrThrow(anyLong());
        verify(itemRepository, times(1)).findAllByOwner_Id(anyLong(), any());
    }

    @Test
    void search_whenTextContained_thenReturnedCollectionOnlyAvailableItem() {
        long userId = 1L;
        Item anotherItem = Item.builder()
                .id(2L)
                .name("Special name anotherItem")
                .description("description")
                .available(true)
                .owner(someUser)
                .request(null)
                .comments(Collections.emptyList())
                .build();
        Item wrongItem = Item.builder()
                .id(3L)
                .name("Name wrongItem")
                .description("specification wrongItem")
                .available(false)
                .owner(someUser)
                .request(null)
                .comments(Collections.emptyList())
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> requests = List.of(someItem, anotherItem);
        Page<Item> page = new PageImpl<>(requests, pageable, requests.size());
        when(itemRepository.findAll(any(BooleanExpression.class), eq(pageable))).thenReturn(page);

        List<ItemResponseDto> response = service.search(userId, "spec", 0, 10);

        assertThat(response, hasSize(2));
        assertThat(response, hasItem(ItemMapper.toDto(someItem)));
        assertThat(response, hasItem(ItemMapper.toDto(anotherItem)));
        assertThat(response, not(hasItem(ItemMapper.toDto(wrongItem))));
        verify(itemRepository, times(1)).findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    void search_whenTextIsBlank_thenReturnedEmptyList() {
        List<ItemResponseDto> response = service.search(1L, " ", 0, 10);

        assertThat(response, empty());
        verify(itemRepository, never()).findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    void search_whenTextNoContained_thenReturnedEmptyList() {
        long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> page = new PageImpl<>(Collections.emptyList());
        when(itemRepository.findAll(any(BooleanExpression.class), eq(pageable))).thenReturn(page);

        List<ItemResponseDto> response = service.search(userId, "test", 0, 10);

        assertThat(response, empty());
        verify(itemRepository, times(1)).findAll(any(BooleanExpression.class), any(Pageable.class));
    }

    @Test
    void addComment_whenBookingComplited_thenUserCreatedComment() {
        Long itemId = 1L;
        long userId = 2L;
        Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(someItem)
                .booker(anotherUser)
                .status(BookingStatus.APPROVED)
                .build();
        CommentInputDto dto = CommentInputDto.builder()
                .text("test comment")
                .build();
        Comment comment = CommentMapper.toEntity(dto, someItem, anotherUser);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(someItem));
        when(userService.findUserOrThrow(userId)).thenReturn(anotherUser);
        when(bookingRepository.existsByItem_IdAndBooker_IdAndEndIsBefore(eq(itemId), eq(userId), any(LocalDateTime.class))).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponseDto response = service.addComment(itemId, userId, dto);

        assertThat(response, notNullValue());
        assertThat(response.getText(), equalTo(dto.getText()));
        assertThat(response.getAuthorName(), equalTo(anotherUser.getName()));
        verify(itemRepository, times(1)).findById(anyLong());
        verify(userService, times(1)).findUserOrThrow(anyLong());
        verify(bookingRepository, times(1)).existsByItem_IdAndBooker_IdAndEndIsBefore(anyLong(), anyLong(), any(LocalDateTime.class));
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addComment_whenBookingNotComplited_thenValidationExceptionThrown() {
        Long itemId = 1L;
        long userId = 2L;
        CommentInputDto dto = CommentInputDto.builder()
                .text("test comment")
                .build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(someItem));
        when(userService.findUserOrThrow(userId)).thenReturn(anotherUser);
        when(bookingRepository.existsByItem_IdAndBooker_IdAndEndIsBefore(eq(itemId), eq(userId), any(LocalDateTime.class))).thenReturn(false);

        assertThrows(ValidationException.class, () -> service.addComment(itemId, userId, dto));
        verify(itemRepository, times(1)).findById(anyLong());
        verify(userService, times(1)).findUserOrThrow(anyLong());
        verify(bookingRepository, times(1)).existsByItem_IdAndBooker_IdAndEndIsBefore(anyLong(), anyLong(), any(LocalDateTime.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }
}
