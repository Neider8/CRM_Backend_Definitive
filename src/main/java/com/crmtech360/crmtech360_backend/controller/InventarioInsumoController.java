package com.crmtech360.crmtech360_backend.controller;

import com.crmtech360.crmtech360_backend.dto.*;
import com.crmtech360.crmtech360_backend.service.InventarioInsumoService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

/**
 * Controlador para la gestión del inventario de insumos.
 * Permite registrar, consultar y actualizar el stock y movimientos de insumos en distintas ubicaciones.
 */
@RestController
@RequestMapping("/api/v1/inventario-insumos")
@Tag(name = "Inventario de Insumos", description = "API para la gestión de stock de insumos y sus movimientos.")
@SecurityRequirement(name = "bearerAuth")
public class InventarioInsumoController {

    private final InventarioInsumoService inventarioInsumoService;

    public InventarioInsumoController(InventarioInsumoService inventarioInsumoService) {
        this.inventarioInsumoService = inventarioInsumoService;
    }

    /**
     * Crea un registro de inventario para un insumo en una ubicación específica.
     */
    @Operation(summary = "Crear inventario de insumo",
            description = "Registra el stock inicial de un insumo en una ubicación. La combinación insumo-ubicación debe ser única.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inventario creado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventarioInsumoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o insumo no existe.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe inventario para ese insumo y ubicación.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'OPERARIO') or hasAuthority('PERMISO_GESTIONAR_INVENTARIO')")
    public ResponseEntity<InventarioInsumoResponseDTO> createInventarioInsumo(@Valid @RequestBody InventarioInsumoCreateRequestDTO createRequestDTO) {
        InventarioInsumoResponseDTO createdInventario = inventarioInsumoService.createInventarioInsumo(createRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdInventario.getIdInventarioInsumo())
                .toUri();
        return ResponseEntity.created(location).body(createdInventario);
    }

    /**
     * Devuelve una lista paginada de todos los registros de inventario de insumos.
     */
    @Operation(summary = "Listar inventario de insumos",
            description = "Obtiene todos los registros de inventario de insumos con paginación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de inventario obtenida.")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'OPERARIO') or hasAuthority('PERMISO_VER_INVENTARIO')")
    public ResponseEntity<Page<InventarioInsumoResponseDTO>> getAllInventarioInsumos(
            @Parameter(description = "Paginación y orden") @PageableDefault(size = 10) Pageable pageable) {
        Page<InventarioInsumoResponseDTO> inventarios = inventarioInsumoService.findAllInventarioInsumos(pageable);
        return ResponseEntity.ok(inventarios);
    }

    /**
     * Consulta un registro de inventario de insumo por su ID.
     */
    @Operation(summary = "Buscar inventario por ID",
            description = "Devuelve el inventario de insumo correspondiente al ID indicado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario encontrado."),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @GetMapping("/{idInventarioInsumo}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'OPERARIO') or hasAuthority('PERMISO_VER_INVENTARIO')")
    public ResponseEntity<InventarioInsumoResponseDTO> getInventarioInsumoById(
            @Parameter(description = "ID del inventario", required = true, example = "1") @PathVariable Integer idInventarioInsumo) {
        InventarioInsumoResponseDTO inventario = inventarioInsumoService.findInventarioInsumoById(idInventarioInsumo);
        return ResponseEntity.ok(inventario);
    }

    /**
     * Consulta un inventario por insumo y ubicación.
     */
    @Operation(summary = "Buscar inventario por insumo y ubicación",
            description = "Devuelve el inventario de un insumo en una ubicación específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario encontrado."),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado.")
    })
    @GetMapping("/insumo/{idInsumo}/ubicacion/{ubicacionInventario}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'OPERARIO') or hasAuthority('PERMISO_VER_INVENTARIO')")
    public ResponseEntity<InventarioInsumoResponseDTO> getInventarioByInsumoAndUbicacion(
            @Parameter(description = "ID del insumo", required = true) @PathVariable Integer idInsumo,
            @Parameter(description = "Ubicación", required = true) @PathVariable String ubicacionInventario) {
        InventarioInsumoResponseDTO inventario = inventarioInsumoService.findByInsumoAndUbicacion(idInsumo, ubicacionInventario);
        return ResponseEntity.ok(inventario);
    }

    /**
     * Devuelve todos los registros de inventario para un insumo específico.
     */
    @Operation(summary = "Listar inventarios por insumo",
            description = "Obtiene todas las ubicaciones y stocks para un insumo dado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de inventarios encontrada.")
    })
    @GetMapping("/insumo/{idInsumo}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'OPERARIO') or hasAuthority('PERMISO_VER_INVENTARIO')")
    public ResponseEntity<List<InventarioInsumoResponseDTO>> getInventariosByInsumoId(
            @Parameter(description = "ID del insumo", required = true) @PathVariable Integer idInsumo) {
        List<InventarioInsumoResponseDTO> inventarios = inventarioInsumoService.findInventariosByInsumoId(idInsumo);
        return ResponseEntity.ok(inventarios);
    }

    /**
     * Registra un movimiento de entrada o salida en el inventario de un insumo.
     */
    @Operation(summary = "Registrar movimiento de inventario",
            description = "Permite registrar entradas o salidas de stock para un insumo en una ubicación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movimiento registrado y stock actualizado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovimientoInventarioInsumoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o stock insuficiente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PostMapping("/movimientos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'OPERARIO') or hasAuthority('PERMISO_REGISTRAR_MOVIMIENTO_INVENTARIO')")
    public ResponseEntity<MovimientoInventarioInsumoResponseDTO> registrarMovimiento(
            @Valid @RequestBody MovimientoInventarioInsumoCreateRequestDTO movimientoRequestDTO) {
        MovimientoInventarioInsumoResponseDTO movimiento = inventarioInsumoService.registrarMovimiento(movimientoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
    }

    /**
     * Devuelve una lista paginada de movimientos de un inventario de insumo.
     */
    @Operation(summary = "Listar movimientos de inventario",
            description = "Obtiene todos los movimientos registrados para un inventario de insumo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de movimientos obtenida.")
    })
    @GetMapping("/{idInventarioInsumo}/movimientos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'OPERARIO') or hasAuthority('PERMISO_VER_INVENTARIO')")
    public ResponseEntity<Page<MovimientoInventarioInsumoResponseDTO>> getMovimientosByInventarioInsumoId(
            @Parameter(description = "ID del inventario", required = true) @PathVariable Integer idInventarioInsumo,
            @Parameter(description = "Paginación") @PageableDefault(size = 20, sort = "fechaMovimiento", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<MovimientoInventarioInsumoResponseDTO> movimientos = inventarioInsumoService.findMovimientosByInventarioInsumoId(idInventarioInsumo, pageable);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * Devuelve el stock actual de un inventario de insumo.
     */
    @Operation(summary = "Consultar stock actual",
            description = "Devuelve la cantidad actual en stock para un inventario de insumo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock actual obtenido.")
    })
    @GetMapping("/{idInventarioInsumo}/stock")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'OPERARIO') or hasAuthority('PERMISO_VER_INVENTARIO')")
    public ResponseEntity<BigDecimal> getStockActual(
            @Parameter(description = "ID del inventario", required = true) @PathVariable Integer idInventarioInsumo) {
        BigDecimal stock = inventarioInsumoService.getStockActual(idInventarioInsumo);
        return ResponseEntity.ok(stock);
    }
}