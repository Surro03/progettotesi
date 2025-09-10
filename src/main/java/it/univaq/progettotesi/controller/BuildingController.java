package it.univaq.progettotesi.controller;

import it.univaq.progettotesi.entity.*;
import it.univaq.progettotesi.forms.AssetForm;
import it.univaq.progettotesi.forms.BuildingForm;
import it.univaq.progettotesi.service.AssetService;
import it.univaq.progettotesi.service.BuildingConfigService;
import it.univaq.progettotesi.service.BuildingService;
import it.univaq.progettotesi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/buildings")
public class BuildingController {

    private final BuildingService buildingService;
    private final BuildingConfigService buildingConfigService;
    private final UserService userService;
    private final AssetService assetService;

    public BuildingController(BuildingService buildingService, UserService userService, AssetService assetService, BuildingConfigService buildingConfigService) {
        this.buildingService = buildingService;
        this.userService = userService;
        this.assetService = assetService;
        this.buildingConfigService = buildingConfigService;
    }

    @GetMapping
    public String list(Model model,
                       @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
                       @PageableDefault(size = 5, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        var u = userService.findAdminByEmail(user.getUsername()).orElseThrow();
        Page<Building> page = buildingService.findByAdminId(u.getId(), pageable);

        model.addAttribute("page", page);                 // l'oggetto Page<Building>
        model.addAttribute("buildings", page.getContent()); // solo la lista per il <tbody>
        return "buildings/list"; // templates/buildings/list.html
    }

    @GetMapping("/new")
    public String buildingForm(Model model){
        model.addAttribute("buildingForm", new BuildingForm("", ""));
        model.addAttribute("edit", false);
        return "buildings/form";
    }

    @PostMapping("/new")
    public String newBuilding(@Valid BuildingForm form, BindingResult bindingResult, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formError", bindingResult.getFieldError().getDefaultMessage());
            model.addAttribute("edit", false);
            return "buildings/form";
        }
        var u = userService.findAdminByEmail(user.getUsername()).orElseThrow();
        Building building = buildingService.create(u, form.name(), form.address());
        return "redirect:/buildings/" + building.getId();
    }

    @GetMapping("/{buildingId}")
    public String buildingDetails(@PathVariable Long buildingId, Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
                                  @PageableDefault(size = 5, sort = "commProtocol", direction = Sort.Direction.ASC) Pageable pageable) {
        var u = userService.findAdminByEmail(user.getUsername()).orElseThrow();
        if(buildingService.findById(buildingId).isEmpty()) {
            model.addAttribute("searchError", "L'edifico con id: " + buildingId + " non esiste");
            return "redirect:/buildings/";
        }
        Building building = buildingService.findById(buildingId).orElseThrow();
        if(!u.getId().equals(building.getAdmin().getId())){
            model.addAttribute("permissionError", "Non sei il proprietario di questo edificio");
            return "redirect:/buildings/";
        }
        Page<Asset> page = assetService.findByBuildingId(buildingId, pageable);
        model.addAttribute("building", building);
        model.addAttribute("page", page);
        model.addAttribute("assets", page.getContent());
        return "buildings/details";
    }

    @GetMapping("/{buildingId}/config")
    public String show(@PathVariable long buildingId, Model model) {
        model.addAttribute("json", buildingConfigService.getJson(buildingId).toPrettyString());
        return "buildings/config"; // tua view
    }

    @GetMapping("/{buildingId}/edit")
    public String editBuilding(@PathVariable Long buildingId, Model model) {
        Building building = buildingService.findById(buildingId).orElseThrow();
        model.addAttribute("buildingId", building.getId());
        model.addAttribute("buildingForm", new BuildingForm(building.getName(),building.getAddress()));
        model.addAttribute("edit", true);
        return "buildings/form";
    }

