package it.univaq.progettotesi.api;

import it.univaq.progettotesi.dto.LoginRequestDTO;
import it.univaq.progettotesi.security.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/app/api/auth")
public class ApiAuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public ApiAuthController(AuthenticationManager am, JwtService js) {
        this.authManager = am;
        this.jwtService = js;
    }



    @PostMapping("/login")
    public Map<String,Object> login(@RequestBody LoginRequestDTO req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        var user = (UserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(user.getUsername());
        return Map.of("access_token", token, "token_type", "Bearer");
    }

}
