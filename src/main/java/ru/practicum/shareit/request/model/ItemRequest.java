package ru.practicum.shareit.request.model;

import lombok.*;
import jakarta.validation.constraints.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {
    private Long id;
    @NotBlank(message = "Описание запроса не может быть пустым")
    private String description;
    private User requestor;
    @PastOrPresent(message = "Дата создания запроса должна быть в прошлом или настоящем")
    private LocalDateTime created;
}
