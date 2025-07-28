package ru.practicum.shareit.request.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Include
    private User requestor;

    @Column(name = "created_date")
    private LocalDateTime created;

    @OneToMany(mappedBy = "request")
    @Builder.Default
    @ToString.Exclude
    private List<Item> items = new ArrayList<>();
}
