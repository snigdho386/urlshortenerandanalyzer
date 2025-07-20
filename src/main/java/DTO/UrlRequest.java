package DTO;

import lombok.Data;

/**
 * Data Transfer Object for URL shortening requests
 * 
 * This DTO is used to receive the original URL from client requests.
 * It provides a clean interface for the API endpoint that creates shortened URLs.
 * 
 * The @Data annotation from Lombok automatically generates getters, setters,
 * toString, equals, and hashCode methods.
 */
@Data
public class UrlRequest {
    /** The original URL that needs to be shortened */
    private String originalUrl;
}
