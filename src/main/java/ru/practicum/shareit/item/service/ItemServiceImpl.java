package ru.practicum.shareit.item.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.entity.QItem;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import static ru.practicum.shareit.exception.errors.ErrorMessage.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ItemDto create(Long ownerId, ItemCreateDto dto) {
        User owner = userService.findByIdOrThrow(ownerId);
        Item item = itemRepository.save(ItemMapper.toEntity(dto, owner));
        log.info("Добавлена сущность Item: {}", item);
        return ItemMapper.toDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(Long itemId, Long userId, ItemUpdateDto dto) {
        Item item = findByIdOrThrow(itemId);
        if (!isOwnerItem(item, userId)) {
            log.warn("Отказано в доступе пользователю userId = {} при обновлении сущности itemId = {}", userId, itemId);
            throw new AccessDeniedException(ONLY_OWNER_MODIFY);
        }
        ItemMapper.updateFieldsItem(item, dto);
        log.info("Обновлена сущность Item: {}", item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemFullDto getById(Long itemId, Long userId) {
        Item item = findByIdOrThrow(itemId);
        log.info("Найдена сущность Item: {}", item);
        LocalDateTime last = findLastDateBooking(item, userId);
        LocalDateTime next = findNextDateBooking(item, userId);
        Collection<CommentDto> comments = findItemComments(itemId);
        return ItemMapper.toFullDto(item, last, next, comments);
    }

    @Override
    public Item findByIdOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND));
    }

    @Override
    public Collection<ItemDto> getAllById(Long ownerId) {
        return itemRepository.findAllByOwner_Id(ownerId)
                .stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> search(String text, int from, int size) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        String search = text.toLowerCase();
        BooleanExpression byName = QItem.item.name.toLowerCase().contains(search);
        BooleanExpression byDescription = QItem.item.description.toLowerCase().contains(search);
        BooleanExpression byAvailable = QItem.item.available.eq(true);
        BooleanExpression bySearch = byName.or(byDescription);
        return itemRepository.findAll(bySearch.and(byAvailable), page)
                .map(ItemMapper::toDto)
                .getContent();
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long authorId, CommentCreateDto dto) {
        Item item = findByIdOrThrow(itemId);
        User author = userService.findByIdOrThrow(authorId);
        if (!isUserCompletedBooking(itemId, authorId)) {
            log.info("Попытка оставить отзыв на вещь без аренды");
            throw new ValidationException(COMPLETED_BOOKING_NOT_FOUND);
        }
        Comment comment = commentRepository.save(CommentMapper.toEntity(dto, item, author));
        log.info("Добавлена сущность Comment: {}", comment);
        return CommentMapper.toDto(comment);
    }

    @Override
    public boolean hasUserAnyItems(Long ownerId) {
        return itemRepository.existsByOwner_Id(ownerId);
    }

    private boolean isOwnerItem(Item item, Long ownerId) {
        return Objects.equals(item.getOwner().getId(), ownerId);
    }

    private LocalDateTime findLastDateBooking(Item item, Long userId) {
        return isOwnerItem(item, userId)
                ? bookingRepository.findFirstByItem_IdAndStatusAndEndIsBeforeOrderByEndDesc(
                        item.getOwner().getId(), BookingStatus.APPROVED, LocalDateTime.now())
                .map(Booking::getEnd)
                .orElse(null)
                : null;
    }

    private LocalDateTime findNextDateBooking(Item item, Long userId) {
        return isOwnerItem(item, userId)
                ? bookingRepository.findFirstByItem_IdAndStatusAndStartIsAfterOrderByStartAsc(
                        item.getOwner().getId(), BookingStatus.APPROVED, LocalDateTime.now())
                .map(Booking::getStart)
                .orElse(null)
                : null;
    }

    private Collection<CommentDto> findItemComments(Long itemId) {
        return commentRepository.findAllByItem_Id(itemId)
                .stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    private boolean isUserCompletedBooking(Long itemId, Long userId) {
        return bookingRepository.existsByItem_IdAndBooker_IdAndEndIsBefore(itemId, userId, LocalDateTime.now());
    }
}
