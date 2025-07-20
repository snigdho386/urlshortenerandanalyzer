package Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import Model.ClickStats;
import Model.ShortUrl;
import Repository.ClickStatsRepository;
import Repository.ShortUrlRepository;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Unit tests for UrlService class
 * 
 * This test class covers all business logic methods in the UrlService,
 * including URL creation, redirection, click tracking, and statistics retrieval.
 */
@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private ShortUrlRepository shortUrlRepo;

    @Mock
    private ClickStatsRepository clickStatsRepo;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private UrlService urlService;

    private ShortUrl testShortUrl;
    private ClickStats testClickStats;

    @BeforeEach
    void setUp() {
        // Create test data
        testShortUrl = ShortUrl.builder()
                .id(1L)
                .originalUrl("https://www.example.com")
                .shortCode("abc123")
                .createdAt(LocalDateTime.now())
                .clickStats(new ArrayList<>())
                .build();

        testClickStats = ClickStats.builder()
                .id(1L)
                .ipAddress("192.168.1.1")
                .referrer("https://google.com")
                .userAgent("Mozilla/5.0")
                .shortUrl(testShortUrl)
                .build();
    }

    /**
     * Test URL creation with unique short code generation
     */
    @Test
    void createShortUrl_ShouldGenerateUniqueShortCode() {
        // Given
        String originalUrl = "https://www.example.com";
        when(shortUrlRepo.findByShortCode(any())).thenReturn(Optional.empty());
        when(shortUrlRepo.save(any(ShortUrl.class))).thenReturn(testShortUrl);

        // When
        ShortUrl result = urlService.createShortUrl(originalUrl);

        // Then
        assertNotNull(result);
        assertEquals(originalUrl, result.getOriginalUrl());
        assertNotNull(result.getShortCode());
        assertEquals(6, result.getShortCode().length());
        verify(shortUrlRepo).save(any(ShortUrl.class));
    }

    /**
     * Test URL creation with duplicate short code handling
     */
    @Test
    void createShortUrl_ShouldHandleDuplicateShortCodes() {
        // Given
        String originalUrl = "https://www.example.com";
        when(shortUrlRepo.findByShortCode(any())).thenReturn(Optional.of(testShortUrl), Optional.empty());
        when(shortUrlRepo.save(any(ShortUrl.class))).thenReturn(testShortUrl);

        // When
        ShortUrl result = urlService.createShortUrl(originalUrl);

        // Then
        assertNotNull(result);
        verify(shortUrlRepo, atLeast(2)).findByShortCode(any());
        verify(shortUrlRepo).save(any(ShortUrl.class));
    }

    /**
     * Test successful URL retrieval and click tracking
     */
    @Test
    void getOriginalUrl_ShouldReturnUrlAndTrackClick() {
        // Given
        String shortCode = "abc123";
        when(shortUrlRepo.findByShortCode(shortCode)).thenReturn(Optional.of(testShortUrl));
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(request.getHeader("Referer")).thenReturn("https://google.com");
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
        when(clickStatsRepo.save(any(ClickStats.class))).thenReturn(testClickStats);

        // When
        Optional<ShortUrl> result = urlService.getOriginalUrl(shortCode, request);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testShortUrl, result.get());
        verify(clickStatsRepo).save(any(ClickStats.class));
    }

    /**
     * Test URL retrieval when short code doesn't exist
     */
    @Test
    void getOriginalUrl_ShouldReturnEmptyWhenNotFound() {
        // Given
        String shortCode = "nonexistent";
        when(shortUrlRepo.findByShortCode(shortCode)).thenReturn(Optional.empty());

        // When
        Optional<ShortUrl> result = urlService.getOriginalUrl(shortCode, request);

        // Then
        assertFalse(result.isPresent());
        verify(clickStatsRepo, never()).save(any(ClickStats.class));
    }

    /**
     * Test retrieval of all URLs
     */
    @Test
    void getAllUrls_ShouldReturnAllUrls() {
        // Given
        List<ShortUrl> expectedUrls = Arrays.asList(testShortUrl);
        when(shortUrlRepo.findAll()).thenReturn(expectedUrls);

        // When
        List<ShortUrl> result = urlService.getAllUrls();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testShortUrl, result.get(0));
        verify(shortUrlRepo).findAll();
    }

    /**
     * Test retrieval of URL statistics when found
     */
    @Test
    void getStats_ShouldReturnUrlWhenFound() {
        // Given
        String shortCode = "abc123";
        when(shortUrlRepo.findByShortCode(shortCode)).thenReturn(Optional.of(testShortUrl));

        // When
        Optional<ShortUrl> result = urlService.getStats(shortCode);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testShortUrl, result.get());
        verify(shortUrlRepo).findByShortCode(shortCode);
    }

    /**
     * Test retrieval of URL statistics when not found
     */
    @Test
    void getStats_ShouldReturnEmptyWhenNotFound() {
        // Given
        String shortCode = "nonexistent";
        when(shortUrlRepo.findByShortCode(shortCode)).thenReturn(Optional.empty());

        // When
        Optional<ShortUrl> result = urlService.getStats(shortCode);

        // Then
        assertFalse(result.isPresent());
        verify(shortUrlRepo).findByShortCode(shortCode);
    }

    /**
     * Test short code generation produces valid codes
     */
    @Test
    void generateCode_ShouldProduceValidCodes() {
        // This test verifies the private generateCode method indirectly
        // by testing that created URLs have valid short codes
        
        // Given
        String originalUrl = "https://www.example.com";
        when(shortUrlRepo.findByShortCode(any())).thenReturn(Optional.empty());
        when(shortUrlRepo.save(any(ShortUrl.class))).thenAnswer(invocation -> {
            ShortUrl savedUrl = invocation.getArgument(0);
            return savedUrl;
        });

        // When
        ShortUrl result = urlService.createShortUrl(originalUrl);

        // Then
        assertNotNull(result.getShortCode());
        assertEquals(6, result.getShortCode().length());
        assertTrue(result.getShortCode().matches("[a-zA-Z0-9]{6}"));
    }
} 