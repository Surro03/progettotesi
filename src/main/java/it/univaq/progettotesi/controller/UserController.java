package it.univaq.progettotesi.controller;

import it.univaq.progettotesi.entity.Building;
import it.univaq.progettotesi.entity.User;
import it.univaq.progettotesi.service.BuildingService;
import it.univaq.progettotesi.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.List;

@Controller
@RequestMapping("/user/")
public class UserController {

    private final UserService service;
    private final BuildingService buildingService;

    public UserController(UserService service,  BuildingService buildingService) {
        this.service = service;
        this.buildingService = buildingService;
    }

    @GetMapping("/details")
    public String userDetails(Model model,  @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        var u = service.findByEmail(user.getUsername()).orElse(null);
        model.addAttribute("user", u);
        return "user/details";
    }

    @GetMapping("/edit")
    public String userEdit(Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
       return "user/form";
    }


}

