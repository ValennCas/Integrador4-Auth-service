package Auth.controller;

import Auth.dto.UsuarioDTO;

import Auth.model.Usuario;
import Auth.repository.UsuarioRepository;
import Auth.security.JWT_Utilidades;
import Auth.services.DefaultUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;


@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    JWT_Utilidades jwt_utilidad;

    @Autowired
    DefaultUserService servicio_usuario;

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @PostMapping("/validateToken")
    public ResponseEntity<?> validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Encabezado de autorización inválido");
        }

        String token = authorizationHeader.substring(7);
        String username = jwt_utilidad.extractUsername(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        boolean isValid = jwt_utilidad.validateToken(token, userDetails);

        if (isValid) {
            String role = userDetails.getAuthorities().stream()
                    .findFirst() // Suponiendo que solo hay un rol
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_USER"); // Valor por defecto si no se encuentra rol

            // Retornar el rol y mensaje de token válido
            return ResponseEntity.ok(Collections.singletonMap("role", role));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }
    }



    @PostMapping("/registro")
    public ResponseEntity<Object> registro(@RequestBody UsuarioDTO usuario_dto) {
        Usuario usuario = this.servicio_usuario.save(usuario_dto);
        if (usuario.equals(null)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No fue posible guardar el usuario.");
        }

        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/registro2")
    public String registro2(@RequestBody UsuarioDTO usuario_dto) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuario_dto.getEmail(), usuario_dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return this.jwt_utilidad.generateToken(authentication);
    }



    @PostMapping("/login")
    public String login(@RequestBody UsuarioDTO usuario_dto) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuario_dto.getEmail(), usuario_dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return this.jwt_utilidad.generateToken(authentication);

    }


    @PostMapping("/genToken")
    public String get_token(@RequestBody UsuarioDTO usuario_dto) throws Exception {
        System.out.println(usuario_dto);
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuario_dto.getEmail(), usuario_dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return this.jwt_utilidad.generateToken(authentication);
    }

    @GetMapping("/welcomeAdmin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String welcome() {
        return "WelcomeAdmin";
    }

    @GetMapping("/welcomeUser")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String welcomeUser() {
        return "WelcomeUSER";
    }



}
