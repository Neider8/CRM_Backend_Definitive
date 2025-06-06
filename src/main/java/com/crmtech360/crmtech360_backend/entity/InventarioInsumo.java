package com.crmtech360.crmtech360_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal; // Importar BigDecimal
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * Entidad JPA que representa un registro de inventario de insumos en una ubicación específica.
 * Incluye el umbral mínimo de stock para alertas.
 */
@Entity
@Table(name = "inventarioinsumos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ubicacion_inventario", "id_insumo"})
})
public class InventarioInsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario_insumo")
    private Integer idInventarioInsumo;

    @Column(name = "ubicacion_inventario", nullable = false, length = 100)
    private String ubicacionInventario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_insumo") // ON DELETE CASCADE en la BD
    private Insumo insumo;

    @Column(name = "cantidad_stock", nullable = false, precision = 10, scale = 3, columnDefinition = "DECIMAL(10,3) DEFAULT 0")
    private BigDecimal cantidadStock;

    @Column(name = "ultima_actualizacion", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime ultimaActualizacion;

    // Nuevo campo para el umbral mínimo de stock
    @Column(name = "umbral_minimo_stock", nullable = false, precision = 10, scale = 3, columnDefinition = "DECIMAL(10,3) DEFAULT 0")
    private BigDecimal umbralMinimoStock;

    @OneToMany(mappedBy = "inventarioInsumo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<MovimientoInventarioInsumo> movimientosInventarioInsumo;

    /**
     * Constructor por defecto. Inicializa la cantidad de stock y el umbral mínimo a cero.
     */
    public InventarioInsumo() {
        this.cantidadStock = BigDecimal.ZERO;
        this.umbralMinimoStock = BigDecimal.ZERO; // Inicializar nuevo campo
    }

    /**
     * Constructor para crear una nueva instancia de InventarioInsumo.
     *
     * @param ubicacionInventario La ubicación del inventario.
     * @param insumo El insumo asociado a este registro de inventario.
     * @param cantidadStock La cantidad inicial de stock.
     * @param umbralMinimoStock El umbral mínimo de stock para el insumo en esta ubicación.
     */
    public InventarioInsumo(String ubicacionInventario, Insumo insumo, BigDecimal cantidadStock, BigDecimal umbralMinimoStock) {
        this.ubicacionInventario = ubicacionInventario;
        this.insumo = insumo;
        this.cantidadStock = (cantidadStock != null) ? cantidadStock : BigDecimal.ZERO;
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
            this.cantidadStock = BigDecimal.ZERO;
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
     * Compara dos objetos InventarioInsumo por su ID o por la combinación de ubicación e insumo.
     *
     * @param o El objeto a comparar.
     * @return true si los objetos son iguales, false en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventarioInsumo that = (InventarioInsumo) o;
        if (idInventarioInsumo != null) {
            return idInventarioInsumo.equals(that.idInventarioInsumo);
        }
        // Si el ID no está, comparar por la combinación única de negocio
        return Objects.equals(ubicacionInventario, that.ubicacionInventario) &&
                Objects.equals(insumo, that.insumo) &&
                Objects.equals(umbralMinimoStock, that.umbralMinimoStock); // Incluir nuevo campo
    }

    /**
     * Genera un código hash para el objeto InventarioInsumo.
     *
     * @return El código hash.
     */
    @Override
    public int hashCode() {
        if (idInventarioInsumo != null) {
            return Objects.hash(idInventarioInsumo);
        }
        return Objects.hash(ubicacionInventario, insumo, umbralMinimoStock); // Incluir nuevo campo
    }

    /**
     * Representación en cadena del objeto InventarioInsumo.
     *
     * @return La cadena de representación.
     */
    @Override
    public String toString() {
        return "InventarioInsumo{" +
                "idInventarioInsumo=" + idInventarioInsumo +
                ", ubicacionInventario='" + ubicacionInventario + '\'' +
                ", insumoId=" + (insumo != null ? insumo.getIdInsumo() : "null") +
                ", cantidadStock=" + cantidadStock +
                ", umbralMinimoStock=" + umbralMinimoStock + // Incluir nuevo campo
                ", ultimaActualizacion=" + ultimaActualizacion +
                '}';
    }
}

