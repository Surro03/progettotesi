package it.univaq.progettotesi.controller;

import it.univaq.progettotesi.entity.Asset;
import it.univaq.progettotesi.entity.Building;
import it.univaq.progettotesi.entity.User;
import it.univaq.progettotesi.forms.AssetForm;
import it.univaq.progettotesi.forms.BuildingForm;
import it.univaq.progettotesi.service.AssetService;
import it.univaq.progettotesi.service.BuildingService;
import it.univaq.progettotesi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/buildings")
public class BuildingController {

    private final BuildingService buildingService;
    private final UserService userService;
    private final AssetService assetService;

    public BuildingController(BuildingService buildingService, UserService userService, AssetService assetService) {
        this.buildingService = buildingService;
        this.userService = userService;
        this.assetService = assetService;
    }

    @GetMapping
    public String list(Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        var u = userService.findByEmail(user.getUsername()).orElseThrow();
        var b = buildingService.findByUserId(u.getId());
        model.addAttribute("buildings", b);
        return "buildings/list"; // templates/buildings/list.html
    }

    @GetMapping("/new")
    public String buildingForm(Model model){
        model.addAttribute("buildingForm", new BuildingForm("", ""));
        return "buildings/new";
    }

    @PostMapping("/new")
    public String newBuilding(@Valid BuildingForm form, BindingResult bindingResult, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formError", bindingResult.getFieldError().getDefaultMessage());
            return "buildings/new";
        }
        var u = userService.findByEmail(user.getUsername()).orElseThrow();
        Building building = buildingService.create(u, form.name(), form.address());
        return "redirect:/buildings/" + building.getId();
    }

    @GetMapping("/{buildingId}")
    public String buildingDetails(@PathVariable Long buildingId, Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        var u = userService.findByEmail(user.getUsername()).orElseThrow();
        if(buildingService.findById(buildingId).isEmpty()) {
            model.addAttribute("searchError", "L'edifico con id: " + buildingId + " non esiste");
            return "redirect:/buildings/";
        }
        Building building = buildingService.findById(buildingId).orElseThrow();
        if(!u.getId().equals(building.getUser().getId())){
            model.addAttribute("permissionError", "Non sei il proprietario di questo edificio");
            return "redirect:/buildings/";
        }
        List<Asset> assets = assetService.findByBuildingId(buildingId);
        model.addAttribute("building", building);
        model.addAttribute("assets", assets);
        return "buildings/details";
    }

    @PostMapping("/{buildingId}")
    public String editBuilding(@PathVariable Long buildingId, BuildingForm buildingForm, Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formError", bindingResult.getFieldError().getDefaultMessage());
            return "buildings/edit";
        }
        Building building = buildingService.update(buildingId,  buildingForm.name(), buildingForm.address());
        model.addAttribute("updated", true);
        return "redirect:/buildings/"  + building.getId();
    }

    @GetMapping("/{buildingId}/assets")
    public String buidingAssets(@PathVariable Long buildingId, Model model){
        Building building = buildingService.findById(buildingId).orElseThrow();
        List<Asset> assets = assetService.findByBuildingId(buildingId);
        model.addAttribute("building", building);
        model.addAttribute("assets", assets);
        return "buildings/assets";
    }

    @GetMapping("/{buildingId}/assets/new")
    public String addAsset(Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        model.addAttribute("assetForm", new AssetForm("", ""));
        return "assets/new";
    }

    @PostMapping("/{buildingId}/delete")
    public String deleteBuilding(@PathVariable Long buildingId, Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        var u = userService.findByEmail(user.getUsername()).orElseThrow();
        buildingService.delete(buildingId);
        model.addAttribute("deleted", true);
        return "redirect:/buildings";
    }

    @GetMapping("/{buildingId}/edit")
    public String editBuilding(@PathVariable Long buildingId, Model model) {
        Building building = buildingService.findById(buildingId).orElseThrow();
        model.addAttribute("building", building);
        model.addAttribute("buildingForm", new BuildingForm("",""));
        return "buildings/edit";
    }







}
