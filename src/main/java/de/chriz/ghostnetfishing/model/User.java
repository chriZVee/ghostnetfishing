package de.chriz.ghostnetfishing.model;

import de.chriz.ghostnetfishing.validation.RecoverChecks;
import de.chriz.ghostnetfishing.validation.ReportChecks;
import de.chriz.ghostnetfishing.validation.ReportRecoveredChecks;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(groups = RecoverChecks.class, message = "Bitte Name angeben")
	@Column(nullable = true)
	private String name; // Name des Nutzers

	@NotBlank(groups = { RecoverChecks.class, ReportRecoveredChecks.class }, message = "Bitte Telefon angeben")
	@Column(nullable = true)
	private String telephone; // Telefonnummer des Nutzers

	@Column(nullable = false)
	private boolean anonym; // Wert für Checkbox, für anonyme Reports

	// Report-Validierung
	@AssertTrue(groups = ReportChecks.class, message = "Bitte Name und Telefonnummer"
			+ " angeben oder \"anonym\" bleiben auswählen")
	public boolean isContactDetailsValid() {
		if (anonym || (((telephone != null) && !telephone.isBlank()) && ((name != null) && !name.isBlank()))) {
			return true;
		}
		return false;
	}

	@Enumerated(EnumType.STRING)
	private UserRole role; // Rolle des Nutzers

	// Konstruktoren
	public User() {
	}

	public User(String name, boolean anonym, String telephone, UserRole role) {
		this.name = name;
		this.anonym = anonym;
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

	public boolean getAnonym() {
		return anonym;
	}

	public void setAnonym(boolean anonym) {
		this.anonym = anonym;
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
