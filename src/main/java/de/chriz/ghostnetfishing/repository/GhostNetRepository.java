package de.chriz.ghostnetfishing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.chriz.ghostnetfishing.model.GhostNet;

public interface GhostNetRepository extends JpaRepository<GhostNet, Long> {

}
