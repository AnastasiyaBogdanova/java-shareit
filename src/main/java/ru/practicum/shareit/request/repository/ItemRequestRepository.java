package ru.practicum.shareit.request.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ItemRequestRepository {
    private final Map<Long, ItemRequest> requests = new HashMap<>();
    private final AtomicLong counter = new AtomicLong(0);

    public ItemRequest createRequest(ItemRequest request, User requestor) {
        if (request.getId() == null) {
            request.setId(counter.incrementAndGet());
        }
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        requests.put(request.getId(), request);
        return request;
    }

    public ItemRequest findRequestById(Long requestId) {
        return Optional.ofNullable(requests.get(requestId))
                .orElseThrow(() -> new NotFoundException("Запрос  id " + requestId + " не найден"));
    }

    public List<ItemRequest> findUserRequests(Long userId) {
        return requests.values().stream()
                .filter(request -> request.getRequestor().getId().equals(userId))
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .collect(Collectors.toList());
    }

    public List<ItemRequest> findAllRequests(Long userId, Integer from, Integer size) {
        List<ItemRequest> result = requests.values().stream()
                .filter(request -> !request.getRequestor().getId().equals(userId))
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .collect(Collectors.toList());

        int start = Math.min(from, result.size());
        int end = Math.min(start + size, result.size());
        return result.subList(start, end);
    }
}