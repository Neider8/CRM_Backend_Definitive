package com.crmtech360.crmtech360_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal; // Importar BigDecimal para el umbral
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * Entidad JPA que representa un registro de inventario de productos terminados en una ubicación específica.
 * Incluye el umbral mínimo de stock para alertas.
 */
@Entity
@Table(name = "inventarioproductos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ubicacion_inventario", "id_producto"})
})
public class InventarioProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario_producto")
    private Integer idInventarioProducto;

    @Column(name = "ubicacion_inventario", nullable = false, length = 100)
    private String ubicacionInventario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto") // ON DELETE CASCADE en la BD
    private Producto producto;

    @Column(name = "cantidad_stock", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer cantidadStock;

    @Column(name = "ultima_actualizacion", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime ultimaActualizacion;

    // Nuevo campo para el umbral mínimo de stock
    @Column(name = "umbral_minimo_stock", nullable = false, precision = 10, scale = 3, columnDefinition = "DECIMAL(10,3) DEFAULT 0")
    private BigDecimal umbralMinimoStock;

    @OneToMany(mappedBy = "inventarioProducto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<MovimientoInventarioProducto> movimientosInventarioProducto;

    /**
     * Constructor por defecto. Inicializa la cantidad de stock y el umbral mínimo a cero.
     */
    public InventarioProducto() {
        this.cantidadStock = 0;
        this.umbralMinimoStock = BigDecimal.ZERO; // Inicializar nuevo campo
    }

    /**
     * Constructor para crear una nueva instancia de InventarioProducto.
     *
     * @param ubicacionInventario La ubicación del inventario.
     * @param producto El producto asociado a este registro de inventario.
     * @param cantidadStock La cantidad inicial de stock.
     * @param umbralMinimoStock El umbral mínimo de stock para el producto en esta ubicación.
     */
    public InventarioProducto(String ubicacionInventario, Producto producto, Integer cantidadStock, BigDecimal umbralMinimoStock) {
        this.ubicacionInventario = ubicacionInventario;
        this.producto = producto;
        this.cantidadStock = (cantidadStock != null) ? cantidadStock : 0;
        this.umbralMinimoStock = (umbralMinimoStock != null) ? umbralMinimoStock : BigDecimal.ZERO; // Asignar nuevo campo
    }

    /**
     * Método que se ejecuta antes de persistir la entidad.
     * Establece la fecha de creación y asegura valores por defecto para stock y umbral.
     */
    @PrePersist
    protected void onCreate() {
        ultimaActualizacion = LocalDateTime.now();
        if (this.cantidadStock == null) {
            this.cantidadStock = 0;
        }
        if (this.umbralMinimoStock == null) { // Asegurar valor por defecto
            this.umbralMinimoStock = BigDecimal.ZERO;
        }
    }

    /**
     * Método que se ejecuta antes de actualizar la entidad.
     * Actualiza la fecha de última actualización.
     */
    @PreUpdate
    protected void onUpdate() {
        ultimaActualizacion = LocalDateTime.now();
    }

    // Getters y Setters

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
     * Compara dos objetos InventarioProducto por su ID o por la combinación de ubicación y producto.
     *
     * @param o El objeto a comparar.
     * @return true si los objetos son iguales, false en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventarioProducto that = (InventarioProducto) o;
        if (idInventarioProducto != null) {
            return idInventarioProducto.equals(that.idInventarioProducto);
        }
        // Si el ID no está, comparar por la combinación única de negocio
        return Objects.equals(ubicacionInventario, that.ubicacionInventario) &&
                Objects.equals(producto, that.producto) &&
                Objects.equals(umbralMinimoStock, that.umbralMinimoStock); // Incluir nuevo campo
    }

    /**
     * Genera un código hash para el objeto InventarioProducto.
     *
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        if (idInventarioProducto != null) {
            return Objects.hash(idInventarioProducto);
        }
        return Objects.hash(ubicacionInventario, producto, umbralMinimoStock); // Incluir nuevo campo
    }

    /**
     * Representación en cadena del objeto InventarioProducto.
     *
     * @return La cadena de representación.
     */
    @Override
    public String toString() {
        return "InventarioProducto{" +
                "idInventarioProducto=" + idInventarioProducto +
                ", ubicacionInventario='" + ubicacionInventario + '\'' +
                ", productoId=" + (producto != null ? producto.getIdProducto() : "null") +
                ", cantidadStock=" + cantidadStock +
                ", umbralMinimoStock=" + umbralMinimoStock + // Incluir nuevo campo
                ", ultimaActualizacion=" + ultimaActualizacion +
                '}';
    }
}