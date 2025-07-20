package Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Model.ClickStats;

/**
 * Repository interface for ClickStats entity
 * 
 * This interface extends JpaRepository to provide basic CRUD operations
 * for the ClickStats entity. It handles database operations for storing
 * and retrieving click statistics data.
 * 
 * Spring Data JPA automatically implements this interface at runtime.
 */
@Repository
public interface ClickStatsRepository extends JpaRepository<ClickStats, Long> {
}

