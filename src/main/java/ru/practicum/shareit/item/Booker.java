package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Booker {
    private Long id;
    private Long bookerId;
}
