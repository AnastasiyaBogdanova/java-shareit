package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ItemRequestRepository requestRepository;

    @InjectMocks
    private ItemService itemService;

    private User owner;
    private Item item;
    private ItemDto itemDto;
    private ItemDtoForCreate itemDtoForCreate;
    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("User");
        owner.setEmail("User@ya.ru");

        item = new Item();
        item.setId(1L);
        item.setName("утюг");
        item.setDescription("хороший");
        item.setAvailable(true);
        item.setOwner(owner);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("стол");
        itemDto.setDescription("хороший");
        itemDto.setAvailable(true);

        itemDtoForCreate = new ItemDtoForCreate();
        itemDtoForCreate.setName("стул");
        itemDtoForCreate.setDescription("хороший");
        itemDtoForCreate.setAvailable(true);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("супер");
        comment.setItem(item);
        comment.setAuthor(owner);

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("супер");
    }

    @Test
    void createItem_shouldCreateItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemMapper.toEntity(any())).thenReturn(item);
        when(itemRepository.save(any())).thenReturn(item);
        when(itemMapper.toDto((Item) any())).thenReturn(itemDto);

        ItemDto result = itemService.createItem(1L, itemDtoForCreate);

        assertNotNull(result);
        assertEquals(itemDto, result);
        verify(itemRepository).save(item);
    }

    @Test
    void createItem_shouldCreateItemWithRequest() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        itemDtoForCreate.setRequestId(1L);
        item.setRequest(request);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemMapper.toEntity(any())).thenReturn(item);
        when(itemRepository.save(any())).thenReturn(item);
        when(itemMapper.toDto((Item) any())).thenReturn(itemDto);

        ItemDto result = itemService.createItem(1L, itemDtoForCreate);

        assertNotNull(result);
        assertEquals(itemDto, result);
        verify(requestRepository).findById(1L);
    }

    @Test
    void createItem_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(1L, itemDtoForCreate));
    }

    @Test
    void updateItem_shouldUpdateItem() {
        ItemDto updateDto = new ItemDto();
        updateDto.setName("утюг утюгович");
        updateDto.setDescription("класс");
        updateDto.setAvailable(false);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);
        when(itemMapper.toDto((Item) any())).thenReturn(itemDto);

        ItemDto result = itemService.updateItem(1L, 1L, updateDto);

        assertEquals("утюг утюгович", item.getName());
        assertEquals("класс", item.getDescription());
        assertFalse(item.getAvailable());
        assertEquals(itemDto, result);
    }

    @Test
    void updateItem_shouldThrowWhenNotOwner() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(2L, 1L, itemDto));
    }

    @Test
    void updateItem_shouldThrowWhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, 1L, itemDto));
    }

    @Test
    void getUserItems_shouldReturnItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(anyLong())).thenReturn(List.of(item));
        when(itemMapper.toDto(anyList())).thenReturn(List.of(itemDto));

        List<ItemDto> result = itemService.getUserItems(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemDto, result.get(0));
    }

    @Test
    void getUserItems_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getUserItems(1L));
    }

    @Test
    void searchItems_shouldReturnEmptyListForBlankText() {
        List<ItemDto> result = itemService.searchItems(" ", 1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItems_shouldReturnItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.searchAvailableItems(anyString())).thenReturn(List.of(item));
        when(itemMapper.toDto(anyList())).thenReturn(List.of(itemDto));

        List<ItemDto> result = itemService.searchItems("утюг", 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemDto, result.get(0));
    }

    @Test
    void getItemById_shouldReturnItemWithBookingsForOwner() {
        Booking lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setBooker(owner);

        Booking nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setBooker(owner);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(
                anyLong(), any(), any())).thenReturn(lastBooking);
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                anyLong(), any(), any())).thenReturn(nextBooking);
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of(comment));
        when(itemMapper.toDto((Item) any())).thenReturn(itemDto);

        ItemDto result = itemService.getItemById(1L, 1L);

        assertNotNull(result);
        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
        assertFalse(result.getComments().isEmpty());
    }

    @Test
    void getItemById_shouldReturnItemWithoutBookingsForNotOwner() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of(comment));
        when(itemMapper.toDto((Item) any())).thenReturn(itemDto);

        ItemDto result = itemService.getItemById(1L, 2L);

        assertNotNull(result);
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        assertFalse(result.getComments().isEmpty());
    }

    @Test
    void getItemById_shouldThrowWhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(1L, 1L));
    }

    @Test
    void addComment_shouldAddComment() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
                anyLong(), anyLong(), any())).thenReturn(true);
        when(commentMapper.toEntity(any())).thenReturn(comment);
        when(commentRepository.save(any())).thenReturn(comment);
        when(commentMapper.toDto(any())).thenReturn(commentDto);

        CommentDto result = itemService.addComment(1L, 1L, commentDto);

        assertNotNull(result);
        assertEquals(commentDto, result);
    }

    @Test
    void addComment_shouldThrowWhenUserNotBookedItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
                anyLong(), anyLong(), any())).thenReturn(false);

        assertThrows(NotAvailableException.class, () ->
                itemService.addComment(1L, 1L, commentDto));
    }

    @Test
    void findUserById_shouldReturnUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        User result = itemService.findUserById(1L);

        assertEquals(owner, result);
    }

    @Test
    void findUserById_shouldThrowWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findUserById(1L));
    }

    @Test
    void findItemById_shouldReturnItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Item result = itemService.findItemById(1L);

        assertEquals(item, result);
    }

    @Test
    void findItemById_shouldThrowWhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findItemById(1L));
    }
}