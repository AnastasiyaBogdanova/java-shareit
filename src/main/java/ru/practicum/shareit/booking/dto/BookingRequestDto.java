package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.time.LocalDateTime;

@Data
public class BookingRequestDto {
    @NotNull(message = "ID не должен быть пустым")
    private Long itemId;

    @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом")
    @NotNull(message = "Дата начала бронирования не может быть пустой")
    private LocalDateTime start;

    @Future(message = "Дата окончания бронирования должна быть в будущем")
    @NotNull(message = "Дата окончания бронирования не может быть пустой")
    private LocalDateTime end;
}