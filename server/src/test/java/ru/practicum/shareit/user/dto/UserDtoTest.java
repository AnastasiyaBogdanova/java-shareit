package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class UserDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void userDtoSerializationTest() throws JsonProcessingException {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Name");
        userDto.setEmail("name@yandex.ru");

        String json = objectMapper.writeValueAsString(userDto);

        assertEquals("{\"id\":1,\"name\":\"Name\",\"email\":\"name@yandex.ru\"}", json);
    }

    @Test
    void userDtoDeserializationTest() throws JsonProcessingException {
        String json = "{\"id\":1,\"name\":\"Name\",\"email\":\"name@yandex.ru\"}";

        UserDto userDto = objectMapper.readValue(json, UserDto.class);

        assertEquals(1L, userDto.getId());
        assertEquals("Name", userDto.getName());
        assertEquals("name@yandex.ru", userDto.getEmail());
    }

    @Test
    void userDtoForCreateSerializationTest() throws JsonProcessingException {
        UserDtoForCreate userDto = new UserDtoForCreate();
        userDto.setName("Name");
        userDto.setEmail("name@yandex.ru");

        String json = objectMapper.writeValueAsString(userDto);

        assertEquals("{\"id\":null,\"name\":\"Name\",\"email\":\"name@yandex.ru\"}", json);
    }

    @Test
    void userDtoForCreateDeserializationTest() throws JsonProcessingException {
        String json = "{\"name\":\"Name\",\"email\":\"name@yandex.ru\"}";

        UserDtoForCreate userDto = objectMapper.readValue(json, UserDtoForCreate.class);

        assertEquals("Name", userDto.getName());
        assertEquals("name@yandex.ru", userDto.getEmail());
    }
}