package com.crmtech360.crmtech360_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa una alerta de stock bajo para insumos o productos.
 * Incluye información sobre el tipo de ítem, nivel actual, umbral configurado y estado de la alerta.
 */
@Entity
@Table(name = "alertasstock")
public class AlertasStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Integer idAlerta;

    @Column(name = "tipo_item", nullable = false, length = 50)
    private String tipoItem; // Puede ser 'Insumo' o 'Producto'

    @Column(name = "id_item", nullable = false)
    private Integer idItem;

    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "nivel_actual", precision = 10, scale = 3)
    private BigDecimal nivelActual;

    @Column(name = "umbral_configurado", precision = 10, scale = 3)
    private BigDecimal umbralConfigurado;

    @Column(name = "fecha_creacion", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_vista", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime fechaVista;

    @Column(name = "id_usuario_vista")
    private Integer idUsuarioVista;

    @Column(name = "estado_alerta", nullable = false, length = 20)
    private String estadoAlerta; // Ejemplo: 'Nueva', 'Vista', 'Resuelta'

    public AlertasStock() {
        this.fechaCreacion = LocalDateTime.now();
        this.estadoAlerta = "Nueva";
    }

    public AlertasStock(String tipoItem, Integer idItem, String mensaje, BigDecimal nivelActual, BigDecimal umbralConfigurado) {
        this();
        this.tipoItem = tipoItem;
        this.idItem = idItem;
        this.mensaje = mensaje;
        this.nivelActual = nivelActual;
        this.umbralConfigurado = umbralConfigurado;
    }

    public Integer getIdAlerta() {
        return idAlerta;
    }

    public void setIdAlerta(Integer idAlerta) {
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

    public LocalDateTime getFechaVista() {
        return fechaVista;
    }

    public void setFechaVista(LocalDateTime fechaVista) {
        this.fechaVista = fechaVista;
    }

    public Integer getIdUsuarioVista() {
        return idUsuarioVista;
    }

    public void setIdUsuarioVista(Integer idUsuarioVista) {
        this.idUsuarioVista = idUsuarioVista;
    }

    public String getEstadoAlerta() {
        return estadoAlerta;
    }

    public void setEstadoAlerta(String estadoAlerta) {
        this.estadoAlerta = estadoAlerta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlertasStock that)) return false;
        return Objects.equals(idAlerta, that.idAlerta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAlerta);
    }

    @Override
    public String toString() {
        return "AlertasStock{" +
                "idAlerta=" + idAlerta +
                ", tipoItem='" + tipoItem + '\'' +
                ", idItem=" + idItem +
                ", mensaje='" + mensaje + '\'' +
                ", nivelActual=" + nivelActual +
                ", umbralConfigurado=" + umbralConfigurado +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaVista=" + fechaVista +
                ", idUsuarioVista=" + idUsuarioVista +
                ", estadoAlerta='" + estadoAlerta + '\'' +
                '}';
    }
}