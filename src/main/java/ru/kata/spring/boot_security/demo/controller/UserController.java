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

@Controller
@RequestMapping("/user")
public class UserController {

	@GetMapping
	public String userHome(@AuthenticationPrincipal User currentUser, Model model) {
		model.addAttribute("currentUser", currentUser);
		return "user-profile";
	}
}
