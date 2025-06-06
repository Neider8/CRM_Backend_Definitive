package com.crmtech360.crmtech360_backend.controller;

import com.crmtech360.crmtech360_backend.dto.StockAlertDTO;
import com.crmtech360.crmtech360_backend.dto.UpdateStockThresholdDTO;
import com.crmtech360.crmtech360_backend.dto.ApiErrorResponseDTO; // Importar para respuestas de error de Swagger
import com.crmtech360.crmtech360_backend.service.StockAlertService;
import com.crmtech360.crmtech360_backend.exception.ResourceNotFoundException; // Importar la excepción personalizada

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement; // Para documentar la seguridad
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Para asegurar los endpoints
import org.springframework.security.core.Authentication; // Para obtener información del usuario autenticado
import org.springframework.security.core.context.SecurityContextHolder; // Para acceder al contexto de seguridad
import org.springframework.security.core.userdetails.UserDetails; // Para obtener detalles del usuario
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de alertas de stock y la configuración de umbrales.
 * Expone endpoints para obtener alertas activas, y para actualizar los umbrales
 * mínimos de stock para insumos y productos terminados.
 */
@RestController
@RequestMapping("/api/v1/stock-alerts")
@Tag(name = "Stock Alerts Management", description = "APIs para la gestión de alertas de stock y configuración de umbrales.")
@SecurityRequirement(name = "bearerAuth") // Aplica autenticación JWT a todos los endpoints en este controlador
public class StockAlertController {

    private static final Logger logger = LoggerFactory.getLogger(StockAlertController.class);

    private final StockAlertService stockAlertService;

    /**
     * Constructor para la inyección de dependencias del servicio de alertas de stock.
     *
     * @param stockAlertService El servicio de alertas de stock.
     */
    @Autowired
    public StockAlertController(StockAlertService stockAlertService) {
        this.stockAlertService = stockAlertService;
    }

