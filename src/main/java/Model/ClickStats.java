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

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClickStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime clickedAt = LocalDateTime.now();
    private String ipAddress;
    private String referrer;
    private String userAgent;

    @ManyToOne
    @JoinColumn(name = "short_url_id")
    private ShortUrl shortUrl;
}

