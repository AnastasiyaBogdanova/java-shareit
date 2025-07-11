package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class BookingRequestDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testBookingRequestDtoSerialization() throws JsonProcessingException {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 2, 12, 0);

        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(start);
        bookingRequestDto.setEnd(end);

        String json = objectMapper.writeValueAsString(bookingRequestDto);

        assertTrue(json.contains("\"itemId\":1"));
        assertTrue(json.contains("\"start\":\"2025-01-01T12:00:00\""));
        assertTrue(json.contains("\"end\":\"2025-01-02T12:00:00\""));
        assertFalse(json.contains("\"status\""));
    }

    @Test
    void testBookingRequestDtoDeserialization() throws JsonProcessingException {
        String json = "{\"itemId\":1,\"start\":\"2025-01-01T12:00:00\",\"end\":\"2025-01-02T12:00:00\"}";

        BookingRequestDto bookingRequestDto = objectMapper.readValue(json, BookingRequestDto.class);

        assertEquals(1L, bookingRequestDto.getItemId());
        assertEquals(LocalDateTime.of(2025, 1, 1, 12, 0), bookingRequestDto.getStart());
        assertEquals(LocalDateTime.of(2025, 1, 2, 12, 0), bookingRequestDto.getEnd());
    }
}