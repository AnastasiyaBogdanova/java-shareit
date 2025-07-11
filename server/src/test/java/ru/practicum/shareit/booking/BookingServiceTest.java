package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Autowired
    private TestEntityManager em;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingService bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;
    private UserShortDto userShortDto;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@ya.ru");

        booker = new User();
        booker.setId(2L);
        booker.setName("Booker");
        booker.setEmail("booker@ya.ru");

        userShortDto = new UserShortDto();
        userShortDto.setId(2L);
        userShortDto.setName("Booker");

        item = new Item();
        item.setId(1L);
        item.setName("утюг");
        item.setDescription("хороший");
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setBooker(userShortDto);
        bookingDto.setItem(item);

        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void createBooking_shouldCreateBooking() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingMapper.toEntity(any(BookingRequestDto.class))).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto result = bookingService.createBooking(bookingRequestDto, 2L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowWhenItemNotAvailable() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        item.setAvailable(false);

        assertThrows(NotAvailableException.class, () ->
                bookingService.createBooking(bookingRequestDto, 2L));
    }

    @Test
    void createBooking_shouldThrowWhenBookOwnItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(NotAvailableException.class, () ->
                bookingService.createBooking(bookingRequestDto, 1L));
    }

    @Test
    void approveBooking_shouldApprove() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking savedBooking = invocation.getArgument(0);
            bookingDto.setStatus(savedBooking.getStatus());
            return savedBooking;
        });
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto result = bookingService.approveBooking(1L, 1L, true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

    @Test
    void approveBooking_shouldReject() {
        bookingDto.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto result = bookingService.approveBooking(1L, 1L, false);

        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }

    @Test
    void approveBooking_shouldThrowWhenNotOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotAvailableException.class, () ->
                bookingService.approveBooking(1L, 2L, true));
    }

    @Test
    void approveBooking_shouldThrowWhenNotWaiting() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotAvailableException.class, () ->
                bookingService.approveBooking(1L, 1L, true));
    }

    @Test
    void getBookingById_shouldReturnForOwner() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto result = bookingService.getBookingById(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getBookingById_shouldReturnForBooker() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto result = bookingService.getBookingById(1L, 2L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getBookingById_shouldThrowWhenNotOwnerOrBooker() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () ->
                bookingService.getBookingById(1L, 3L));
    }

    @Test
    void createBooking_shouldThrowWhenItemNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(bookingRequestDto, 2L));
    }
}