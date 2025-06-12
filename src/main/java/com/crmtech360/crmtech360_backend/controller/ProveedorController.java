package com.crmtech360.crmtech360_backend.controller;

import com.crmtech360.crmtech360_backend.dto.ApiErrorResponseDTO;
import com.crmtech360.crmtech360_backend.dto.ProveedorCreateRequestDTO;
import com.crmtech360.crmtech360_backend.dto.ProveedorResponseDTO;
import com.crmtech360.crmtech360_backend.dto.ProveedorUpdateRequestDTO;
import com.crmtech360.crmtech360_backend.service.ProveedorService;
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
 * Controlador para la gestión de proveedores.
 * Permite registrar, consultar, actualizar y eliminar proveedores de insumos y servicios.
 */
@RestController
@RequestMapping("/api/v1/proveedores")
@Tag(name = "Proveedores", description = "API para la gestión de proveedores de insumos y servicios.")
@SecurityRequirement(name = "bearerAuth")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    /**
     * Registra un nuevo proveedor en el sistema.
     * El NIT debe ser único.
     */
    @Operation(summary = "Crear un nuevo proveedor",
            description = "Registra un nuevo proveedor en el sistema. El NIT debe ser único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Proveedor creado exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProveedorResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe un proveedor con el mismo NIT.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_CREAR_PROVEEDORES')")
    public ResponseEntity<ProveedorResponseDTO> createProveedor(@Valid @RequestBody ProveedorCreateRequestDTO proveedorCreateRequestDTO) {
        ProveedorResponseDTO createdProveedor = proveedorService.createProveedor(proveedorCreateRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdProveedor.getIdProveedor())
                .toUri();
        return ResponseEntity.created(location).body(createdProveedor);
    }

    /**
     * Devuelve una lista paginada de todos los proveedores.
     */
    @Operation(summary = "Obtener todos los proveedores (paginado)",
            description = "Devuelve una lista paginada de todos los proveedores registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de proveedores obtenida exitosamente.")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_VER_PROVEEDORES')")
    public ResponseEntity<Page<ProveedorResponseDTO>> getAllProveedores(
            @Parameter(description = "Configuración de paginación") 
            @PageableDefault(size = 10, sort = "nombreComercialProveedor") Pageable pageable) {
        Page<ProveedorResponseDTO> proveedores = proveedorService.findAllProveedores(pageable);
        return ResponseEntity.ok(proveedores);
    }

    /**
     * Consulta un proveedor por su ID.
     */
    @Operation(summary = "Obtener un proveedor por su ID",
            description = "Devuelve los detalles de un proveedor específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proveedor encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProveedorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_VER_PROVEEDORES')")
    public ResponseEntity<ProveedorResponseDTO> getProveedorById(
            @Parameter(description = "ID del proveedor a obtener.", required = true) @PathVariable Integer id) {
        ProveedorResponseDTO proveedor = proveedorService.findProveedorById(id);
        return ResponseEntity.ok(proveedor);
    }

    /**
     * Consulta un proveedor por su NIT.
     */
    @Operation(summary = "Obtener un proveedor por su NIT",
            description = "Devuelve los detalles de un proveedor específico basado en su NIT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proveedor encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProveedorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @GetMapping("/nit/{nit}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_VER_PROVEEDORES')")
    public ResponseEntity<ProveedorResponseDTO> getProveedorByNit(
            @Parameter(description = "NIT del proveedor a obtener.", required = true) @PathVariable String nit) {
        ProveedorResponseDTO proveedor = proveedorService.findProveedorByNit(nit);
        return ResponseEntity.ok(proveedor);
    }

    /**
     * Actualiza los datos de un proveedor existente.
     * El NIT no se puede modificar.
     */
    @Operation(summary = "Actualizar un proveedor existente",
            description = "Permite modificar los datos de un proveedor. El NIT no se puede cambiar.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proveedor actualizado exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProveedorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_EDITAR_PROVEEDORES')")
    public ResponseEntity<ProveedorResponseDTO> updateProveedor(
            @Parameter(description = "ID del proveedor a actualizar.", required = true) @PathVariable Integer id,
            @Valid @RequestBody ProveedorUpdateRequestDTO proveedorUpdateRequestDTO) {
        ProveedorResponseDTO updatedProveedor = proveedorService.updateProveedor(id, proveedorUpdateRequestDTO);
        return ResponseEntity.ok(updatedProveedor);
    }

    /**
     * Elimina un proveedor por su ID.
     * No se puede eliminar si tiene órdenes de compra asociadas.
     */
    @Operation(summary = "Eliminar un proveedor por su ID",
            description = "Elimina un proveedor del sistema. No se puede eliminar si tiene órdenes de compra asociadas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Proveedor eliminado exitosamente."),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "El proveedor no se puede eliminar (tiene órdenes de compra asociadas).",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('PERMISO_ELIMINAR_PROVEEDORES')")
    public ResponseEntity<Void> deleteProveedor(
            @Parameter(description = "ID del proveedor a eliminar.", required = true) @PathVariable Integer id) {
        proveedorService.deleteProveedor(id);
        return ResponseEntity.noContent().build();
    }
}