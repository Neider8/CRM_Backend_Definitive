package com.crmtech360.crmtech360_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * Entidad que representa el inventario de un insumo en una ubicación específica.
 * Permite controlar el stock y definir un umbral mínimo para alertas.
 */
@Entity
@Table(
    name = "inventarioinsumos",
    uniqueConstraints = @UniqueConstraint(columnNames = {"ubicacion_inventario", "id_insumo"})
)
public class InventarioInsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario_insumo")
    private Integer idInventarioInsumo;

    @Column(name = "ubicacion_inventario", nullable = false, length = 100)
    private String ubicacionInventario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_insumo")
    private Insumo insumo;

    @Column(
        name = "cantidad_stock",
        nullable = false,
        precision = 10,
        scale = 3,
        columnDefinition = "DECIMAL(10,3) DEFAULT 0"
    )
    private BigDecimal cantidadStock;

    @Column(
        name = "ultima_actualizacion",
        columnDefinition = "TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP"
    )
    private LocalDateTime ultimaActualizacion;

    @Column(
        name = "umbral_minimo_stock",
        nullable = false,
        precision = 10,
        scale = 3,
        columnDefinition = "DECIMAL(10,3) DEFAULT 0"
    )
    private BigDecimal umbralMinimoStock;

    @OneToMany(
        mappedBy = "inventarioInsumo",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private Set<MovimientoInventarioInsumo> movimientosInventarioInsumo;

    public InventarioInsumo() {
        this.cantidadStock = BigDecimal.ZERO;
        this.umbralMinimoStock = BigDecimal.ZERO;
    }

    public InventarioInsumo(
        String ubicacionInventario,
        Insumo insumo,
        BigDecimal cantidadStock,
        BigDecimal umbralMinimoStock
    ) {
        this.ubicacionInventario = ubicacionInventario;
        this.insumo = insumo;
        this.cantidadStock = (cantidadStock != null) ? cantidadStock : BigDecimal.ZERO;
        this.umbralMinimoStock = (umbralMinimoStock != null) ? umbralMinimoStock : BigDecimal.ZERO;
    }

    @PrePersist
    protected void onCreate() {
        ultimaActualizacion = LocalDateTime.now();
        if (this.cantidadStock == null) {
            this.cantidadStock = BigDecimal.ZERO;
        }
        if (this.umbralMinimoStock == null) {
            this.umbralMinimoStock = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        ultimaActualizacion = LocalDateTime.now();
    }

    public Integer getIdInventarioInsumo() {
        return idInventarioInsumo;
    }

    public void setIdInventarioInsumo(Integer idInventarioInsumo) {
        this.idInventarioInsumo = idInventarioInsumo;
    }

    public String getUbicacionInventario() {
        return ubicacionInventario;
    }

    public void setUbicacionInventario(String ubicacionInventario) {
        this.ubicacionInventario = ubicacionInventario;
    }

    public Insumo getInsumo() {
        return insumo;
    }

    public void setInsumo(Insumo insumo) {
        this.insumo = insumo;
    }

    public BigDecimal getCantidadStock() {
        return cantidadStock;
    }

    public void setCantidadStock(BigDecimal cantidadStock) {
        this.cantidadStock = cantidadStock;
    }

    public LocalDateTime getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }

    public BigDecimal getUmbralMinimoStock() {
        return umbralMinimoStock;
    }

    public void setUmbralMinimoStock(BigDecimal umbralMinimoStock) {
        this.umbralMinimoStock = umbralMinimoStock;
    }

    public Set<MovimientoInventarioInsumo> getMovimientosInventarioInsumo() {
        return movimientosInventarioInsumo;
    }

    public void setMovimientosInventarioInsumo(Set<MovimientoInventarioInsumo> movimientosInventarioInsumo) {
        this.movimientosInventarioInsumo = movimientosInventarioInsumo;
    }

    /**
     * Dos inventarios son iguales si tienen el mismo ID o la misma combinación de ubicación e insumo.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventarioInsumo that = (InventarioInsumo) o;
        if (idInventarioInsumo != null) {
            return idInventarioInsumo.equals(that.idInventarioInsumo);
        }
        return Objects.equals(ubicacionInventario, that.ubicacionInventario)
            && Objects.equals(insumo, that.insumo)
            && Objects.equals(umbralMinimoStock, that.umbralMinimoStock);
    }

    @Override
    public int hashCode() {
        if (idInventarioInsumo != null) {
            return Objects.hash(idInventarioInsumo);
        }
        return Objects.hash(ubicacionInventario, insumo, umbralMinimoStock);
    }

    @Override
    public String toString() {
        return "InventarioInsumo{" +
            "idInventarioInsumo=" + idInventarioInsumo +
            ", ubicacionInventario='" + ubicacionInventario + '\'' +
            ", insumoId=" + (insumo != null ? insumo.getIdInsumo() : "null") +
            ", cantidadStock=" + cantidadStock +
            ", umbralMinimoStock=" + umbralMinimoStock +
            ", ultimaActualizacion=" + ultimaActualizacion +
            '}';
    }
}