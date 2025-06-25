package ru.practicum.shareit.comment;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@Component
public class CommentMapper {
    public Comment toEntity(CommentDto dto, Item item, User author) {
        return Comment.builder()
                .text(dto.getText())
                .item(item)
                .author(author)
                .build();
    }

    public CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .build();
    }
}