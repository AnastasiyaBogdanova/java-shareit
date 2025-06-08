package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    public BookingDto createBooking(Long userId, Booking booking) {
        User booker = userRepository.findById(userId);

        Item item = itemRepository.findById(booking.getItemId());

        Booking createdBooking = bookingRepository.createBooking(booking, booker, item);
        return bookingMapper.toBookingDto(createdBooking);
    }

    public BookingDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        userRepository.findById(userId);
        Booking approvedBooking = bookingRepository.approveBooking(userId, bookingId, approved);
        return bookingMapper.toBookingDto(approvedBooking);
    }

    public BookingDto getBooking(Long userId, Long bookingId) {
        userRepository.findById(userId);
        Booking booking = bookingRepository.findBookingForUser(userId, bookingId);
        return bookingMapper.toBookingDto(booking);
    }

    public List<BookingDto> getUserBookings(Long userId, String state, Integer from, Integer size) {
        userRepository.findById(userId);
        return bookingRepository.findUserBookings(userId, state, from, size).stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    public List<BookingDto> getOwnerBookings(Long userId, String state, Integer from, Integer size) {
        userRepository.findById(userId);
        return bookingRepository.findOwnerBookings(userId, state, from, size).stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}