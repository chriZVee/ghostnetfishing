package de.chriz.ghostnetfishing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String showHomePage(Model model) {
		model.addAttribute("pageTitle", "Geisternetze melden und bergen");
		return "home";
	}
}
