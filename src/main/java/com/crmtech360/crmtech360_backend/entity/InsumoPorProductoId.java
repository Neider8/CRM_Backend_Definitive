package com.crmtech360.crmtech360_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * ID compuesto para la relación entre producto e insumo en la lista de materiales (BOM).
 * Incluye el identificador del producto y el del insumo.
 */
@Embeddable
public class InsumoPorProductoId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "id_insumo")
    private Integer idInsumo;

    public InsumoPorProductoId() {
    }

    public InsumoPorProductoId(Integer idProducto, Integer idInsumo) {
        this.idProducto = idProducto;
        this.idInsumo = idInsumo;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getIdInsumo() {
        return idInsumo;
    }

    public void setIdInsumo(Integer idInsumo) {
        this.idInsumo = idInsumo;
    }

    /**
     * Dos IDs son iguales si coinciden producto e insumo.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InsumoPorProductoId that = (InsumoPorProductoId) o;
        return Objects.equals(idProducto, that.idProducto) &&
                Objects.equals(idInsumo, that.idInsumo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProducto, idInsumo);
    }
}