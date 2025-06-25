package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class BookingMapper {
    public Booking toEntity(BookingRequestDto dto, Item item, User booker) {
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    public Collection<BookingDto> toDto(Collection<Booking> bookings) {
        return bookings.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(new Booker(booking.getBooker().getId(), booking.getBooker().getName()))
                .status(booking.getStatus())
                .build();
    }
}