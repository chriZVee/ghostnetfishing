package de.chriz.ghostnetfishing.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import de.chriz.ghostnetfishing.model.GhostNet;
import de.chriz.ghostnetfishing.model.GhostNetStatus;
import de.chriz.ghostnetfishing.model.User;
import de.chriz.ghostnetfishing.model.UserRole;
import de.chriz.ghostnetfishing.repository.GhostNetRepository;
import de.chriz.ghostnetfishing.validation.ReportChecks;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
public class GhostNetController {
	private final GhostNetRepository ghostNetRepository;

	// Konstruktor
	public GhostNetController(GhostNetRepository ghostNetRepository) {
		this.ghostNetRepository = ghostNetRepository;
	}

	// Bereitet alle Daten fürs Formulars vor
	@GetMapping("/report")
	public String showForm(Model model) {
		GhostNet ghostNet = new GhostNet();
		ghostNet.setUser(new User());
		model.addAttribute("ghostNet", ghostNet);
		model.addAttribute("formAction", "/report");
		return "report";
	}

	// Sendet die Daten des gemeldeten Netzes an die View
	@PostMapping("/report")
	public String submitReport(@Validated(ReportChecks.class) @ModelAttribute GhostNet ghostNet, BindingResult br,
			Model model, RedirectAttributes ra) {

		// Weitergabe an die View
		model.addAttribute("formAction", "/report");

		// Stabilität: Falls kein Nutzer besteht, wird einer angelegt
		if (ghostNet.getUser() == null) {
			ghostNet.setUser(new User());
		}

		// Validierung
		if (br.hasErrors()) {
			return "report";
		}

		List<GhostNetStatus> activeList = List.of(GhostNetStatus.GEMELDET, GhostNetStatus.BERGUNG_BEVORSTEHEND);
		boolean duplicate = ghostNetRepository.existsByLatitudeAndLongitudeAndStatusIn(ghostNet.getLatitude(),
				ghostNet.getLongitude(), activeList);

		if (duplicate) {
			br.reject(null, "Für diese Koordinaten wurde bereits ein Geisternetz gemeldet");
			return "report";
		}

		// Setze Nutzerrollen und Netz-Status
		ghostNet.getUser().setRole(UserRole.MELDENDE_PERSON);
		ghostNet.setStatus(GhostNetStatus.GEMELDET);

		// Lies den Nutzer aus
		User user = ghostNet.getUser();

		// Anonymitätscheck
		boolean anonym = user.getAnonym();
		if (anonym) {
			user.setName(null);
			user.setTelephone(null);
		} else {
			if (user.getName() != null && user.getName().isBlank())
				user.setName(null);
			if (user.getTelephone() != null && user.getTelephone().isBlank())
				user.setTelephone(null);
		}

		// Speichere das Netz ins Repository
		GhostNet saved = ghostNetRepository.save(ghostNet);

		// Gib die ID für das gemeldete Netz weiter
		ra.addFlashAttribute("reportedNetId", saved.getId());

		return "redirect:/report-success";
	}

	// Bereitet alle GhostNets aus der Datenbank für die View vor
	@GetMapping("/report-success")
	public String showSuccess(Model model, @RequestParam(defaultValue = "0") int reportSuccessPageIndex) {
		// Regelt die Zahl der Items der Pagination
		Pageable pageable = PageRequest.of(reportSuccessPageIndex, 8);
		Page<GhostNet> reportSuccessPage = ghostNetRepository.findAllByOrderByLastUpdatedDesc(pageable);

		model.addAttribute("reportSuccessPage", reportSuccessPage);
		model.addAttribute("reportSuccessNets", reportSuccessPage.getContent());

		// Pfade
		final String basePath = "/report-success";
		model.addAttribute("reportSuccessPath", basePath + "?reportSuccessPageIndex=");

		return "report-success";
	}

