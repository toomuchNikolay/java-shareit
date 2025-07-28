package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User firstUser;
    private User secondUser;
    private Item firstItem;
    private Item secondItem;
    private Item anotherItem;

    @BeforeEach
    void setUp() {
        firstUser = userRepository.save(User.builder()
                .id(1L)
                .name("First user")
                .email("firstmail@box.com")
                .build()
        );
        secondUser = userRepository.save(User.builder()
                .id(2L)
                .name("Second user")
                .email("secondmail@box.com")
                .build()
        );

        firstItem = itemRepository.save(Item.builder()
                .id(1L)
                .name("First item")
                .description("specification first item")
                .available(true)
                .owner(firstUser)
                .request(null)
                .comments(Collections.emptyList())
                .build()
        );
        secondItem = itemRepository.save(Item.builder()
                .id(2L)
                .name("Second item")
                .description("specification second item")
                .available(false)
                .owner(firstUser)
                .request(null)
                .comments(Collections.emptyList())
                .build()
        );
        anotherItem = itemRepository.save(Item.builder()
                .id(3L)
                .name("Some item")
                .description("specification some item")
                .available(true)
                .owner(secondUser)
                .request(null)
                .comments(Collections.emptyList())
                .build()
        );
    }

    @Test
    void existsByOwner_Id_whenItemFound_whenReturnedTrue() {
        boolean result = itemRepository.existsByOwner_Id(secondUser.getId());

        assertThat(result, equalTo(true));
    }

    @Test
    void existsByOwner_Id_whenItemNotFound_whenReturnedFalse() {
        boolean result = itemRepository.existsByOwner_Id(10L);

        assertThat(result, equalTo(false));
    }

    @Test
    void findAllByOwner_Id() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> result = itemRepository.findAllByOwner_Id(firstUser.getId(), pageable).getContent();

        assertThat(result, hasSize(2));
        assertThat(result, hasItem(firstItem));
        assertThat(result, hasItem(secondItem));
        assertThat(result, not(hasItem(anotherItem)));
    }
}
