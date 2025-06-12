package com.crmtech360.crmtech360_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entidad que representa el detalle de una orden de venta.
 * Incluye el producto vendido, la cantidad, el precio unitario y el subtotal.
 */
@Entity
@Table(name = "detallesordenventa")
public class DetalleOrdenVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_orden")
    private Integer idDetalleOrden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orden_venta", nullable = false)
    private OrdenVenta ordenVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(name = "cantidad_producto", nullable = false)
    private Integer cantidadProducto;

    @Column(name = "precio_unitario_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitarioVenta;

    @Column(name = "subtotal_detalle", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotalDetalle;

    public DetalleOrdenVenta() {
    }

    public DetalleOrdenVenta(OrdenVenta ordenVenta, Producto producto, Integer cantidadProducto, BigDecimal precioUnitarioVenta, BigDecimal subtotalDetalle) {
        this.ordenVenta = ordenVenta;
        this.producto = producto;
        this.cantidadProducto = cantidadProducto;
        this.precioUnitarioVenta = precioUnitarioVenta;
        this.subtotalDetalle = subtotalDetalle;
    }

    public Integer getIdDetalleOrden() {
        return idDetalleOrden;
    }

    public void setIdDetalleOrden(Integer idDetalleOrden) {
        this.idDetalleOrden = idDetalleOrden;
    }

    public OrdenVenta getOrdenVenta() {
        return ordenVenta;
    }

    public void setOrdenVenta(OrdenVenta ordenVenta) {
        this.ordenVenta = ordenVenta;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidadProducto() {
        return cantidadProducto;
    }

    public void setCantidadProducto(Integer cantidadProducto) {
        this.cantidadProducto = cantidadProducto;
    }

    public BigDecimal getPrecioUnitarioVenta() {
        return precioUnitarioVenta;
    }

    public void setPrecioUnitarioVenta(BigDecimal precioUnitarioVenta) {
        this.precioUnitarioVenta = precioUnitarioVenta;
    }

    public BigDecimal getSubtotalDetalle() {
        return subtotalDetalle;
    }

    public void setSubtotalDetalle(BigDecimal subtotalDetalle) {
        this.subtotalDetalle = subtotalDetalle;
    }

    /**
     * Dos detalles son iguales si tienen el mismo ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetalleOrdenVenta that = (DetalleOrdenVenta) o;
        return Objects.equals(idDetalleOrden, that.idDetalleOrden);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDetalleOrden);
    }

    @Override
    public String toString() {
        return "DetalleOrdenVenta{" +
                "idDetalleOrden=" + idDetalleOrden +
                ", ordenVentaId=" + (ordenVenta != null ? ordenVenta.getIdOrdenVenta() : "null") +
                ", productoId=" + (producto != null ? producto.getIdProducto() : "null") +
                ", cantidadProducto=" + cantidadProducto +
                ", precioUnitarioVenta=" + precioUnitarioVenta +
                ", subtotalDetalle=" + subtotalDetalle +
                '}';
    }
}