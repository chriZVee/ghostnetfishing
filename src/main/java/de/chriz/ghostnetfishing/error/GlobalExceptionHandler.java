package de.chriz.ghostnetfishing.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice // Hört global auf alle Exceptions
public class GlobalExceptionHandler {

	private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(DuplicateActiveNetException.class)
	public String handleDuplicate(DuplicateActiveNetException ex, Model model) {
		model.addAttribute("errorMessage", "Dieses Netz wurde bereits gemeldet.");
		return "error";
	}

	@ExceptionHandler(EmptyFieldException.class)
	public String handleEmptyFieldException(EmptyFieldException ex, Model model) {
		model.addAttribute("errorMessage", "Bitte füllen sie alle Felder aus.");
		return "error";
	}

	// Schließe falsche Eingabeformate aus -> Error Page
	@ExceptionHandler({org.springframework.web.bind.MethodArgumentNotValidException.class,
			  org.springframework.validation.BindException.class,
			  org.springframework.beans.TypeMismatchException.class,
			  org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
			  NumberFormatException.class})
	public String handleInvalidFormatException(Exception ex, Model model) {
		model.addAttribute("errorMessage", "Für Dezimalzahlen bitte + '.' +  verwenden. Bsp.: 12.345");
		return "error";
	}

	@ExceptionHandler(Exception.class)
	public String handleException(Exception ex, Model model) {
		logger.error("Unbekannter Fehler", ex);
		model.addAttribute("errorMessage", "Leider ist ein unbekannter Fehler aufgetreten. "
				+ "Für weitere Hilfe, kontaktieren Sie bitte einen Administrator.");

		return "error";
	}

}
