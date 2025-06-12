package com.crmtech360.crmtech360_backend.controller;

import com.crmtech360.crmtech360_backend.dto.*;
import com.crmtech360.crmtech360_backend.service.InventarioProductoService;
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

import java.net.URI;
import java.util.List;

/**
 * Controlador para la gestión del inventario de productos terminados.
 * Permite registrar, consultar y actualizar el stock y movimientos de productos en distintas ubicaciones.
 */
@RestController
@RequestMapping("/api/v1/inventario-productos")
@Tag(name = "Inventario de Productos", description = "API para la gestión de stock de productos terminados y sus movimientos.")
@SecurityRequirement(name = "bearerAuth")
public class InventarioProductoController {

    private final InventarioProductoService inventarioProductoService;

    public InventarioProductoController(InventarioProductoService inventarioProductoService) {
        this.inventarioProductoService = inventarioProductoService;
    }

    /**
     * Registra el stock inicial de un producto en una ubicación.
     * La combinación producto-ubicación debe ser única.
     */
    @Operation(summary = "Crear inventario de producto",
            description = "Registra el stock inicial de un producto en una ubicación específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inventario creado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventarioProductoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o producto no existe.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe inventario para ese producto y ubicación.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'OPERARIO') or hasAuthority('PERMISO_GESTIONAR_INVENTARIO')")
    public ResponseEntity<InventarioProductoResponseDTO> createInventarioProducto(@Valid @RequestBody InventarioProductoCreateRequestDTO createRequestDTO) {
        InventarioProductoResponseDTO createdInventario = inventarioProductoService.createInventarioProducto(createRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdInventario.getIdInventarioProducto())
                .toUri();
        return ResponseEntity.created(location).body(createdInventario);
    }

    /**
     * Devuelve una lista paginada de todos los registros de inventario de productos.
     */
    @Operation(summary = "Listar inventario de productos",
            description = "Obtiene todos los registros de inventario de productos con paginación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de inventario obtenida.")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'OPERARIO', 'VENTAS') or hasAuthority('PERMISO_VER_INVENTARIO')")
    public ResponseEntity<Page<InventarioProductoResponseDTO>> getAllInventarioProductos(
            @Parameter(description = "Paginación y orden") @PageableDefault(size = 10) Pageable pageable) {
        Page<InventarioProductoResponseDTO> inventarios = inventarioProductoService.findAllInventarioProductos(pageable);
        return ResponseEntity.ok(inventarios);
    }

    /**
     * Consulta un registro de inventario de producto por su ID.
     */
    @Operation(summary = "Buscar inventario por ID",
            description = "Devuelve el inventario de producto correspondiente al ID indicado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario encontrado."),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @GetMapping("/{idInventarioProducto}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'OPERARIO', 'VENTAS') or hasAuthority('PERMISO_VER_INVENTARIO')")
    public ResponseEntity<InventarioProductoResponseDTO> getInventarioProductoById(
            @Parameter(description = "ID del inventario", required = true, example = "1") @PathVariable Integer idInventarioProducto) {
        InventarioProductoResponseDTO inventario = inventarioProductoService.findInventarioProductoById(idInventarioProducto);
        return ResponseEntity.ok(inventario);
    }

    /**
     * Consulta un inventario por producto y ubicación.
     */
    @Operation(summary = "Buscar inventario por producto y ubicación",
            description = "Devuelve el inventario de un producto en una ubicación específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario encontrado."),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado.")
    })
    @GetMapping("/producto/{idProducto}/ubicacion/{ubicacionInventario}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'OPERARIO', 'VENTAS') or hasAuthority('PERMISO_VER_INVENTARIO')")
    public ResponseEntity<InventarioProductoResponseDTO> getInventarioByProductoAndUbicacion(
            @Parameter(description = "ID del producto", required = true) @PathVariable Integer idProducto,
            @Parameter(description = "Ubicación", required = true) @PathVariable String ubicacionInventario) {
        InventarioProductoResponseDTO inventario = inventarioProductoService.findByProductoAndUbicacion(idProducto, ubicacionInventario);
        return ResponseEntity.ok(inventario);
    }

    /**
     * Devuelve todos los registros de inventario para un producto específico.
     */
    @Operation(summary = "Listar inventarios por producto",
            description = "Obtiene todas las ubicaciones y stocks para un producto dado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de inventarios encontrada.")
    })
    @GetMapping("/producto/{idProducto}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'OPERARIO', 'VENTAS') or hasAuthority('PERMISO_VER_INVENTARIO')")
    public ResponseEntity<List<InventarioProductoResponseDTO>> getInventariosByProductoId(
            @Parameter(description = "ID del producto", required = true) @PathVariable Integer idProducto) {
        List<InventarioProductoResponseDTO> inventarios = inventarioProductoService.findInventariosByProductoId(idProducto);
        return ResponseEntity.ok(inventarios);
    }

    /**
     * Registra un movimiento de entrada o salida en el inventario de un producto.
     */
    @Operation(summary = "Registrar movimiento de inventario de producto",
            description = "Permite registrar entradas o salidas de stock para un producto en una ubicación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movimiento registrado y stock actualizado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MovimientoInventarioProductoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o stock insuficiente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PostMapping("/movimientos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'OPERARIO', 'VENTAS') or hasAuthority('PERMISO_REGISTRAR_MOVIMIENTO_INVENTARIO')")
    public ResponseEntity<MovimientoInventarioProductoResponseDTO> registrarMovimiento(
            @Valid @RequestBody MovimientoInventarioProductoCreateRequestDTO movimientoRequestDTO) {
        MovimientoInventarioProductoResponseDTO movimiento = inventarioProductoService.registrarMovimiento(movimientoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
    }

    /**
     * Devuelve una lista paginada de movimientos de un inventario de producto.
     */
    @Operation(summary = "Listar movimientos de inventario de producto",
            description = "Obtiene todos los movimientos registrados para un inventario de producto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de movimientos obtenida.")
    })
    @GetMapping("/{idInventarioProducto}/movimientos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'OPERARIO') or hasAuthority('PERMISO_VER_INVENTARIO')")
    public ResponseEntity<Page<MovimientoInventarioProductoResponseDTO>> getMovimientosByInventarioProductoId(
            @Parameter(description = "ID del inventario", required = true) @PathVariable Integer idInventarioProducto,
            @Parameter(description = "Paginación") @PageableDefault(size = 20, sort = "fechaMovimiento", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<MovimientoInventarioProductoResponseDTO> movimientos = inventarioProductoService.findMovimientosByInventarioProductoId(idInventarioProducto, pageable);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * Devuelve el stock actual de un inventario de producto.
     */
    @Operation(summary = "Consultar stock actual de producto",
            description = "Devuelve la cantidad actual en stock para un inventario de producto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock actual obtenido.")
    })
    @GetMapping("/{idInventarioProducto}/stock")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'OPERARIO', 'VENTAS') or hasAuthority('PERMISO_VER_INVENTARIO')")
    public ResponseEntity<Integer> getStockActual(
            @Parameter(description = "ID del inventario", required = true) @PathVariable Integer idInventarioProducto) {
        Integer stock = inventarioProductoService.getStockActual(idInventarioProducto);
        return ResponseEntity.ok(stock);
    }
}