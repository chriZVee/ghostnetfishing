package de.chriz.ghostnetfishing.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

import de.chriz.ghostnetfishing.model.GhostNet;
import de.chriz.ghostnetfishing.model.GhostNetStatus;
import de.chriz.ghostnetfishing.model.User;
import de.chriz.ghostnetfishing.model.UserRole;
import de.chriz.ghostnetfishing.repository.GhostNetRepository;
import de.chriz.ghostnetfishing.error.DuplicateActiveNetException;
import de.chriz.ghostnetfishing.error.EmptyFieldException;

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
	public String submitReport(@ModelAttribute GhostNet ghostNet, Model model, RedirectAttributes ra) {
		if (ghostNet.getUser() == null) {
			ghostNet.setUser(new User());
		}

		// Setze Nutzerrollen und Netz-Status
		ghostNet.getUser().setRole(UserRole.MELDENDE_PERSON);
		ghostNet.setStatus(GhostNetStatus.GEMELDET);

		// Lies den Nutzer aus
		User user = ghostNet.getUser();

		boolean isAnonym = user.getIsAnonym();

		if (isAnonym) {
			user.setName(null);
			user.setTelephone(null);
		} else {
			if (user.getName() != null && user.getName().isBlank())
				user.setName(null);
			if (user.getTelephone() != null && user.getTelephone().isBlank())
				user.setTelephone(null);
		}

		// Error-Handling
		Double latitude = ghostNet.getLatitude();
		Double longitude = ghostNet.getLongitude();
		List<GhostNetStatus> active = List.of(GhostNetStatus.GEMELDET, GhostNetStatus.BERGUNG_BEVORSTEHEND);
		boolean activeNetExists = ghostNetRepository.existsByLatitudeAndLongitudeAndStatusIn(latitude, longitude,
				active);

		Double size = ghostNet.getSize();
		String telephone = ghostNet.getUser().getTelephone();
		String name = ghostNet.getUser().getName();

		// Verhindere unausgefüllte Felder -> Error Page
		boolean isNetEmpty = latitude == null || longitude == null || size == null;
		boolean isUserEmpty = name == null || telephone == null;

		if (isNetEmpty || (isUserEmpty && !isAnonym)) {
			throw new EmptyFieldException();
		}

		// Schließe Duplikate aus -> Error Page
		boolean isDuplicate = latitude != null && longitude != null && activeNetExists;
		if (isDuplicate) {
			throw new DuplicateActiveNetException();
		}

		GhostNet saved = ghostNetRepository.save(ghostNet);

		// Gib die ID für das gemeldete Netz weiter
		ra.addFlashAttribute("reportedNetId", saved.getId());

		return "redirect:/report-success";
	}

	// Bereitet alle GhostNets aus der Datenbank für die View vor
	@GetMapping("/report-success")
	public String showSuccess(Model model, @RequestParam(defaultValue = "0") int reportSuccessPageIndex) {
		// Regelt die Zahl der Items der Pagination
		Pageable pageable = PageRequest.of(reportSuccessPageIndex, 10);
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

		// Regelt die Zahl der Items der Pagination
		final int pageSize = 5;
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
		GhostNet ghostNet = ghostNetRepository.findById(id).orElseThrow();
		// Erstelle einen neuen Nutzer, falls nicht vorhanden

		ghostNet.setUser(new User());

		// ReadOnly-Felder im Formular
		List<String> readOnlyFields = new ArrayList<>();
		readOnlyFields.add("id");
		readOnlyFields.add("latitude");
		readOnlyFields.add("longitude");
		readOnlyFields.add("size");

		model.addAttribute("ghostNet", ghostNet);
		model.addAttribute("readOnlyFields", readOnlyFields);
		model.addAttribute("formAction", "/recover");
		return "recover";
	}

	@PostMapping("/recover")
	public String submitRecover(@ModelAttribute GhostNet ghostNet, @RequestParam Long id) {
		GhostNet updatedNet = ghostNetRepository.findById(id).orElseThrow();

		// Safety
		if (ghostNet.getUser() != null && updatedNet.getUser() != null) {
			updatedNet.getUser().setName(ghostNet.getUser().getName());
			updatedNet.getUser().setTelephone(ghostNet.getUser().getTelephone());
		}
		if (updatedNet.getUser() == null) {
			updatedNet.setUser(new User());
		}

		updatedNet.getUser().setRole(UserRole.BERGENDE_PERSON);
		updatedNet.setStatus(GhostNetStatus.BERGUNG_BEVORSTEHEND);
		ghostNetRepository.save(updatedNet);
		return "redirect:/report-success";
	}

	@GetMapping("/report-recovered")
	public String showRecovered(@RequestParam Long id, Model model) {
		GhostNet ghostNet = ghostNetRepository.findById(id).orElseThrow();

		// Safety
		if (ghostNet.getUser() == null) {
			ghostNet.setUser(new User());
		}
		// ReadOnly-Felder im Formular
		List<String> readOnlyFields = new ArrayList<>();
		readOnlyFields.add("id");
		readOnlyFields.add("latitude");
		readOnlyFields.add("longitude");
		readOnlyFields.add("size");

		model.addAttribute("ghostNet", ghostNet);
		model.addAttribute("formAction", "/report-recovered");
		model.addAttribute("readOnlyFields", readOnlyFields);
		return "report-recovered";
	}

	@PostMapping("/report-recovered")
	public String submitRecovered(@ModelAttribute GhostNet ghostNet) {
		// Safety
		User user = ghostNet.getUser();
		// wenn kein Nutzer vorhanden ist, erstelle einen neuen Nutzer
		if (user == null) {
			user = new User();
			ghostNet.setUser(new User());
		}

		ghostNet.setStatus(GhostNetStatus.GEBORGEN);

		// Leite zur Errorseite
		// wenn die Bergung eingetragenen Nutzer entspricht
		if (!user.equals(ghostNetRepository.findById(ghostNet.getId()).orElse(null))) {
			return "error";
		}
		// wenn die Telefonnummer nicht dem zur Bergung eingetragenen Nutzers entspricht
		if (!user.getTelephone().equals(ghostNetRepository.findById(ghostNet.getId()).orElse(null))) {
			return "error";
		}
		// wenn das Netz vorher nicht zur Bergung eingetragen wurde
		ghostNetRepository.save(ghostNet);
		return "redirect:/report-success";
	}

	public boolean isValidNetValue(Double netValue) {
		String stringValue = String.valueOf(netValue);
		String pattern = "^-?\\d+([.,]\\d+)?$";
		boolean isValidNetValue = stringValue.matches(pattern);
		return isValidNetValue;
	}

}
