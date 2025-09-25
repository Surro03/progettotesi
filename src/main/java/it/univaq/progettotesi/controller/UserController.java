package it.univaq.progettotesi.controller;

import it.univaq.progettotesi.config.MyUserDetailsService;
import it.univaq.progettotesi.forms.RegisterForm;
import it.univaq.progettotesi.forms.UpdateForm;
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
        model.addAttribute("client", false);
        model.addAttribute("user", u);
        return "user/details";
    }

    @GetMapping("/edit")
    public String userForm(Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        var u = userService.findAdminByEmail(user.getUsername()).orElse(null);
        model.addAttribute("objectForm", new UpdateForm(u.getName(), u.getSurname() ,u.getEmail(), "", "", u.getBirthDate(), u.getCellphone()));
        model.addAttribute("client",false);
        model.addAttribute("edit", false);
        return "user/form";
    }

    @PostMapping("/edit")
    public String userEdit(
            @Valid @ModelAttribute("objectForm") UpdateForm form,
            BindingResult bindingResult,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("client", false);
            model.addAttribute("edit", true);
            model.addAttribute("formError", bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "Errore di validazione");
            return "user/form";
        }

        var u = userService.findAdminByEmail(principal.getUsername()).orElseThrow();
        String oldUsername = u.getUsername();

        String oldPassword = form.oldPassword();
        String newPassword = form.newPassword();

        //Cambio password richiesto?
        boolean wantsPwdChange = org.springframework.util.StringUtils.hasText(oldPassword)
                || org.springframework.util.StringUtils.hasText(newPassword);

        if (wantsPwdChange) {

            if (!org.springframework.util.StringUtils.hasText(oldPassword)) {
                bindingResult.rejectValue("oldPassword", "password.required", "Inserisci la vecchia password.");
            }
            if (!org.springframework.util.StringUtils.hasText(newPassword)) {
                bindingResult.rejectValue("newPassword", "password.required", "Inserisci la nuova password.");
            } else if (newPassword.length() < 8) {
                bindingResult.rejectValue("newPassword", "password.length", "La nuova password deve avere almeno 8 caratteri.");
            }

            // Verifica vecchia password
            if (org.springframework.util.StringUtils.hasText(oldPassword) &&
                    !passwordEncoder.matches(oldPassword, u.getPassword())) {
                bindingResult.rejectValue("oldPassword", "password.mismatch", "La vecchia password non è corretta.");
            }

            if (bindingResult.hasErrors()) {
                model.addAttribute("client", false);
                model.addAttribute("edit", true);
                model.addAttribute("formError", bindingResult.getAllErrors().get(0).getDefaultMessage());

                return "user/form";
            }


            u.setPassword(passwordEncoder.encode(newPassword));
        }


        u.setName(form.name());
        u.setSurname(form.surname());
        u.setUsername(form.name() + form.surname() + u.getId());

        //Cambio email con controllo duplicati
        if (!form.email().equalsIgnoreCase(u.getEmail())) {
            if (userService.existsAdminByEmail(form.email())) {
                bindingResult.rejectValue("email", "email.taken", "Email già in uso");
                model.addAttribute("client", false);
                model.addAttribute("edit", true);
                model.addAttribute("formError", bindingResult.getAllErrors().get(0).getDefaultMessage());
                return "user/form";
            }
            u.setEmail(form.email());
        }

        u.setBirthDate(form.birthDate());
        u.setCellphone(form.cellphone());

        userService.updateAdmin(u, oldUsername, newPassword);

        //Aggiorna il SecurityContext
        var context = SecurityContextHolder.getContext();
        var currentAuth = context.getAuthentication();

        var newPrincipal = myUserDetailsService.loadUserByUsername(u.getEmail());
        var newAuth = new UsernamePasswordAuthenticationToken(
                newPrincipal,
                (currentAuth != null ? currentAuth.getCredentials() : null),
                newPrincipal.getAuthorities()
        );
        if (currentAuth != null) newAuth.setDetails(currentAuth.getDetails());
        context.setAuthentication(newAuth);

        return "redirect:/user/details";
    }


}

