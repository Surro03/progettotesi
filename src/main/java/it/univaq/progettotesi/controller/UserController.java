package it.univaq.progettotesi.controller;

import it.univaq.progettotesi.config.MyUserDetailsService;
import it.univaq.progettotesi.forms.RegisterForm;
import it.univaq.progettotesi.service.BuildingService;
import it.univaq.progettotesi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user/")
public class UserController {

    private final UserService userService;
    private final BuildingService buildingService;
    private final PasswordEncoder passwordEncoder;
    private final MyUserDetailsService myUserDetailsService;

    public UserController(UserService userService,  BuildingService buildingService, PasswordEncoder passwordEncoder, MyUserDetailsService myUserDetailsService) {
        this.userService = userService;
        this.buildingService = buildingService;
        this.passwordEncoder = passwordEncoder;
        this.myUserDetailsService = myUserDetailsService;
    }

    @GetMapping("/details")
    public String userDetails(Model model,  @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        var u = userService.findAdminByEmail(user.getUsername()).orElse(null);
        model.addAttribute("user", u);
        return "user/details";
    }

    @GetMapping("/edit")
    public String userForm(Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        var u = userService.findAdminByEmail(user.getUsername()).orElse(null);
        model.addAttribute("registerForm", new RegisterForm(u.getName(), u.getSurname() ,u.getEmail(), ""));
        return "user/form";
    }

    @PostMapping("/edit")
    public String userEdit(@Valid @ModelAttribute("form") RegisterForm form,
                           BindingResult binding,
                           @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
                           Model model) {

        var u = userService.findAdminByEmail(principal.getUsername()).orElseThrow();

        if (binding.hasErrors()) {
            return "user/form";
        }

        // email cambiata?
        boolean emailChanged = !form.email().equalsIgnoreCase(u.getEmail());
        u.setName(form.name());
        u.setSurname(form.surname());

        if (form.password() != null && !form.password().isBlank()) {
            u.setPassword(passwordEncoder.encode(form.password()));
        }
        if (emailChanged) {
            if (userService.existsAdminByEmail(form.email())) {
                binding.rejectValue("email", "email.taken", "Email già in uso");
                return "user/form";
            }
            u.setEmail(form.email());
        }

        userService.saveAdmin(u);
        var context = SecurityContextHolder.getContext();
        var currentAuth = context.getAuthentication();

        var newPrincipal = myUserDetailsService.loadUserByUsername(u.getEmail());
        var newAuth = new UsernamePasswordAuthenticationToken(
                newPrincipal,
                (currentAuth != null ? currentAuth.getCredentials() : null), // può essere null, va bene
                newPrincipal.getAuthorities()
        );
        if (currentAuth != null) newAuth.setDetails(currentAuth.getDetails());
        context.setAuthentication(newAuth);

        model.addAttribute("edit", true);
        return "redirect:/user/details";
    }

}

