package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;

    public ItemRequestDto createRequest(Long userId, ItemRequest itemRequest) {
        User user = userRepository.findById(userId);

        ItemRequest savedRequest = requestRepository.createRequest(itemRequest, user);
        return ItemRequestMapper.toItemRequestDto(savedRequest);
    }

    public List<ItemRequestDto> getUserRequests(Long userId) {
        userRepository.findById(userId);

        return requestRepository.findUserRequests(userId).stream()
                .map(this::mapToRequestWithItems)
                .collect(Collectors.toList());
    }

    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        userRepository.findById(userId);

        return requestRepository.findAllRequests(userId, from, size).stream()
                .map(this::mapToRequestWithItems)
                .collect(Collectors.toList());
    }

    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId);

        ItemRequest request = requestRepository.findRequestById(requestId);
        return mapToRequestWithItems(request);
    }

    private ItemRequestDto mapToRequestWithItems(ItemRequest request) {
        return ItemRequestMapper.toItemRequestDto(request);
    }
}