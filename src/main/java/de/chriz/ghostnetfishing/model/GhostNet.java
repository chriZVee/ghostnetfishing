package de.chriz.ghostnetfishing.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import de.chriz.ghostnetfishing.validation.ReportChecks;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@EntityListeners(AuditingEntityListener.class)
@Entity
public class GhostNet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // ID des Netzes

	@NotNull(groups = ReportChecks.class, message = "Bitte Breitengrad angeben")
	@Column(name = "latitude", nullable = false) // Verbindung zur Datenbank-Spalte
	private Double latitude; // Koordinaten des Netzes

	@NotNull(groups = ReportChecks.class, message = "Bitte Längengrad angeben")
	@Column(name = "longitude", nullable = false)
	private Double longitude; // Koordinaten des Netzes

	@NotNull(groups = ReportChecks.class, message = "Bitte Netzgröße angeben")
	@Column(name = "size", nullable = false)
	private Double size; // Netzgröße in Quadratmetern

	@LastModifiedDate
	@Column(name = "last_updated")
	private LocalDateTime lastUpdated;

	@Valid
	@ManyToOne(cascade = CascadeType.PERSIST) // Wenn ein Netz gelöscht wird, bleibt der Nutzer bestehen
	@JoinColumn(name = "user_id")
	private User user; // Nutzer Objekt, beinhaltet alle nutzerbezogenen Variablen

	@Enumerated(EnumType.STRING)
	private GhostNetStatus status;

	// Konstruktoren
	public GhostNet() {

	}

	public GhostNet(Long id, Double latitude, Double longitude, Double size, GhostNetStatus status,
			LocalDateTime lastUpdated, User user) {
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
