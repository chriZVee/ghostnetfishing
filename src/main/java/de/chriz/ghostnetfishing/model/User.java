package de.chriz.ghostnetfishing.model;

import jakarta.persistence.*;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = true)
	private String name; // Selbstgewählter Name des Nutzers
	@Column(nullable = true)
	private boolean isAnonym; // Wert für Checkbox, für anonyme Reports
	private String telephone; // Telefonnummer des Nutzers
	@Enumerated(EnumType.STRING)
	private UserRole role; // Rolle des Nutzers

	// Konstruktoren
	public User() {
	}

	public User(String name, boolean isAnonym, String telephone, UserRole role) {
		this.name = name;
		this.isAnonym = isAnonym;
		this.telephone = telephone;
		this.role = role;
	}

	// Getter und Setter
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getIsAnonym() {
		return isAnonym;
	}

	public void setIsAnonym(boolean isAnonym) {
		this.isAnonym = isAnonym;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}
}
