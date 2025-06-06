package com.crmtech360.crmtech360_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad JPA que representa una alerta de bajo stock generada por el sistema.
 * Mapea la tabla 'AlertasStock' en la base de datos.
 */
@Entity
@Table(name = "alertasstock") // Asegúrate de que el nombre de la tabla coincida con tu DDL
public class AlertasStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Integer idAlerta; // Coincide con SERIAL PRIMARY KEY en PostgreSQL

    @Column(name = "tipo_item", nullable = false, length = 50)
    private String tipoItem; // 'Insumo' o 'Producto'

    @Column(name = "id_item", nullable = false)
    private Integer idItem; // ID del insumo o producto

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
    private Integer idUsuarioVista; // Quién vio la alerta

    @Column(name = "estado_alerta", nullable = false, length = 20)
    private String estadoAlerta; // 'Nueva', 'Vista', 'Resuelta'

    // Constructor por defecto
    public AlertasStock() {
        this.fechaCreacion = LocalDateTime.now(); // Establecer fecha de creación por defecto
        this.estadoAlerta = "Nueva"; // Establecer estado inicial por defecto
    }

    // Constructor con parámetros para facilitar la creación de alertas
    public AlertasStock(String tipoItem, Integer idItem, String mensaje, BigDecimal nivelActual, BigDecimal umbralConfigurado) {
        this(); // Llama al constructor por defecto para inicializar fechaCreacion y estadoAlerta
        this.tipoItem = tipoItem;
        this.idItem = idItem;
        this.mensaje = mensaje;
        this.nivelActual = nivelActual;
        this.umbralConfigurado = umbralConfigurado;
    }

    // Getters y Setters
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
        if (o == null || getClass() != o.getClass()) return false;
        AlertasStock that = (AlertasStock) o;
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
