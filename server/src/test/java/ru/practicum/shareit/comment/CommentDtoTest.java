package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testSerialize() throws Exception {
        CommentDto dto = CommentDto.builder()
                .id(1L)
                .text("comment")
                .authorName("Author")
                .created(LocalDateTime.of(2025, 1, 1, 12, 0))
                .build();

        JsonContent<CommentDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("comment");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Author");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025-01-01T12:00:00");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"text\":\"comment\",\"authorName\":\"Author\",\"created\":\"2025-01-01T12:00:00\"}";

        CommentDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("comment");
        assertThat(dto.getAuthorName()).isEqualTo("Author");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2025, 1, 1, 12, 0));
    }
}