    @PostMapping("/{buildingId}/edit")
    public String editBuilding(@PathVariable Long buildingId, BuildingForm buildingForm, Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formError", bindingResult.getFieldError().getDefaultMessage());
            model.addAttribute("edit", true);
            model.addAttribute("buildingId", buildingId);
            return "buildings/form";
        }
        Building building = buildingService.update(buildingId,  buildingForm.name(), buildingForm.address());
        model.addAttribute("updated", true);
        return "redirect:/buildings/"  + building.getId();
    }

    @PostMapping("/{buildingId}/delete")
    public String deleteBuilding(@PathVariable Long buildingId, Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        var u = userService.findAdminByEmail(user.getUsername()).orElseThrow();
        buildingService.delete(buildingId);
        model.addAttribute("deleted", true);
        return "redirect:/buildings";
    }

    @GetMapping("/{buildingId}/assets") //per ora lascio in sospeso perché mostriamo gli asset già nella vista dettagliata dell'edificio
    public String buildingAssets(@PathVariable Long buildingId, Model model,
                                 @PageableDefault(size = 5, sort = "name", direction = Sort.Direction.ASC) Pageable pageable){
        Building building = buildingService.findById(buildingId).orElseThrow();
        Page<Asset> assets = assetService.findByBuildingId(buildingId, pageable);
        model.addAttribute("building", building);
        model.addAttribute("assets", assets);
        return "buildings/assets";
    }

    @GetMapping("/{buildingId}/assets/new")
    public String assetForm(@PathVariable Long buildingId, Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        model.addAttribute("assetForm", new AssetForm("","", AssetType.INVERTER,"", CommProtocol.MODBUS));
        model.addAttribute("buildingId", buildingId );
        return "assets/form";
    }

    @PostMapping("/{buildingId}/assets/new")
    public String addAsset(@PathVariable Long buildingId, Model model, @Valid AssetForm assetForm, BindingResult bindingResult, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        if(bindingResult.hasErrors()){
            model.addAttribute("formError", bindingResult.getFieldError().getDefaultMessage());
            return "assets/form";
        }
        var u =  userService.findAdminByEmail(user.getUsername()).orElseThrow();
        var b = buildingService.findById(buildingId).orElseThrow();
        assetService.create( u, b, assetForm.name(), assetForm.brand(), assetForm.type(), assetForm.model(), assetForm.commProtocol(), "");
        return "redirect:/buildings/"  + buildingId;
    }

    @GetMapping("/{buildingId}/assets/{assetId}/edit")
    public String assetFormEdit(@PathVariable Long buildingId, Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user, @PathVariable Long assetId) {
        Asset asset = assetService.findById(assetId).orElseThrow();
        model.addAttribute("assetForm", new AssetForm(asset.getName(),asset.getBrand(), asset.getType(), asset.getModel(), asset.getCommProtocol()));
        model.addAttribute("buildingId", buildingId );
        model.addAttribute("assetId", assetId );
        model.addAttribute("edit", true);
        return "assets/form";
    }

    @PostMapping("/{buildingId}/assets/{assetId}/edit")
    public String editAsset(@PathVariable Long buildingId, @PathVariable Long assetId, @Valid AssetForm assetForm, BindingResult bindingResult, Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        if(bindingResult.hasErrors()){
            model.addAttribute("formError", bindingResult.getFieldError().getDefaultMessage());
            model.addAttribute("buildingId", buildingId );
            model.addAttribute("assetId", assetId );
            model.addAttribute("edit", true);
            return "assets/form";
        }
        var b = buildingService.findById(buildingId).orElseThrow();
        assetService.update(assetId, b, assetForm.name(), assetForm.brand(), assetForm.type(), assetForm.model(), assetForm.commProtocol(), "");
        model.addAttribute("updated", true);
        return "redirect:/buildings/"  + buildingId;
    }

    @PostMapping("/{buildingId}/assets/{assetId}/delete")
    public String deleteAsset(@PathVariable Long buildingId, @PathVariable Long assetId, Model model) {
        assetService.delete(assetId);
        model.addAttribute("deleted", true);
        return "redirect:/buildings/"  + buildingId;
    }








}