	@GetMapping("/overview")
	public String showOverview(Model model, @RequestParam(defaultValue = "0") int reportedPageIndex,
			@RequestParam(defaultValue = "0") int recoveryPageIndex,
			@RequestParam(defaultValue = "0") int recoveredLostPageIndex) {

		// Pagination
		final int pageSize = 5; // Regelt die Zahl der Items der Pagination
		Pageable pageableReported = PageRequest.of(reportedPageIndex, pageSize);
		Pageable pageableRecovery = PageRequest.of(recoveryPageIndex, pageSize);
		Pageable pageableRecoveredLost = PageRequest.of(recoveredLostPageIndex, pageSize);

		List<GhostNetStatus> ghostNetStatusList = List.of(GhostNetStatus.GEBORGEN, GhostNetStatus.VERSCHOLLEN);

		Page<GhostNet> reportedPage = ghostNetRepository.findByStatus(GhostNetStatus.GEMELDET, pageableReported);
		Page<GhostNet> recoveryPage = ghostNetRepository.findByStatus(GhostNetStatus.BERGUNG_BEVORSTEHEND,
				pageableRecovery);
		Page<GhostNet> recoveredLostPage = ghostNetRepository.findByStatusIn(ghostNetStatusList, pageableRecoveredLost);

		// Pagination-Objekte
		model.addAttribute("reportedPage", reportedPage);
		model.addAttribute("recoveryPage", recoveryPage);
		model.addAttribute("recoveredLostPage", recoveredLostPage);

		// Inhalt der Netz-Tabellen
		model.addAttribute("reportedNets", reportedPage.getContent());
		model.addAttribute("recoveryNets", recoveryPage.getContent());
		model.addAttribute("recoveredLostNets", recoveredLostPage.getContent());

		// Pfade
		final String basePath = "/overview";
		model.addAttribute("reportedPath", basePath + "?recoveryPageIndex=" + recoveryPageIndex
				+ "&recoveredLostPageIndex=" + recoveredLostPageIndex + "&reportedPageIndex=");
		model.addAttribute("recoveryPath", basePath + "?reportedPageIndex=" + reportedPageIndex
				+ "&recoveredLostPageIndex=" + recoveredLostPageIndex + "&recoveryPageIndex=");
		model.addAttribute("recoveredLostPath", basePath + "?reportedPageIndex=" + reportedPageIndex
				+ "&recoveryPageIndex=" + recoveryPageIndex + "&recoveredLostPageIndex=");

		// Anzeige der Netze für die Map
		List<GhostNet> reportedNetsList = ghostNetRepository.findByStatus(GhostNetStatus.GEMELDET);
		model.addAttribute("reportedNetsList", reportedNetsList);
		return "overview";
	}

	@GetMapping("/recover")
	public String showRecover(@RequestParam Long id, Model model) {
		GhostNet ghostNet = ghostNetRepository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
		// Erstelle einen neuen Nutzer, für leere Eingabefelder
		ghostNet.setUser(new User());

		model.addAttribute("ghostNet", ghostNet);
		model.addAttribute("formAction", "/recover");
		return "recover";
	}

	@PostMapping("/recover")
	public String submitRecover(@ModelAttribute GhostNet ghostNet, @RequestParam Long id, RedirectAttributes ra) {
		GhostNet updatedNet = ghostNetRepository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

		// Errohandling - Falls kein User vorhanden ist
		if (updatedNet.getUser() == null) {
			throw new IllegalStateException();
		}

		updatedNet.setUser(ghostNet.getUser());

		updatedNet.getUser().setRole(UserRole.BERGENDE_PERSON);
		updatedNet.setStatus(GhostNetStatus.BERGUNG_BEVORSTEHEND);

		GhostNet saved = ghostNetRepository.save(updatedNet);
		Long recoveryNetId = saved.getId();

		// Gib die ID für das gemeldete Netz weiter
		ra.addFlashAttribute("recoveryNetId", recoveryNetId);

		return "redirect:/report-success";
	}

	@GetMapping("/report-recovered")
	public String showRecovered(@RequestParam Long id, Model model) {
		GhostNet ghostNet = ghostNetRepository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

		// Stabilität - Falls kein User vorhanden ist, wird ein neuer erstellt
		if (ghostNet.getUser() == null) {
			ghostNet.setUser(new User());
		}

		model.addAttribute("ghostNet", ghostNet);
		// Gib die Seitenkennung weiter
		model.addAttribute("formAction", "/report-recovered");
		return "report-recovered";
	}

	@PostMapping("/report-recovered")
	public String submitRecovered(@RequestParam Long id, @ModelAttribute GhostNet ghostNet, RedirectAttributes ra) {
		ghostNet = ghostNetRepository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
		// Sicherheitsprüfung des Nutzers
		User user = ghostNet.getUser();
		// wenn kein Nutzer vorhanden ist, erstelle einen neuen Nutzer
		if (user == null) {
			throw new IllegalStateException();
		}

		// Setze neuen Status fürs Netz
		ghostNet.setStatus(GhostNetStatus.GEBORGEN);
		// Speichere das Netz ins Repository ab
		GhostNet saved = ghostNetRepository.save(ghostNet);
		// Gib die ID für das gemeldete Netz weiter
		ra.addFlashAttribute("recoveredNetId", saved.getId());
		// Gib die Seitenkennung weiter
		ra.addFlashAttribute("formAction", "/report-recovered");
		return "redirect:/report-success";
	}

}
