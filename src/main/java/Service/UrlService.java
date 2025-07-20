package Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

import Model.ClickStats;
import Model.ShortUrl;
import Repository.ClickStatsRepository;
import Repository.ShortUrlRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * Service Layer for URL Shortener Business Logic
 * 
 * This service contains all the business logic for URL shortening operations.
 * It handles URL creation, redirection, click tracking, and statistics retrieval.
 * The service layer acts as an intermediary between the controller and repository layers.
 */

@Service
@RequiredArgsConstructor
public class UrlService {
    private final ShortUrlRepository shortUrlRepo;
    private final ClickStatsRepository clickStatsRepo;

    /**
     * Creates a shortened URL from the provided original URL
     * 
     * This method generates a unique 6-character short code and creates a new ShortUrl entity.
     * It ensures the generated code is unique by checking against existing codes.
     * 
     * @param originalUrl The original URL to be shortened
     * @return ShortUrl object with generated short code and metadata
     */
    public ShortUrl createShortUrl(String originalUrl) {
        // Generate a unique short code
        String shortCode = generateCode();
        // Ensure uniqueness by regenerating if code already exists
        while (shortUrlRepo.findByShortCode(shortCode).isPresent()) {
            shortCode = generateCode();
        }
        // Create and save the new ShortUrl with current timestamp and empty click stats
        return shortUrlRepo.save(ShortUrl.builder()
                .originalUrl(originalUrl)
                .shortCode(shortCode)
                .createdAt(LocalDateTime.now())
                .clickStats(new ArrayList<>())
                .build());
    }

    /**
     * Retrieves the original URL for a given short code and tracks the click
     * 
     * This method finds the original URL and automatically creates a click statistics record
     * with information about the visitor (IP address, referrer, user agent).
     * 
     * @param shortCode The short code to look up
     * @param request HTTP request object to extract visitor information
     * @return Optional containing the ShortUrl if found, empty otherwise
     */
    public Optional<ShortUrl> getOriginalUrl(String shortCode, HttpServletRequest request) {
        // Find the short URL by code
        Optional<ShortUrl> shortUrl = shortUrlRepo.findByShortCode(shortCode);
        // If found, create click statistics record
        shortUrl.ifPresent(url -> clickStatsRepo.save(
                ClickStats.builder()
                        .ipAddress(request.getRemoteAddr())
                        .referrer(request.getHeader("Referer"))
                        .userAgent(request.getHeader("User-Agent"))
                        .shortUrl(url)
                        .build()));
        return shortUrl;
    }

    /**
     * Retrieves all shortened URLs in the system
     * 
     * @return List of all ShortUrl objects
     */
    public List<ShortUrl> getAllUrls() {
        return shortUrlRepo.findAll();
    }

    /**
     * Retrieves statistics for a specific short URL
     * 
     * @param shortCode The short code to get statistics for
     * @return Optional containing the ShortUrl with click statistics if found
     */
    public Optional<ShortUrl> getStats(String shortCode) {
        return shortUrlRepo.findByShortCode(shortCode);
    }

    /**
     * Generates a random 6-character short code
     * 
     * Creates a unique identifier using alphanumeric characters (a-z, A-Z, 0-9).
     * This method is used to generate short codes for URL shortening.
     * 
     * @return 6-character random string
     */
    private String generateCode() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        // Generate 6 random characters
        for (int i = 0; i < 6; i++)
            sb.append(chars.charAt(r.nextInt(chars.length())));
        return sb.toString();
    }
}
