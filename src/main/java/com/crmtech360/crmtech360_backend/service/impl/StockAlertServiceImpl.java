package com.crmtech360.crmtech360_backend.service.impl;

import com.crmtech360.crmtech360_backend.dto.StockAlertDTO;
import com.crmtech360.crmtech360_backend.entity.InventarioInsumo;
import com.crmtech360.crmtech360_backend.entity.InventarioProducto;
import com.crmtech360.crmtech360_backend.entity.Insumo;
import com.crmtech360.crmtech360_backend.entity.Producto;
import com.crmtech360.crmtech360_backend.entity.AlertasStock; // Importar la entidad AlertasStock si la vas a usar

// Importar repositorios necesarios
import com.crmtech360.crmtech360_backend.repository.InventarioInsumoRepository;
import com.crmtech360.crmtech360_backend.repository.InventarioProductoRepository;
import com.crmtech360.crmtech360_backend.repository.InsumoRepository;
import com.crmtech360.crmtech360_backend.repository.ProductoRepository;
import com.crmtech360.crmtech360_backend.repository.AlertasStockRepository; // Si usas la tabla AlertasStock
import com.crmtech360.crmtech360_backend.exception.ResourceNotFoundException; // Para manejar excepciones

import com.crmtech360.crmtech360_backend.service.StockAlertService; // Importar la interfaz

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de alertas de stock.
 * Contiene la lógica para verificar niveles de stock, generar y gestionar alertas,
 * y actualizar umbrales.
 */
@Service
public class StockAlertServiceImpl implements StockAlertService { // Implementa la interfaz

    private static final Logger logger = LoggerFactory.getLogger(StockAlertServiceImpl.class);

    private final InventarioInsumoRepository inventarioInsumoRepository;
    private final InventarioProductoRepository inventarioProductoRepository;
    private final InsumoRepository insumoRepository;
    private final ProductoRepository productoRepository;
    private final AlertasStockRepository alertasStockRepository;

    /**
     * Constructor para la inyección de dependencias.
     *
     * @param inventarioInsumoRepository Repositorio para InventarioInsumo.
     * @param inventarioProductoRepository Repositorio para InventarioProducto.
     * @param insumoRepository Repositorio para Insumo.
     * @param productoRepository Repositorio para Producto.
     * @param alertasStockRepository Repositorio para AlertasStock.
     */
    @Autowired
    public StockAlertServiceImpl(InventarioInsumoRepository inventarioInsumoRepository,
                                 InventarioProductoRepository inventarioProductoRepository,
                                 InsumoRepository insumoRepository,
                                 ProductoRepository productoRepository,
                                 AlertasStockRepository alertasStockRepository) {
        this.inventarioInsumoRepository = inventarioInsumoRepository;
        this.inventarioProductoRepository = inventarioProductoRepository;
        this.insumoRepository = insumoRepository;
        this.productoRepository = productoRepository;
        this.alertasStockRepository = alertasStockRepository;
    }

