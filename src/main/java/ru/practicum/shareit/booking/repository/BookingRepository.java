package ru.practicum.shareit.booking.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class BookingRepository {
    private final Map<Long, Booking> bookings = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Booking createBooking(Booking booking, User booker, Item item) {
        if (!item.getAvailable()) {
            throw new UnavailableException("Вещь не доступна");
        }
        booking.setBooker(booker);
        booking.setItemId(item.getId());
        booking.setStatus(BookingStatus.WAITING);
        booking.setId(idGenerator.getAndIncrement());
        bookings.put(booking.getId(), booking);
        return booking;
    }

    public Booking approveBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = findById(bookingId);

        /*if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Only owner can approve booking");
        }*/

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new UnavailableException("Booking status is already set");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return save(booking);
    }

    public Booking findBookingForUser(Long userId, Long bookingId) {
        Booking booking = findById(bookingId);

        /*if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Only booker or owner can view booking");
        }*/

        return booking;
    }

    public List<Booking> findUserBookings(Long userId, String state, Integer from, Integer size) {
        List<Booking> bookings = findAllByBookerId(userId).stream()
                .filter(b -> filterByState(b, state))
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());

        return bookings;
    }

    public List<Booking> findOwnerBookings(Long userId, String state, Integer from, Integer size) {
        List<Booking> bookings = findAllByItemOwnerId(userId).stream()
                .filter(b -> filterByState(b, state))
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());

        return bookings;
    }

    private boolean filterByState(Booking booking, String state) {
        switch (state.toUpperCase()) {
            case "ALL":
                return true;
            case "WAITING":
                return booking.getStatus() == BookingStatus.WAITING;
            case "REJECTED":
                return booking.getStatus() == BookingStatus.REJECTED;
            case "CANCELED":
                return booking.getStatus() == BookingStatus.CANCELLED;
            default:
                throw new UnavailableException("Unknown state: " + state);
        }
    }

    public Booking save(Booking booking) {
        bookings.put(booking.getId(), booking);
        return booking;
    }

    public Booking findById(Long id) {
        return Optional.ofNullable(bookings.get(id))
                .orElseThrow(() -> new NotFoundException("Бронирование с ID " + id + " не найдено"));
    }

    public List<Booking> findAllByBookerId(Long bookerId) {
        return bookings.values().stream()
                .filter(booking -> booking.getBooker() != null && booking.getBooker().getId().equals(bookerId))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
    }

    public List<Booking> findAllByItemOwnerId(Long ownerId) {
        return /*bookings.values().stream()
                .filter(booking -> booking.getItemId() != null &&
                        booking.getItem().getOwner() != null &&
                        booking.getItem().getOwner().getId().equals(ownerId))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());*/null;
    }
}