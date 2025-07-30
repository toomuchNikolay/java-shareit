package ru.practicum.shareit.item.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.entity.Booking;
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
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.practicum.shareit.exception.errors.ErrorMessage.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRequestService requestService;

    @Override
    @Transactional
    public ItemResponseDto create(long ownerId, ItemInputDto dto) {
        User owner = userService.findUserOrThrow(ownerId);
        ItemRequest request = dto.getRequestId() != null
                ? requestService.findItemRequestOrThrow(dto.getRequestId())
                : null;
        Item item = itemRepository.save(ItemMapper.toEntity(dto, owner, request));
        log.info("Добавлена сущность Item: {}", item);
        return ItemMapper.toDto(item);
    }

    @Override
    @Transactional
    public ItemResponseDto update(Long itemId, long userId, ItemInputDto dto) {
        Item item = findItemOrThrow(itemId);
        if (!isOwnerItem(item, userId)) {
            log.warn("Отказано в доступе пользователю userId = {} при обновлении сущности itemId = {}", userId, itemId);
            throw new AccessDeniedException(ONLY_OWNER_MODIFY);
        }
        updateFields(item, dto);
        log.info("Обновлена сущность Item: {}", item);
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemResponseDetailsDto getById(Long itemId, long userId) {
        Item item = findItemOrThrow(itemId);
        LocalDateTime last = findNearestBooking(item, userId, false);
        LocalDateTime next = findNearestBooking(item, userId, true);
        log.info("Возвращена сущность Item: {}", item);
        return ItemMapper.toDetailsDto(item, last, next);
    }

    @Override
    public List<ItemResponseDto> getAllById(long ownerId, int from, int size) {
        User owner = userService.findUserOrThrow(ownerId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Item> result = itemRepository.findAllByOwner_Id(owner.getId(), page).getContent();
        log.info("Возвращен список в размере {} сущностей Item пользователя id={}", result.size(), ownerId);
        return ItemMapper.toDto(result);
    }

    @Override
    public List<ItemResponseDto> search(long userId, String text, int from, int size) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        BooleanExpression search = QItem.item.owner.id.ne(userId)
                .and(QItem.item.available.isTrue())
                .and(QItem.item.name.containsIgnoreCase(text)
                        .or(QItem.item.description.containsIgnoreCase(text)));
        List<Item> result = itemRepository.findAll(search, page).getContent();
        log.info("Возвращен список в размере {} найденных сущностей Item пользователю id={}", result.size(), userId);
        return ItemMapper.toDto(result);
    }

    @Override
    public Item findItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND));
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(Long itemId, long authorId, CommentInputDto dto) {
        Item item = findItemOrThrow(itemId);
        User author = userService.findUserOrThrow(authorId);
        if (!isUserCompletedBooking(itemId, authorId)) {
            log.info("Попытка оставить отзыв на вещь без аренды");
            throw new ValidationException(COMPLETED_BOOKING_NOT_FOUND);
        }
        Comment comment = commentRepository.save(CommentMapper.toEntity(dto, item, author));
        log.info("Добавлена сущность Comment: {}", comment);
        return CommentMapper.toDto(comment);
    }

    @Override
    public boolean hasUserAnyItems(long ownerId) {
        return itemRepository.existsByOwner_Id(ownerId);
    }

    private boolean isOwnerItem(Item item, long ownerId) {
        return Objects.equals(item.getOwner().getId(), ownerId);
    }

    private void updateFields(Item entity, ItemInputDto dto) {
        Optional.ofNullable(dto.getName()).ifPresent(entity::setName);
        Optional.ofNullable(dto.getDescription()).ifPresent(entity::setDescription);
        Optional.ofNullable(dto.getAvailable()).ifPresent(entity::setAvailable);
    }

    private LocalDateTime findNearestBooking(Item item, long userId, boolean isStart) {
        return isOwnerItem(item, userId)
                ? bookingRepository.findNearestBooking(item.getId(), isStart)
                .map(Booking::getStart)
                .orElse(null)
                : null;
    }

    private boolean isUserCompletedBooking(Long itemId, long userId) {
        return bookingRepository.existsByItem_IdAndBooker_IdAndEndIsBefore(itemId, userId, LocalDateTime.now());
    }
}
