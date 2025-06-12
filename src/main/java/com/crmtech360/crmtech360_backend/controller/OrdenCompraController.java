package com.crmtech360.crmtech360_backend.controller;

import com.crmtech360.crmtech360_backend.dto.*;
import com.crmtech360.crmtech360_backend.service.OrdenCompraService;
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
 * Controlador para la gestión de órdenes de compra de insumos a proveedores.
 * Permite registrar, consultar, actualizar y anular órdenes, así como administrar sus detalles.
 */
@RestController
@RequestMapping("/api/v1/ordenes-compra")
@Tag(name = "Órdenes de Compra", description = "API para la gestión de órdenes de compra de insumos a proveedores.")
@SecurityRequirement(name = "bearerAuth")
public class OrdenCompraController {

    private final OrdenCompraService ordenCompraService;

    public OrdenCompraController(OrdenCompraService ordenCompraService) {
        this.ordenCompraService = ordenCompraService;
    }

    /**
     * Registra una nueva orden de compra para adquirir insumos de un proveedor.
     */
    @Operation(summary = "Crear una nueva orden de compra",
            description = "Registra una nueva orden de compra para adquirir insumos de un proveedor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden de compra creada exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenCompraResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Proveedor o insumo no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_CREAR_ORDEN_COMPRA')")
    public ResponseEntity<OrdenCompraResponseDTO> createOrdenCompra(@Valid @RequestBody OrdenCompraCreateRequestDTO createRequestDTO) {
        OrdenCompraResponseDTO createdOrden = ordenCompraService.createOrdenCompra(createRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdOrden.getIdOrdenCompra())
                .toUri();
        return ResponseEntity.created(location).body(createdOrden);
    }

