package Repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import Model.ShortUrl;

/**
 * Integration tests for ShortUrlRepository
 * 
 * This test class tests the database operations for the ShortUrl entity,
 * including CRUD operations and custom finder methods.
 */
@DataJpaTest
@ContextConfiguration(classes = {com.urlshorteneanalyser.urlshortenerandanalyzer.UrlshortenerandanalyzerApplication.class})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ShortUrlRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ShortUrlRepository shortUrlRepository;

    private ShortUrl testShortUrl;

    @BeforeEach
    void setUp() {
        // Create test data
        testShortUrl = ShortUrl.builder()
                .originalUrl("https://www.example.com")
                .shortCode("abc123")
                .createdAt(LocalDateTime.now())
                .clickStats(new ArrayList<>())
                .build();
    }

    /**
     * Test saving a new ShortUrl
     */
    @Test
    void save_ShouldPersistShortUrl() {
        // When
        ShortUrl savedUrl = shortUrlRepository.save(testShortUrl);

        // Then
        assertNotNull(savedUrl.getId());
        assertEquals(testShortUrl.getOriginalUrl(), savedUrl.getOriginalUrl());
        assertEquals(testShortUrl.getShortCode(), savedUrl.getShortCode());
        assertNotNull(savedUrl.getCreatedAt());
    }

    /**
     * Test finding ShortUrl by short code when it exists
     */
    @Test
    void findByShortCode_ShouldReturnUrlWhenExists() {
        // Given
        ShortUrl savedUrl = entityManager.persistAndFlush(testShortUrl);

        // When
        Optional<ShortUrl> found = shortUrlRepository.findByShortCode("abc123");

        // Then
        assertTrue(found.isPresent());
        assertEquals(savedUrl.getId(), found.get().getId());
        assertEquals("abc123", found.get().getShortCode());
    }

    /**
     * Test finding ShortUrl by short code when it doesn't exist
     */
    @Test
    void findByShortCode_ShouldReturnEmptyWhenNotExists() {
        // When
        Optional<ShortUrl> found = shortUrlRepository.findByShortCode("nonexistent");

        // Then
        assertFalse(found.isPresent());
    }

    /**
     * Test finding all ShortUrls
     */
    @Test
    void findAll_ShouldReturnAllUrls() {
        // Given
        ShortUrl url1 = ShortUrl.builder()
                .originalUrl("https://www.example1.com")
                .shortCode("abc123")
                .createdAt(LocalDateTime.now())
                .clickStats(new ArrayList<>())
                .build();

        ShortUrl url2 = ShortUrl.builder()
                .originalUrl("https://www.example2.com")
                .shortCode("def456")
                .createdAt(LocalDateTime.now())
                .clickStats(new ArrayList<>())
                .build();

        entityManager.persistAndFlush(url1);
        entityManager.persistAndFlush(url2);

        // When
        List<ShortUrl> allUrls = shortUrlRepository.findAll();

        // Then
        assertEquals(2, allUrls.size());
        assertTrue(allUrls.stream().anyMatch(url -> url.getShortCode().equals("abc123")));
        assertTrue(allUrls.stream().anyMatch(url -> url.getShortCode().equals("def456")));
    }

    /**
     * Test updating an existing ShortUrl
     */
    @Test
    void save_ShouldUpdateExistingUrl() {
        // Given
        ShortUrl savedUrl = entityManager.persistAndFlush(testShortUrl);
        String newOriginalUrl = "https://www.updated-example.com";
        savedUrl.setOriginalUrl(newOriginalUrl);

        // When
        ShortUrl updatedUrl = shortUrlRepository.save(savedUrl);

        // Then
        assertEquals(savedUrl.getId(), updatedUrl.getId());
        assertEquals(newOriginalUrl, updatedUrl.getOriginalUrl());
        assertEquals(testShortUrl.getShortCode(), updatedUrl.getShortCode());
    }

    /**
     * Test deleting a ShortUrl
     */
    @Test
    void delete_ShouldRemoveUrl() {
        // Given
        ShortUrl savedUrl = entityManager.persistAndFlush(testShortUrl);

        // When
        shortUrlRepository.delete(savedUrl);
        entityManager.flush();

        // Then
        Optional<ShortUrl> found = shortUrlRepository.findByShortCode("abc123");
        assertFalse(found.isPresent());
    }

    /**
     * Test that short codes are unique
     */
    @Test
    void save_ShouldEnforceUniqueShortCodes() {
        // Given
        ShortUrl url1 = ShortUrl.builder()
                .originalUrl("https://www.example1.com")
                .shortCode("abc123")
                .createdAt(LocalDateTime.now())
                .clickStats(new ArrayList<>())
                .build();

        ShortUrl url2 = ShortUrl.builder()
                .originalUrl("https://www.example2.com")
                .shortCode("abc123") // Same short code
                .createdAt(LocalDateTime.now())
                .clickStats(new ArrayList<>())
                .build();

        // When & Then
        entityManager.persistAndFlush(url1);
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(url2);
        });
    }

    /**
     * Test that required fields are not null
     */
    @Test
    void save_ShouldRequireNonNullFields() {
        // Given
        ShortUrl urlWithoutShortCode = ShortUrl.builder()
                .originalUrl("https://www.example.com")
                .shortCode(null) // Null short code
                .createdAt(LocalDateTime.now())
                .clickStats(new ArrayList<>())
                .build();

        ShortUrl urlWithoutOriginalUrl = ShortUrl.builder()
                .originalUrl(null) // Null original URL
                .shortCode("abc123")
                .createdAt(LocalDateTime.now())
                .clickStats(new ArrayList<>())
                .build();

        // When & Then
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(urlWithoutShortCode);
        });

        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(urlWithoutOriginalUrl);
        });
    }
} 