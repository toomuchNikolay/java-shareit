package ru.practicum.shareit.exception.errors;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessage {
    public static final String HEADER_USER_ID = "X-Sharer-User-Id";
    public static final String BOOKING_NOT_FOUND = "Бронирование не найдено";
    public static final String ITEM_NOT_FOUND = "Вещь не найдена";
    public static final String USER_NOT_FOUND = "Пользователь не найден";
    public static final String COMPLETED_BOOKING_NOT_FOUND = "Оставлять комментарии могут только пользователи к вещам, которые брали в аренду";
    public static final String BOOKING_ITEM_UNAVAILABLE = "Вещь для бронирования недоступна";
    public static final String BOOKING_TIME_INCORRECT = "Проверьте корректность указанного времени бронирования";
    public static final String BOOKING_STATE_INCORRECT = "Проверьте корректность указанного состояния бронирования";
    public static final String ONLY_OWNER_APPROVED = "Подтверждать бронирование может только владелец вещи";
    public static final String ONLY_BOOKER_OR_OWNER_VIEW = "Просматривать информацию о бронировании может только автор бронирования и владелец вещи";
    public static final String ONLY_OWNER_VIEW = "Просматривать список бронирований вещей может только владелец";
    public static final String ONLY_OWNER_MODIFY = "Редактировать данные может только владелец вещи";
    public static final String EMAIL_ALREADY_EXISTS = "Указанный почтовый адрес уже зарегистрирован";
}
