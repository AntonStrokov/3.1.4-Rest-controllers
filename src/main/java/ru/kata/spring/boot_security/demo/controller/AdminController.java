package ru.kata.spring.boot_security.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.validation.OnCreate;
import ru.kata.spring.boot_security.demo.validation.OnUpdate;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	private final UserService userService;
	private final RoleService roleService;

	@GetMapping
	public String listUsers(Model model, @AuthenticationPrincipal User currentUser) {
		model.addAttribute("users", userService.getAllUsers());
		model.addAttribute("currentUser", currentUser);
		model.addAttribute("allRoles", roleService.getAllRoles());
		// Добавляем пустой объект для формы "New User"
		if (!model.containsAttribute("newUser")) {
			model.addAttribute("newUser", new User());
		}
		return "admin-list";
	}

	@PostMapping("/add")
	public String addUser(@Validated(OnCreate.class) @ModelAttribute("newUser") User user,
	                      BindingResult bindingResult,
	                      @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
	                      Model model,
	                      @AuthenticationPrincipal User currentUser) {

		// Сеттим роли сразу, чтобы если будет ошибка, пользователь видел, что он выбрал
		if (roleIds != null) {
			user.setRoles(new HashSet<>(roleService.getRolesByIds(roleIds)));
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute("users", userService.getAllUsers());
			model.addAttribute("allRoles", roleService.getAllRoles());
			model.addAttribute("currentUser", currentUser);
			model.addAttribute("activeTab", "newuser");

			// ВНИМАНИЕ: Мы НЕ добавляем здесь model.addAttribute("newUser", new User());
			// Потому что Spring уже положил в модель 'user' с ошибками под именем 'newUser'

			return "admin-list";
		}

		userService.addUser(user, roleIds);
		return "redirect:/admin";
	}
	@PostMapping("/update")
	public String updateUser(@Validated(OnUpdate.class) @ModelAttribute("user") User user,
	                         BindingResult bindingResult,
	                         @RequestParam(value = "roleIds", required = false) List<Long> roleIds,
	                         Model model,
	                         @AuthenticationPrincipal User currentUser) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("users", userService.getAllUsers());
			model.addAttribute("currentUser", currentUser);
			model.addAttribute("allRoles", roleService.getAllRoles());
			model.addAttribute("errorUser", user); // Чтобы JS открыл нужную модалку
			return "admin-list";
		}
		userService.updateUser(user, roleIds);
		return "redirect:/admin";
	}

	@PostMapping("/delete")
	public String deleteUser(@RequestParam("id") Long id) {
		userService.removeUser(id);
		return "redirect:/admin";
	}
}
