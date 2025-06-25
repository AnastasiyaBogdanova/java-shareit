package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.NotFoundException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState fromString(String state) {
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Неизвестный статус: " + state);
        }
    }
}