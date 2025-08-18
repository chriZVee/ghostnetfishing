package de.chriz.ghostnetfishing.model;

import jakarta.persistence.*;

@Entity
public class GhostNet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // ID des Netzes
	@Column(name = "gps_coordinates") // Verbindung zur Datenbank-Spalte
	private String gpsCoordinates; // Koordinaten des Netzes
	private float size; // Netzgröße in Quadratmetern
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user; // Nutzer Objekt, beinhaltet alle nutzerbezogenen Variablen

	@Enumerated(EnumType.STRING)
	private GhostNetStatus status;

	// Konstruktoren
	public GhostNet() {

	}

	public GhostNet(String gpsCoordinates, float size, GhostNetStatus status, User user) {
		this.gpsCoordinates = gpsCoordinates;
		this.size = size;
		this.status = status;
		this.user = user;
	}

	// Getter und Setter
	public Long getId() {
		return id;
	}

	public String getGpsCoordinates() {
		return gpsCoordinates;
	}

	public void setGpsCoordinates(String gpsCoordinates) {
		this.gpsCoordinates = gpsCoordinates;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public GhostNetStatus getStatus() {
		return status;
	}

	public void setStatus(GhostNetStatus status) {
		this.status = status;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
