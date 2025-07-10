package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CommentMapperTest {

    private final CommentMapper mapper = new CommentMapper();

    @Test
    void toEntity_shouldConvertDtoToEntity() {
        CommentDto dto = CommentDto.builder()
                .text("comment")
                .build();

        Comment comment = mapper.toEntity(dto);

        assertNull(comment.getId());
        assertEquals("comment", comment.getText());
        assertNull(comment.getItem());
        assertNull(comment.getAuthor());
    }

    @Test
    void toDto_shouldConvertEntityToDto() {
        User author = new User();
        author.setName("Author");

        Comment comment = Comment.builder()
                .id(1L)
                .text("comment")
                .author(author)
                .build();

        CommentDto dto = mapper.toDto(comment);

        assertEquals(1L, dto.getId());
        assertEquals("comment", dto.getText());
        assertEquals("Author", dto.getAuthorName());
        assertNull(dto.getCreated());
    }
}