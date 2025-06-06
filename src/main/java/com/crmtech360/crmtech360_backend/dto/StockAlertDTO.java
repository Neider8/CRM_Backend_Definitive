package com.crmtech360.crmtech360_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal; // Importar BigDecimal
import java.time.LocalDateTime;

/**
 * DTO para representar una alerta de stock bajo.
 * Utilizado para enviar información detallada de las alertas al frontend.
 */
@Schema(description = "DTO para representar una alerta de stock bajo.")
public class StockAlertDTO {

    @Schema(description = "ID de la alerta (si se almacena en tabla dedicada).", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idAlerta;

    @Schema(description = "Tipo de ítem (Insumo o Producto).", example = "Insumo", accessMode = Schema.AccessMode.READ_ONLY)
    private String tipoItem;

    @Schema(description = "ID del ítem en bajo stock.", example = "101", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer idItem;

    @Schema(description = "Nombre o descripción del ítem.", example = "Algodón Egipcio Rollo 50m", accessMode = Schema.AccessMode.READ_ONLY)
    private String nombreItem; // Necesitarás obtener este nombre del Insumo/Producto

    @Schema(description = "Mensaje de la alerta.", example = "El stock de Algodón Egipcio Rollo 50m está bajo.", accessMode = Schema.AccessMode.READ_ONLY)
    private String mensaje;

    @Schema(description = "Nivel de stock actual.", example = "5.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal nivelActual; // Usar BigDecimal para consistencia con la entidad

    @Schema(description = "Umbral de stock configurado.", example = "10.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal umbralConfigurado; // Usar BigDecimal para consistencia con la entidad

    @Schema(description = "Fecha de creación de la alerta.", example = "2024-05-20T14:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fechaCreacion;

    @Schema(description = "Estado de la alerta (Nueva, Vista, Resuelta).", example = "Nueva", accessMode = Schema.AccessMode.READ_ONLY)
    private String estadoAlerta; // Si usas la tabla AlertasStock

    // Constructores
    public StockAlertDTO() {
    }

    public StockAlertDTO(Long idAlerta, String tipoItem, Integer idItem, String nombreItem, String mensaje, BigDecimal nivelActual, BigDecimal umbralConfigurado, LocalDateTime fechaCreacion, String estadoAlerta) {
        this.idAlerta = idAlerta;
        this.tipoItem = tipoItem;
        this.idItem = idItem;
        this.nombreItem = nombreItem;
        this.mensaje = mensaje;
        this.nivelActual = nivelActual;
        this.umbralConfigurado = umbralConfigurado;
        this.fechaCreacion = fechaCreacion;
        this.estadoAlerta = estadoAlerta;
    }

    // Getters y Setters
    public Long getIdAlerta() {
        return idAlerta;
    }

    public void setIdAlerta(Long idAlerta) {
        this.idAlerta = idAlerta;
    }

    public String getTipoItem() {
        return tipoItem;
    }

    public void setTipoItem(String tipoItem) {
        this.tipoItem = tipoItem;
    }

    public Integer getIdItem() {
        return idItem;
    }

    public void setIdItem(Integer idItem) {
        this.idItem = idItem;
    }

    public String getNombreItem() {
        return nombreItem;
    }

    public void setNombreItem(String nombreItem) {
        this.nombreItem = nombreItem;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public BigDecimal getNivelActual() {
        return nivelActual;
    }

    public void setNivelActual(BigDecimal nivelActual) {
        this.nivelActual = nivelActual;
    }

    public BigDecimal getUmbralConfigurado() {
        return umbralConfigurado;
    }

    public void setUmbralConfigurado(BigDecimal umbralConfigurado) {
        this.umbralConfigurado = umbralConfigurado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getEstadoAlerta() {
        return estadoAlerta;
    }

    public void setEstadoAlerta(String estadoAlerta) {
        this.estadoAlerta = estadoAlerta;
    }
}
