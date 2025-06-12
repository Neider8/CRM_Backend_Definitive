package com.crmtech360.crmtech360_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * Entidad que representa a un cliente de la empresa.
 * Incluye datos de identificación, contacto y relaciones con contactos y órdenes de venta.
 */
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "tipo_documento", nullable = false, length = 20)
    private String tipoDocumento;

    @Column(name = "numero_documento", nullable = false, unique = true, length = 20)
    private String numeroDocumento;

    @Column(name = "nombre_cliente", nullable = false, length = 255)
    private String nombreCliente;

    @Column(name = "direccion_cliente", length = 255)
    private String direccionCliente;

    @Column(name = "telefono_cliente", length = 20)
    private String telefonoCliente;

    @Column(name = "correo_cliente", length = 100)
    private String correoCliente;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ContactoCliente> contactosCliente;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrdenVenta> ordenesVenta;

    public Cliente() {
    }

    public Cliente(String tipoDocumento, String numeroDocumento, String nombreCliente, String direccionCliente, String telefonoCliente, String correoCliente) {
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.nombreCliente = nombreCliente;
        this.direccionCliente = direccionCliente;
        this.telefonoCliente = telefonoCliente;
        this.correoCliente = correoCliente;
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getDireccionCliente() {
        return direccionCliente;
    }

    public void setDireccionCliente(String direccionCliente) {
        this.direccionCliente = direccionCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getCorreoCliente() {
        return correoCliente;
    }

    public void setCorreoCliente(String correoCliente) {
        this.correoCliente = correoCliente;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Set<ContactoCliente> getContactosCliente() {
        return contactosCliente;
    }

    public void setContactosCliente(Set<ContactoCliente> contactosCliente) {
        this.contactosCliente = contactosCliente;
    }

    public Set<OrdenVenta> getOrdenesVenta() {
        return ordenesVenta;
    }

    public void setOrdenesVenta(Set<OrdenVenta> ordenesVenta) {
        this.ordenesVenta = ordenesVenta;
    }

    /**
     * Dos clientes son iguales si tienen el mismo número de documento o, en su defecto, el mismo ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        if (numeroDocumento != null ? !numeroDocumento.equals(cliente.numeroDocumento) : cliente.numeroDocumento != null) {
            if (numeroDocumento == null || cliente.numeroDocumento == null) {
                return idCliente != null && idCliente.equals(cliente.idCliente);
            }
            return false;
        }
        if (numeroDocumento == null && cliente.numeroDocumento == null) {
            return idCliente != null && idCliente.equals(cliente.idCliente);
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = numeroDocumento != null ? numeroDocumento.hashCode() : 0;
        if (numeroDocumento == null) {
            result = 31 * result + (idCliente != null ? idCliente.hashCode() : 0);
        }
        return result;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "idCliente=" + idCliente +
                ", tipoDocumento='" + tipoDocumento + '\'' +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", nombreCliente='" + nombreCliente + '\'' +
                ", direccionCliente='" + direccionCliente + '\'' +
                ", telefonoCliente='" + telefonoCliente + '\'' +
                ", correoCliente='" + correoCliente + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaActualizacion=" + fechaActualizacion +
                '}';
    }
}