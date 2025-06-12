package com.crmtech360.crmtech360_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que representa un usuario del sistema.
 * Incluye la relación con el empleado, credenciales, rol y estado de habilitación.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado", referencedColumnName = "id_empleado", unique = true)
    private Empleado empleado;

    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 50)
    private String nombreUsuario;

    @Column(name = "contrasena", nullable = false, length = 255)
    private String contrasena; // Debe almacenarse hasheada

    @Column(name = "rol_usuario", nullable = false, length = 20)
    private String rolUsuario;

    @Column(name = "habilitado", nullable = false)
    private boolean habilitado;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public Usuario() {
        this.habilitado = true;
    }

    public Usuario(Empleado empleado, String nombreUsuario, String contrasena, String rolUsuario, boolean habilitado) {
        this.empleado = empleado;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.rolUsuario = rolUsuario;
        this.habilitado = habilitado;
    }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (!this.habilitado) {
            this.habilitado = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRolUsuario() {
        return rolUsuario;
    }

    public void setRolUsuario(String rolUsuario) {
        this.rolUsuario = rolUsuario;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
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

    /**
     * Dos usuarios son iguales si tienen el mismo nombre de usuario o, en su defecto, el mismo ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        if (nombreUsuario != null ? !nombreUsuario.equals(usuario.nombreUsuario) : usuario.nombreUsuario != null) {
            if (nombreUsuario == null || usuario.nombreUsuario == null) {
                return idUsuario != null && idUsuario.equals(usuario.idUsuario);
            }
            return false;
        }
        if (nombreUsuario == null && usuario.nombreUsuario == null) {
            return idUsuario != null && idUsuario.equals(usuario.idUsuario);
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = nombreUsuario != null ? nombreUsuario.hashCode() : 0;
        if (nombreUsuario == null) {
            result = 31 * result + (idUsuario != null ? idUsuario.hashCode() : 0);
        }
        return result;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", empleadoId=" + (empleado != null ? empleado.getIdEmpleado() : "null") +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", contrasena='[PROTECTED]'" +
                ", rolUsuario='" + rolUsuario + '\'' +
                ", habilitado=" + habilitado +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaActualizacion=" + fechaActualizacion +
                '}';
    }
}