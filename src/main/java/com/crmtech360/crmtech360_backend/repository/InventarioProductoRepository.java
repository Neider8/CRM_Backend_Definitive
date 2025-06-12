package com.crmtech360.crmtech360_backend.repository;

import com.crmtech360.crmtech360_backend.entity.InventarioProducto;
import com.crmtech360.crmtech360_backend.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

/**
 * Repositorio para gestionar el inventario de productos terminados.
 * Permite búsquedas por producto, ubicación y combinaciones de ambos.
 */
@Repository
public interface InventarioProductoRepository extends JpaRepository<InventarioProducto, Integer> {
    List<InventarioProducto> findByProducto_IdProducto(Integer idProducto);
    List<InventarioProducto> findByProductoIdProducto(Integer idProducto);
    List<InventarioProducto> findByUbicacionInventario(String ubicacion);
    Optional<InventarioProducto> findByUbicacionInventarioAndProducto(String ubicacionInventario, Producto producto);
    boolean existsByProducto_IdProductoAndUbicacionInventario(Integer idProducto, String ubicacion);
}