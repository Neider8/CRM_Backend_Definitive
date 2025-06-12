package com.crmtech360.crmtech360_backend.repository;

import com.crmtech360.crmtech360_backend.entity.InventarioInsumo;
import com.crmtech360.crmtech360_backend.entity.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

/**
 * Repositorio para consultar y administrar inventario de insumos.
 * Permite búsquedas por insumo, ubicación y combinaciones de ambos.
 */
@Repository
public interface InventarioInsumoRepository extends JpaRepository<InventarioInsumo, Integer> {

    List<InventarioInsumo> findByInsumo_IdInsumo(Integer idInsumo);

    List<InventarioInsumo> findByInsumoIdInsumo(Integer idInsumo);

    List<InventarioInsumo> findByUbicacionInventario(String ubicacion);

    Optional<InventarioInsumo> findByUbicacionInventarioAndInsumo(String ubicacionInventario, Insumo insumo);

    boolean existsByInsumo_IdInsumoAndUbicacionInventario(Integer idInsumo, String ubicacion);
}