    /**
     * Endpoint para obtener todas las alertas de stock activas.
     * Requiere que el usuario autenticado tenga el rol 'GERENTE', 'ADMINISTRADOR' o 'OPERARIO'.
     *
     * @return ResponseEntity con una lista de StockAlertDTOs.
     */
    @Operation(summary = "Obtener todas las alertas de stock activas.",
            description = "Recupera una lista de ítems cuyo stock está por debajo del umbral configurado. " +
                    "Requiere rol GERENTE, ADMINISTRADOR u OPERARIO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alertas recuperadas exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StockAlertDTO.class))),
            @ApiResponse(responseCode = "401", description = "No Autorizado - Se requiere token JWT o token inválido.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Prohibido - El usuario no tiene los permisos necesarios.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    // CORRECCIÓN: Se añade "/active" para que coincida con la llamada del frontend.
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR', 'OPERARIO')")
    public ResponseEntity<List<StockAlertDTO>> getActiveAlerts() {
        logger.info("Solicitud para obtener todas las alertas de stock activas.");
        List<StockAlertDTO> alerts = stockAlertService.getActiveAlerts();
        return ResponseEntity.ok(alerts);
    }

    /**
     * Endpoint para actualizar el umbral mínimo de stock para un insumo específico.
     * Requiere que el usuario autenticado tenga el rol 'GERENTE' o 'ADMINISTRADOR'.
     *
     * @param idInsumo El ID del insumo cuyo umbral se actualizará.
     * @param thresholdDTO DTO que contiene el nuevo valor del umbral.
     * @return ResponseEntity con un mensaje de éxito.
     */
    @Operation(summary = "Actualizar el umbral de stock para un insumo.",
            description = "Permite configurar el nivel mínimo de stock para un insumo específico. " +
                    "Requiere rol GERENTE o ADMINISTRADOR.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Umbral actualizado exitosamente.",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Umbral para insumo ID 123 actualizado a 20.00"))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej. umbral negativo).",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No Autorizado."),
            @ApiResponse(responseCode = "403", description = "Prohibido."),
            @ApiResponse(responseCode = "404", description = "Insumo no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PutMapping("/threshold/insumo/{idInsumo}")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public ResponseEntity<String> updateInsumoThreshold(
            @Parameter(description = "ID del insumo", required = true) @PathVariable Integer idInsumo,
            @Valid @RequestBody UpdateStockThresholdDTO thresholdDTO) {
        logger.info("Solicitud para actualizar umbral de insumo ID {}: {}", idInsumo, thresholdDTO.getNuevoUmbral());
        try {
            stockAlertService.updateInsumoThreshold(idInsumo, thresholdDTO.getNuevoUmbral());
            return ResponseEntity.ok("Umbral para insumo ID " + idInsumo + " actualizado a " + thresholdDTO.getNuevoUmbral());
        } catch (ResourceNotFoundException e) {
            logger.warn("Error al actualizar umbral de insumo ID {}: {}", idInsumo, e.getMessage());
            throw e; // Spring Boot manejará esta excepción con un @ControllerAdvice si está configurado
        }
    }

    /**
     * Endpoint para actualizar el umbral mínimo de stock para un producto terminado específico.
     * Requiere que el usuario autenticado tenga el rol 'GERENTE' o 'ADMINISTRADOR'.
     *
     * @param idProducto El ID del producto cuyo umbral se actualizará.
     * @param thresholdDTO DTO que contiene el nuevo valor del umbral.
     * @return ResponseEntity con un mensaje de éxito.
     */
    @Operation(summary = "Actualizar el umbral de stock para un producto terminado.",
            description = "Permite configurar el nivel mínimo de stock para un producto terminado específico. " +
                    "Requiere rol GERENTE o ADMINISTRADOR.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Umbral actualizado exitosamente.",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Umbral para producto ID 456 actualizado a 15.00"))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej. umbral negativo).",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No Autorizado."),
            @ApiResponse(responseCode = "403", description = "Prohibido."),
            @ApiResponse(responseCode = "404", description = "Producto terminado no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PutMapping("/threshold/producto/{idProducto}")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public ResponseEntity<String> updateProductoThreshold(
            @Parameter(description = "ID del producto terminado", required = true) @PathVariable Integer idProducto,
            @Valid @RequestBody UpdateStockThresholdDTO thresholdDTO) {
        logger.info("Solicitud para actualizar umbral de producto ID {}: {}", idProducto, thresholdDTO.getNuevoUmbral());
        try {
            stockAlertService.updateProductoThreshold(idProducto, thresholdDTO.getNuevoUmbral());
            return ResponseEntity.ok("Umbral para producto terminado ID " + idProducto + " actualizado a " + thresholdDTO.getNuevoUmbral());
        } catch (ResourceNotFoundException e) {
            logger.warn("Error al actualizar umbral de producto ID {}: {}", idProducto, e.getMessage());
            throw e;
        }
    }

    /**
     * Endpoint para marcar una alerta de stock como "Vista".
     * Requiere que el usuario autenticado tenga el rol 'GERENTE', 'ADMINISTRADOR' o 'OPERARIO'.
     *
     * @param idAlerta El ID de la alerta a marcar como vista.
     * @return ResponseEntity con estado 200 OK.
     */
    @Operation(summary = "Marcar una alerta de stock como vista.",
            description = "Cambia el estado de una alerta a 'Vista'. " +
                    "Requiere rol GERENTE, ADMINISTRADOR u OPERARIO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alerta marcada como vista exitosamente."),
            @ApiResponse(responseCode = "401", description = "No Autorizado."),
            @ApiResponse(responseCode = "403", description = "Prohibido."),
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
            // Asumiendo que tu UserDetails personalizado (si lo tienes) o la implementación
            // estándar de Spring Security puede darte el ID del usuario.
            // Si tu UserDetails tiene un método getId(), lo usarías aquí.
            // Por ahora, se usa un valor de ejemplo o se deja null si no se puede obtener.
            // Ejemplo: if (authentication.getPrincipal() instanceof CustomUserDetails) {
            //              userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
            //          }
            logger.warn("No se pudo obtener el ID del usuario autenticado para marcar la alerta como vista. Usando null.");
        }

        try {
            stockAlertService.markAlertAsViewed(idAlerta, userId); // Pasa el ID del usuario si lo obtienes
            logger.info("Alerta ID {} marcada como 'Vista'.", idAlerta);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            logger.warn("Error al marcar alerta ID {} como vista: {}", idAlerta, e.getMessage());
            throw e;
        }
    }

    /**
     * Endpoint para marcar una alerta de stock como "Resuelta".
     * Requiere que el usuario autenticado tenga el rol 'GERENTE', 'ADMINISTRADOR' o 'OPERARIO'.
     *
     * @param idAlerta El ID de la alerta a marcar como resuelta.
     * @return ResponseEntity con estado 200 OK.
     */
    @Operation(summary = "Marcar una alerta de stock como resuelta.",
            description = "Cambia el estado de una alerta a 'Resuelta'. " +
                    "Requiere rol GERENTE, ADMINISTRADOR u OPERARIO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alerta marcada como resuelta exitosamente."),
            @ApiResponse(responseCode = "401", description = "No Autorizado."),
            @ApiResponse(responseCode = "403", description = "Prohibido."),
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
            // Similar al método `markAlertAsViewed`, obtén el ID del usuario autenticado.
            logger.warn("No se pudo obtener el ID del usuario autenticado para marcar la alerta como resuelta. Usando null.");
        }

        try {
            stockAlertService.markAlertAsResolved(idAlerta, userId); // Pasa el ID del usuario si lo obtienes
            logger.info("Alerta ID {} marcada como 'Resuelta'.", idAlerta);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            logger.warn("Error al marcar alerta ID {} como resuelta: {}", idAlerta, e.getMessage());
            throw e;
        }
    }
}
