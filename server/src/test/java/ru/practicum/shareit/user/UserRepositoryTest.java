package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    @Test
    void existsByEmail_whenEmailExists_ReturnTrue() {
        repository.save(User.builder()
                .name("TestName")
                .email("test@box.com")
                .build());
        boolean result = repository.existsByEmail("test@box.com");

        assertThat(result, equalTo(true));
    }

    @Test
    void existsByEmail_whenEmailNotExists_ReturnFalse() {
        boolean result = repository.existsByEmail("test@box.com");

        assertThat(result, equalTo(false));
    }
}
