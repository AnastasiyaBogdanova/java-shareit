package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    private final ItemMapper itemMapper = new ItemMapper();

    @Test
    void toDto_shouldConvertItemToDto() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("User");

        Item item = new Item();
        item.setId(1L);
        item.setName("утюг");
        item.setDescription("хороший");
        item.setAvailable(true);
        item.setOwner(owner);

        ItemDto dto = itemMapper.toDto(item);

        assertEquals(1L, dto.getId());
        assertEquals("утюг", dto.getName());
        assertEquals("хороший", dto.getDescription());
        assertTrue(dto.getAvailable());
    }

    @Test
    void toDto_shouldConvertCollectionOfItems() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("User");

        Item item = new Item();
        item.setId(1L);
        item.setName("утюг");
        item.setDescription("хороший");
        item.setAvailable(true);
        item.setOwner(owner);

        List<ItemDto> dtos = new ArrayList<>(itemMapper.toDto(Collections.singletonList(item)));

        assertEquals(1, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals("утюг", dtos.get(0).getName());
    }

    @Test
    void toEntity_shouldConvertDtoToItem() {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("утюг");
        dto.setDescription("хороший");
        dto.setAvailable(true);

        Item item = itemMapper.toEntity(dto);

        assertEquals(1L, item.getId());
        assertEquals("утюг", item.getName());
        assertEquals("хороший", item.getDescription());
        assertTrue(item.getAvailable());
    }
}