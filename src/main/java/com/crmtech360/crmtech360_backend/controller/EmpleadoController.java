package com.crmtech360.crmtech360_backend.controller;

import com.crmtech360.crmtech360_backend.dto.ApiErrorResponseDTO;
import com.crmtech360.crmtech360_backend.dto.EmpleadoCreateRequestDTO;
import com.crmtech360.crmtech360_backend.dto.EmpleadoResponseDTO;
import com.crmtech360.crmtech360_backend.dto.EmpleadoUpdateRequestDTO;
import com.crmtech360.crmtech360_backend.service.EmpleadoService;
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
 * Controlador para la gestión de empleados.
 * Permite registrar, consultar, actualizar y eliminar empleados del sistema.
 */
@RestController
@RequestMapping("/api/v1/empleados")
@Tag(name = "Empleados", description = "Operaciones para la administración de empleados.")
@SecurityRequirement(name = "bearerAuth")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    /**
     * Crea un nuevo empleado.
     * El número de documento debe ser único.
     */
    @Operation(summary = "Crear empleado", description = "Registra un nuevo empleado en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empleado creado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmpleadoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Empleado ya existe.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_CREAR_EMPLEADOS')")
    public ResponseEntity<EmpleadoResponseDTO> createEmpleado(@Valid @RequestBody EmpleadoCreateRequestDTO empleadoCreateRequestDTO) {
        EmpleadoResponseDTO createdEmpleado = empleadoService.createEmpleado(empleadoCreateRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdEmpleado.getIdEmpleado())
                .toUri();
        return ResponseEntity.created(location).body(createdEmpleado);
    }

    /**
     * Devuelve una lista paginada de empleados.
     */
    @Operation(summary = "Listar empleados", description = "Obtiene todos los empleados registrados, con paginación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de empleados obtenida.")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_VER_EMPLEADOS')")
    public ResponseEntity<Page<EmpleadoResponseDTO>> getAllEmpleados(
            @Parameter(description = "Parámetros de paginación") @PageableDefault(size = 10, sort = "nombreEmpleado") Pageable pageable) {
        Page<EmpleadoResponseDTO> empleados = empleadoService.findAllEmpleados(pageable);
        return ResponseEntity.ok(empleados);
    }

    /**
     * Consulta un empleado por su ID.
     */
    @Operation(summary = "Buscar empleado por ID", description = "Devuelve los datos de un empleado específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado encontrado."),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado.")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_VER_EMPLEADOS') or @userSecurity.isEmployeeSelf(authentication, #id)")
    public ResponseEntity<EmpleadoResponseDTO> getEmpleadoById(
            @Parameter(description = "ID del empleado", required = true, example = "1") @PathVariable Integer id) {
        EmpleadoResponseDTO empleado = empleadoService.findEmpleadoById(id);
        return ResponseEntity.ok(empleado);
    }

    /**
     * Consulta un empleado por su número de documento.
     */
    @Operation(summary = "Buscar empleado por documento", description = "Busca un empleado usando su número de documento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado encontrado."),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado.")
    })
    @GetMapping("/documento/{numeroDocumento}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_VER_EMPLEADOS')")
    public ResponseEntity<EmpleadoResponseDTO> getEmpleadoByNumeroDocumento(
            @Parameter(description = "Número de documento", required = true, example = "10203040") @PathVariable String numeroDocumento) {
        EmpleadoResponseDTO empleado = empleadoService.findEmpleadoByNumeroDocumento(numeroDocumento);
        return ResponseEntity.ok(empleado);
    }

    /**
     * Actualiza los datos de un empleado existente.
     */
    @Operation(summary = "Actualizar empleado", description = "Modifica los datos de un empleado existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado actualizado."),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado.")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_EDITAR_EMPLEADOS')")
    public ResponseEntity<EmpleadoResponseDTO> updateEmpleado(
            @Parameter(description = "ID del empleado", required = true) @PathVariable Integer id,
            @Valid @RequestBody EmpleadoUpdateRequestDTO empleadoUpdateRequestDTO) {
        EmpleadoResponseDTO updatedEmpleado = empleadoService.updateEmpleado(id, empleadoUpdateRequestDTO);
        return ResponseEntity.ok(updatedEmpleado);
    }

    /**
     * Elimina un empleado por su ID.
     */
    @Operation(summary = "Eliminar empleado", description = "Elimina un empleado del sistema. Esta acción es irreversible.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Empleado eliminado."),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado.")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('PERMISO_ELIMINAR_EMPLEADOS')")
    public ResponseEntity<Void> deleteEmpleado(
            @Parameter(description = "ID del empleado", required = true) @PathVariable Integer id) {
        empleadoService.deleteEmpleado(id);
        return ResponseEntity.noContent().build();
    }
}