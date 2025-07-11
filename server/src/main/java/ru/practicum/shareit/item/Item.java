package ru.practicum.shareit.item;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 512)
    private String description;

    @Column(nullable = false, name = "is_available")
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    @Transient
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
}