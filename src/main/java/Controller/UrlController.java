package Controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import DTO.UrlRequest;
import Model.ShortUrl;
import Service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for URL Shortener Operations
 * 
 * This controller handles all HTTP requests related to URL shortening and analytics.
 * It provides endpoints for creating short URLs, redirecting to original URLs,
 * retrieving all URLs, and getting click statistics.
 * 
 * All endpoints are prefixed with /api due to server.servlet.context-path configuration.
 */

@Controller
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    /**
     * Creates a shortened URL from the provided original URL
     * 
     * @param request Contains the original URL to be shortened
     * @return ResponseEntity with the created ShortUrl object
     * 
     * Endpoint: POST /api/shorten
     * Request Body: {"originalUrl": "https://www.example.com"}
     */
    @PostMapping("/shorten")
    public ResponseEntity<ShortUrl> shorten(@RequestBody UrlRequest request) {

        System.out.println(request.getOriginalUrl());
        return ResponseEntity.ok(urlService.createShortUrl(request.getOriginalUrl()));
    }

    /**
     * Redirects a short code to its original URL
     * 
     * This endpoint handles URL redirection and automatically tracks click statistics
     * including IP address, referrer, and user agent information.
     * 
     * @param shortCode The short code to redirect
     * @param request HTTP request object for extracting client information
     * @return ResponseEntity with 302 redirect or 404 if not found
     * 
     * Endpoint: GET /api/{shortCode}
     * Example: GET /api/abc123
     */
    @GetMapping("/{shortCode}")
public ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request) {
    return urlService.getOriginalUrl(shortCode, request)
            .map(shortUrl -> {
                String originalUrl = shortUrl.getOriginalUrl();
                // Add http:// prefix if URL doesn't have a protocol
                if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
                    originalUrl = "http://" + originalUrl;
                }
                return ResponseEntity.status(302).location(URI.create(originalUrl)).<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
}


    /**
     * Retrieves all shortened URLs in the system
     * 
     * @return ResponseEntity with list of all ShortUrl objects
     * 
     * Endpoint: GET /api/urls
     */
    @GetMapping("/urls")
    public ResponseEntity<List<ShortUrl>> getAll() {
        return ResponseEntity.ok(urlService.getAllUrls());
    }

    /**
     * Retrieves statistics for a specific short URL
     * 
     * @param code The short code to get statistics for
     * @return ResponseEntity with ShortUrl object including click statistics, or 404 if not found
     * 
     * Endpoint: GET /api/stats/{code}
     * Example: GET /api/stats/abc123
     */
    @GetMapping("/stats/{code}")
    public ResponseEntity<ShortUrl> getStats(@PathVariable String code) {
        return urlService.getStats(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
