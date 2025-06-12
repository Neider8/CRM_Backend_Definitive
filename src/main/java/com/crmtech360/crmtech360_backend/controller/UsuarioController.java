package com.crmtech360.crmtech360_backend.controller;

import com.crmtech360.crmtech360_backend.dto.*;
import com.crmtech360.crmtech360_backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Controlador para la administración de usuarios del sistema.
 * Permite crear, consultar, actualizar, cambiar contraseña y eliminar cuentas de usuario.
 */
@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "API para la gestión de cuentas de usuario del sistema.")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Crea un nuevo usuario (solo administradores).
     */
    @Operation(summary = "Crear un nuevo usuario",
            description = "Registra una nueva cuenta de usuario. Solo disponible para administradores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida."),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Nombre de usuario o empleado ya asociado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponseDTO> createUsuario(@Valid @RequestBody UsuarioCreateRequestDTO usuarioCreateRequestDTO) {
        UsuarioResponseDTO createdUsuario = usuarioService.createUsuario(usuarioCreateRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUsuario.getIdUsuario())
                .toUri();
        return ResponseEntity.created(location).body(createdUsuario);
    }

    /**
     * Devuelve una lista paginada de usuarios.
     */
    @Operation(summary = "Obtener todos los usuarios (paginado)",
            description = "Devuelve una lista paginada de todos los usuarios registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente.")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<Page<UsuarioResponseDTO>> getAllUsuarios(
            @Parameter(description = "Configuración de paginación") 
            @PageableDefault(size = 10, sort = "nombreUsuario") Pageable pageable) {
        Page<UsuarioResponseDTO> usuarios = usuarioService.findAllUsuarios(pageable);
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Consulta un usuario por su ID.
     */
    @Operation(summary = "Obtener un usuario por su ID",
            description = "Devuelve los detalles de un usuario específico. Accesible por el propio usuario o por administradores y gerentes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("@userSecurity.hasUserId(authentication, #id) or hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioById(
            @Parameter(description = "ID del usuario a obtener.", required = true) @PathVariable Integer id) {
        UsuarioResponseDTO usuario = usuarioService.findUsuarioById(id);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Consulta un usuario por su nombre de usuario.
     */
    @Operation(summary = "Obtener un usuario por su nombre de usuario",
            description = "Devuelve los detalles de un usuario por su nombre de usuario. Accesible por el propio usuario o por administradores y gerentes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @GetMapping("/username/{nombreUsuario}")
    @PreAuthorize("@userSecurity.isOwner(authentication, #nombreUsuario) or hasAnyRole('ADMINISTRADOR', 'GERENTE')")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioByNombreUsuario(
            @Parameter(description = "Nombre de usuario único.", required = true) @PathVariable String nombreUsuario) {
        UsuarioResponseDTO usuario = usuarioService.findUsuarioByNombreUsuario(nombreUsuario);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Actualiza los datos de un usuario.
     */
    @Operation(summary = "Actualizar información de un usuario",
            description = "Permite al propio usuario o a un administrador actualizar los datos de la cuenta. El nombre de usuario no se puede cambiar.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto (ej. empleado ya tiene usuario).",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("@userSecurity.hasUserId(authentication, #id) or hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponseDTO> updateUsuario(
            @Parameter(description = "ID del usuario a actualizar.", required = true) @PathVariable Integer id,
            @Valid @RequestBody UsuarioUpdateRequestDTO usuarioUpdateRequestDTO) {
        UsuarioResponseDTO updatedUsuario = usuarioService.updateUsuario(id, usuarioUpdateRequestDTO);
        return ResponseEntity.ok(updatedUsuario);
    }

    /**
     * Cambia la contraseña de un usuario.
     */
    @Operation(summary = "Cambiar la contraseña de un usuario",
            description = "Permite al propio usuario o a un administrador cambiar la contraseña de una cuenta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente.",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PostMapping("/{id}/change-password")
    @PreAuthorize("@userSecurity.hasUserId(authentication, #id) or hasRole('ADMINISTRADOR')")
    public ResponseEntity<String> changePassword(
            @Parameter(description = "ID del usuario cuya contraseña se cambiará.", required = true) @PathVariable Integer id,
            @Valid @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO) {
        usuarioService.changePassword(id, changePasswordRequestDTO);
        return ResponseEntity.ok("Contraseña cambiada exitosamente.");
    }

    /**
     * Elimina un usuario por su ID (solo administradores).
     */
    @Operation(summary = "Eliminar un usuario por su ID",
            description = "Elimina una cuenta de usuario del sistema. Solo disponible para administradores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteUsuario(
            @Parameter(description = "ID del usuario a eliminar.", required = true) @PathVariable Integer id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }
}