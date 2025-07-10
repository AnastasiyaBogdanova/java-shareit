package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class BookingServiceIntegrationTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private BookingService bookingService;

    @Test
    void createBooking_shouldSaveBookingToDatabase() {
        bookingService = new BookingService(bookingRepository, itemRepository,
                userRepository, new BookingMapper());

        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@ya.ru");
        em.persist(owner);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@ya.ru");
        em.persist(booker);

        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        BookingRequestDto bookingDto = new BookingRequestDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto result = bookingService.createBooking(bookingDto, booker.getId());

        assertNotNull(result.getId());
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(booker.getId(), result.getBooker().getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());

        Booking savedBooking = em.find(Booking.class, result.getId());
        assertEquals(item.getId(), savedBooking.getItem().getId());
        assertEquals(booker.getId(), savedBooking.getBooker().getId());
    }

    @Test
    void approveBooking_shouldUpdateBookingStatus() {
        bookingService = new BookingService(bookingRepository, itemRepository,
                userRepository, new BookingMapper());

        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@ya.ru");
        em.persist(owner);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@ya.ru");
        em.persist(booker);

        Item item = new Item();
        item.setName("утюг");
        item.setDescription("хороший");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);

        BookingDto result = bookingService.approveBooking(booking.getId(), owner.getId(), true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());

        Booking updatedBooking = em.find(Booking.class, booking.getId());
        assertEquals(BookingStatus.APPROVED, updatedBooking.getStatus());
    }

    @Test
    void getUserBookings_shouldReturnBookingsForUser() {
        bookingService = new BookingService(bookingRepository, itemRepository,
                userRepository, new BookingMapper());

        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@ya.ru");
        em.persist(owner);

        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@ya.ru");
        em.persist(booker);

        Item item = new Item();
        item.setName("утюг");
        item.setDescription("хороший");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        em.persist(booking);
        List<BookingDto> results = bookingService.getUserBookings(booker.getId(), BookingState.ALL);

        assertEquals(1, results.size());
        assertEquals(booking.getId(), results.get(0).getId());
    }
}