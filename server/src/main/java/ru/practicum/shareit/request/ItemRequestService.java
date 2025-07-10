package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper requestMapper;
    private final ItemMapper itemMapper;

    public ItemRequestDto createRequest(Long userId, ItemRequestDto requestDto) {
        User requester = findUserById(userId);

        ItemRequest request = requestMapper.toEntity(requestDto);
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());

        ItemRequest savedRequest = requestRepository.save(request);
        return requestMapper.toDto(savedRequest);
    }

    public List<ItemRequestWithItemsDto> getUserRequests(Long userId) {
        findUserById(userId);

        List<ItemRequest> requests = requestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        return addItemsToRequests(requests);
    }

    public List<ItemRequestWithItemsDto> getAllRequests(Long userId, Integer from, Integer size) {
        findUserById(userId);

        PageRequest page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> requests = requestRepository.findByRequesterIdNot(userId, page).getContent();

        return addItemsToRequests(requests);
    }

    public ItemRequestWithItemsDto getRequestById(Long requestId, Long userId) {
        findUserById(userId);

        ItemRequest request = findRequestById(requestId);
        List<ItemDto> items = new ArrayList<>(itemMapper.toDto(itemRepository.findByRequestId(requestId)));

        return requestMapper.toDtoWithItems(request, items);
    }

    private List<ItemRequestWithItemsDto> addItemsToRequests(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<Item>> itemsByRequest = itemRepository.findByRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> {
                    List<ItemDto> itemDtos = itemsByRequest.getOrDefault(request.getId(), Collections.emptyList())
                            .stream()
                            .map(itemMapper::toDto)
                            .collect(Collectors.toList());
                    return requestMapper.toDtoWithItems(request, itemDtos);
                })
                .collect(Collectors.toList());
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
    }

    private ItemRequest findRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Запрос с ID " + id + " не найден"));
    }
}