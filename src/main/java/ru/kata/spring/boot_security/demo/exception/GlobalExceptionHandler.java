package ru.kata.spring.boot_security.demo.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public String handleAll(Exception ex, Model model) {
		model.addAttribute("message", "Произошла ошибка: " + ex.getMessage());
		return "error";
	}
}
