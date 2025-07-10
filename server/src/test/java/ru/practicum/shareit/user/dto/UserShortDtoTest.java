package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class UserShortDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testUserShortDtoSerialization() throws JsonProcessingException {
        UserShortDto userShortDto = UserShortDto.builder()
                .id(1L)
                .name("User")
                .build();

        String json = objectMapper.writeValueAsString(userShortDto);

        assertEquals("{\"id\":1,\"name\":\"User\"}", json);
    }

    @Test
    void testUserShortDtoDeserialization() throws JsonProcessingException {
        String json = "{\"id\":1,\"name\":\"User\"}";

        UserShortDto userShortDto = objectMapper.readValue(json, UserShortDto.class);

        assertEquals(1L, userShortDto.getId());
        assertEquals("User", userShortDto.getName());
    }
}