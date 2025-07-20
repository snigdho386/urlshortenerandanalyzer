package Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Model.ShortUrl;

/**
 * Repository interface for ShortUrl entity
 * 
 * This interface extends JpaRepository to provide basic CRUD operations
 * for the ShortUrl entity. It also includes a custom finder method
 * to locate URLs by their short codes.
 * 
 * Spring Data JPA automatically implements this interface at runtime.
 */

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    /**
     * Finds a ShortUrl by its unique short code
     * 
     * @param shortCode The short code to search for
     * @return Optional containing the ShortUrl if found, empty otherwise
     */
    Optional<ShortUrl> findByShortCode(String shortCode);
} 