package com.crmtech360.crmtech360_backend.controller;

import com.crmtech360.crmtech360_backend.dto.ApiErrorResponseDTO;
import com.crmtech360.crmtech360_backend.dto.JwtResponseDTO;
import com.crmtech360.crmtech360_backend.dto.LoginRequestDTO;
import com.crmtech360.crmtech360_backend.dto.UsuarioCreateRequestDTO;
import com.crmtech360.crmtech360_backend.dto.UsuarioResponseDTO;
import com.crmtech360.crmtech360_backend.security.jwt.JwtUtil;
import com.crmtech360.crmtech360_backend.service.UsuarioService;
import com.crmtech360.crmtech360_backend.service.impl.UserDetailsServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Controlador para la autenticación y registro de usuarios.
 * Permite iniciar sesión y registrar nuevos usuarios en el sistema.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Operaciones de login y registro de usuarios.")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UsuarioService usuarioService,
                          UserDetailsServiceImpl userDetailsServiceImpl) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
    }

    /**
     * Permite a un usuario autenticarse y obtener un token JWT si las credenciales son válidas.
     */
    @Operation(summary = "Autenticar usuario y obtener token JWT",
            description = "Recibe usuario y contraseña, y retorna un token JWT si la autenticación es exitosa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas o usuario deshabilitado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            log.info("Intentando autenticar al usuario: {}", loginRequestDTO.getNombreUsuario());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getNombreUsuario(),
                            loginRequestDTO.getContrasena()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            UsuarioResponseDTO usuarioInfo = usuarioService.findUsuarioByNombreUsuario(userDetails.getUsername());

            if (!usuarioInfo.isHabilitado()) {
                log.warn("Intento de login fallido: El usuario {} no está habilitado.", userDetails.getUsername());
                throw new DisabledException("Su usuario no está habilitado. Por favor, contacte a soporte.");
            }

            String jwt = jwtUtil.generateTokenFromUserDetails(userDetails);

            log.info("Usuario {} autenticado exitosamente. Token generado.", userDetails.getUsername());
            return ResponseEntity.ok(new JwtResponseDTO(
                    jwt,
                    usuarioInfo.getIdUsuario(),
                    userDetails.getUsername(),
                    usuarioInfo.getRolUsuario(),
                    usuarioInfo.isHabilitado()
            ));

        } catch (BadCredentialsException e) {
            log.warn("Credenciales inválidas para el usuario: {}", loginRequestDTO.getNombreUsuario());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiErrorResponseDTO(
                            HttpStatus.UNAUTHORIZED.value(),
                            "No Autorizado",
                            "Credenciales de inicio de sesión inválidas.",
                            "/api/v1/auth/login"
                    ));
        } catch (DisabledException e) {
            log.warn("Intento de login fallido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiErrorResponseDTO(
                            HttpStatus.UNAUTHORIZED.value(),
                            "Usuario Deshabilitado",
                            e.getMessage(),
                            "/api/v1/auth/login"
                    ));
        } catch (Exception e) {
            log.error("Error durante la autenticación del usuario: {}. Error: {}", loginRequestDTO.getNombreUsuario(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiErrorResponseDTO(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error Interno del Servidor",
                            "Ocurrió un problema durante el proceso de autenticación.",
                            "/api/v1/auth/login"
                    ));
        }
    }

    /**
     * Permite registrar un nuevo usuario en el sistema.
     */
    @Operation(summary = "Registrar un nuevo usuario",
            description = "Crea una nueva cuenta de usuario. El rol y la asociación con empleado se gestionan en el servicio.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto, usuario o empleado ya existen.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> registerUser(@Valid @RequestBody UsuarioCreateRequestDTO signUpRequest) {
        log.info("Intentando registrar nuevo usuario: {}", signUpRequest.getNombreUsuario());
        UsuarioResponseDTO createdUsuario = usuarioService.createUsuario(signUpRequest);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/v1/usuarios/{id}")
                .buildAndExpand(createdUsuario.getIdUsuario()).toUri();
        log.info("Usuario {} registrado exitosamente con ID: {}", createdUsuario.getNombreUsuario(), createdUsuario.getIdUsuario());
        return ResponseEntity.created(location).body(createdUsuario);
    }
}