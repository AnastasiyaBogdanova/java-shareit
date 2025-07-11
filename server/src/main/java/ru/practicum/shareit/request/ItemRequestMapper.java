package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemRequestMapper {

    public ItemRequestDto toDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public ItemRequestWithItemsDto toDtoWithItems(ItemRequest itemRequest, List<ItemDto> items) {
        return ItemRequestWithItemsDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items != null ? items : Collections.emptyList())
                .build();
    }

    public ItemRequest toEntity(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .build();
    }

    public Collection<ItemRequestDto> toDto(Collection<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}