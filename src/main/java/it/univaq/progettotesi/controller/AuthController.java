package it.univaq.progettotesi.controller;

import it.univaq.progettotesi.entity.User;
import it.univaq.progettotesi.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService service;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService service, PasswordEncoder passwordEncoder) {
        this.service = service;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerForm(Model model, @ModelAttribute("user") User user) {
        if(service.existsByEmail(user.getEmail())){
            model.addAttribute("emailError", true);
            return "register";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("CLIENT");
        service.save(user);
        return "redirect:/login?register=true";
    }

    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        var u = service.findByEmail(user.getUsername()).orElse(null);
        model.addAttribute("user", u);
        return "home"; // risolve templates/home.html
    }
}
