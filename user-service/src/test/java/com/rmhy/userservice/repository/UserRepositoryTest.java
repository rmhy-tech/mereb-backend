package com.rmhy.userservice.repository;

import com.rmhy.userservice.model.Role;
import com.rmhy.userservice.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveUser() {
        // Arrange
        User user = new User();
        user.setFirstName("test");
        user.setLastName("user");
        user.setUsername("test_user");
        user.setEmail("testuser@example.com");
        user.setPassword("password1234");
        user.setRole(Role.USER);

        // Act
        User savedUser = userRepository.save(user);

        // Assert
        assertNotNull(savedUser.getUserId());
        assertEquals("test", savedUser.getFirstName());
        assertEquals("user", savedUser.getLastName());
        assertEquals("test_user", savedUser.getUsername());
        assertEquals("testuser@example.com", savedUser.getEmail());
        assertEquals("password1234", savedUser.getPassword());
        assertEquals(Role.USER, savedUser.getRole());
        assertNotNull(savedUser.getCreatedDate());
        assertNotNull(savedUser.getUpdatedDate());
    }

    @Test
    public void testFindByUsername_UserFound() {
        // Arrange
        User user = new User();
        user.setFirstName("test");
        user.setLastName("user");
        user.setUsername("test_user1");
        user.setEmail("testuser1@example.com");
        user.setPassword("password1234");
        user.setRole(Role.USER);
        entityManager.persistAndFlush(user);

        // Act
        Optional<User> foundUser = userRepository.findByUsername("test_user1");

        // Assert
        assertTrue(foundUser.isPresent());
        assertNotNull(foundUser.get().getUserId());
        assertEquals("test", foundUser.get().getFirstName());
        assertEquals("user", foundUser.get().getLastName());
        assertEquals("test_user1", foundUser.get().getUsername());
        assertEquals("testuser1@example.com", foundUser.get().getEmail());
        assertEquals("password1234", foundUser.get().getPassword());
        assertEquals(Role.USER, foundUser.get().getRole());
        assertNotNull(foundUser.get().getCreatedDate());
        assertNotNull(foundUser.get().getUpdatedDate());
    }

    @Test
    public void testFindByUsername_UserNotFound() {
        // Act
        Optional<User> foundUser = userRepository.findByUsername("unknownuser");

        // Assert
        assertFalse(foundUser.isPresent());
    }

    @Test
    public void testFindAllUsers() {
        // Arrange
        User user = new User();
        user.setFirstName("test");
        user.setLastName("user");
        user.setUsername("test_user2");
        user.setEmail("testuser2@example.com");
        user.setPassword("password1234");
        user.setRole(Role.USER);
        entityManager.persistAndFlush(user);

        // Act
        int page = 0;
        int size = 10;
        String sortBy = "updatedDate";
        String sortDirection = "desc";

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<User> usersPage = userRepository.findAll(pageable);

        // Assert
        assertTrue(usersPage.hasContent());
        assertNotNull(usersPage.getContent());

        User exampleUser = usersPage
                .getContent().stream()
                .filter((User u) -> u.getUsername().equals(user.getUsername()))
                .toList().get(0);
        assertNotNull(exampleUser.getUserId());
        assertEquals("test", exampleUser.getFirstName());
        assertEquals("user", exampleUser.getLastName());
        assertEquals("test_user2", exampleUser.getUsername());
        assertEquals("testuser2@example.com", exampleUser.getEmail());
        assertEquals("password1234", exampleUser.getPassword());
        assertEquals(Role.USER, exampleUser.getRole());
        assertNotNull(exampleUser.getCreatedDate());
        assertNotNull(exampleUser.getUpdatedDate());
    }

    @Test
    public void testDeleteUser_UserFound() {
        // Arrange
        User user = new User();
        user.setFirstName("test");
        user.setLastName("user");
        user.setUsername("test_user3");
        user.setEmail("testuser3@example.com");
        user.setPassword("password1234");
        user.setRole(Role.USER);
        entityManager.persistAndFlush(user);


        // Act
        Optional<User> foundUser = userRepository.findByUsername("test_user3");
        assertTrue(foundUser.isPresent());
        userRepository.delete(foundUser.get());

        Optional<User> deletedUser = userRepository.findByUsername("test_user3");

        assertFalse(deletedUser.isPresent());
    }
}
