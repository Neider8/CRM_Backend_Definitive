package com.crmtech360.crmtech360_backend.repository;

import com.crmtech360.crmtech360_backend.entity.AlertasStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // <-- AÑADE ESTE IMPORT

/**
 * Repositorio JPA para la entidad AlertasStock.
 * Proporciona métodos para operaciones CRUD y consultas personalizadas
 * para la gestión de alertas de stock.
 */
@Repository
public interface AlertasStockRepository extends JpaRepository<AlertasStock, Integer> {

    List<AlertasStock> findByEstadoAlerta(String estadoAlerta);

    List<AlertasStock> findByEstadoAlertaIn(List<String> estados);

    List<AlertasStock> findByTipoItemAndIdItem(String tipoItem, Integer idItem);

    // --- MÉTODO NUEVO ---
    /**
     * Busca una alerta activa por tipo de item, id de item y estado.
     * Útil para evitar crear alertas duplicadas si ya existe una 'Nueva'.
     */
    Optional<AlertasStock> findByTipoItemAndIdItemAndEstadoAlerta(String tipoItem, Integer idItem, String estadoAlerta);
}