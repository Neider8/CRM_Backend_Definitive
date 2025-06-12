package com.crmtech360.crmtech360_backend.repository;

import com.crmtech360.crmtech360_backend.entity.AlertasStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de alertas de stock.
 * Permite consultar alertas por estado, tipo de ítem e identificador.
 */
@Repository
public interface AlertasStockRepository extends JpaRepository<AlertasStock, Integer> {

    List<AlertasStock> findByEstadoAlerta(String estadoAlerta);

    List<AlertasStock> findByEstadoAlertaIn(List<String> estados);

    List<AlertasStock> findByTipoItemAndIdItem(String tipoItem, Integer idItem);

    /**
     * Busca una alerta por tipo de ítem, id y estado.
     */
    Optional<AlertasStock> findByTipoItemAndIdItemAndEstadoAlerta(String tipoItem, Integer idItem, String estadoAlerta);
}