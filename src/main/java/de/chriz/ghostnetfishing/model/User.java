package de.chriz.ghostnetfishing.model;

import jakarta.persistence.*;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String name; // Selbstgewählter Name des Nutzers
	private boolean anonymCheck; // Wert für Checkbox, für anonyme Reports
	private String telefon; // Telefonnummer des Nutzers
	@Enumerated(EnumType.STRING)
	private UserRole role;

	// Konstruktoren
	public User() {

	}

	public User(String name, boolean anonymCheck, String telefon, UserRole role) {
		this.name = name;
		this.anonymCheck = anonymCheck;
		this.telefon = telefon;
		this.role = role;
	}

	// Getter und Setter
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getAnonymCheck() {
		return anonymCheck;
	}

	public void setAnonymCheck(boolean anonymCheck) {
		this.anonymCheck = anonymCheck;
	}

	public String getTelefon() {
		return telefon;
	}

	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}
}
