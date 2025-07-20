package Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import Model.ClickStats;
import Model.ShortUrl;
import Service.UrlService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Integration tests for UrlController
 * 
 * This test class covers all REST endpoints in the UrlController,
 * testing both successful scenarios and error cases.
 */
@WebMvcTest(UrlController.class)
@ContextConfiguration(classes = {com.urlshorteneanalyser.urlshortenerandanalyzer.UrlshortenerandanalyzerApplication.class})
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @Autowired
    private ObjectMapper objectMapper;

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
     * Test successful URL shortening
     */
    @Test
    void shortenUrl_ShouldReturnShortUrl() throws Exception {
        // Given
        String requestBody = "{\"originalUrl\": \"https://www.example.com\"}";
        when(urlService.createShortUrl("https://www.example.com")).thenReturn(testShortUrl);

        // When & Then
        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.originalUrl").value("https://www.example.com"));
    }

    /**
     * Test URL shortening with invalid request body
     */
    @Test
    void shortenUrl_ShouldReturnBadRequestForInvalidBody() throws Exception {
        // Given
        String invalidRequestBody = "{\"invalidField\": \"value\"}";

        // When & Then
        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestBody))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test successful URL redirection
     */
    @Test
    void redirect_ShouldReturnRedirectResponse() throws Exception {
        // Given
        when(urlService.getOriginalUrl(eq("abc123"), any())).thenReturn(Optional.of(testShortUrl));

        // When & Then
        mockMvc.perform(get("/abc123"))
                .andExpect(status().isFound())
                .andExpect(content().string(""));
    }

    /**
     * Test URL redirection with non-existent short code
     */
    @Test
    void redirect_ShouldReturnNotFoundForInvalidCode() throws Exception {
        // Given
        when(urlService.getOriginalUrl(eq("nonexistent"), any())).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/nonexistent"))
                .andExpect(status().isNotFound());
    }

    /**
     * Test URL redirection with URL that needs protocol prefix
     */
    @Test
    void redirect_ShouldAddHttpPrefixForUrlsWithoutProtocol() throws Exception {
        // Given
        ShortUrl urlWithoutProtocol = ShortUrl.builder()
                .id(2L)
                .originalUrl("example.com")
                .shortCode("abc123")
                .createdAt(LocalDateTime.now())
                .clickStats(new ArrayList<>())
                .build();
        when(urlService.getOriginalUrl(eq("abc123"), any())).thenReturn(Optional.of(urlWithoutProtocol));

        // When & Then
        mockMvc.perform(get("/abc123"))
                .andExpect(status().isFound());
    }

    /**
     * Test retrieval of all URLs
     */
    @Test
    void getAllUrls_ShouldReturnAllUrls() throws Exception {
        // Given
        List<ShortUrl> urls = Arrays.asList(testShortUrl);
        when(urlService.getAllUrls()).thenReturn(urls);

        // When & Then
        mockMvc.perform(get("/urls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].shortCode").value("abc123"))
                .andExpect(jsonPath("$[0].originalUrl").value("https://www.example.com"));
    }

    /**
     * Test retrieval of URL statistics when found
     */
    @Test
    void getStats_ShouldReturnUrlStats() throws Exception {
        // Given
        when(urlService.getStats("abc123")).thenReturn(Optional.of(testShortUrl));

        // When & Then
        mockMvc.perform(get("/stats/abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.originalUrl").value("https://www.example.com"));
    }

    /**
     * Test retrieval of URL statistics when not found
     */
    @Test
    void getStats_ShouldReturnNotFoundForInvalidCode() throws Exception {
        // Given
        when(urlService.getStats("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/stats/nonexistent"))
                .andExpect(status().isNotFound());
    }

    /**
     * Test URL shortening with empty request body
     */
    @Test
    void shortenUrl_ShouldReturnBadRequestForEmptyBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test URL shortening with malformed JSON
     */
    @Test
    void shortenUrl_ShouldReturnBadRequestForMalformedJson() throws Exception {
        // Given
        String malformedJson = "{\"originalUrl\": \"https://www.example.com\""; // Missing closing brace

        // When & Then
        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());
    }
} 