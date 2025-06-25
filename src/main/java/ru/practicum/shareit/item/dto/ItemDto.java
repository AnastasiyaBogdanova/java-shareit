package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.item.Booker;
import ru.practicum.shareit.comment.Comment;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private Booker lastBooking;
    private Booker nextBooking;
    private List<Comment> comments;


}