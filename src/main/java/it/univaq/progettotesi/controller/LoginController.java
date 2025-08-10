package it.univaq.progettotesi.controller;

import it.univaq.progettotesi.config.MyUserDetailsService;
import it.univaq.progettotesi.entity.User;
import it.univaq.progettotesi.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class LoginController {

    private final UserRepository repository;

    public LoginController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        var u = repository.findByEmail(user.getUsername()).orElse(null);
        model.addAttribute("user", u);
        return "home"; // risolve templates/home.html
    }
}

