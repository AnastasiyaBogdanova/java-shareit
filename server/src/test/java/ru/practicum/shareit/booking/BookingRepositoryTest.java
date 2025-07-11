package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository bookingRepository;

    private User booker;
    private Item item;
    private Booking pastBooking;
    private Booking currentBooking;
    private Booking futureBooking;

    @BeforeEach
    void setUp() {
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@ya.ru");
        em.persist(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@ya.ru");
        em.persist(booker);

        item = new Item();
        item.setName("утюг");
        item.setDescription("хороший");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        LocalDateTime now = LocalDateTime.now();

        pastBooking = new Booking();
        pastBooking.setStart(now.minusDays(2));
        pastBooking.setEnd(now.minusDays(1));
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStatus(BookingStatus.APPROVED);
        em.persist(pastBooking);

        currentBooking = new Booking();
        currentBooking.setStart(now.minusHours(1));
        currentBooking.setEnd(now.plusHours(1));
        currentBooking.setItem(item);
        currentBooking.setBooker(booker);
        currentBooking.setStatus(BookingStatus.APPROVED);
        em.persist(currentBooking);

        futureBooking = new Booking();
        futureBooking.setStart(now.plusDays(1));
        futureBooking.setEnd(now.plusDays(2));
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStatus(BookingStatus.WAITING);
        em.persist(futureBooking);

    }

    @Test
    void findByBookerIdOrderByStartDesc_shouldReturnAllBookings() {
        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(booker.getId());

        assertEquals(3, bookings.size());
        assertEquals(futureBooking.getId(), bookings.get(0).getId());
        assertEquals(currentBooking.getId(), bookings.get(1).getId());
        assertEquals(pastBooking.getId(), bookings.get(2).getId());
    }

    @Test
    void findByBookerIdAndEndBeforeOrderByStartDesc_shouldReturnPastBookings() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                booker.getId(), LocalDateTime.now());

        assertEquals(1, bookings.size());
        assertEquals(pastBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void findByBookerIdAndStartAfterOrderByStartDesc_shouldReturnFutureBookings() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                booker.getId(), LocalDateTime.now());

        assertEquals(1, bookings.size());
        assertEquals(futureBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc_shouldReturnCurrentBookings() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                booker.getId(), LocalDateTime.now(), LocalDateTime.now());

        assertEquals(1, bookings.size());
        assertEquals(currentBooking.getId(), bookings.get(0).getId());
    }

    @Test
    void findByBookerIdAndStatusOrderByStartDesc_shouldReturnByStatus() {
        List<Booking> waiting = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                booker.getId(), BookingStatus.WAITING);

        assertEquals(1, waiting.size());
        assertEquals(futureBooking.getId(), waiting.get(0).getId());

        List<Booking> approved = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                booker.getId(), BookingStatus.APPROVED);

        assertEquals(2, approved.size());
    }

    @Test
    void findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc_shouldReturnLastPastBooking() {
        Booking result = bookingRepository.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(
                item.getId(), LocalDateTime.now(), BookingStatus.APPROVED);

        assertEquals(pastBooking.getId(), result.getId());
    }

    @Test
    void existsByBookerIdAndItemIdAndEndBefore_shouldCheckIfUserBookedItem() {
        boolean exists = bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
                booker.getId(), item.getId(), LocalDateTime.now());

        assertTrue(exists);

        boolean notExists = bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
                999L, item.getId(), LocalDateTime.now());

        assertFalse(notExists);
    }
}