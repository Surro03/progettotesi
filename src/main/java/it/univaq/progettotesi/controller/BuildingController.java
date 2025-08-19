package it.univaq.progettotesi.controller;

import it.univaq.progettotesi.entity.User;
import it.univaq.progettotesi.repository.BuildingRepository;
import it.univaq.progettotesi.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/buildings")
public class BuildingController {

    private final BuildingRepository buildingRepository;
    private final UserRepository userRepository;

    public BuildingController(BuildingRepository buildingRepository, UserRepository userRepository) {
        this.buildingRepository = buildingRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String list(Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        var u = userRepository.findByEmail(user.getUsername()).orElseThrow();
        var b = buildingRepository.findByUser_Id(u.getId());
        model.addAttribute("buildings", b);
        return "buildings/list"; // templates/buildings/list.html
    }

    @GetMapping("/new")
    public String newBuilding(Model model) {
        return "buildings/new";
    }
}
