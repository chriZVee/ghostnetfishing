package de.chriz.ghostnetfishing.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import de.chriz.ghostnetfishing.model.GhostNet;
import de.chriz.ghostnetfishing.model.GhostNetStatus;

public interface GhostNetRepository extends JpaRepository<GhostNet, Long> {
	Page<GhostNet> findAllByOrderByLastUpdatedDesc(Pageable pageable);
	
	Page<GhostNet> findByStatus(GhostNetStatus status, Pageable pageable);

	Page<GhostNet> findByStatusIn(List<GhostNetStatus> statuses, Pageable pageable);

	List<GhostNet> findByStatus(GhostNetStatus status);
	
	boolean existsByLatitudeAndLongitudeAndStatusIn(Double latitude, Double longitude, Collection<GhostNetStatus> statuses);
}