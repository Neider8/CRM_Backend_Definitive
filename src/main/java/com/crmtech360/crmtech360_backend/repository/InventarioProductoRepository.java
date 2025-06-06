package com.crmtech360.crmtech360_backend.repository;

import com.crmtech360.crmtech360_backend.entity.InventarioProducto;
import com.crmtech360.crmtech360_backend.entity.Producto; // Importar la entidad Producto
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Se mantiene por si se usa findById u otros Optional
import java.util.List;

/**
 * Repositorio JPA para la entidad InventarioProducto.
 * Proporciona métodos para operaciones CRUD y consultas personalizadas
 * relacionadas con el inventario de productos terminados.
 */
@Repository
public interface InventarioProductoRepository extends JpaRepository<InventarioProducto, Integer> {

    /**
     * Busca un registro de inventario de producto por el ID del producto asociado.
     * (Versión con underscore para claridad, ya debería existir o fue añadida).
     * Cambiado a List porque un producto puede estar en múltiples registros de inventario.
     *
     * @param idProducto El ID del producto.
     * @return Una lista de InventarioProducto que contienen el producto si se encuentra, o una lista vacía.
     */
    List<InventarioProducto> findByProducto_IdProducto(Integer idProducto);

    /**
     * Busca un registro de inventario de producto por el ID del producto asociado.
     * (Versión sin underscore, para coincidir con posibles llamadas existentes).
     * Cambiado a List porque un producto puede estar en múltiples registros de inventario.
     *
     * @param idProducto El ID del producto.
     * @return Una lista de InventarioProducto que contienen el producto si se encuentra, o una lista vacía.
     */
    List<InventarioProducto> findByProductoIdProducto(Integer idProducto);

    /**
     * Busca registros de inventario de productos por su ubicación.
     *
     * @param ubicacion La ubicación del inventario.
     * @return Una lista de InventarioProducto que coinciden con la ubicación.
     */
    List<InventarioProducto> findByUbicacionInventario(String ubicacion);

    /**
     * Busca un registro de inventario de producto por la ubicación y el objeto Producto.
     *
     * @param ubicacionInventario La ubicación del inventario.
     * @param producto El objeto Producto.
     * @return Un Optional que contiene el InventarioProducto si se encuentra, o vacío si no.
     */
    Optional<InventarioProducto> findByUbicacionInventarioAndProducto(String ubicacionInventario, Producto producto);

    /**
     * Verifica si existe un registro de inventario para un producto y ubicación específicos.
     *
     * @param idProducto El ID del producto.
     * @param ubicacion La ubicación del inventario.
     * @return true si existe, false en caso contrario.
     */
    boolean existsByProducto_IdProductoAndUbicacionInventario(Integer idProducto, String ubicacion);

    // Puedes añadir más métodos de consulta según tus necesidades.
}
