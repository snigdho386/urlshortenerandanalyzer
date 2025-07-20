package Model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import Model.ShortUrl;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing click statistics for a shortened URL
 * 
 * This class maps to the 'click_stats' table and tracks detailed information
 * about each click on a shortened URL, including visitor analytics.
 * 
 * Key features:
 * - Click timestamp
 * - Visitor IP address
 * - Referrer information
 * - User agent (browser/device info)
 * - Many-to-one relationship with ShortUrl
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClickStats {
    /** Primary key - auto-generated unique identifier */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Timestamp when the click occurred */
    private LocalDateTime clickedAt = LocalDateTime.now();
    
    /** IP address of the visitor */
    private String ipAddress;
    
    /** Referrer URL (where the visitor came from) */
    private String referrer;
    
    /** User agent string (browser/device information) */
    private String userAgent;

    /** Reference to the shortened URL this click belongs to */
    @ManyToOne
    @JoinColumn(name = "short_url_id")
    private ShortUrl shortUrl;
}

