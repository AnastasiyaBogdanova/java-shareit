package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Test
    void findByRequesterIdOrderByCreatedDesc_shouldReturnRequests() {
        User requester = new User();
        requester.setName("Requester");
        requester.setEmail("requester@yandex.ru");
        em.persist(requester);

        ItemRequest request1 = new ItemRequest();
        request1.setDescription("Request 1");
        request1.setRequester(requester);
        request1.setCreated(LocalDateTime.now().minusDays(1));
        em.persist(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("Request 2");
        request2.setRequester(requester);
        request2.setCreated(LocalDateTime.now());
        em.persist(request2);

        List<ItemRequest> requests = requestRepository.findByRequesterIdOrderByCreatedDesc(requester.getId());

        assertEquals(2, requests.size());
        assertEquals("Request 2", requests.get(0).getDescription()); // Должен быть первым, так как создан позже
        assertEquals("Request 1", requests.get(1).getDescription());
    }

    @Test
    void findByRequesterIdNot_shouldReturnPaginatedRequests() {
        User requester1 = new User();
        requester1.setName("Requester 1");
        requester1.setEmail("requester1@yandex.ru");
        em.persist(requester1);

        User requester2 = new User();
        requester2.setName("Requester 2");
        requester2.setEmail("requester2@yandex.ru");
        em.persist(requester2);


        ItemRequest request1 = new ItemRequest();
        request1.setDescription("Request 1");
        request1.setRequester(requester1);
        request1.setCreated(LocalDateTime.now().minusDays(1));
        em.persist(request1);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("Request 2");
        request2.setRequester(requester2);
        request2.setCreated(LocalDateTime.now());
        em.persist(request2);

        PageRequest pageable = PageRequest.of(0, 1, Sort.by("created").descending());

        var result = requestRepository.findByRequesterIdNot(requester1.getId(), pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("Request 2", result.getContent().get(0).getDescription());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void findByRequesterIdNot_shouldReturnEmptyPageWhenNoRequests() {
        User user = new User();
        user.setName("User");
        user.setEmail("user@yandex.ru");
        em.persist(user);

        PageRequest pageable = PageRequest.of(0, 10);
        var result = requestRepository.findByRequesterIdNot(user.getId(), pageable);

        assertTrue(result.isEmpty());
    }
}