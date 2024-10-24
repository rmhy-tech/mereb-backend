package com.rmhy.userservice.repository;

import com.rmhy.userservice.model.RefreshToken;
import com.rmhy.userservice.model.Role;
import com.rmhy.userservice.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class RefreshRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository tokenRepository;

    @Test
    public void testSaveRefreshToken() {
        User user = new User();
        user.setFirstName("test");
        user.setLastName("user");
        user.setUsername("token_test_user");
        user.setEmail("tokentestuser@example.com");
        user.setPassword("password1234");
        user.setRole(Role.USER);
        entityManager.persistAndFlush(user);

        // Act
        Optional<User> foundUser = userRepository.findByUsername("token_test_user");
        assertTrue(foundUser.isPresent());
        assertNotNull(foundUser.get().getUserId());

        RefreshToken refreshToken = new RefreshToken("refreshToken1234", new Date());

        foundUser.get().addRefreshToken(refreshToken);

        entityManager.persistAndFlush(foundUser.get());

        Optional<RefreshToken> foundRefreshToken = tokenRepository.findByToken("refreshToken1234");
        assertTrue(foundRefreshToken.isPresent());
        assertNotNull(foundRefreshToken.get().getId());
        assertEquals("refreshToken1234", foundRefreshToken.get().getToken());
        assertEquals(foundUser.get(), foundRefreshToken.get().getUser());
        assertNotNull(refreshToken.getExpiryDate());
    }

    @Test
    public void testDeleteRefreshTokenByUser() {
        User user = new User();
        user.setFirstName("test");
        user.setLastName("user");
        user.setUsername("token_test_user1");
        user.setEmail("tokentestuser1@example.com");
        user.setPassword("password1234");
        user.setRole(Role.USER);
        entityManager.persistAndFlush(user);

        // Act
        Optional<User> foundUser = userRepository.findByUsername("token_test_user1");
        assertTrue(foundUser.isPresent());
        assertNotNull(foundUser.get().getUserId());

        RefreshToken refreshToken = new RefreshToken("refreshToken1234", new Date());

        foundUser.get().addRefreshToken(refreshToken);

        entityManager.persistAndFlush(foundUser.get());

        Optional<RefreshToken> foundRefreshToken = tokenRepository.findByToken("refreshToken1234");
        assertTrue(foundRefreshToken.isPresent());
        assertNotNull(foundRefreshToken.get().getId());
        assertEquals("refreshToken1234", foundRefreshToken.get().getToken());
        assertEquals(foundUser.get(), foundRefreshToken.get().getUser());
        assertNotNull(refreshToken.getExpiryDate());

        foundUser.get().removeRefreshToken(foundRefreshToken.get());
        entityManager.persistAndFlush(foundUser.get());

        Optional<RefreshToken> deletedRefreshToken = tokenRepository.findByToken("refreshToken1234");
        assertFalse(deletedRefreshToken.isPresent());

    }

}
