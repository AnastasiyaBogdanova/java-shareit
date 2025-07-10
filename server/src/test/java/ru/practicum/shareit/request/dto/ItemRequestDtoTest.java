package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void itemRequestDtoSerializationTest() throws JsonProcessingException {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("утюг");
        requestDto.setCreated(LocalDateTime.of(2025, 1, 1, 10, 0));

        String json = objectMapper.writeValueAsString(requestDto);

        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"description\":\"утюг\""));
        assertTrue(json.contains("\"created\":\"2025-01-01T10:00:00\""));
    }

    @Test
    void itemRequestDtoDeserializationTest() throws JsonProcessingException {
        String json = "{\"id\":1,\"description\":\"утюг\",\"created\":\"2025-01-01T10:00:00\"}";

        ItemRequestDto requestDto = objectMapper.readValue(json, ItemRequestDto.class);

        assertEquals(1L, requestDto.getId());
        assertEquals("утюг", requestDto.getDescription());
        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), requestDto.getCreated());
    }

    @Test
    void itemRequestWithItemsDtoSerializationTest() throws JsonProcessingException {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("утюг");

        ItemRequestWithItemsDto requestDto = new ItemRequestWithItemsDto();
        requestDto.setId(1L);
        requestDto.setDescription("утюг");
        requestDto.setCreated(LocalDateTime.of(2025, 1, 1, 10, 0));
        requestDto.setItems(List.of(itemDto));

        String json = objectMapper.writeValueAsString(requestDto);

        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"description\":\"утюг\""));
        assertTrue(json.contains("\"created\":\"2025-01-01T10:00:00\""));
        assertTrue(json.contains("\"items\":[{\"id\":1"));
    }

    @Test
    void itemRequestWithItemsDtoDeserializationTest() throws JsonProcessingException {
        String json = "{\"id\":1,\"description\":\"утюг\",\"created\":\"2025-01-01T10:00:00\",\"items\":[{\"id\":1,\"name\":\"утюг\"}]}";

        ItemRequestWithItemsDto requestDto = objectMapper.readValue(json, ItemRequestWithItemsDto.class);

        assertEquals(1L, requestDto.getId());
        assertEquals("утюг", requestDto.getDescription());
        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), requestDto.getCreated());
        assertEquals(1, requestDto.getItems().size());
        assertEquals(1L, requestDto.getItems().get(0).getId());
    }
}