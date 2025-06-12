package com.crmtech360.crmtech360_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Relación entre un producto terminado y un insumo requerido para su fabricación.
 * Define la cantidad necesaria de cada insumo para un producto específico.
 */
@Entity
@Table(name = "insumosporproducto")
public class InsumoPorProducto {

    @EmbeddedId
    private InsumoPorProductoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idProducto")
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idInsumo")
    @JoinColumn(name = "id_insumo")
    private Insumo insumo;

    @Column(name = "cantidad_requerida", nullable = false, precision = 10, scale = 3)
    private BigDecimal cantidadRequerida;

    public InsumoPorProducto() {
    }

    public InsumoPorProducto(Producto producto, Insumo insumo, BigDecimal cantidadRequerida) {
        this.producto = producto;
        this.insumo = insumo;
        this.cantidadRequerida = cantidadRequerida;
        this.id = new InsumoPorProductoId(producto.getIdProducto(), insumo.getIdInsumo());
    }

    public InsumoPorProductoId getId() {
        return id;
    }

    public void setId(InsumoPorProductoId id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Insumo getInsumo() {
        return insumo;
    }

    public void setInsumo(Insumo insumo) {
        this.insumo = insumo;
    }

    public BigDecimal getCantidadRequerida() {
        return cantidadRequerida;
    }

    public void setCantidadRequerida(BigDecimal cantidadRequerida) {
        this.cantidadRequerida = cantidadRequerida;
    }

    /**
     * Dos relaciones son iguales si tienen el mismo ID compuesto (producto e insumo).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InsumoPorProducto that = (InsumoPorProducto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "InsumoPorProducto{" +
                "id=" + id +
                ", productoId=" + (producto != null ? producto.getIdProducto() : "null") +
                ", insumoId=" + (insumo != null ? insumo.getIdInsumo() : "null") +
                ", cantidadRequerida=" + cantidadRequerida +
                '}';
    }
}