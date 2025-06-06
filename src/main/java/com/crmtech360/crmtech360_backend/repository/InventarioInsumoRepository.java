package com.crmtech360.crmtech360_backend.repository;

import com.crmtech360.crmtech360_backend.entity.InventarioInsumo;
import com.crmtech360.crmtech360_backend.entity.Insumo; // Importar la entidad Insumo
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Se mantiene por si se usa findById u otros Optional
import java.util.List; // Importante: para los métodos que devuelven listas

/**
 * Repositorio JPA para la entidad InventarioInsumo.
 * Proporciona métodos para operaciones CRUD y consultas personalizadas
 * relacionadas con el inventario de insumos.
 */
@Repository
public interface InventarioInsumoRepository extends JpaRepository<InventarioInsumo, Integer> {

    /**
     * Busca un registro de inventario de insumo por el ID del insumo asociado.
     * (Versión con underscore para claridad).
     * Cambiado a List porque un insumo puede estar en múltiples registros de inventario.
     *
     * @param idInsumo El ID del insumo.
     * @return Una lista de InventarioInsumo que contienen el insumo si se encuentra, o una lista vacía.
     */
    List<InventarioInsumo> findByInsumo_IdInsumo(Integer idInsumo);

    /**
     * Busca un registro de inventario de insumo por el ID del insumo asociado.
     * (Versión sin underscore, para coincidir con posibles llamadas existentes).
     * Cambiado a List porque un insumo puede estar en múltiples registros de inventario.
     *
     * @param idInsumo El ID del insumo.
     * @return Una lista de InventarioInsumo que contienen el insumo si se encuentra, o una lista vacía.
     */
    List<InventarioInsumo> findByInsumoIdInsumo(Integer idInsumo);


    /**
     * Busca registros de inventario de insumos por su ubicación.
     *
     * @param ubicacion La ubicación del inventario.
     * @return Una lista de InventarioInsumo que coinciden con la ubicación.
     */
    List<InventarioInsumo> findByUbicacionInventario(String ubicacion);

    /**
     * Busca un registro de inventario de insumo por la ubicación y el objeto Insumo.
     *
     * @param ubicacionInventario La ubicación del inventario.
     * @param insumo El objeto Insumo.
     * @return Un Optional que contiene el InventarioInsumo si se encuentra, o vacío si no.
     */
    Optional<InventarioInsumo> findByUbicacionInventarioAndInsumo(String ubicacionInventario, Insumo insumo);

    /**
     * Verifica si existe un registro de inventario para un insumo y ubicación específicos.
     *
     * @param idInsumo El ID del insumo.
     * @param ubicacion La ubicación del inventario.
     * @return true si existe, false en caso contrario.
     */
    boolean existsByInsumo_IdInsumoAndUbicacionInventario(Integer idInsumo, String ubicacion);

    // Puedes añadir más métodos de consulta según tus necesidades.
}
