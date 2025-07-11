package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;
import ru.practicum.shareit.user.dto.UserShortDto;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class ItemDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void itemDtoSerializationTest() throws JsonProcessingException {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("утюг");
        itemDto.setDescription("хороший");
        itemDto.setAvailable(true);
        itemDto.setLastBooking(new UserShortDto(1L, "User"));
        itemDto.setNextBooking(new UserShortDto(2L, "User2"));

        String json = objectMapper.writeValueAsString(itemDto);

        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"name\":\"утюг\""));
        assertTrue(json.contains("\"description\":\"хороший\""));
        assertTrue(json.contains("\"available\":true"));
        assertTrue(json.contains("\"lastBooking\""));
        assertTrue(json.contains("\"nextBooking\""));
    }

    @Test
    void itemDtoDeserializationTest() throws JsonProcessingException {
        String json = "{\"id\":1,\"name\":\"утюг\",\"description\":\"хороший\",\"available\":true}";

        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);

        assertEquals(1L, itemDto.getId());
        assertEquals("утюг", itemDto.getName());
        assertEquals("хороший", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
    }

    @Test
    void itemDtoForCreateSerializationTest() throws JsonProcessingException {
        ItemDtoForCreate itemDto = new ItemDtoForCreate();
        itemDto.setName("утюг");
        itemDto.setDescription("хороший");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);

        String json = objectMapper.writeValueAsString(itemDto);

        assertTrue(json.contains("\"name\":\"утюг\""));
        assertTrue(json.contains("\"description\":\"хороший\""));
        assertTrue(json.contains("\"available\":true"));
        assertTrue(json.contains("\"requestId\":1"));
    }

    @Test
    void itemDtoForCreateDeserializationTest() throws JsonProcessingException {
        String json = "{\"name\":\"утюг\",\"description\":\"хороший\",\"available\":true,\"requestId\":1}";

        ItemDtoForCreate itemDto = objectMapper.readValue(json, ItemDtoForCreate.class);

        assertEquals("утюг", itemDto.getName());
        assertEquals("хороший", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(1L, itemDto.getRequestId());
    }
}