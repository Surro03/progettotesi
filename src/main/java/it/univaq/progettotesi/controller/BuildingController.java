package it.univaq.progettotesi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.univaq.progettotesi.dto.BuildingDTO;
import it.univaq.progettotesi.dto.BuildingMapDTO;
import it.univaq.progettotesi.entity.*;
import it.univaq.progettotesi.forms.AssetForm;
import it.univaq.progettotesi.forms.BuildingForm;
import it.univaq.progettotesi.forms.RegisterForm;
import it.univaq.progettotesi.service.AssetService;
import it.univaq.progettotesi.service.BuildingConfigService;
import it.univaq.progettotesi.service.BuildingService;
import it.univaq.progettotesi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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
        model.addAttribute("user", u);
        model.addAttribute("page", page);                 // l'oggetto Page<Building>
        model.addAttribute("buildings", page.getContent()); // solo la lista per il <tbody>

        return "buildings/list"; // templates/buildings/list.html
    }

    @GetMapping("/new")
    public String buildingForm(Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        var u = userService.findAdminByEmail(user.getUsername()).orElseThrow();
        model.addAttribute("user", u);
        model.addAttribute("buildingForm", new BuildingForm("", "","", null,  null, null, null, null, null));
        model.addAttribute("edit", false);
        return "buildings/form";
    }

    @PostMapping("/new")
    public String newBuilding(@Valid BuildingForm form, BindingResult bindingResult, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user, Model model) {
        var u = userService.findAdminByEmail(user.getUsername()).orElseThrow();
        if (bindingResult.hasErrors()) {
            model.addAttribute("formError", bindingResult.getFieldError().getDefaultMessage());
            model.addAttribute("edit", false);
            model.addAttribute("user", u);
            return "buildings/form";
        }
        model.addAttribute("user", u);
        Building building = buildingService.create(u, form.name(), form.address(), form.energeticClass(), form.apartments(), form.yearOfConstruction(), form.numberOfFloors(), form.surface(), form.latitude(), form.longitude());
        return "redirect:/buildings/" + building.getId();
    }

    @GetMapping("/{buildingId}")
    public String buildingDetails(@PathVariable Long buildingId,
                                  Model model,
                                  @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,

                                  @Qualifier("assets")
                                  @PageableDefault(size = 5, sort = "commProtocol", direction = Sort.Direction.ASC)
                                  Pageable assetsPageable,

                                  @Qualifier("clients")
                                  @PageableDefault(size = 5, sort = "name", direction = Sort.Direction.DESC)
                                  Pageable clientsPageable
    ) throws JsonProcessingException{

        var u = userService.findAdminByEmail(user.getUsername()).orElseThrow();

        Building building = buildingService.findById(buildingId).orElseThrow();

        if (!u.getId().equals(building.getAdmin().getId())) {
            model.addAttribute("permissionError", "Non sei il proprietario di questo edificio");
            return "redirect:/buildings/";
        }

        Page<Asset> pageAssets = assetService.findByBuildingId(buildingId, assetsPageable);
        Page<Client> pageClients = userService.findByBuildingId(buildingId, clientsPageable);

        model.addAttribute("building", building);
        model.addAttribute("assetsPage", pageAssets);
        model.addAttribute("assets", pageAssets.getContent());
        model.addAttribute("clientsPage", pageClients);
        model.addAttribute("clients", pageClients.getContent());
        model.addAttribute("user", u);

        // 🔑 aggiungi la parte per la mappa
        BuildingMapDTO dto = new BuildingMapDTO(building.getId(), building.getName(), building.getLatitude(), building.getLongitude());
        ObjectMapper mapper = new ObjectMapper();
        String buildingsJson = mapper.writeValueAsString(List.of(dto));
        model.addAttribute("buildingsJson", buildingsJson);

        return "buildings/details";

        /*var u = userService.findAdminByEmail(user.getUsername()).orElseThrow();

        if (buildingService.findById(buildingId).isEmpty()) {
            model.addAttribute("searchError", "L'edifico con id: " + buildingId + " non esiste");
            return "redirect:/buildings/";
        }

        Building building = buildingService.findById(buildingId).orElseThrow();

        if (!u.getId().equals(building.getAdmin().getId())) {
            model.addAttribute("permissionError", "Non sei il proprietario di questo edificio");
            return "redirect:/buildings/";
        }

        Page<Asset> pageAssets = assetService.findByBuildingId(buildingId, assetsPageable);
        Page<Client> pageClients = userService.findByBuildingId(buildingId, clientsPageable);

        model.addAttribute("building", building);
        model.addAttribute("assetsPage", pageAssets);
        model.addAttribute("assets", pageAssets.getContent());


        model.addAttribute("clientsPage", pageClients);
        model.addAttribute("clients", pageClients.getContent());

        return "buildings/details"; */
    }


    @GetMapping("/{buildingId}/config")
    public String show(@PathVariable long buildingId, Model model) {
        model.addAttribute("json", buildingConfigService.getJson(buildingId).toPrettyString());
        return "buildings/config"; // tua view
    }

    @GetMapping("/{buildingId}/edit")
    public String editBuilding(@PathVariable Long buildingId, Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        var u = userService.findAdminByEmail(user.getUsername()).orElseThrow();
        Building building = buildingService.findById(buildingId).orElseThrow();
        model.addAttribute("buildingId", building.getId());
        model.addAttribute("buildingForm", new BuildingForm(building.getName(),building.getAddress(), building.getEnergeticClass(), building.getApartments(), building.getYearOfConstruction(), building.getNumbersOfFloors(), building.getSurface(), building.getLatitude(), building.getLongitude()));
        model.addAttribute("edit", true);
        model.addAttribute("user", u);
        return "buildings/form";
    }

    @PostMapping("/{buildingId}/edit")
    public String editBuilding(@PathVariable Long buildingId, BuildingForm buildingForm, Model model, BindingResult bindingResult,  @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        var u = userService.findAdminByEmail(user.getUsername()).orElseThrow();
        if (bindingResult.hasErrors()) {
            model.addAttribute("formError", bindingResult.getFieldError().getDefaultMessage());
            model.addAttribute("edit", true);
            model.addAttribute("buildingId", buildingId);
            model.addAttribute("user", u);
            return "buildings/form";
        }
        Building building = buildingService.update(buildingId,  buildingForm.name(), buildingForm.address());
        model.addAttribute("updated", true);
        model.addAttribute("user", u);
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
        var u = userService.findAdminByEmail(user.getUsername()).orElseThrow();
        model.addAttribute("assetForm", new AssetForm("","", AssetType.INVERTER,"", CommProtocol.MODBUS,""));
        model.addAttribute("buildingId", buildingId );
        model.addAttribute("user", u);
        return "assets/form";
    }

    @PostMapping("/{buildingId}/assets/new")
    public String addAsset(@PathVariable Long buildingId, Model model, @Valid AssetForm assetForm, BindingResult bindingResult, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        var u =  userService.findAdminByEmail(user.getUsername()).orElseThrow();
        if(bindingResult.hasErrors()){
            model.addAttribute("formError", bindingResult.getFieldError().getDefaultMessage());
            model.addAttribute("user", u);
            return "assets/form";
        }

        var b = buildingService.findById(buildingId).orElseThrow();
        var client = userService.findClientByEmail(assetForm.clientEmail());
        if (client.isEmpty()) {
            model.addAttribute("formError", "Il cliente con email " + assetForm.clientEmail() + " non esiste");
            return "assets/form"; // torno al form con errore
        }
        Client c = client.get();
        Asset asset = assetService.create( u, b, assetForm.name(), assetForm.brand(), assetForm.type(), assetForm.model(), assetForm.commProtocol(), c);
        b.addAsset(asset);
        return "redirect:/buildings/"  + buildingId;
    }

    @GetMapping("/{buildingId}/assets/{assetId}/edit")
    public String assetFormEdit(@PathVariable Long buildingId, Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user, @PathVariable Long assetId) {
        var u =  userService.findAdminByEmail(user.getUsername()).orElseThrow();
        Asset asset = assetService.findById(assetId).orElseThrow();
        model.addAttribute("assetForm", new AssetForm(asset.getName(),asset.getBrand(), asset.getType(), asset.getModel(), asset.getCommProtocol(),asset.getClient().getEmail()));
        model.addAttribute("buildingId", buildingId );
        model.addAttribute("assetId", assetId );
        model.addAttribute("edit", true);
        model.addAttribute("user", u);
        return "assets/form";
    }

    @PostMapping("/{buildingId}/assets/{assetId}/edit")
    public String editAsset(@PathVariable Long buildingId, @PathVariable Long assetId, @Valid AssetForm assetForm, BindingResult bindingResult, Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        var u =  userService.findAdminByEmail(user.getUsername()).orElseThrow();
        if(bindingResult.hasErrors()){
            model.addAttribute("formError", bindingResult.getFieldError().getDefaultMessage());
            model.addAttribute("buildingId", buildingId );
            model.addAttribute("assetId", assetId );
            model.addAttribute("no", "qualcosa da errore");
            model.addAttribute("edit", true);
            model.addAttribute("user", u);
            return "assets/form";
        }
        Building b = buildingService.findById(buildingId).orElseThrow();
        var client = userService.findClientByEmail(assetForm.clientEmail());
        if (client.isEmpty()) {
            model.addAttribute("formError", "Il cliente con email " + assetForm.clientEmail() + " non esiste");
            return "assets/form"; // torno al form con errore
        }
        Client c = client.get();
        assetService.update(assetId, b, assetForm.name(), assetForm.brand(), assetForm.type(), assetForm.model(), assetForm.commProtocol(), c);
        model.addAttribute("updated", true);
        return "redirect:/buildings/"  + buildingId;
    }

    @PostMapping("/{buildingId}/assets/{assetId}/delete")
    public String deleteAsset(@PathVariable Long buildingId, @PathVariable Long assetId, Model model) {
        assetService.delete(assetId);
        model.addAttribute("deleted", true);
        return "redirect:/buildings/"  + buildingId;
    }

    @GetMapping("/{buildingId}/clients/add")
    public String clientForm(@PathVariable Long buildingId, Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        var u = userService.findAdminByEmail(user.getUsername()).orElseThrow();
        model.addAttribute("objectForm", new RegisterForm("","","","", null, ""));
        model.addAttribute("buildingId", buildingId );
        model.addAttribute("edit", false);
        model.addAttribute("client", true);
        model.addAttribute("user", u);
        return "user/clientForm";
    }

    @PostMapping("/{buildingId}/clients/add")
    public String addClient(@PathVariable Long buildingId, Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user, @Valid RegisterForm registerForm, BindingResult result) {
        var u = userService.findAdminByEmail(user.getUsername()).orElseThrow();
        if(result.hasErrors()){
            model.addAttribute("formError", result.getFieldError().getDefaultMessage());
            model.addAttribute("buildingId", buildingId );
            model.addAttribute("edit", false);
            model.addAttribute("client", true);
            model.addAttribute("user", u);
            return "user/clientForm";
        }
        Building building = buildingService.findById(buildingId).orElseThrow();
        Client client = userService.createClient(registerForm.name(), registerForm.surname(), registerForm.email(), registerForm.password(), building, registerForm.birthDate(), registerForm.cellphone());
        building.addClient(client);
        buildingService.save(building);
        model.addAttribute("created", true);
        model.addAttribute("user", u);
        return "redirect:/buildings/"  + buildingId;

    }

    @GetMapping("/{buildingId}/clients/{clientId}/edit")
    public String clientFormEdit(@PathVariable Long buildingId, Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user, @PathVariable Long clientId) {
        var u = userService.findAdminByEmail(user.getUsername()).orElseThrow();
        Client client = userService.findClientById(clientId).orElseThrow();
        model.addAttribute("objectForm", new RegisterForm(client.getName(), client.getSurname(), client.getEmail(), "", client.getBirthDate(), client.getCellphone()));
        model.addAttribute("buildingId", buildingId );
        model.addAttribute("clientId", clientId );
        model.addAttribute("client", true);
        model.addAttribute("edit", true);
        model.addAttribute("user", u);
        return "user/clientForm";
    }

    @PostMapping("/{buildingId}/clients/{clientId}/edit")
    public String editClient(@PathVariable Long clientId, @PathVariable Long buildingId, @Valid RegisterForm registerForm, BindingResult bindingResult, Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user, RedirectAttributes ra) {
        var u = userService.findAdminByEmail(user.getUsername()).orElseThrow();
        if(bindingResult.hasErrors()){
            model.addAttribute("formError", bindingResult.getFieldError().getDefaultMessage());
            model.addAttribute("buildingId", buildingId );
            model.addAttribute("clientId", clientId );
            model.addAttribute("edit", true);
            model.addAttribute("client", true);
            model.addAttribute("user", u);
            return "user/clientForm";
        }
        Building building = buildingService.findById(buildingId).orElseThrow();
        userService.updateClient(clientId, registerForm.name(), registerForm.surname(), registerForm.email(), registerForm.password(), building, registerForm.birthDate(), registerForm.cellphone());
        ra.addAttribute("updated", true);
        model.addAttribute("user", u);
        return "redirect:/buildings/"  + buildingId;
    }

    @PostMapping("/{buildingId}/clients/{clientId}/delete")
    public String deleteClient(@PathVariable Long buildingId, @PathVariable Long clientId, Model model) {
        userService.deleteClient(clientId);
        model.addAttribute("deleted", true);
        return "redirect:/buildings/"  + buildingId;
    }

    @GetMapping("/map")
    public String showMap(Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) throws Exception {
        var u = userService.findAdminByEmail(user.getUsername()).orElseThrow();

        List<BuildingMapDTO> buildingsDto = buildingService.findAll()
                .stream()
                .map(b -> new BuildingMapDTO(b.getId(), b.getName(), b.getLatitude(), b.getLongitude()))
                .toList();

        // Serializza in JSON
        ObjectMapper mapper = new ObjectMapper();
        String buildingsJson = mapper.writeValueAsString(buildingsDto);

        model.addAttribute("buildingsJson", buildingsJson);
        model.addAttribute("user", u);

        return "buildings/map";
    }








}
