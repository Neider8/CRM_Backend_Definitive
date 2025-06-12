package com.crmtech360.crmtech360_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * Entidad que representa el inventario de un producto terminado en una ubicación específica.
 * Permite controlar el stock y definir un umbral mínimo para alertas.
 */
@Entity
@Table(
    name = "inventarioproductos",
    uniqueConstraints = @UniqueConstraint(columnNames = {"ubicacion_inventario", "id_producto"})
)
public class InventarioProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario_producto")
    private Integer idInventarioProducto;

    @Column(name = "ubicacion_inventario", nullable = false, length = 100)
    private String ubicacionInventario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @Column(name = "cantidad_stock", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer cantidadStock;

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
        mappedBy = "inventarioProducto",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private Set<MovimientoInventarioProducto> movimientosInventarioProducto;

    public InventarioProducto() {
        this.cantidadStock = 0;
        this.umbralMinimoStock = BigDecimal.ZERO;
    }

    public InventarioProducto(String ubicacionInventario, Producto producto, Integer cantidadStock, BigDecimal umbralMinimoStock) {
        this.ubicacionInventario = ubicacionInventario;
        this.producto = producto;
        this.cantidadStock = (cantidadStock != null) ? cantidadStock : 0;
        this.umbralMinimoStock = (umbralMinimoStock != null) ? umbralMinimoStock : BigDecimal.ZERO;
    }

    @PrePersist
    protected void onCreate() {
        ultimaActualizacion = LocalDateTime.now();
        if (this.cantidadStock == null) {
            this.cantidadStock = 0;
        }
        if (this.umbralMinimoStock == null) {
            this.umbralMinimoStock = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        ultimaActualizacion = LocalDateTime.now();
    }

    public Integer getIdInventarioProducto() {
        return idInventarioProducto;
    }

    public void setIdInventarioProducto(Integer idInventarioProducto) {
        this.idInventarioProducto = idInventarioProducto;
    }

    public String getUbicacionInventario() {
        return ubicacionInventario;
    }

    public void setUbicacionInventario(String ubicacionInventario) {
        this.ubicacionInventario = ubicacionInventario;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidadStock() {
        return cantidadStock;
    }

    public void setCantidadStock(Integer cantidadStock) {
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

    public Set<MovimientoInventarioProducto> getMovimientosInventarioProducto() {
        return movimientosInventarioProducto;
    }

    public void setMovimientosInventarioProducto(Set<MovimientoInventarioProducto> movimientosInventarioProducto) {
        this.movimientosInventarioProducto = movimientosInventarioProducto;
    }

    /**
     * Dos inventarios de producto son iguales si tienen el mismo ID o la misma combinación de ubicación y producto.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventarioProducto that = (InventarioProducto) o;
        if (idInventarioProducto != null) {
            return idInventarioProducto.equals(that.idInventarioProducto);
        }
        return Objects.equals(ubicacionInventario, that.ubicacionInventario)
                && Objects.equals(producto, that.producto)
                && Objects.equals(umbralMinimoStock, that.umbralMinimoStock);
    }

    @Override
    public int hashCode() {
        if (idInventarioProducto != null) {
            return Objects.hash(idInventarioProducto);
        }
        return Objects.hash(ubicacionInventario, producto, umbralMinimoStock);
    }

    @Override
    public String toString() {
        return "InventarioProducto{" +
                "idInventarioProducto=" + idInventarioProducto +
                ", ubicacionInventario='" + ubicacionInventario + '\'' +
                ", productoId=" + (producto != null ? producto.getIdProducto() : "null") +
                ", cantidadStock=" + cantidadStock +
                ", umbralMinimoStock=" + umbralMinimoStock +
                ", ultimaActualizacion=" + ultimaActualizacion +
                '}';
    }
}