package com.rmhy.postservice.repository;

import com.rmhy.postservice.model.Post;
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
public class PostRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PostRepository postRepository;

    @Test
    public void testCreatePost() {
        Post post = new Post(
                "Repository test post",
                1L,
                "test_user"
        );

        Post savedPost = postRepository.save(post);

        assertNotNull(savedPost.getId());
        assertEquals(1L, savedPost.getUserId());
        assertEquals("test_user", savedPost.getUsername());
        assertEquals("Repository test post", savedPost.getContent());
        assertNotNull(savedPost.getCreatedAt());
        assertNotNull(savedPost.getUpdatedAt());
    }

    @Test
    public void testGetPosts() {
        Post post = new Post(
                "Repository test post 2",
                2L,
                "test_user2"
        );
        entityManager.persistAndFlush(post);

        int page = 0;
        int size = 10;
        String sortBy = "createdAt";
        String sortDirection = "desc";

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Post> postPage = postRepository.findAll(pageable);

        assertTrue(postPage.hasContent());
        assertFalse(postPage.getContent().isEmpty());

        Post examplePost = postPage
                .getContent().stream()
                .filter((Post p) -> p.getId().equals(post.getId()))
                .toList().get(0);

        assertNotNull(examplePost.getId());
        assertEquals(2L, examplePost.getUserId());
        assertEquals("test_user2", examplePost.getUsername());
        assertEquals("Repository test post 2", examplePost.getContent());
        assertNotNull(examplePost.getCreatedAt());
        assertNotNull(examplePost.getUpdatedAt());
    }

    @Test
    public void testGetPostsByUser() {
        Post post = new Post(
                "Repository test post 3",
                3L,
                "test_user3"
        );
        entityManager.persistAndFlush(post);

        int page = 0;
        int size = 10;
        String sortBy = "createdAt";
        String sortDirection = "desc";

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Post> postPage = postRepository.findPostsByUserId(post.getUserId(), pageable);

        assertTrue(postPage.hasContent());
        assertFalse(postPage.getContent().isEmpty());

        Post examplePost = postPage
                .getContent().stream()
                .filter((Post p) -> p.getId().equals(post.getId()))
                .toList().get(0);

        assertNotNull(examplePost.getId());
        assertEquals(3L, examplePost.getUserId());
        assertEquals("test_user3", examplePost.getUsername());
        assertEquals("Repository test post 3", examplePost.getContent());
        assertNotNull(examplePost.getCreatedAt());
        assertNotNull(examplePost.getUpdatedAt());
    }

    @Test
    public void testDeleteUser_UserFound() {
        Post post = new Post(
                "Repository test post 4",
                4L,
                "test_user4"
        );
        Post savedPost = entityManager.persistAndFlush(post);
        Long postId = savedPost.getId();

        postRepository.delete(savedPost);

        Optional<Post> deletedUser = postRepository.findById(postId);

        assertFalse(deletedUser.isPresent());
    }
}
