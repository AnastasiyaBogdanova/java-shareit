package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRequestServiceTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private ItemRequestService requestService;

    @Test
    void createRequest_shouldSaveRequestToDatabase() {
        requestService = new ItemRequestService(requestRepository, userRepository,
                itemRepository, new ItemRequestMapper(), new ItemMapper());

        User requester = new User();
        requester.setName("user");
        requester.setEmail("user@yandex.ru");
        em.persist(requester);

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("утюг");

        ItemRequestDto result = requestService.createRequest(requester.getId(), requestDto);

        assertNotNull(result.getId());
        assertEquals("утюг", result.getDescription());
        assertNotNull(result.getCreated());

        ItemRequest savedRequest = em.find(ItemRequest.class, result.getId());
        assertEquals("утюг", savedRequest.getDescription());
        assertEquals(requester.getId(), savedRequest.getRequester().getId());
    }

    @Test
    void getUserRequests_shouldReturnRequestsForUser() {
        requestService = new ItemRequestService(requestRepository, userRepository,
                itemRepository, new ItemRequestMapper(), new ItemMapper());

        User requester = new User();
        requester.setName("user");
        requester.setEmail("user@yandex.ru");
        em.persist(requester);

        ItemRequest request = new ItemRequest();
        request.setDescription("утюг");
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        em.persist(request);
        List<ItemRequestWithItemsDto> results = requestService.getUserRequests(requester.getId());

        assertEquals(1, results.size());
        assertEquals(request.getId(), results.get(0).getId());
        assertEquals("утюг", results.get(0).getDescription());
    }

    @Test
    void getAllRequests_shouldReturnOtherUsersRequests() {
        requestService = new ItemRequestService(requestRepository, userRepository,
                itemRepository, new ItemRequestMapper(), new ItemMapper());

        User requester1 = new User();
        requester1.setName("user 1");
        requester1.setEmail("user@yandex.ru");
        em.persist(requester1);

        User requester2 = new User();
        requester2.setName("user 2");
        requester2.setEmail("user2@yandex.ru");
        em.persist(requester2);

        ItemRequest request1 = new ItemRequest();
        request1.setDescription("утюг");
        request1.setRequester(requester1);
        request1.setCreated(LocalDateTime.now());
        em.persist(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("тарелка");
        request2.setRequester(requester2);
        request2.setCreated(LocalDateTime.now());
        em.persist(request2);

        List<ItemRequestWithItemsDto> results = requestService.getAllRequests(
                requester1.getId(), 0, 10);

        assertEquals(1, results.size());
        assertEquals(request2.getId(), results.get(0).getId());
        assertEquals("тарелка", results.get(0).getDescription());
    }
}