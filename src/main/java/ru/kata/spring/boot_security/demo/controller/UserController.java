package ru.kata.spring.boot_security.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Добавил импорт
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid; // Добавил импорт

@Controller
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final RoleDao roleDao;

	@GetMapping("/")
	public String rootRedirect() {
		return "redirect:/login";
	}

	// ===================== ADMIN CRUD =====================

	@GetMapping("/admin")
	public String listUsers(Model model) {
		model.addAttribute("users", userService.getAllUsers());
		return "admin-list";
	}

	@GetMapping("/admin/add")
	public String showAddForm(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("allRoles", roleDao.getAllRoles());
		return "user-form";
	}

	@PostMapping("/admin/add")
	public String addUser(@ModelAttribute("user") @Valid User user,
	                      BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "user-form";
		}
		userService.addUser(user);
		return "redirect:/admin";
	}

	@GetMapping("/admin/edit")
	public String showEditForm(@RequestParam("id") Long id, Model model) {
		model.addAttribute("user", userService.getUserById(id));
		model.addAttribute("allRoles", roleDao.getAllRoles());
		return "user-form";
	}

	@PostMapping("/admin/update")
	public String updateUser(@ModelAttribute("user") @Valid User user,
	                         BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "user-form";
		}
		userService.updateUser(user);
		return "redirect:/admin";
	}

	@PostMapping("/admin/delete")
	public String deleteUser(@RequestParam("id") Long id) {
		userService.removeUser(id);
		return "redirect:/admin";
	}

	// ===================== USER HOME PAGE =====================

	@GetMapping("/user")
	public String userHome(@AuthenticationPrincipal User currentUser, Model model) {
		model.addAttribute("user", currentUser);
		return "user-profile";
	}
}
