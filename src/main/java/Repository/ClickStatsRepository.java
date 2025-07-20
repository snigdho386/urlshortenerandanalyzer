package Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import Model.ClickStats;

public interface ClickStatsRepository extends JpaRepository<ClickStats, Long> {
}

