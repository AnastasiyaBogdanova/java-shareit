package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final AtomicLong counter = new AtomicLong(0);

    public Item createItem(Item item, User owner) {
        if (item.getId() == null) {
            item.setId(counter.incrementAndGet());
        }
        item.setOwner(owner);
        items.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Long userId, Long itemId, Item itemUpdates) {
        Item existingItem = findById(itemId);

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }

        if (itemUpdates.getName() != null) {
            existingItem.setName(itemUpdates.getName());
        }
        if (itemUpdates.getDescription() != null) {
            existingItem.setDescription(itemUpdates.getDescription());
        }
        if (itemUpdates.getAvailable() != null) {
            existingItem.setAvailable(itemUpdates.getAvailable());
        }

        items.put(itemId, existingItem);
        return existingItem;
    }

    public Item findById(Long id) {
        return Optional.ofNullable(items.get(id))
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + id + " не найдена"));
    }

    public List<Item> findByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    public List<Item> searchAvailableItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String lowerText = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(lowerText) ||
                        item.getDescription().toLowerCase().contains(lowerText))
                .collect(Collectors.toList());
    }

}