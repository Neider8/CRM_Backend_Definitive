package com.crmtech360.crmtech360_backend.controller;

import com.crmtech360.crmtech360_backend.dto.*;
import com.crmtech360.crmtech360_backend.service.OrdenVentaService;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controlador para la gestión de órdenes de venta y sus detalles.
 * Permite crear, consultar, actualizar, anular y administrar los productos de cada orden.
 */
@RestController
@RequestMapping("/api/v1/ordenes-venta")
@Tag(name = "Órdenes de Venta", description = "API para la gestión de órdenes de venta de productos y sus detalles.")
@SecurityRequirement(name = "bearerAuth")
public class OrdenVentaController {

    private final OrdenVentaService ordenVentaService;

    public OrdenVentaController(OrdenVentaService ordenVentaService) {
        this.ordenVentaService = ordenVentaService;
    }

    /**
     * Registra una nueva orden de venta para un cliente.
     */
    @Operation(summary = "Crear una nueva orden de venta",
            description = "Registra una nueva orden de venta para un cliente, incluyendo los productos solicitados. El total se calcula automáticamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden de venta creada exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenVentaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente o producto no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENTAS') or hasAuthority('PERMISO_CREAR_ORDEN_VENTA')")
    public ResponseEntity<OrdenVentaResponseDTO> createOrdenVenta(@Valid @RequestBody OrdenVentaCreateRequestDTO createRequestDTO) {
        OrdenVentaResponseDTO createdOrdenVenta = ordenVentaService.createOrdenVenta(createRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdOrdenVenta.getIdOrdenVenta())
                .toUri();
        return ResponseEntity.created(location).body(createdOrdenVenta);
    }

