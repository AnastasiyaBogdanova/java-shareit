package ru.practicum.shareit.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentRepository commentRepository;

    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        User author = new User();
        author.setName("Author");
        author.setEmail("author@ya.ru");
        em.persist(author);

        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@ya.ru");
        em.persist(owner);

        item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(owner);
        em.persist(item1);

        item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwner(owner);
        em.persist(item2);

        Comment comment1 = new Comment();
        comment1.setText("Comment 1");
        comment1.setItem(item1);
        comment1.setAuthor(author);
        em.persist(comment1);

        Comment comment2 = new Comment();
        comment2.setText("Comment 2");
        comment2.setItem(item1);
        comment2.setAuthor(author);
        em.persist(comment2);

        Comment comment3 = new Comment();
        comment3.setText("Comment 3");
        comment3.setItem(item2);
        comment3.setAuthor(author);
        em.persist(comment3);

    }

    @Test
    void findByItemId_shouldReturnCommentsForItem() {
        List<Comment> comments = commentRepository.findByItemId(item1.getId());

        assertEquals(2, comments.size());
        assertTrue(comments.stream().allMatch(c -> c.getItem().equals(item1)));
    }

    @Test
    void findByItemIdIn_shouldReturnCommentsForMultipleItems() {
        List<Comment> comments = commentRepository.findByItemIdIn(List.of(item1.getId(), item2.getId()));

        assertEquals(3, comments.size());
        assertTrue(comments.stream().anyMatch(c -> c.getItem().equals(item1)));
        assertTrue(comments.stream().anyMatch(c -> c.getItem().equals(item2)));
    }
}