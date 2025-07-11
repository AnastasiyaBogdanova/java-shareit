package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForCreate;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ItemRequestRepository requestRepository;

    public ItemDto createItem(Long userId, ItemDtoForCreate itemDto) {
        User owner = findUserById(userId);
        Item item = itemMapper.toEntity(itemDto);
        item.setOwner(owner);
        if (itemDto.getRequestId() != null) {
            ItemRequest request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос с ID " + itemDto.getRequestId() + " не найден"));
            item.setRequest(request);
        }
        Item savedItem = itemRepository.save(item);
        return itemMapper.toDto(savedItem);
    }

    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item existingItem = findItemById(itemId);

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return itemMapper.toDto(itemRepository.save(existingItem));
    }

    public List<ItemDto> getUserItems(Long userId) {
        findUserById(userId);

        return new ArrayList<>(itemMapper.toDto(itemRepository.findByOwnerId(userId)));
    }

    public List<ItemDto> searchItems(String text, Long userId) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        findUserById(userId);

        return new ArrayList<>(itemMapper.toDto(itemRepository.searchAvailableItems(text)));
    }

    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = findItemById(itemId);

        ItemDto itemDto = itemMapper.toDto(item);

        if (item.getOwner().getId().equals(userId)) {
            addBookingDates(itemDto);
        }

        itemDto.setComments(commentRepository.findByItemId(itemId));

        return itemDto;
    }

    private void addBookingDates(ItemDto itemDto) {
        LocalDateTime now = LocalDateTime.now();

        Booking lastBooking = bookingRepository
                .findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(
                        itemDto.getId(), now, BookingStatus.APPROVED);
        Booking nextBooking = bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                        itemDto.getId(), now, BookingStatus.APPROVED);

        if (lastBooking != null) {
            itemDto.setLastBooking(new UserShortDto(
                    lastBooking.getId(), lastBooking.getBooker().getName()));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(new UserShortDto(
                    nextBooking.getId(), nextBooking.getBooker().getName()));
        }
    }

    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = findUserById(userId);
        Item item = findItemById(itemId);

        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
                userId, itemId, LocalDateTime.now())) {
            throw new NotAvailableException("Пользователь не может забронировать свою же вещь");
        }

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
    }

    Item findItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + id + " не найдена"));
    }

}