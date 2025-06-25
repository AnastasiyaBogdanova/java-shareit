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
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

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

    public ItemDto createItem(Long userId, ItemDtoForCreate itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        Item item = itemMapper.toEntity(itemDto);
        item.setOwner(owner);
        Item savedItem = itemRepository.save(item);
        return itemMapper.toDto(savedItem);
    }

    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));

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
        userRepository.findById(userId);

        return new ArrayList<>(itemMapper.toDto(itemRepository.findByOwnerId(userId)));
    }

    public List<ItemDto> searchItems(String text, Long userId) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        userRepository.findById(userId);

        return new ArrayList<>(itemMapper.toDto(itemRepository.searchAvailableItems(text)));
    }

    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));

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
            itemDto.setLastBooking(new Booker(
                    lastBooking.getId(), lastBooking.getBooker().getId()));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(new Booker(
                    nextBooking.getId(), nextBooking.getBooker().getId()));
        }
    }

    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));

        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
                userId, itemId, LocalDateTime.now())) {
            throw new NotAvailableException("Пользователь не может забронировать свою же вещь");
        }

        Comment comment = commentMapper.toEntity(commentDto, item, author);
        return commentMapper.toDto(commentRepository.save(comment));
    }
}