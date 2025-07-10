package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    public BookingDto createBooking(BookingRequestDto bookingRequestDto, Long userId) {
        User booker = getUser(userId);
        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + bookingRequestDto.getItemId() + " не найдена"));

        if (!item.getAvailable()) {
            throw new NotAvailableException("Вещь недоступна для бронирования");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotAvailableException("Пользователь не может забронировать свою же вещь");
        }
        Booking booking = bookingMapper.toEntity(bookingRequestDto);
        booking.setItem(item);
        booking.setBooker(booker);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    public BookingDto approveBooking(Long bookingId, Long userId, boolean approved) {
        Booking booking = getBooking(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotAvailableException("Только владелец может одобрить бронирование");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new NotAvailableException("Вещь уже забронирована");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = getBooking(bookingId);

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является валадельцем или арендатором вещи");
        }

        return bookingMapper.toDto(booking);
    }

    public List<BookingDto> getUserBookings(Long userId, BookingState state) {
        getUser(userId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                return new ArrayList<>(bookingMapper.toDto(bookingRepository
                        .findByBookerIdOrderByStartDesc(userId)));
            case CURRENT:
                return new ArrayList<>(bookingMapper.toDto(bookingRepository
                        .findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                userId, now, now)));
            case PAST:
                return new ArrayList<>(bookingMapper.toDto(bookingRepository
                        .findByBookerIdAndEndBeforeOrderByStartDesc(userId, now)));
            case FUTURE:
                return new ArrayList<>(bookingMapper.toDto(bookingRepository
                        .findByBookerIdAndStartAfterOrderByStartDesc(userId, now)));
            case WAITING:
                return new ArrayList<>(bookingMapper.toDto(bookingRepository
                        .findByBookerIdAndStatusOrderByStartDesc(
                                userId, BookingStatus.WAITING)));
            case REJECTED:
                return new ArrayList<>(bookingMapper.toDto(bookingRepository
                        .findByBookerIdAndStatusOrderByStartDesc(
                                userId, BookingStatus.REJECTED)));
            default:
                throw new NotFoundException("Неизвестный статус: " + state);
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено: " + bookingId));
    }
}

