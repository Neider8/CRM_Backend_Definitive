package com.crmtech360.crmtech360_backend.repository;

import com.crmtech360.crmtech360_backend.entity.AlertasStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad AlertasStock.
 * Proporciona métodos para operaciones CRUD y consultas personalizadas
 * para la gestión de alertas de stock.
 */
@Repository
public interface AlertasStockRepository extends JpaRepository<AlertasStock, Integer> {

    /**
     * Busca alertas de stock por su estado.
     *
     * @param estadoAlerta El estado de la alerta (ej. 'Nueva', 'Vista', 'Resuelta').
     * @return Una lista de alertas que coinciden con el estado dado.
     */
    List<AlertasStock> findByEstadoAlerta(String estadoAlerta);

    /**
     * Busca alertas de stock cuyo estado se encuentre en una lista de estados.
     *
     * @param estados Una lista de estados a buscar (ej. ['Nueva', 'Vista']).
     * @return Una lista de alertas que coinciden con alguno de los estados dados.
     */
    List<AlertasStock> findByEstadoAlertaIn(List<String> estados);

    /**
     * Busca alertas de stock por tipo de ítem y ID de ítem.
     *
     * @param tipoItem El tipo de ítem ('Insumo' o 'Producto').
     * @param idItem El ID del ítem.
     * @return Una lista de alertas para el ítem especificado.
     */
    List<AlertasStock> findByTipoItemAndIdItem(String tipoItem, Integer idItem);

    // Puedes añadir más métodos de consulta según tus necesidades.
}

