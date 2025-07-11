package ru.practicum.shareit.user;

import lombok.*;

import jakarta.persistence.*;
import ru.practicum.shareit.item.Item;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 512)
    private String email;

    @Transient
    @Builder.Default
    private List<Item> items = new ArrayList<>();
}