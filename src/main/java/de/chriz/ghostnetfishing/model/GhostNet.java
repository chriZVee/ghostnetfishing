package de.chriz.ghostnetfishing.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.*;

@EntityListeners(AuditingEntityListener.class)
@Entity
public class GhostNet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // ID des Netzes
	@Column(name = "latitude", nullable = false) // Verbindung zur Datenbank-Spalte
	private Double latitude; // Koordinaten des Netzes
	@Column(name = "longitude", nullable = false)
	private Double longitude; // Koordinaten des Netzes
	private Double size; // Netzgröße in Quadratmetern
	@LastModifiedDate
	@Column(name = "last_updated")
	private LocalDateTime lastUpdated;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id")
	private User user; // Nutzer Objekt, beinhaltet alle nutzerbezogenen Variablen

	@Enumerated(EnumType.STRING)
	private GhostNetStatus status;

	// Konstruktoren
	public GhostNet() {

	}

	public GhostNet(Long id, Double latitude, Double longitude, Double size, GhostNetStatus status, LocalDateTime lastUpdated, User user) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.size = size;
		this.status = status;
		this.lastUpdated = lastUpdated;
		this.user = user;
	}

	// Getter und Setter
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getSize() {
		return size;
	}

	public void setSize(Double size) {
		this.size = size;
	}

	public GhostNetStatus getStatus() {
		return status;
	}

	public void setStatus(GhostNetStatus status) {
		this.status = status;
	}

	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(LocalDateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
