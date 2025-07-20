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

@Service
@RequiredArgsConstructor
public class UrlService {
    private final ShortUrlRepository shortUrlRepo;
    private final ClickStatsRepository clickStatsRepo;

    public ShortUrl createShortUrl(String originalUrl) {
        String shortCode = generateCode();
        while (shortUrlRepo.findByShortCode(shortCode).isPresent()) {
            shortCode = generateCode();
        }
        return shortUrlRepo.save(ShortUrl.builder()
                .originalUrl(originalUrl)
                .shortCode(shortCode)
                .createdAt(LocalDateTime.now())
                .clickStats(new ArrayList<>())
                .build());
    }

    public Optional<ShortUrl> getOriginalUrl(String shortCode, HttpServletRequest request) {
        Optional<ShortUrl> shortUrl = shortUrlRepo.findByShortCode(shortCode);
        shortUrl.ifPresent(url -> clickStatsRepo.save(
                ClickStats.builder()
                        .ipAddress(request.getRemoteAddr())
                        .referrer(request.getHeader("Referer"))
                        .userAgent(request.getHeader("User-Agent"))
                        .shortUrl(url)
                        .build()));
        return shortUrl;
    }

    public List<ShortUrl> getAllUrls() {
        return shortUrlRepo.findAll();
    }

    public Optional<ShortUrl> getStats(String shortCode) {
        return shortUrlRepo.findByShortCode(shortCode);
    }

    private String generateCode() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < 6; i++)
            sb.append(chars.charAt(r.nextInt(chars.length())));
        return sb.toString();
    }
}
