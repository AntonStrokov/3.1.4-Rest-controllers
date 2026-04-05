package ru.kata.spring.boot_security.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.model.User;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	@GetMapping
	public String adminPage(@AuthenticationPrincipal User currentUser, Model model) {
		model.addAttribute("currentUser", currentUser);
		return "admin-list";
	}
}