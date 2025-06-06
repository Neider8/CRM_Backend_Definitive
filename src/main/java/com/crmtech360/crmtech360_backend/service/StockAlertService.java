package com.crmtech360.crmtech360_backend.service;

import com.crmtech360.crmtech360_backend.dto.StockAlertDTO;
import com.crmtech360.crmtech360_backend.dto.UpdateStockThresholdDTO; // Importar el DTO para actualizar umbrales
import java.math.BigDecimal;
import java.util.List;

/**
 * Interfaz para el servicio de gestión de alertas de stock.
 * Define las operaciones disponibles para verificar niveles de stock,
 * generar y gestionar alertas, y actualizar umbrales.
 */
public interface StockAlertService {

    /**
     * Verifica los niveles de stock de insumos y productos terminados
     * y genera alertas si el stock actual cae por debajo del umbral mínimo configurado.
     * Este método puede ser llamado periódicamente o después de operaciones de inventario.
     *
     * @return Una lista de StockAlertDTOs que representan las alertas de stock generadas.
     */
    List<StockAlertDTO> checkStockLevelsAndGenerateAlerts();

    /**
     * Obtiene una lista de alertas de stock activas.
     * La implementación puede generar las alertas en tiempo real o recuperarlas de una tabla de persistencia.
     *
     * @return Una lista de StockAlertDTOs.
     */
    List<StockAlertDTO> getActiveAlerts();

    /**
     * Actualiza el umbral mínimo de stock para un insumo específico.
     *
     * @param idInsumo El ID del insumo cuyo umbral se actualizará.
     * @param nuevoUmbral El nuevo valor del umbral mínimo de stock.
     */
    void updateInsumoThreshold(Integer idInsumo, BigDecimal nuevoUmbral);

    /**
     * Actualiza el umbral mínimo de stock para un producto terminado específico.
     *
     * @param idProducto El ID del producto cuyo umbral se actualizará.
     * @param nuevoUmbral El nuevo valor del umbral mínimo de stock.
     */
    void updateProductoThreshold(Integer idProducto, BigDecimal nuevoUmbral);

    /**
     * Marca una alerta de stock como "Vista".
     *
     * @param idAlerta El ID de la alerta a marcar.
     * @param idUsuarioVista El ID del usuario que vio la alerta.
     */
    void markAlertAsViewed(Long idAlerta, Integer idUsuarioVista);

    /**
     * Marca una alerta de stock como "Resuelta".
     *
     * @param idAlerta El ID de la alerta a marcar.
     * @param idUsuarioResuelve El ID del usuario que resolvió la alerta.
     */
    void markAlertAsResolved(Long idAlerta, Integer idUsuarioResuelve);
}