    /**
     * Verifica periódicamente los niveles de stock de insumos y productos terminados
     * y genera alertas si el stock actual cae por debajo del umbral mínimo configurado.
     * Este método puede ser programado para ejecutarse automáticamente.
     *
     * @return Una lista de StockAlertDTOs que representan las alertas de stock generadas.
     */
    // Puedes descomentar y configurar @Scheduled para ejecución periódica
    // @Scheduled(cron = "0 0 * * * ?") // Ejemplo: se ejecuta cada hora
    @Override
    @Transactional
    public List<StockAlertDTO> checkStockLevelsAndGenerateAlerts() {
        List<StockAlertDTO> alerts = new ArrayList<>();
        logger.info("Iniciando verificación de niveles de stock...");

        // 1. Verificar Insumos
        List<InventarioInsumo> insumosEnInventario = inventarioInsumoRepository.findAll();
        for (InventarioInsumo item : insumosEnInventario) {
            BigDecimal umbral = Optional.ofNullable(item.getUmbralMinimoStock()).orElse(BigDecimal.ZERO);
            BigDecimal cantidad = Optional.ofNullable(item.getCantidadStock()).orElse(BigDecimal.ZERO);

            if (umbral.compareTo(BigDecimal.ZERO) > 0 && cantidad.compareTo(umbral) < 0) {
                String nombreItem = item.getInsumo() != null ? item.getInsumo().getNombreInsumo() : "Insumo ID: " + item.getInsumo().getIdInsumo();

                StockAlertDTO alert = new StockAlertDTO();
                alert.setTipoItem("Insumo");
                alert.setIdItem(item.getInsumo().getIdInsumo());
                alert.setNombreItem(nombreItem);
                alert.setMensaje("El stock de " + nombreItem + " (" + item.getUbicacionInventario() + ") está bajo: " + cantidad + " " + item.getInsumo().getUnidadMedidaInsumo() + " (Umbral: " + umbral + " " + item.getInsumo().getUnidadMedidaInsumo() + ").");
                alert.setNivelActual(cantidad);
                alert.setUmbralConfigurado(umbral);
                alert.setFechaCreacion(LocalDateTime.now());
                alert.setEstadoAlerta("Nueva");

                alerts.add(alert);

                // Opcional: Guardar la alerta en la tabla AlertasStock
                // Descomenta este bloque si has creado la entidad AlertasStock y su repositorio
                /*
                AlertasStock nuevaAlerta = new AlertasStock();
                nuevaAlerta.setTipoItem("Insumo");
                nuevaAlerta.setIdItem(item.getInsumo().getIdInsumo());
                nuevaAlerta.setMensaje(alert.getMensaje());
                nuevaAlerta.setNivelActual(cantidad);
                nuevaAlerta.setUmbralConfigurado(umbral);
                nuevaAlerta.setFechaCreacion(LocalDateTime.now());
                nuevaAlerta.setEstadoAlerta("Nueva");
                alertasStockRepository.save(nuevaAlerta);
                */

                logger.warn("Alerta de stock bajo para Insumo ID {}: Cantidad {} < Umbral {}",
                        item.getInsumo().getIdInsumo(), cantidad, umbral);
            }
        }

        // 2. Verificar Productos Terminados
        List<InventarioProducto> productosEnInventario = inventarioProductoRepository.findAll();
        for (InventarioProducto item : productosEnInventario) {
            BigDecimal umbral = Optional.ofNullable(item.getUmbralMinimoStock()).orElse(BigDecimal.ZERO);
            BigDecimal cantidad = Optional.ofNullable(item.getCantidadStock()).map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);

            if (umbral.compareTo(BigDecimal.ZERO) > 0 && cantidad.compareTo(umbral) < 0) {
                String nombreItem = item.getProducto() != null ? item.getProducto().getNombreProducto() : "Producto ID: " + item.getProducto().getIdProducto();

                StockAlertDTO alert = new StockAlertDTO();
                alert.setTipoItem("Producto");
                alert.setIdItem(item.getProducto().getIdProducto());
                alert.setNombreItem(nombreItem);
                alert.setMensaje("El stock de " + nombreItem + " (" + item.getUbicacionInventario() + ") está bajo: " + cantidad + " " + item.getProducto().getUnidadMedidaProducto() + " (Umbral: " + umbral + " " + item.getProducto().getUnidadMedidaProducto() + ").");
                alert.setNivelActual(cantidad);
                alert.setUmbralConfigurado(umbral);
                alert.setFechaCreacion(LocalDateTime.now());
                alert.setEstadoAlerta("Nueva");

                alerts.add(alert);

                // Opcional: Guardar la alerta en la tabla AlertasStock
                // Descomenta este bloque si has creado la entidad AlertasStock y su repositorio
                /*
                AlertasStock nuevaAlerta = new AlertasStock();
                nuevaAlerta.setTipoItem("Producto");
                nuevaAlerta.setIdItem(item.getProducto().getIdProducto());
                nuevaAlerta.setMensaje(alert.getMensaje());
                nuevaAlerta.setNivelActual(cantidad);
                nuevaAlerta.setUmbralConfigurado(umbral);
                nuevaAlerta.setFechaCreacion(LocalDateTime.now());
                nuevaAlerta.setEstadoAlerta("Nueva");
                alertasStockRepository.save(nuevaAlerta);
                */

                logger.warn("Alerta de stock bajo para Producto ID {}: Cantidad {} < Umbral {}",
                        item.getProducto().getIdProducto(), cantidad, umbral);
            }
        }
        logger.info("Verificación de niveles de stock completada. {} alertas generadas.", alerts.size());
        return alerts;
    }

    /**
     * Obtiene una lista de alertas de stock activas.
     * Si la tabla AlertasStock se usa, recupera las alertas de allí.
     * De lo contrario, genera las alertas en tiempo real.
     *
     * @return Una lista de StockAlertDTOs.
     */
    @Override
    @Transactional(readOnly = true)
    public List<StockAlertDTO> getActiveAlerts() {
        // Opción 1: Generar alertas en tiempo real (si no se usa la tabla AlertasStock para persistencia)
        return checkStockLevelsAndGenerateAlerts();

        // Opción 2: Si usas la tabla AlertasStock para persistencia de alertas
        /*
        return alertasStockRepository.findByEstadoAlertaIn(List.of("Nueva", "Vista"))
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        */
    }

    /**
     * Actualiza el umbral mínimo de stock para un insumo específico.
     *
     * @param idInsumo El ID del insumo cuyo umbral se actualizará.
     * @param nuevoUmbral El nuevo valor del umbral mínimo de stock.
     * @throws ResourceNotFoundException si el registro de inventario del insumo no se encuentra.
     */
    @Override
    @Transactional
    public void updateInsumoThreshold(Integer idInsumo, BigDecimal nuevoUmbral) {
        // Ahora findByInsumo_IdInsumo devuelve una List, no un Optional
        List<InventarioInsumo> items = inventarioInsumoRepository.findByInsumo_IdInsumo(idInsumo);

        if (items.isEmpty()) {
            throw new ResourceNotFoundException("Inventario de Insumo", "idInsumo", idInsumo);
        }

        // Actualizar el umbral para todos los registros de inventario asociados a este insumo
        for (InventarioInsumo item : items) {
            item.setUmbralMinimoStock(nuevoUmbral);
            inventarioInsumoRepository.save(item); // Guardar cada item actualizado
            logger.info("Umbral de stock actualizado para Insumo ID {} en ubicación {}: {}", idInsumo, item.getUbicacionInventario(), nuevoUmbral);
        }
    }

    /**
     * Actualiza el umbral mínimo de stock para un producto terminado específico.
     *
     * @param idProducto El ID del producto cuyo umbral se actualizará.
     * @param nuevoUmbral El nuevo valor del umbral mínimo de stock.
     * @throws ResourceNotFoundException si el registro de inventario del producto no se encuentra.
     */
    @Override
    @Transactional
    public void updateProductoThreshold(Integer idProducto, BigDecimal nuevoUmbral) {
        // Ahora findByProducto_IdProducto devuelve una List, no un Optional
        List<InventarioProducto> items = inventarioProductoRepository.findByProducto_IdProducto(idProducto);

        if (items.isEmpty()) {
            throw new ResourceNotFoundException("Inventario de Producto", "idProducto", idProducto);
        }

        // Actualizar el umbral para todos los registros de inventario asociados a este producto
        for (InventarioProducto item : items) {
            item.setUmbralMinimoStock(nuevoUmbral);
            inventarioProductoRepository.save(item); // Guardar cada item actualizado
            logger.info("Umbral de stock actualizado para Producto ID {} en ubicación {}: {}", idProducto, item.getUbicacionInventario(), nuevoUmbral);
        }
    }

    /**
     * Convierte una entidad AlertasStock a un DTO StockAlertDTO.
     * Este método es útil si decides persistir las alertas en la base de datos.
     *
     * @param alerta La entidad AlertasStock a convertir.
     * @return Un StockAlertDTO.
     */
    private StockAlertDTO convertToDto(AlertasStock alerta) {
        StockAlertDTO dto = new StockAlertDTO();
        dto.setIdAlerta(alerta.getIdAlerta().longValue());
        dto.setTipoItem(alerta.getTipoItem());
        dto.setIdItem(alerta.getIdItem());
        dto.setMensaje(alerta.getMensaje());
        dto.setNivelActual(alerta.getNivelActual());
        dto.setUmbralConfigurado(alerta.getUmbralConfigurado());
        dto.setFechaCreacion(alerta.getFechaCreacion());
        dto.setEstadoAlerta(alerta.getEstadoAlerta());

        if ("Insumo".equals(alerta.getTipoItem())) {
            insumoRepository.findById(alerta.getIdItem())
                    .ifPresent(insumo -> dto.setNombreItem(insumo.getNombreInsumo()));
        } else if ("Producto".equals(alerta.getTipoItem())) {
            productoRepository.findById(alerta.getIdItem())
                    .ifPresent(producto -> dto.setNombreItem(producto.getNombreProducto()));
        }
        return dto;
    }

    /**
     * Marca una alerta de stock como "Vista".
     *
     * @param idAlerta El ID de la alerta a marcar.
     * @param idUsuarioVista El ID del usuario que vio la alerta.
     * @throws ResourceNotFoundException si la alerta no se encuentra.
     */
    @Override
    @Transactional
    public void markAlertAsViewed(Long idAlerta, Integer idUsuarioVista) {
        AlertasStock alerta = alertasStockRepository.findById(idAlerta.intValue())
                .orElseThrow(() -> new ResourceNotFoundException("Alerta de stock", "id", idAlerta));
        alerta.setEstadoAlerta("Vista");
        alerta.setFechaVista(LocalDateTime.now());
        alerta.setIdUsuarioVista(idUsuarioVista);
        alertasStockRepository.save(alerta);
        logger.info("Alerta ID {} marcada como 'Vista' por usuario ID {}", idAlerta, idUsuarioVista);
    }

    /**
     * Marca una alerta de stock como "Resuelta".
     *
     * @param idAlerta El ID de la alerta a marcar.
     * @param idUsuarioResuelve El ID del usuario que resolvió la alerta.
     * @throws ResourceNotFoundException si la alerta no se encuentra.
     */
    @Override
    @Transactional
    public void markAlertAsResolved(Long idAlerta, Integer idUsuarioResuelve) {
        AlertasStock alerta = alertasStockRepository.findById(idAlerta.intValue())
                .orElseThrow(() -> new ResourceNotFoundException("Alerta de stock", "id", idAlerta));
        alerta.setEstadoAlerta("Resuelta");
        alerta.setFechaVista(LocalDateTime.now());
        alerta.setIdUsuarioVista(idUsuarioResuelve);
        alertasStockRepository.save(alerta);
        logger.info("Alerta ID {} marcada como 'Resuelta' por usuario ID {}", idAlerta, idUsuarioResuelve);
    }
}
