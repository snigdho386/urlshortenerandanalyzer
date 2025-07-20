package Repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import Model.ClickStats;
import Model.ShortUrl;

/**
 * Integration tests for ClickStatsRepository
 * 
 * This test class tests the database operations for the ClickStats entity,
 * including CRUD operations and relationship handling.
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
class ClickStatsRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClickStatsRepository clickStatsRepository;

    private ShortUrl testShortUrl;
    private ClickStats testClickStats;

    @BeforeEach
    void setUp() {
        // Create test data
        testShortUrl = ShortUrl.builder()
                .originalUrl("https://www.example.com")
                .shortCode("abc123")
                .createdAt(LocalDateTime.now())
                .clickStats(new ArrayList<>())
                .build();

        testClickStats = ClickStats.builder()
                .ipAddress("192.168.1.1")
                .referrer("https://google.com")
                .userAgent("Mozilla/5.0")
                .shortUrl(testShortUrl)
                .build();
    }

    /**
     * Test saving a new ClickStats
     */
    @Test
    void save_ShouldPersistClickStats() {
        // Given
        ShortUrl savedShortUrl = entityManager.persistAndFlush(testShortUrl);
        testClickStats.setShortUrl(savedShortUrl);
        testClickStats.setClickedAt(LocalDateTime.now()); // Ensure timestamp is set

        // When
        ClickStats savedClickStats = clickStatsRepository.save(testClickStats);

        // Then
        assertNotNull(savedClickStats.getId());
        assertEquals(testClickStats.getIpAddress(), savedClickStats.getIpAddress());
        assertEquals(testClickStats.getReferrer(), savedClickStats.getReferrer());
        assertEquals(testClickStats.getUserAgent(), savedClickStats.getUserAgent());
        assertNotNull(savedClickStats.getClickedAt());
        assertEquals(savedShortUrl.getId(), savedClickStats.getShortUrl().getId());
    }

    /**
     * Test finding all ClickStats
     */
    @Test
    void findAll_ShouldReturnAllClickStats() {
        // Given
        ShortUrl savedShortUrl = entityManager.persistAndFlush(testShortUrl);

        ClickStats clickStats1 = ClickStats.builder()
                .ipAddress("192.168.1.1")
                .referrer("https://google.com")
                .userAgent("Mozilla/5.0")
                .shortUrl(savedShortUrl)
                .build();

        ClickStats clickStats2 = ClickStats.builder()
                .ipAddress("192.168.1.2")
                .referrer("https://bing.com")
                .userAgent("Chrome/90.0")
                .shortUrl(savedShortUrl)
                .build();

        entityManager.persistAndFlush(clickStats1);
        entityManager.persistAndFlush(clickStats2);

        // When
        List<ClickStats> allClickStats = clickStatsRepository.findAll();

        // Then
        assertEquals(2, allClickStats.size());
        assertTrue(allClickStats.stream().anyMatch(stats -> stats.getIpAddress().equals("192.168.1.1")));
        assertTrue(allClickStats.stream().anyMatch(stats -> stats.getIpAddress().equals("192.168.1.2")));
    }

    /**
     * Test finding ClickStats by ID
     */
    @Test
    void findById_ShouldReturnClickStats() {
        // Given
        ShortUrl savedShortUrl = entityManager.persistAndFlush(testShortUrl);
        testClickStats.setShortUrl(savedShortUrl);
        ClickStats savedClickStats = entityManager.persistAndFlush(testClickStats);

        // When
        var found = clickStatsRepository.findById(savedClickStats.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(savedClickStats.getId(), found.get().getId());
        assertEquals(testClickStats.getIpAddress(), found.get().getIpAddress());
    }

    /**
     * Test updating an existing ClickStats
     */
    @Test
    void save_ShouldUpdateExistingClickStats() {
        // Given
        ShortUrl savedShortUrl = entityManager.persistAndFlush(testShortUrl);
        testClickStats.setShortUrl(savedShortUrl);
        ClickStats savedClickStats = entityManager.persistAndFlush(testClickStats);

        String newIpAddress = "192.168.1.100";
        savedClickStats.setIpAddress(newIpAddress);

        // When
        ClickStats updatedClickStats = clickStatsRepository.save(savedClickStats);

        // Then
        assertEquals(savedClickStats.getId(), updatedClickStats.getId());
        assertEquals(newIpAddress, updatedClickStats.getIpAddress());
        assertEquals(testClickStats.getReferrer(), updatedClickStats.getReferrer());
    }

    /**
     * Test deleting a ClickStats
     */
    @Test
    void delete_ShouldRemoveClickStats() {
        // Given
        ShortUrl savedShortUrl = entityManager.persistAndFlush(testShortUrl);
        testClickStats.setShortUrl(savedShortUrl);
        ClickStats savedClickStats = entityManager.persistAndFlush(testClickStats);

        // When
        clickStatsRepository.delete(savedClickStats);
        entityManager.flush();

        // Then
        var found = clickStatsRepository.findById(savedClickStats.getId());
        assertFalse(found.isPresent());
    }

    /**
     * Test that ClickStats can have null optional fields
     */
    @Test
    void save_ShouldAllowNullOptionalFields() {
        // Given
        ShortUrl savedShortUrl = entityManager.persistAndFlush(testShortUrl);

        ClickStats clickStatsWithNulls = ClickStats.builder()
                .ipAddress(null)
                .referrer(null)
                .userAgent(null)
                .clickedAt(LocalDateTime.now())
                .shortUrl(savedShortUrl)
                .build();

        // When
        ClickStats savedClickStats = clickStatsRepository.save(clickStatsWithNulls);

        // Then
        assertNotNull(savedClickStats.getId());
        assertNull(savedClickStats.getIpAddress());
        assertNull(savedClickStats.getReferrer());
        assertNull(savedClickStats.getUserAgent());
        assertNotNull(savedClickStats.getClickedAt());
    }

    /**
     * Test relationship between ClickStats and ShortUrl
     */
    @Test
    void save_ShouldMaintainRelationshipWithShortUrl() {
        // Given
        ShortUrl savedShortUrl = entityManager.persistAndFlush(testShortUrl);
        testClickStats.setShortUrl(savedShortUrl);

        // When
        ClickStats savedClickStats = clickStatsRepository.save(testClickStats);

        // Then
        assertNotNull(savedClickStats.getShortUrl());
        assertEquals(savedShortUrl.getId(), savedClickStats.getShortUrl().getId());
        assertEquals(savedShortUrl.getShortCode(), savedClickStats.getShortUrl().getShortCode());
    }

    /**
     * Test that ClickStats are automatically timestamped
     */
    @Test
    void save_ShouldSetClickedAtTimestamp() {
        // Given
        ShortUrl savedShortUrl = entityManager.persistAndFlush(testShortUrl);
        testClickStats.setShortUrl(savedShortUrl);
        testClickStats.setClickedAt(LocalDateTime.now()); // Set current timestamp

        // When
        ClickStats savedClickStats = clickStatsRepository.save(testClickStats);

        // Then
        assertNotNull(savedClickStats.getClickedAt());
        assertTrue(savedClickStats.getClickedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }
} 