    /**
     * Devuelve una lista paginada de todas las órdenes de compra.
     */
    @Operation(summary = "Obtener todas las órdenes de compra (paginado)",
            description = "Devuelve una lista paginada de todas las órdenes de compra.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de órdenes de compra obtenida exitosamente.")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_VER_ORDENES_COMPRA')")
    public ResponseEntity<Page<OrdenCompraResponseDTO>> getAllOrdenesCompra(
            @Parameter(description = "Configuración de paginación") 
            @PageableDefault(size = 10, sort = "fechaPedidoCompra", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Page<OrdenCompraResponseDTO> ordenes = ordenCompraService.findAllOrdenesCompra(pageable);
        return ResponseEntity.ok(ordenes);
    }

    /**
     * Devuelve todas las órdenes de compra de un proveedor específico.
     */
    @Operation(summary = "Obtener todas las órdenes de compra de un proveedor específico",
            description = "Lista todas las órdenes de compra asociadas a un proveedor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de órdenes de compra para el proveedor."),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado.")
    })
    @GetMapping("/proveedor/{idProveedor}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_VER_ORDENES_COMPRA')")
    public ResponseEntity<List<OrdenCompraResponseDTO>> getOrdenesCompraByProveedorId(
            @Parameter(description = "ID del proveedor", required = true) @PathVariable Integer idProveedor) {
        List<OrdenCompraResponseDTO> ordenes = ordenCompraService.findOrdenesCompraByProveedorId(idProveedor);
        return ResponseEntity.ok(ordenes);
    }

    /**
     * Consulta una orden de compra por su ID.
     */
    @Operation(summary = "Obtener una orden de compra por su ID",
            description = "Devuelve los datos de una orden de compra específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden de compra encontrada."),
            @ApiResponse(responseCode = "404", description = "Orden de compra no encontrada.")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_VER_ORDENES_COMPRA')")
    public ResponseEntity<OrdenCompraResponseDTO> getOrdenCompraById(
            @Parameter(description = "ID de la orden de compra", required = true) @PathVariable Integer id) {
        OrdenCompraResponseDTO orden = ordenCompraService.findOrdenCompraById(id);
        return ResponseEntity.ok(orden);
    }

    /**
     * Actualiza la cabecera de una orden de compra existente.
     */
    @Operation(summary = "Actualizar la cabecera de una orden de compra",
            description = "Permite modificar campos como fechas y estado de una orden de compra.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden de compra actualizada exitosamente."),
            @ApiResponse(responseCode = "404", description = "Orden de compra no encontrada.")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_EDITAR_ORDEN_COMPRA')")
    public ResponseEntity<OrdenCompraResponseDTO> updateOrdenCompraHeader(
            @Parameter(description = "ID de la orden de compra", required = true) @PathVariable Integer id,
            @Valid @RequestBody OrdenCompraUpdateRequestDTO updateRequestDTO) {
        OrdenCompraResponseDTO updatedOrden = ordenCompraService.updateOrdenCompraHeader(id, updateRequestDTO);
        return ResponseEntity.ok(updatedOrden);
    }

    /**
     * Anula una orden de compra (no se puede anular si ya fue recibida).
     */
    @Operation(summary = "Anular una orden de compra",
            description = "Marca una orden de compra como anulada. No se puede anular si ya fue recibida.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden de compra anulada exitosamente."),
            @ApiResponse(responseCode = "400", description = "No se puede anular la orden en su estado actual."),
            @ApiResponse(responseCode = "404", description = "Orden de compra no encontrada.")
    })
    @PostMapping("/{id}/anular")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_ANULAR_ORDEN_COMPRA')")
    public ResponseEntity<Void> anularOrdenCompra(
            @Parameter(description = "ID de la orden de compra", required = true) @PathVariable Integer id) {
        ordenCompraService.anularOrdenCompra(id);
        return ResponseEntity.ok().build();
    }

    // --- Detalles de Orden de Compra ---

    /**
     * Añade un nuevo detalle a una orden de compra existente.
     */
    @Operation(summary = "Añadir un nuevo detalle a una orden de compra",
            description = "Agrega un insumo y su cantidad a una orden de compra existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Detalle añadido exitosamente a la orden.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetalleOrdenCompraResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Orden de compra o insumo no encontrado.")
    })
    @PostMapping("/{idOrdenCompra}/detalles")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_EDITAR_ORDEN_COMPRA')")
    public ResponseEntity<DetalleOrdenCompraResponseDTO> addDetalleToOrdenCompra(
            @Parameter(description = "ID de la orden de compra", required = true) @PathVariable Integer idOrdenCompra,
            @Valid @RequestBody DetalleOrdenCompraRequestDTO detalleRequestDTO) {
        DetalleOrdenCompraResponseDTO createdDetalle = ordenCompraService.addDetalleToOrdenCompra(idOrdenCompra, detalleRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDetalle);
    }

    /**
     * Actualiza un detalle específico en una orden de compra.
     */
    @Operation(summary = "Actualizar un detalle de una orden de compra",
            description = "Permite modificar la cantidad o el insumo de un detalle en una orden de compra.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle actualizado exitosamente."),
            @ApiResponse(responseCode = "404", description = "Orden de compra, detalle o insumo no encontrado.")
    })
    @PutMapping("/{idOrdenCompra}/detalles/{idDetalleCompra}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_EDITAR_ORDEN_COMPRA')")
    public ResponseEntity<DetalleOrdenCompraResponseDTO> updateDetalleInOrdenCompra(
            @Parameter(description = "ID de la orden de compra", required = true) @PathVariable Integer idOrdenCompra,
            @Parameter(description = "ID del detalle de compra", required = true) @PathVariable Integer idDetalleCompra,
            @Valid @RequestBody DetalleOrdenCompraRequestDTO detalleRequestDTO) {
        DetalleOrdenCompraResponseDTO updatedDetalle = ordenCompraService.updateDetalleInOrdenCompra(idOrdenCompra, idDetalleCompra, detalleRequestDTO);
        return ResponseEntity.ok(updatedDetalle);
    }

    /**
     * Elimina un detalle específico de una orden de compra.
     */
    @Operation(summary = "Eliminar un detalle de una orden de compra",
            description = "Elimina un insumo de una orden de compra existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Detalle eliminado exitosamente de la orden."),
            @ApiResponse(responseCode = "404", description = "Orden de compra o detalle no encontrado.")
    })
    @DeleteMapping("/{idOrdenCompra}/detalles/{idDetalleCompra}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_EDITAR_ORDEN_COMPRA')")
    public ResponseEntity<Void> removeDetalleFromOrdenCompra(
            @Parameter(description = "ID de la orden de compra", required = true) @PathVariable Integer idOrdenCompra,
            @Parameter(description = "ID del detalle de compra", required = true) @PathVariable Integer idDetalleCompra) {
        ordenCompraService.removeDetalleFromOrdenCompra(idOrdenCompra, idDetalleCompra);
        return ResponseEntity.noContent().build();
    }
}