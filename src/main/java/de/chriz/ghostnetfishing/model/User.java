package de.chriz.ghostnetfishing.model;

import de.chriz.ghostnetfishing.validation.RecoverChecks;
import de.chriz.ghostnetfishing.validation.ReportChecks;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(groups = RecoverChecks.class, message = "Name erforderlich")
	@Column(nullable = true)
	private String name; // Selbstgewählter Name des Nutzers

	@NotBlank(groups = RecoverChecks.class, message = "Telefon erforderlich")
	@Column(nullable = true)
	private String telephone; // Telefonnummer des Nutzers

	@Column(nullable = false)
	private boolean anonym; // Wert für Checkbox, für anonyme Reports
	
	// Report-Validation
	@AssertTrue(groups = ReportChecks.class, message = "Bitte Name und Telefon angeben")
	public boolean isContactDetailsValid() {
		return anonym || (name != null && !name.isBlank() || telephone != null && !telephone.isBlank());
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
