package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestMapperTest {

    private final ItemRequestMapper mapper = new ItemRequestMapper();

    @Test
    void toDto_shouldConvertEntityToDto() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("утюг");
        request.setCreated(LocalDateTime.of(2025, 1, 1, 10, 0));

        ItemRequestDto dto = mapper.toDto(request);

        assertEquals(request.getId(), dto.getId());
        assertEquals(request.getDescription(), dto.getDescription());
        assertEquals(request.getCreated(), dto.getCreated());
    }

    @Test
    void toDtoWithItems_shouldConvertEntityToDtoWithItems() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("утюг");
        request.setCreated(LocalDateTime.of(2025, 1, 1, 10, 0));

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("утюг");

        ItemRequestWithItemsDto dto = mapper.toDtoWithItems(request, List.of(itemDto));

        assertEquals(request.getId(), dto.getId());
        assertEquals(request.getDescription(), dto.getDescription());
        assertEquals(request.getCreated(), dto.getCreated());
        assertEquals(1, dto.getItems().size());
        assertEquals(itemDto.getId(), dto.getItems().get(0).getId());
    }

    @Test
    void toEntity_shouldConvertDtoToEntity() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("утюг");
        dto.setCreated(LocalDateTime.of(2025, 1, 1, 10, 0));

        User requester = new User();
        requester.setId(1L);

        ItemRequest request = mapper.toEntity(dto);
        request.setRequester(requester);

        assertEquals(dto.getId(), request.getId());
        assertEquals(dto.getDescription(), request.getDescription());
        assertEquals(dto.getCreated(), request.getCreated());
        assertEquals(requester.getId(), request.getRequester().getId());
    }

    @Test
    void toDto_shouldConvertCollectionOfEntities() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("утюг");
        request.setCreated(LocalDateTime.of(2025, 1, 1, 10, 0));

        List<ItemRequestDto> dtos = new ArrayList<>(mapper.toDto(Collections.singletonList(request)));

        assertEquals(1, dtos.size());
        assertEquals(request.getId(), dtos.get(0).getId());
    }
}