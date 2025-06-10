package com.crmtech360.crmtech360_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal; // Importar BigDecimal

/**
 * DTO para actualizar el umbral mínimo de stock de un ítem.
 * Utilizado para que los usuarios puedan configurar los umbrales desde el frontend.
 */
@Schema(description = "DTO para actualizar el umbral mínimo de stock de un ítem.")
public class UpdateStockThresholdDTO {

    @Schema(description = "Nuevo umbral mínimo de stock. Debe ser un valor positivo o cero.",
            example = "20.00",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El umbral no puede ser nulo.")
    @PositiveOrZero(message = "El umbral debe ser un valor positivo o cero.")
    private BigDecimal nuevoUmbral; // Usar BigDecimal para consistencia con la entidad

    // Constructores
    public UpdateStockThresholdDTO() {
    }

    public UpdateStockThresholdDTO(BigDecimal nuevoUmbral) {
        this.nuevoUmbral = nuevoUmbral;
    }

    // Getters y Setters
    public BigDecimal getNuevoUmbral() {
        return nuevoUmbral;
    }

    public void setNuevoUmbral(BigDecimal nuevoUmbral) {
        this.nuevoUmbral = nuevoUmbral;
    }
}