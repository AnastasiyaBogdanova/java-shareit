package ru.practicum.shareit.comment;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.dto.CommentDto;

@Component
public class CommentMapper {
    public Comment toEntity(CommentDto dto) {
        return Comment.builder()
                .text(dto.getText())
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