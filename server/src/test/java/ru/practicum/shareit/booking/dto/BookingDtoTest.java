package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
class BookingDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void bookingDtoSerializationTest() throws JsonProcessingException {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2025, 1, 1, 10, 0));
        bookingDto.setEnd(LocalDateTime.of(2025, 1, 2, 10, 0));
        bookingDto.setItem(new Item());
        bookingDto.setBooker(new UserShortDto(1L, "Booker"));
        bookingDto.setStatus(BookingStatus.WAITING);

        String json = objectMapper.writeValueAsString(bookingDto);

        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"start\":\"2025-01-01T10:00:00\""));
        assertTrue(json.contains("\"end\":\"2025-01-02T10:00:00\""));
        assertTrue(json.contains("\"status\":\"WAITING\""));
    }

    @Test
    void bookingDtoDeserializationTest() throws JsonProcessingException {
        String json = "{\"id\":1,\"start\":\"2025-01-01T10:00:00\",\"end\":\"2025-01-02T10:00:00\",\"status\":\"WAITING\"}";

        BookingDto bookingDto = objectMapper.readValue(json, BookingDto.class);

        assertEquals(1L, bookingDto.getId());
        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), bookingDto.getStart());
        assertEquals(LocalDateTime.of(2025, 1, 2, 10, 0), bookingDto.getEnd());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
    }

    @Test
    void bookingRequestDtoSerializationTest() throws JsonProcessingException {
        BookingRequestDto bookingDto = new BookingRequestDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.of(2025, 1, 1, 10, 0));
        bookingDto.setEnd(LocalDateTime.of(2025, 1, 2, 10, 0));

        String json = objectMapper.writeValueAsString(bookingDto);

        assertTrue(json.contains("\"itemId\":1"));
        assertTrue(json.contains("\"start\":\"2025-01-01T10:00:00\""));
        assertTrue(json.contains("\"end\":\"2025-01-02T10:00:00\""));
    }

    @Test
    void bookingRequestDtoDeserializationTest() throws JsonProcessingException {
        String json = "{\"itemId\":1,\"start\":\"2025-01-01T10:00:00\",\"end\":\"2025-01-02T10:00:00\"}";

        BookingRequestDto bookingDto = objectMapper.readValue(json, BookingRequestDto.class);

        assertEquals(1L, bookingDto.getItemId());
        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), bookingDto.getStart());
        assertEquals(LocalDateTime.of(2025, 1, 2, 10, 0), bookingDto.getEnd());
    }
}