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

@Controller
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<ShortUrl> shorten(@RequestBody UrlRequest request) {

        System.out.println(request.getOriginalUrl());
        return ResponseEntity.ok(urlService.createShortUrl(request.getOriginalUrl()));
    }

    @GetMapping("/{shortCode}")
public ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request) {
    return urlService.getOriginalUrl(shortCode, request)
            .map(shortUrl -> {
                String originalUrl = shortUrl.getOriginalUrl();
                if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
                    originalUrl = "http://" + originalUrl;
                }
                return ResponseEntity.status(302).location(URI.create(originalUrl)).<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
}


    @GetMapping("/urls")
    public ResponseEntity<List<ShortUrl>> getAll() {
        return ResponseEntity.ok(urlService.getAllUrls());
    }

    @GetMapping("/stats/{code}")
    public ResponseEntity<ShortUrl> getStats(@PathVariable String code) {
        return urlService.getStats(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
