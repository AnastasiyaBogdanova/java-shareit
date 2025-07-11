package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class UserDtoForCreateTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testUserDtoForCreateSerialization() throws JsonProcessingException {
        UserDtoForCreate userDto = new UserDtoForCreate();
        userDto.setName("Test User");
        userDto.setEmail("test@ya.ru");

        String json = objectMapper.writeValueAsString(userDto);

        assertTrue(json.contains("\"name\":\"Test User\""));
        assertTrue(json.contains("\"email\":\"test@ya.ru\""));
    }

    @Test
    void testUserDtoForCreateDeserialization() throws JsonProcessingException {
        String json = "{\"name\":\"Test User\",\"email\":\"test@ya.ru\"}";

        UserDtoForCreate userDto = objectMapper.readValue(json, UserDtoForCreate.class);

        assertEquals("Test User", userDto.getName());
        assertEquals("test@ya.ru", userDto.getEmail());
    }

    @Test
    void testInheritanceFromUserDto() {
        UserDtoForCreate userDto = new UserDtoForCreate();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@ya.ru");

        assertInstanceOf(UserDto.class, userDto);
        assertEquals(1L, userDto.getId());
    }
}