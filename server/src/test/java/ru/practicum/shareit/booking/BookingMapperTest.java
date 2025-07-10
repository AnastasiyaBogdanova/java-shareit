package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {

    private final BookingMapper bookingMapper = new BookingMapper();

    @Test
    void toEntity_shouldConvertRequestDtoToEntity() {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setStart(LocalDateTime.of(2025, 1, 1, 10, 0));
        dto.setEnd(LocalDateTime.of(2025, 1, 2, 10, 0));

        Booking booking = bookingMapper.toEntity(dto);

        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void toDto_shouldConvertEntityToDto() {
        User booker = new User();
        booker.setId(1L);
        booker.setName("User");

        Item item = new Item();
        item.setId(1L);
        item.setName("Item");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2025, 1, 1, 10, 0));
        booking.setEnd(LocalDateTime.of(2025, 1, 2, 10, 0));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        BookingDto dto = bookingMapper.toDto(booking);

        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(booking.getItem().getId(), dto.getItem().getId());
        assertEquals(booking.getBooker().getId(), dto.getBooker().getId());
        assertEquals(booking.getStatus(), dto.getStatus());
    }

    @Test
    void toDto_shouldConvertCollectionOfEntities() {
        User booker = new User();
        booker.setId(1L);
        booker.setName("User");

        Item item = new Item();
        item.setId(1L);
        item.setName("Item");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2025, 1, 1, 10, 0));
        booking.setEnd(LocalDateTime.of(2025, 1, 2, 10, 0));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        List<BookingDto> dtos = new ArrayList<>(bookingMapper.toDto(Collections.singletonList(booking)));

        assertEquals(1, dtos.size());
        assertEquals(booking.getId(), dtos.get(0).getId());
    }
}