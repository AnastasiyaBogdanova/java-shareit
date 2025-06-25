package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private UserShortDto booker;
    private BookingStatus status;

}