package com.crmtech360.crmtech360_backend.controller;

import com.crmtech360.crmtech360_backend.dto.StockAlertDTO;
import com.crmtech360.crmtech360_backend.dto.UpdateStockThresholdDTO;
import com.crmtech360.crmtech360_backend.dto.ApiErrorResponseDTO;
import com.crmtech360.crmtech360_backend.service.StockAlertService;
import com.crmtech360.crmtech360_backend.exception.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la gestión de alertas de stock y configuración de umbrales mínimos.
 * Permite consultar alertas activas, actualizar umbrales y marcar alertas como vistas o resueltas.
 */
@RestController
@RequestMapping("/api/v1/stock-alerts")
@Tag(name = "Stock Alerts Management", description = "APIs para la gestión de alertas de stock y configuración de umbrales.")
@SecurityRequirement(name = "bearerAuth")
public class StockAlertController {

    private static final Logger logger = LoggerFactory.getLogger(StockAlertController.class);

    private final StockAlertService stockAlertService;

    @Autowired
    public StockAlertController(StockAlertService stockAlertService) {
        this.stockAlertService = stockAlertService;
    }

    /**
     * Devuelve todas las alertas de stock activas.
     */
    @Operation(summary = "Obtener todas las alertas de stock activas",
            description = "Lista los ítems cuyo stock está por debajo del umbral configurado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alertas recuperadas exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StockAlertDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Prohibido.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR', 'OPERARIO')")
    public ResponseEntity<List<StockAlertDTO>> getActiveAlerts() {
        logger.info("Consulta de alertas de stock activas.");
        List<StockAlertDTO> alerts = stockAlertService.getActiveAlerts();
        return ResponseEntity.ok(alerts);
    }

    /**
     * Actualiza el umbral mínimo de stock para un insumo.
     */
    @Operation(summary = "Actualizar el umbral de stock para un insumo",
            description = "Permite configurar el nivel mínimo de stock para un insumo específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Umbral actualizado exitosamente.",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Insumo no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PutMapping("/threshold/insumo/{idInsumo}")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public ResponseEntity<String> updateInsumoThreshold(
            @Parameter(description = "ID del insumo", required = true) @PathVariable Integer idInsumo,
            @Valid @RequestBody UpdateStockThresholdDTO thresholdDTO) {
        logger.info("Actualizando umbral de insumo ID {}: {}", idInsumo, thresholdDTO.getNuevoUmbral());
        stockAlertService.updateInsumoThreshold(idInsumo, thresholdDTO.getNuevoUmbral());
        return ResponseEntity.ok("Umbral para insumo ID " + idInsumo + " actualizado a " + thresholdDTO.getNuevoUmbral());
    }

    /**
     * Actualiza el umbral mínimo de stock para un producto terminado.
     */
    @Operation(summary = "Actualizar el umbral de stock para un producto terminado",
            description = "Permite configurar el nivel mínimo de stock para un producto terminado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Umbral actualizado exitosamente.",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PutMapping("/threshold/producto/{idProducto}")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public ResponseEntity<String> updateProductoThreshold(
            @Parameter(description = "ID del producto terminado", required = true) @PathVariable Integer idProducto,
            @Valid @RequestBody UpdateStockThresholdDTO thresholdDTO) {
        logger.info("Actualizando umbral de producto ID {}: {}", idProducto, thresholdDTO.getNuevoUmbral());
        stockAlertService.updateProductoThreshold(idProducto, thresholdDTO.getNuevoUmbral());
        return ResponseEntity.ok("Umbral para producto terminado ID " + idProducto + " actualizado a " + thresholdDTO.getNuevoUmbral());
    }

    /**
     * Marca una alerta de stock como vista.
     */
    @Operation(summary = "Marcar una alerta de stock como vista",
            description = "Cambia el estado de una alerta a 'Vista'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alerta marcada como vista exitosamente."),
            @ApiResponse(responseCode = "404", description = "Alerta no encontrada.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PostMapping("/{idAlerta}/view")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR', 'OPERARIO')")
    public ResponseEntity<Void> markAlertAsViewed(
            @Parameter(description = "ID de la alerta a marcar como vista.", required = true) @PathVariable Long idAlerta) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            // Aquí puedes obtener el ID del usuario si tu UserDetails lo permite.
            logger.debug("No se pudo obtener el ID del usuario autenticado para marcar la alerta como vista.");
        }
        stockAlertService.markAlertAsViewed(idAlerta, userId);
        logger.info("Alerta ID {} marcada como vista.", idAlerta);
        return ResponseEntity.ok().build();
    }

    /**
     * Marca una alerta de stock como resuelta.
     */
    @Operation(summary = "Marcar una alerta de stock como resuelta",
            description = "Cambia el estado de una alerta a 'Resuelta'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alerta marcada como resuelta exitosamente."),
            @ApiResponse(responseCode = "404", description = "Alerta no encontrada.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PostMapping("/{idAlerta}/resolve")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR', 'OPERARIO')")
    public ResponseEntity<Void> markAlertAsResolved(
            @Parameter(description = "ID de la alerta a marcar como resuelta.", required = true) @PathVariable Long idAlerta) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            logger.debug("No se pudo obtener el ID del usuario autenticado para marcar la alerta como resuelta.");
        }
        stockAlertService.markAlertAsResolved(idAlerta, userId);
        logger.info("Alerta ID {} marcada como resuelta.", idAlerta);
        return ResponseEntity.ok().build();
    }
}