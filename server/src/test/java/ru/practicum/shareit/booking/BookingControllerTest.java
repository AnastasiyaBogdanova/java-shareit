package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void createBooking_shouldReturnCreatedBooking() throws Exception {
        BookingRequestDto bookingDto = new BookingRequestDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        User booker = new User();
        booker.setId(2L);
        booker.setName("User");

        Item item = new Item();
        item.setId(1L);
        item.setName("Item");

        BookingDto responseDto = new BookingDto();
        responseDto.setId(1L);
        responseDto.setStart(bookingDto.getStart());
        responseDto.setEnd(bookingDto.getEnd());
        responseDto.setItem(item);
        responseDto.setBooker(new UserShortDto(booker.getId(), booker.getName()));
        responseDto.setStatus(BookingStatus.WAITING);

        when(bookingService.createBooking(any(BookingRequestDto.class), anyLong())).thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.booker.id").value(2L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void approveBooking_shouldReturnApprovedBooking() throws Exception {
        BookingDto responseDto = new BookingDto();
        responseDto.setId(1L);
        responseDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBooking_shouldReturnBooking() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getUserBookings_shouldReturnListOfBookings() throws Exception {
        BookingDto booking1 = new BookingDto();
        booking1.setId(1L);
        BookingDto booking2 = new BookingDto();
        booking2.setId(2L);

        when(bookingService.getUserBookings(anyLong(), any(BookingState.class)))
                .thenReturn(List.of(booking1, booking2));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void getOwnerBookings_shouldReturnListOfBookings() throws Exception {
        BookingDto booking1 = new BookingDto();
        booking1.setId(1L);
        BookingDto booking2 = new BookingDto();
        booking2.setId(2L);

        when(bookingService.getUserBookings(anyLong(), any(BookingState.class)))
                .thenReturn(List.of(booking1, booking2));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }
}