package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User requester;
    private ItemRequest request1;
    private ItemRequest request2;

    @BeforeEach
    void setUp() {

        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@ya.ru");
        em.persist(owner);

        requester = new User();
        requester.setName("Requester");
        requester.setEmail("requester@ya.ru");
        em.persist(requester);

        request1 = new ItemRequest();
        request1.setDescription("утюг");
        request1.setRequester(requester);
        request1.setCreated(LocalDateTime.now());
        em.persist(request1);

        request2 = new ItemRequest();
        request2.setDescription("стол");
        request2.setRequester(requester);
        request2.setCreated(LocalDateTime.now());
        em.persist(request2);

        createTestItems();

    }

    private void createTestItems() {
        Item item1 = new Item();
        item1.setName("утюг");
        item1.setDescription("хороший");
        item1.setAvailable(true);
        item1.setOwner(owner);
        em.persist(item1);

        Item item2 = new Item();
        item2.setName("стол");
        item2.setDescription("хороший");
        item2.setAvailable(true);
        item2.setOwner(owner);
        item2.setRequest(request1);
        em.persist(item2);

        Item item3 = new Item();
        item3.setName("вещица");
        item3.setDescription("интересная");
        item3.setAvailable(false);
        item3.setOwner(owner);
        item3.setRequest(request2);
        em.persist(item3);

        User otherOwner = new User();
        otherOwner.setName("Other Owner");
        otherOwner.setEmail("other@ya.ru");
        em.persist(otherOwner);

        Item item4 = new Item();
        item4.setName("скотч");
        item4.setDescription("липкий");
        item4.setAvailable(true);
        item4.setOwner(otherOwner);
        em.persist(item4);
    }

    @Test
    void findByOwnerId_shouldReturnOnlyOwnersItems() {
        List<Item> items = itemRepository.findByOwnerId(owner.getId());

        assertEquals(3, items.size(), "Должно вернуть 3 вещи владельца");
        assertTrue(items.stream().allMatch(item -> item.getOwner().equals(owner)),
                "Все вещи должны принадлежать владельцу");
    }

    @Test
    void findByRequestId_shouldReturnItemsForSpecificRequest() {
        List<Item> items = itemRepository.findByRequestId(request1.getId());

        assertEquals(1, items.size(), "Должна вернуться 1 вещь для запроса");
        assertEquals("стол", items.get(0).getName(), "Некорректное имя вещи");
        assertEquals(request1.getId(), items.get(0).getRequest().getId(),
                "Некорректный ID запроса");
    }

    @Test
    void findByRequestIdIn_shouldReturnItemsForMultipleRequests() {
        List<Item> items = itemRepository.findByRequestIdIn(List.of(request1.getId(), request2.getId()));

        assertEquals(2, items.size(), "Должно вернуть 2 вещи для двух запросов");
        assertTrue(items.stream().anyMatch(item -> item.getRequest().getId().equals(request1.getId())),
                "Должна быть вещь для первого запроса");
        assertTrue(items.stream().anyMatch(item -> item.getRequest().getId().equals(request2.getId())),
                "Должна быть вещь для второго запроса");
    }

    @Test
    void searchAvailableItems_shouldFindAvailableItemsByText() {
        List<Item> result = itemRepository.searchAvailableItems("утюг");
        assertEquals(1, result.size(), "Должен найти утюг");
        assertEquals("утюг", result.get(0).getName());

        result = itemRepository.searchAvailableItems("стол");
        assertEquals(1, result.size(), "Должен найти стол");
        assertEquals("стол", result.get(0).getName());

        result = itemRepository.searchAvailableItems("УтЮг");
        assertEquals(1, result.size(), "Должен найти утюг (регистронезависимый)");

        result = itemRepository.searchAvailableItems("ванна");
        assertTrue(result.isEmpty(), "Не должен находить недоступные вещи");
    }

    @Test
    void searchAvailableItems_shouldReturnEmptyListForNoMatches() {
        List<Item> result = itemRepository.searchAvailableItems("что-то там");
        assertTrue(result.isEmpty(), "Должен вернуть пустой список при отсутствии совпадений");
    }
}