package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long counter = 0;

    public Item createItem(Item item) {
        if (item.getId() == null) {
            item.setId(++counter);
        }
        items.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Long userId, Long itemId, Item itemUpdates) {
        Optional<Item> existingItem = findById(itemId);

        if (!existingItem.get().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }

        if (itemUpdates.getName() != null) {
            existingItem.get().setName(itemUpdates.getName());
        }
        if (itemUpdates.getDescription() != null) {
            existingItem.get().setDescription(itemUpdates.getDescription());
        }
        if (itemUpdates.getAvailable() != null) {
            existingItem.get().setAvailable(itemUpdates.getAvailable());
        }

        items.put(itemId, existingItem.get());
        return existingItem.get();
    }

    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
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