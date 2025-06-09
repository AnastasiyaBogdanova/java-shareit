package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    public ItemDto createItem(Long userId, ItemDtoForCreate itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        Item item = itemMapper.toEntity(itemDto);
        item.setOwner(owner);
        Item savedItem = itemRepository.createItem(item);
        return itemMapper.toDto(savedItem);
    }

    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        userRepository.findById(userId);
        itemRepository.findById(itemId);
        Item item = itemMapper.toEntity(itemDto);
        Item updatedItem = itemRepository.updateItem(userId, itemId, item);
        return itemMapper.toDto(updatedItem);
    }

    public ItemDto getItemById(Long itemId, Long userId) {
        userRepository.findById(userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));
        return itemMapper.toDto(item);
    }

    public List<ItemDto> getUserItems(Long userId) {
        userRepository.findById(userId);

        return new ArrayList<>(itemMapper.toDto(itemRepository.findByOwnerId(userId)));
    }

    public List<ItemDto> searchItems(String text, Long userId) {
        userRepository.findById(userId);

        return new ArrayList<>(itemMapper.toDto(itemRepository.searchAvailableItems(text)));
    }
}