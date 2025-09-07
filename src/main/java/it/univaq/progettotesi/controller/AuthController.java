package it.univaq.progettotesi.controller;

import it.univaq.progettotesi.entity.User;
import it.univaq.progettotesi.forms.BuildingForm;
import it.univaq.progettotesi.forms.RegisterForm;
import it.univaq.progettotesi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
        model.addAttribute("registerForm", new RegisterForm("", "","", ""));
        return "register";
    }

    @PostMapping("/register")
    public String registerForm(Model model, @Valid RegisterForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formError", bindingResult.getFieldError().getDefaultMessage());
            return "register";
        }
        if(service.existsByEmail(form.email())){
            model.addAttribute("emailError", true);
            return "register";
        }
        User u = service.create(form.name(), form.surname(), form.email(), form.password());
        model.addAttribute("email", form.email());
        return "redirect:/login?register=true";
    }

    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("user", user);
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        var u = service.findByEmail(user.getUsername()).orElse(null);
        model.addAttribute("user", u);
        return "home";
    }
}
