package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    public ItemDto createItem(Long userId, Item item) {
        User owner = userRepository.findById(userId);

        Item savedItem = itemRepository.createItem(item, owner);
        return itemMapper.toItemDto(savedItem);
    }

    public ItemDto updateItem(Long userId, Long itemId, Item item) {
        userRepository.findById(userId);
        itemRepository.findById(itemId);
        Item updatedItem = itemRepository.updateItem(userId, itemId, item);
        return itemMapper.toItemDto(updatedItem);
    }

    public ItemDto getItemById(Long itemId, Long userId) {
        userRepository.findById(userId);

        Item item = itemRepository.findById(itemId);
        return itemMapper.toItemDto(item);
    }

    public List<ItemDto> getUserItems(Long userId) {
        userRepository.findById(userId);

        return itemRepository.findByOwnerId(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItems(String text, Long userId) {
        userRepository.findById(userId);

        return itemRepository.searchAvailableItems(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}