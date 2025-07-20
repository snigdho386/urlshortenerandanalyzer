package Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a shortened URL
 * 
 * This class maps to the 'short_url' table in the database and represents
 * a shortened URL with its metadata and associated click statistics.
 * 
 * Key features:
 * - Unique short code for URL identification
 * - Original URL storage
 * - Creation timestamp
 * - One-to-many relationship with ClickStats
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortUrl {
    /** Primary key - auto-generated unique identifier */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique short code used for URL redirection (e.g., "abc123") */
    @Column(unique = true, nullable = false)
    private String shortCode;

    /** The original long URL that was shortened */
    @Column(nullable = false)
    private String originalUrl;

    /** Timestamp when the URL was created */
    private LocalDateTime createdAt = LocalDateTime.now();

    /** Collection of click statistics for this URL */
    @OneToMany(mappedBy = "shortUrl", cascade = CascadeType.ALL)
    private List<ClickStats> clickStats = new ArrayList<>();
} 