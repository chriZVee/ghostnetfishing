package de.chriz.ghostnetfishing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import de.chriz.ghostnetfishing.model.GhostNet;
import de.chriz.ghostnetfishing.model.GhostNetStatus;
import de.chriz.ghostnetfishing.model.User;
import de.chriz.ghostnetfishing.model.UserRole;
import de.chriz.ghostnetfishing.repository.GhostNetRepository;

@Controller
public class GhostNetController {
	private final GhostNetRepository ghostNetRepository;

	// Konstruktur
	public GhostNetController(GhostNetRepository ghostNetRepository) {
		this.ghostNetRepository = ghostNetRepository;
	}

	// Holt alle GhostNets aus der Datenbank und übergibt sie dem Model
	@GetMapping("/overview")
	public String showGhostNets(Model model) {
		model.addAttribute("overview", ghostNetRepository.findAll());
		return "overview";
	}

	// Liest die Daten des Formulars ein
	@GetMapping("/report")
	public String showForm(Model model, GhostNet ghostNet) {
		ghostNet = new GhostNet();
		ghostNet.setUser(new User());
		model.addAttribute("ghostNet", ghostNet);
		return "report";
	}

	// Sendet die Daten des gemeldeten Netzes an die View
	@PostMapping("/report")
	public String submitForm(@ModelAttribute GhostNet ghostNet) {
		ghostNet.getUser().setRole(UserRole.MELDENDE_PERSON);
		ghostNet.setStatus(GhostNetStatus.GEMELDET);
		ghostNetRepository.save(ghostNet);
		return "redirect:/ghostnets";
	}

	@GetMapping("/recover")
	public String showRecover(Model model, GhostNet ghostNet) {
//		ghostNet = new GhostNet();
//		ghostNet.setUser(new User());
//		model.addAttribute("ghostNet", ghostNet);
		return "recover";
	}

}
