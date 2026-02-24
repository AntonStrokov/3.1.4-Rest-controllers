package ru.kata.spring.boot_security.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final RoleDao roleDao;

	@GetMapping("/")
	public String rootRedirect() {
		return "redirect:/login";
	}

	@GetMapping("/admin")
	public String listUsers(Model model, @AuthenticationPrincipal User currentUser) {
		log.info("Admin accessing user list: admin={}, roles={}",
				currentUser.getEmail(),
				currentUser.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
		model.addAttribute("users", userService.getAllUsers());
		return "admin-list";
	}

	@GetMapping("/admin/add")
	public String showAddForm(Model model) {
		log.info("Opening add user form");
		model.addAttribute("user", new User());
		model.addAttribute("allRoles", roleDao.getAllRoles());
		return "user-form";
	}

	@PostMapping("/admin/add")
	public String addUser(@ModelAttribute("user") @Valid User user,
	                      BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			log.warn("Validation errors when adding user: {}", bindingResult.getAllErrors());
			return "user-form";
		}
		userService.addUser(user);
		return "redirect:/admin";
	}

	@GetMapping("/admin/edit")
	public String showEditForm(@RequestParam("id") Long id, Model model) {
		log.info("Opening edit user form: userId={}", id);
		model.addAttribute("user", userService.getUserById(id));
		model.addAttribute("allRoles", roleDao.getAllRoles());
		return "user-form";
	}

	@PostMapping("/admin/update")
	public String updateUser(@ModelAttribute("user") @Valid User user,
	                         BindingResult bindingResult,
	                         @RequestParam(value = "roles", required = false) Long[] roleIds) {
		log.info("Updating user: id={}, email={}, roleIds={}", user.getId(), user.getEmail(), roleIds);

		if (bindingResult.hasErrors()) {
			log.warn("Validation errors when updating user id={}: {}", user.getId(), bindingResult.getAllErrors());
			return "user-form";
		}

		if (roleIds != null && roleIds.length > 0) {
			log.debug("Processing {} roles for user id={}", roleIds.length, user.getId());
			user.setRoles(
					java.util.Arrays.stream(roleIds)
							.map(roleId -> {
								var role = roleDao.getRoleById(roleId);
								log.debug("Role mapping: roleId={}, found={}", roleId, role.isPresent());
								return role;
							})
							.filter(java.util.Optional::isPresent)
							.map(java.util.Optional::get)
							.collect(Collectors.toSet())
			);
		}

		userService.updateUser(user);
		log.info("User updated successfully: id={}", user.getId());
		return "redirect:/admin";
	}

	@PostMapping("/admin/delete")
	public String deleteUser(@RequestParam("id") Long id) {
		log.info("Deleting user: id={}", id);
		userService.removeUser(id);
		return "redirect:/admin";
	}

	@GetMapping("/user")
	public String userHome(@AuthenticationPrincipal User currentUser, Model model) {
		log.info("User accessing home: email={}, roles={}",
				currentUser.getEmail(),
				currentUser.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
		model.addAttribute("user", currentUser);
		return "user-profile";
	}
}