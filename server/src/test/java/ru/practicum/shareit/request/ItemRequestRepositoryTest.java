package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    private User firstUser;
    private ItemRequest firstRequestFirstUser;
    private ItemRequest firstRequestSecondUser;
    private ItemRequest secondRequestFirstUser;
    private ItemRequest secondRequestSecondUser;

    @BeforeEach
    void setUp() {
        firstUser = userRepository.save(User.builder()
                .id(1L)
                .name("First user")
                .email("firstmail@box.com")
                .build()
        );
        User secondUser = userRepository.save(User.builder()
                .id(2L)
                .name("Second user")
                .email("secondmail@box.com")
                .build()
        );

        firstRequestFirstUser = requestRepository.save(ItemRequest.builder()
                .description("specification first request firstUser")
                .requestor(firstUser)
                .created(LocalDateTime.now())
                .build()
        );
        firstRequestSecondUser = requestRepository.save(ItemRequest.builder()
                .description("specification first request secondUser")
                .requestor(secondUser)
                .created(LocalDateTime.now())
                .build()
        );
        secondRequestFirstUser = requestRepository.save(ItemRequest.builder()
                .description("specification second request firstUser")
                .requestor(firstUser)
                .created(LocalDateTime.now())
                .build()
        );
        secondRequestSecondUser = requestRepository.save(ItemRequest.builder()
                .description("specification second request secondUser")
                .requestor(secondUser)
                .created(LocalDateTime.now())
                .build()
        );
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ItemRequest> result = requestRepository
                .findAllByRequestorIdOrderByCreatedDesc(firstUser.getId(), pageable).getContent();

        assertThat(result, hasSize(2));
        assertThat(result, not(hasItem(firstRequestSecondUser)));
        assertThat(result, not(hasItem(secondRequestSecondUser)));
        assertThat(result.getFirst(), is(secondRequestFirstUser));
    }

    @Test
    void findAllByRequestorIdNotOrderByCreatedDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ItemRequest> result = requestRepository
                .findAllByRequestorIdNotOrderByCreatedDesc(firstUser.getId(), pageable).getContent();

        assertThat(result, hasSize(2));
        assertThat(result, not(hasItem(firstRequestFirstUser)));
        assertThat(result, not(hasItem(secondRequestFirstUser)));
        assertThat(result.getFirst(), is(secondRequestSecondUser));
    }
}
