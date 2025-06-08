package ru.practicum.shareit.booking.model;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    private Long id;
    @FutureOrPresent(message = "Дата начала бронирования должна быть в настоящем или будущем")
    private LocalDateTime start;
    @Future(message = "Дата окончания бронирования должна быть в будущем")
    private LocalDateTime end;
    @NotNull(message = "Вещь для бронирования не может быть пустой")
    private Long itemId;
    private User booker;
    private BookingStatus status;
}