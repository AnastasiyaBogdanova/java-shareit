package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Booker {
    private Long id;
    private String name;
}