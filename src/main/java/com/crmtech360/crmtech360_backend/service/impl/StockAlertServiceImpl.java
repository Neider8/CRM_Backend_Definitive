package com.crmtech360.crmtech360_backend.service.impl;

import com.crmtech360.crmtech360_backend.dto.StockAlertDTO;
import com.crmtech360.crmtech360_backend.entity.*;
import com.crmtech360.crmtech360_backend.repository.*;
import com.crmtech360.crmtech360_backend.exception.ResourceNotFoundException;
import com.crmtech360.crmtech360_backend.service.StockAlertService;
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

@Service
public class StockAlertServiceImpl implements StockAlertService {

    private static final Logger logger = LoggerFactory.getLogger(StockAlertServiceImpl.class);

    private final InventarioInsumoRepository inventarioInsumoRepository;
    private final InventarioProductoRepository inventarioProductoRepository;
    private final InsumoRepository insumoRepository;
    private final ProductoRepository productoRepository;
    private final AlertasStockRepository alertasStockRepository;

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

    @Override
    @Scheduled(cron = "0 * * * * ?") // Se ejecuta cada minuto
    @Transactional
    public List<StockAlertDTO> checkStockLevelsAndGenerateAlerts() {
        logger.info("Iniciando verificación programada de niveles de stock...");

        // 1. Verificar Insumos
        List<InventarioInsumo> insumosEnInventario = inventarioInsumoRepository.findAll();
        for (InventarioInsumo item : insumosEnInventario) {
            BigDecimal umbral = Optional.ofNullable(item.getUmbralMinimoStock()).orElse(BigDecimal.ZERO);
            BigDecimal cantidad = Optional.ofNullable(item.getCantidadStock()).orElse(BigDecimal.ZERO);

            if (umbral.compareTo(BigDecimal.ZERO) > 0 && cantidad.compareTo(umbral) < 0) {
                boolean existeAlertaActiva = alertasStockRepository.findByTipoItemAndIdItemAndEstadoAlerta(
                        "Insumo", item.getInsumo().getIdInsumo(), "Nueva"
                ).isPresent();

                if (!existeAlertaActiva) {
                    String nombreItem = item.getInsumo().getNombreInsumo();
                    String mensaje = String.format("El stock de %s (%s) está bajo: %s %s (Umbral: %s %s).",
                            nombreItem, item.getUbicacionInventario(), cantidad, item.getInsumo().getUnidadMedidaInsumo(), umbral, item.getInsumo().getUnidadMedidaInsumo());

                    AlertasStock nuevaAlerta = new AlertasStock("Insumo", item.getInsumo().getIdInsumo(), mensaje, cantidad, umbral);
                    alertasStockRepository.save(nuevaAlerta);
                    logger.warn("NUEVA ALERTA DE STOCK GENERADA para Insumo ID {}: Cantidad {} < Umbral {}", item.getInsumo().getIdInsumo(), cantidad, umbral);
                }
            }
        }

        // 2. Verificar Productos Terminados
        List<InventarioProducto> productosEnInventario = inventarioProductoRepository.findAll();
        for (InventarioProducto item : productosEnInventario) {
            BigDecimal umbral = Optional.ofNullable(item.getUmbralMinimoStock()).orElse(BigDecimal.ZERO);
            BigDecimal cantidad = Optional.ofNullable(item.getCantidadStock()).map(BigDecimal::valueOf).orElse(BigDecimal.ZERO);

            if (umbral.compareTo(BigDecimal.ZERO) > 0 && cantidad.compareTo(umbral) < 0) {
                boolean existeAlertaActiva = alertasStockRepository.findByTipoItemAndIdItemAndEstadoAlerta(
                        "Producto", item.getProducto().getIdProducto(), "Nueva"
                ).isPresent();

                if (!existeAlertaActiva) {
                    String nombreItem = item.getProducto().getNombreProducto();
                    String mensaje = String.format("El stock de %s (%s) está bajo: %s %s (Umbral: %s %s).",
                            nombreItem, item.getUbicacionInventario(), cantidad, item.getProducto().getUnidadMedidaProducto(), umbral, item.getProducto().getUnidadMedidaProducto());

                    AlertasStock nuevaAlerta = new AlertasStock("Producto", item.getProducto().getIdProducto(), mensaje, cantidad, umbral);
                    alertasStockRepository.save(nuevaAlerta);
                    logger.warn("NUEVA ALERTA DE STOCK GENERADA para Producto ID {}: Cantidad {} < Umbral {}", item.getProducto().getIdProducto(), cantidad, umbral);
                }
            }
        }
        logger.info("Verificación de niveles de stock completada.");
        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockAlertDTO> getActiveAlerts() {
        logger.info("Recuperando alertas activas ('Nueva', 'Vista') desde la base de datos.");
        return alertasStockRepository.findByEstadoAlertaIn(List.of("Nueva", "Vista"))
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateInsumoThreshold(Integer idInsumo, BigDecimal nuevoUmbral) {
        Insumo insumo = insumoRepository.findById(idInsumo)
                .orElseThrow(() -> new ResourceNotFoundException("Insumo", "id", idInsumo));

        insumo.setStockMinimoInsumo(nuevoUmbral.intValue());
        insumoRepository.save(insumo);
        logger.info("Umbral PREDETERMINADO actualizado para Insumo ID {}: {}", idInsumo, nuevoUmbral.intValue());

        List<InventarioInsumo> items = inventarioInsumoRepository.findByInsumo_IdInsumo(idInsumo);

        if (items.isEmpty()) {
            logger.warn("No se encontraron registros de inventario para el Insumo ID {}. El umbral predeterminado fue actualizado, pero no hay inventarios que modificar.", idInsumo);
        }

        for (InventarioInsumo item : items) {
            item.setUmbralMinimoStock(nuevoUmbral);
            inventarioInsumoRepository.save(item);
            logger.info("Umbral de INVENTARIO actualizado para Insumo ID {} en ubicación '{}': {}", idInsumo, item.getUbicacionInventario(), nuevoUmbral);
        }

        // --- INICIO DE LA LÓGICA AÑADIDA ---
        // Resolver alertas activas existentes para este insumo.
        logger.info("Buscando y resolviendo alertas activas existentes para el Insumo ID {} para reflejar el nuevo umbral.", idInsumo);
        List<AlertasStock> existingAlerts = alertasStockRepository.findByTipoItemAndIdItem("Insumo", idInsumo)
                .stream()
                .filter(a -> "Nueva".equals(a.getEstadoAlerta()) || "Vista".equals(a.getEstadoAlerta()))
                .toList();

        if (!existingAlerts.isEmpty()) {
            for (AlertasStock alerta : existingAlerts) {
                alerta.setEstadoAlerta("Resuelta");
                // Opcional: añadir una nota si tuvieras un campo para ello en la entidad AlertasStock.
                // alerta.setObservaciones("Resuelta automáticamente por actualización de umbral.");
                alertasStockRepository.save(alerta);
                logger.info("Alerta ID {} para Insumo ID {} resuelta automáticamente.", alerta.getIdAlerta(), idInsumo);
            }
        }
        // --- FIN DE LA LÓGICA AÑADIDA ---
    }

    @Override
    @Transactional
    public void updateProductoThreshold(Integer idProducto, BigDecimal nuevoUmbral) {
        List<InventarioProducto> items = inventarioProductoRepository.findByProducto_IdProducto(idProducto);

        if (items.isEmpty()) {
            throw new ResourceNotFoundException("Inventario de Producto", "idProducto", idProducto);
        }
        for (InventarioProducto item : items) {
            item.setUmbralMinimoStock(nuevoUmbral);
            inventarioProductoRepository.save(item);
            logger.info("Umbral de stock actualizado para Producto ID {} en ubicación {}: {}", idProducto, item.getUbicacionInventario(), nuevoUmbral);
        }

        // --- INICIO DE LA LÓGICA AÑADIDA ---
        // Resolver alertas activas existentes para este producto.
        logger.info("Buscando y resolviendo alertas activas existentes para el Producto ID {} para reflejar el nuevo umbral.", idProducto);
        List<AlertasStock> existingAlerts = alertasStockRepository.findByTipoItemAndIdItem("Producto", idProducto)
                .stream()
                .filter(a -> "Nueva".equals(a.getEstadoAlerta()) || "Vista".equals(a.getEstadoAlerta()))
                .toList();

        if (!existingAlerts.isEmpty()) {
            for (AlertasStock alerta : existingAlerts) {
                alerta.setEstadoAlerta("Resuelta");
                alertasStockRepository.save(alerta);
                logger.info("Alerta ID {} para Producto ID {} resuelta automáticamente.", alerta.getIdAlerta(), idProducto);
            }
        }
        // --- FIN DE LA LÓGICA AÑADIDA ---
    }

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