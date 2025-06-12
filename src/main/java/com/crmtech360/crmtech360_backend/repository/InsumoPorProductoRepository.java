package com.crmtech360.crmtech360_backend.repository;

import com.crmtech360.crmtech360_backend.entity.InsumoPorProducto;
import com.crmtech360.crmtech360_backend.entity.InsumoPorProductoId;
import com.crmtech360.crmtech360_backend.entity.Producto;
import com.crmtech360.crmtech360_backend.entity.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar la relación entre productos y sus insumos.
 * Permite búsquedas por producto, insumo y por partes del ID compuesto.
 */
@Repository
public interface InsumoPorProductoRepository extends JpaRepository<InsumoPorProducto, InsumoPorProductoId> {
    List<InsumoPorProducto> findByProducto(Producto producto);
    List<InsumoPorProducto> findByIdIdProducto(Integer idProducto);
    List<InsumoPorProducto> findByInsumo(Insumo insumo);
    List<InsumoPorProducto> findByIdIdInsumo(Integer idInsumo);
    Optional<InsumoPorProducto> findByProductoAndInsumo(Producto producto, Insumo insumo);
}