    /**
     * Devuelve una lista paginada de todas las órdenes de venta.
     */
    @Operation(summary = "Obtener todas las órdenes de venta (paginado)",
            description = "Devuelve una lista paginada de todas las órdenes de venta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de órdenes de venta obtenida exitosamente.")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENTAS') or hasAuthority('PERMISO_VER_ORDENES_VENTA')")
    public ResponseEntity<Page<OrdenVentaResponseDTO>> getAllOrdenesVenta(
            @Parameter(description = "Configuración de paginación") 
            @PageableDefault(size = 10, sort = "fechaPedido", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<OrdenVentaResponseDTO> ordenes = ordenVentaService.findAllOrdenesVenta(pageable);
        return ResponseEntity.ok(ordenes);
    }

    /**
     * Consulta una orden de venta por su ID.
     */
    @Operation(summary = "Obtener una orden de venta por su ID",
            description = "Devuelve los detalles completos de una orden de venta específica, incluyendo sus ítems.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden de venta encontrada.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenVentaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Orden de venta no encontrada.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENTAS') or hasAuthority('PERMISO_VER_ORDENES_VENTA')")
    public ResponseEntity<OrdenVentaResponseDTO> getOrdenVentaById(
            @Parameter(description = "ID de la orden de venta a obtener.", required = true) @PathVariable Integer id) {
        OrdenVentaResponseDTO ordenVenta = ordenVentaService.findOrdenVentaById(id);
        return ResponseEntity.ok(ordenVenta);
    }

    /**
     * Devuelve todas las órdenes de venta de un cliente específico.
     */
    @Operation(summary = "Obtener todas las órdenes de venta de un cliente específico",
            description = "Devuelve una lista de todas las órdenes de venta asociadas a un cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de órdenes de venta para el cliente."),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado.")
    })
    @GetMapping("/cliente/{idCliente}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENTAS') or hasAuthority('PERMISO_VER_ORDENES_VENTA')")
    public ResponseEntity<List<OrdenVentaResponseDTO>> getOrdenesVentaByClienteId(
            @Parameter(description = "ID del cliente para filtrar las órdenes de venta.", required = true) @PathVariable Integer idCliente) {
        List<OrdenVentaResponseDTO> ordenes = ordenVentaService.findOrdenesVentaByClienteId(idCliente);
        return ResponseEntity.ok(ordenes);
    }

    /**
     * Actualiza la cabecera de una orden de venta existente.
     */
    @Operation(summary = "Actualizar la cabecera de una orden de venta existente",
            description = "Permite modificar campos como fechas, estado y observaciones de una orden de venta. No modifica los detalles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden de venta actualizada exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenVentaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida o no se puede modificar la orden en su estado actual."),
            @ApiResponse(responseCode = "404", description = "Orden de venta no encontrada.")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENTAS') or hasAuthority('PERMISO_EDITAR_ORDEN_VENTA')")
    public ResponseEntity<OrdenVentaResponseDTO> updateOrdenVentaHeader(
            @Parameter(description = "ID de la orden de venta a actualizar.", required = true) @PathVariable Integer id,
            @Valid @RequestBody OrdenVentaUpdateRequestDTO updateRequestDTO) {
        OrdenVentaResponseDTO updatedOrdenVenta = ordenVentaService.updateOrdenVentaHeader(id, updateRequestDTO);
        return ResponseEntity.ok(updatedOrdenVenta);
    }

    /**
     * Anula una orden de venta.
     */
    @Operation(summary = "Anular una orden de venta",
            description = "Marca una orden de venta como ANULADA. No se puede anular si ya fue entregada o está en producción avanzada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden de venta anulada exitosamente."),
            @ApiResponse(responseCode = "400", description = "No se puede anular la orden en su estado actual."),
            @ApiResponse(responseCode = "404", description = "Orden de venta no encontrada.")
    })
    @PostMapping("/{id}/anular")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENTAS') or hasAuthority('PERMISO_ANULAR_ORDEN_VENTA')")
    public ResponseEntity<Void> anularOrdenVenta(
            @Parameter(description = "ID de la orden de venta a anular.", required = true) @PathVariable Integer id) {
        ordenVentaService.anularOrdenVenta(id);
        return ResponseEntity.ok().build();
    }

    // --- Detalles de Orden de Venta ---

    /**
     * Añade un nuevo detalle a una orden de venta existente.
     */
    @Operation(summary = "Añadir un nuevo detalle a una orden de venta existente",
            description = "Permite agregar un producto a una orden de venta que no esté en un estado final.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Detalle añadido exitosamente a la orden.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetalleOrdenVentaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida."),
            @ApiResponse(responseCode = "404", description = "Orden de venta o producto no encontrado.")
    })
    @PostMapping("/{idOrdenVenta}/detalles")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENTAS') or hasAuthority('PERMISO_EDITAR_ORDEN_VENTA')")
    public ResponseEntity<DetalleOrdenVentaResponseDTO> addDetalleToOrdenVenta(
            @Parameter(description = "ID de la orden de venta a la que se añade el detalle.", required = true) @PathVariable Integer idOrdenVenta,
            @Valid @RequestBody DetalleOrdenVentaRequestDTO detalleRequestDTO) {
        DetalleOrdenVentaResponseDTO createdDetalle = ordenVentaService.addDetalleToOrdenVenta(idOrdenVenta, detalleRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDetalle);
    }

    /**
     * Actualiza un detalle específico en una orden de venta.
     */
    @Operation(summary = "Actualizar un detalle específico en una orden de venta",
            description = "Permite modificar la cantidad o precio de un ítem en una orden de venta no finalizada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle actualizado exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetalleOrdenVentaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida."),
            @ApiResponse(responseCode = "404", description = "Orden de venta, detalle o producto no encontrado.")
    })
    @PutMapping("/{idOrdenVenta}/detalles/{idDetalleVenta}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENTAS') or hasAuthority('PERMISO_EDITAR_ORDEN_VENTA')")
    public ResponseEntity<DetalleOrdenVentaResponseDTO> updateDetalleInOrdenVenta(
            @Parameter(description = "ID de la orden de venta.", required = true) @PathVariable Integer idOrdenVenta,
            @Parameter(description = "ID del detalle de la orden a actualizar.", required = true) @PathVariable Integer idDetalleVenta,
            @Valid @RequestBody DetalleOrdenVentaRequestDTO detalleRequestDTO) {
        DetalleOrdenVentaResponseDTO updatedDetalle = ordenVentaService.updateDetalleInOrdenVenta(idOrdenVenta, idDetalleVenta, detalleRequestDTO);
        return ResponseEntity.ok(updatedDetalle);
    }

    /**
     * Elimina un detalle específico de una orden de venta.
     */
    @Operation(summary = "Eliminar un detalle específico de una orden de venta",
            description = "Elimina un ítem de una orden de venta no finalizada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Detalle eliminado exitosamente de la orden."),
            @ApiResponse(responseCode = "404", description = "Orden de venta o detalle no encontrado.")
    })
    @DeleteMapping("/{idOrdenVenta}/detalles/{idDetalleVenta}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENTAS') or hasAuthority('PERMISO_EDITAR_ORDEN_VENTA')")
    public ResponseEntity<Void> removeDetalleFromOrdenVenta(
            @Parameter(description = "ID de la orden de venta.", required = true) @PathVariable Integer idOrdenVenta,
            @Parameter(description = "ID del detalle de la orden a eliminar.", required = true) @PathVariable Integer idDetalleVenta) {
        ordenVentaService.removeDetalleFromOrdenVenta(idOrdenVenta, idDetalleVenta);
        return ResponseEntity.noContent().build();
    }
}