package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class ItemDtoForCreateTest {

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void testItemDtoForCreateDeserialization() throws JsonProcessingException {
        String json = "{\"name\":\"Item\",\"description\":\"Description\"," +
                "\"available\":true,\"requestId\":10}";

        ItemDtoForCreate itemDto = objectMapper.readValue(json, ItemDtoForCreate.class);

        assertEquals("Item", itemDto.getName());
        assertEquals("Description", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(10L, itemDto.getRequestId());
    }

    @Test
    void testInheritanceFromItemDto() {
        ItemDtoForCreate itemDto = new ItemDtoForCreate();
        itemDto.setId(1L);
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(10L);

        assertInstanceOf(ItemDto.class, itemDto);
        assertEquals(1L, itemDto.getId());
    }
}