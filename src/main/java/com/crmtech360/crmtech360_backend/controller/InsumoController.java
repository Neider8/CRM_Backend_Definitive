package com.crmtech360.crmtech360_backend.controller;

import com.crmtech360.crmtech360_backend.dto.ApiErrorResponseDTO;
import com.crmtech360.crmtech360_backend.dto.InsumoCreateRequestDTO;
import com.crmtech360.crmtech360_backend.dto.InsumoResponseDTO;
import com.crmtech360.crmtech360_backend.dto.InsumoUpdateRequestDTO;
import com.crmtech360.crmtech360_backend.service.InsumoService;
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
 * Controlador para la gestión de insumos (materias primas y componentes).
 * Permite registrar, consultar, actualizar y eliminar insumos.
 */
@RestController
@RequestMapping("/api/v1/insumos")
@Tag(name = "Insumos", description = "API para la gestión de insumos (materias primas y componentes).")
@SecurityRequirement(name = "bearerAuth")
public class InsumoController {

    private final InsumoService insumoService;

    public InsumoController(InsumoService insumoService) {
        this.insumoService = insumoService;
    }

    /**
     * Registra un nuevo insumo en el sistema.
     * El nombre del insumo debe ser único.
     */
    @Operation(summary = "Crear un nuevo insumo",
            description = "Registra un nuevo insumo. Solo usuarios autorizados pueden acceder.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Insumo creado exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InsumoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe un insumo con ese nombre.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_CREAR_INSUMOS')")
    public ResponseEntity<InsumoResponseDTO> createInsumo(@Valid @RequestBody InsumoCreateRequestDTO insumoCreateRequestDTO) {
        InsumoResponseDTO createdInsumo = insumoService.createInsumo(insumoCreateRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdInsumo.getIdInsumo())
                .toUri();
        return ResponseEntity.created(location).body(createdInsumo);
    }

    /**
     * Devuelve una lista paginada de insumos.
     */
    @Operation(summary = "Obtener todos los insumos (paginado)",
            description = "Devuelve una lista paginada de insumos registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de insumos obtenida.")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'OPERARIO') or hasAuthority('PERMISO_VER_INSUMOS')")
    public ResponseEntity<Page<InsumoResponseDTO>> getAllInsumos(
            @Parameter(description = "Configuración de paginación") @PageableDefault(size = 10, sort = "nombreInsumo") Pageable pageable) {
        Page<InsumoResponseDTO> insumos = insumoService.findAllInsumos(pageable);
        return ResponseEntity.ok(insumos);
    }

    /**
     * Consulta los datos de un insumo por su ID.
     */
    @Operation(summary = "Obtener un insumo por su ID",
            description = "Devuelve los detalles de un insumo específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Insumo encontrado."),
            @ApiResponse(responseCode = "404", description = "Insumo no encontrado.")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'OPERARIO') or hasAuthority('PERMISO_VER_INSUMOS')")
    public ResponseEntity<InsumoResponseDTO> getInsumoById(
            @Parameter(description = "ID del insumo", required = true, example = "1") @PathVariable Integer id) {
        InsumoResponseDTO insumo = insumoService.findInsumoById(id);
        return ResponseEntity.ok(insumo);
    }

    /**
     * Consulta los datos de un insumo por su nombre.
     */
    @Operation(summary = "Obtener un insumo por su nombre",
            description = "Busca un insumo usando su nombre exacto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Insumo encontrado."),
            @ApiResponse(responseCode = "404", description = "Insumo no encontrado.")
    })
    @GetMapping("/nombre/{nombre}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'OPERARIO') or hasAuthority('PERMISO_VER_INSUMOS')")
    public ResponseEntity<InsumoResponseDTO> getInsumoByNombre(
            @Parameter(description = "Nombre del insumo", required = true, example = "Tela Algodón Azul") @PathVariable String nombre) {
        InsumoResponseDTO insumo = insumoService.findInsumoByNombre(nombre);
        return ResponseEntity.ok(insumo);
    }

    /**
     * Actualiza los datos de un insumo existente.
     */
    @Operation(summary = "Actualizar un insumo existente",
            description = "Permite modificar los datos de un insumo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Insumo actualizado."),
            @ApiResponse(responseCode = "404", description = "Insumo no encontrado.")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_EDITAR_INSUMOS')")
    public ResponseEntity<InsumoResponseDTO> updateInsumo(
            @Parameter(description = "ID del insumo", required = true) @PathVariable Integer id,
            @Valid @RequestBody InsumoUpdateRequestDTO insumoUpdateRequestDTO) {
        InsumoResponseDTO updatedInsumo = insumoService.updateInsumo(id, insumoUpdateRequestDTO);
        return ResponseEntity.ok(updatedInsumo);
    }

    /**
     * Elimina un insumo por su ID.
     */
    @Operation(summary = "Eliminar un insumo por su ID",
            description = "Elimina un insumo del sistema. No se puede eliminar si está en uso.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Insumo eliminado."),
            @ApiResponse(responseCode = "404", description = "Insumo no encontrado."),
            @ApiResponse(responseCode = "409", description = "No se puede eliminar el insumo porque está en uso.")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('PERMISO_ELIMINAR_INSUMOS')")
    public ResponseEntity<Void> deleteInsumo(
            @Parameter(description = "ID del insumo", required = true) @PathVariable Integer id) {
        insumoService.deleteInsumo(id);
        return ResponseEntity.noContent().build();
    }
}