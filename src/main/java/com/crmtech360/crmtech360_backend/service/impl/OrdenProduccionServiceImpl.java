// src/main/java/com/crmtech360/crmtech360_backend/service/impl/OrdenProduccionServiceImpl.java

package com.crmtech360.crmtech360_backend.service.impl;

import com.crmtech360.crmtech360_backend.dto.*;
import com.crmtech360.crmtech360_backend.entity.*;
import com.crmtech360.crmtech360_backend.repository.*;
import com.crmtech360.crmtech360_backend.service.OrdenProduccionService;
import com.crmtech360.crmtech360_backend.exception.BadRequestException;
import com.crmtech360.crmtech360_backend.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired; // Asegúrate de tener esta si usas field injection
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrdenProduccionServiceImpl implements OrdenProduccionService {

    private static final Logger log = LoggerFactory.getLogger(OrdenProduccionServiceImpl.class);

    private final OrdenProduccionRepository ordenProduccionRepository;
    private final TareaProduccionRepository tareaProduccionRepository;
    private final OrdenVentaRepository ordenVentaRepository;
    private final EmpleadoRepository empleadoRepository;

    // Nuevas dependencias para la lógica de inventario
    private final ProductoRepository productoRepository;
    private final InsumoPorProductoRepository insumoPorProductoRepository;
    private final InventarioInsumoRepository inventarioInsumoRepository;
    private final MovimientoInventarioInsumoRepository movimientoInsumoRepository;
    private final InventarioProductoRepository inventarioProductoRepository;
    private final MovimientoInventarioProductoRepository movimientoInventarioProductoRepository;


    @Autowired // Constructor actualizado
    public OrdenProduccionServiceImpl(OrdenProduccionRepository ordenProduccionRepository,
                                      TareaProduccionRepository tareaProduccionRepository,
                                      OrdenVentaRepository ordenVentaRepository,
                                      EmpleadoRepository empleadoRepository,
                                      ProductoRepository productoRepository,
                                      InsumoPorProductoRepository insumoPorProductoRepository,
                                      InventarioInsumoRepository inventarioInsumoRepository,
                                      MovimientoInventarioInsumoRepository movimientoInsumoRepository,
                                      InventarioProductoRepository inventarioProductoRepository,
                                      MovimientoInventarioProductoRepository movimientoInventarioProductoRepository) {
        this.ordenProduccionRepository = ordenProduccionRepository;
        this.tareaProduccionRepository = tareaProduccionRepository;
        this.ordenVentaRepository = ordenVentaRepository;
        this.empleadoRepository = empleadoRepository;
        this.productoRepository = productoRepository;
        this.insumoPorProductoRepository = insumoPorProductoRepository;
        this.inventarioInsumoRepository = inventarioInsumoRepository;
        this.movimientoInsumoRepository = movimientoInsumoRepository;
        this.inventarioProductoRepository = inventarioProductoRepository;
        this.movimientoInventarioProductoRepository = movimientoInventarioProductoRepository;
    }

    @Override
    public OrdenProduccionResponseDTO createOrdenProduccion(OrdenProduccionCreateRequestDTO createRequestDTO) {
        log.info("Creando nueva orden de producción para orden de venta ID: {}", createRequestDTO.getIdOrdenVenta());

        OrdenVenta ordenVenta = ordenVentaRepository.findById(createRequestDTO.getIdOrdenVenta())
                .orElseThrow(() -> new ResourceNotFoundException("OrdenVenta", "id", createRequestDTO.getIdOrdenVenta())); // [cite: 8]

        if (!"Confirmada".equals(ordenVenta.getEstadoOrden()) && !"En Producción".equals(ordenVenta.getEstadoOrden())) { // [cite: 9]
            log.warn("Intento de crear orden de producción para orden de venta ID {} con estado {}", ordenVenta.getIdOrdenVenta(), ordenVenta.getEstadoOrden());
            throw new BadRequestException("La orden de venta debe estar en estado 'Confirmada' o 'En Producción' para iniciar producción."); // [cite: 10]
        }

        OrdenProduccion ordenProduccion = new OrdenProduccion();
        ordenProduccion.setOrdenVenta(ordenVenta);
        ordenProduccion.setFechaInicioProduccion(createRequestDTO.getFechaInicioProduccion()); // [cite: 11]
        ordenProduccion.setFechaFinEstimadaProduccion(createRequestDTO.getFechaFinEstimadaProduccion()); // [cite: 12]
        ordenProduccion.setObservacionesProduccion(createRequestDTO.getObservacionesProduccion());
        ordenProduccion.setEstadoProduccion("Pendiente");
        ordenProduccion.setTareasProduccion(new HashSet<>());

        // Si la OV no estaba "En Producción", se actualiza.
        if (!"En Producción".equals(ordenVenta.getEstadoOrden())) { // [cite: 14]
            ordenVenta.setEstadoOrden("En Producción");
            ordenVentaRepository.save(ordenVenta);
        }

        OrdenProduccion savedOrdenProduccion = ordenProduccionRepository.save(ordenProduccion);
        log.info("Orden de producción creada con ID: {}", savedOrdenProduccion.getIdOrdenProduccion());
        return mapToResponseDTO(savedOrdenProduccion); // [cite: 16]
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrdenProduccionResponseDTO> findAllOrdenesProduccion(Pageable pageable) {
        log.info("Buscando todas las órdenes de producción, página: {}, tamaño: {}", pageable.getPageNumber(), pageable.getPageSize());
        return ordenProduccionRepository.findAll(pageable).map(this::mapToResponseDTO); // [cite: 17]
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenProduccionResponseDTO> findOrdenesProduccionByOrdenVentaId(Integer idOrdenVenta) {
        log.info("Buscando órdenes de producción para la orden de venta ID: {}", idOrdenVenta);
        if (!ordenVentaRepository.existsById(idOrdenVenta)) { // [cite: 18]
            throw new ResourceNotFoundException("OrdenVenta", "id", idOrdenVenta);
        }
        List<OrdenProduccion> ordenes = ordenProduccionRepository.findByOrdenVentaIdOrdenVenta(idOrdenVenta); // [cite: 19]
        return ordenes.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenProduccionResponseDTO findOrdenProduccionById(Integer id) {
        log.info("Buscando orden de producción con ID: {}", id);
        OrdenProduccion ordenProduccion = findOrdenProduccionEntityById(id); // [cite: 21]
        return mapToResponseDTO(ordenProduccion);
    }

    @Override
    public OrdenProduccionResponseDTO updateOrdenProduccionHeader(Integer id, OrdenProduccionUpdateRequestDTO updateRequestDTO) {
        log.info("Actualizando cabecera de orden de producción ID: {}", id);
        OrdenProduccion ordenProduccion = findOrdenProduccionEntityById(id); // [cite: 22]
        String estadoAnterior = ordenProduccion.getEstadoProduccion();

        if ("Anulada".equals(ordenProduccion.getEstadoProduccion()) || "Terminada".equals(ordenProduccion.getEstadoProduccion())) {
            throw new BadRequestException("No se puede modificar una orden de producción en estado '" + ordenProduccion.getEstadoProduccion() + "'."); // [cite: 23]
        }

        if (updateRequestDTO.getFechaInicioProduccion() != null) {
            ordenProduccion.setFechaInicioProduccion(updateRequestDTO.getFechaInicioProduccion()); // [cite: 23, 24]
        }
        if (updateRequestDTO.getFechaFinEstimadaProduccion() != null) {
            ordenProduccion.setFechaFinEstimadaProduccion(updateRequestDTO.getFechaFinEstimadaProduccion()); // [cite: 24, 25]
        }
        if (updateRequestDTO.getFechaFinRealProduccion() != null) {
            ordenProduccion.setFechaFinRealProduccion(updateRequestDTO.getFechaFinRealProduccion()); // [cite: 25, 26]
        }
        if (updateRequestDTO.getEstadoProduccion() != null) {
            ordenProduccion.setEstadoProduccion(updateRequestDTO.getEstadoProduccion()); // [cite: 28]
        }
        if (updateRequestDTO.getObservacionesProduccion() != null) {
            ordenProduccion.setObservacionesProduccion(updateRequestDTO.getObservacionesProduccion()); // [cite: 29]
        }
        
        // Guardar los cambios en la cabecera primero
        OrdenProduccion updatedOrdenProduccion = ordenProduccionRepository.save(ordenProduccion); // [cite: 29]

        // Lógica para consumir insumos si el estado cambia a "En Proceso" y antes no lo estaba
        if ("En Proceso".equals(updatedOrdenProduccion.getEstadoProduccion()) && !"En Proceso".equals(estadoAnterior)) {
            log.info("Orden de Producción ID {} ha cambiado a 'En Proceso'. Intentando consumir insumos del BOM.", updatedOrdenProduccion.getIdOrdenProduccion());
            consumirInsumosParaOP(updatedOrdenProduccion);
        }
        
        // Lógica para registrar entrada de productos terminados si el estado cambia a "Terminada"
        if ("Terminada".equals(updatedOrdenProduccion.getEstadoProduccion()) && !"Terminada".equals(estadoAnterior)) {
            log.info("Orden de Producción ID {} ha cambiado a 'Terminada'. Registrando entrada de producto terminado.", updatedOrdenProduccion.getIdOrdenProduccion());
            if (updatedOrdenProduccion.getFechaFinRealProduccion() == null) { // Si no se seteó explícitamente
                 updatedOrdenProduccion.setFechaFinRealProduccion(LocalDate.now()); // [cite: 26]
                 ordenProduccionRepository.save(updatedOrdenProduccion); // Guardar el cambio de fecha
            }
            registrarEntradaProductoTerminado(updatedOrdenProduccion);
        }

        log.info("Cabecera de orden de producción ID: {} actualizada.", updatedOrdenProduccion.getIdOrdenProduccion());
        return mapToResponseDTO(updatedOrdenProduccion); // [cite: 30, 31]
    }

    @Transactional
    protected void consumirInsumosParaOP(OrdenProduccion ordenProduccion) {
        OrdenVenta ordenVenta = ordenProduccion.getOrdenVenta();
        if (ordenVenta == null) {
            log.warn("La Orden de Producción ID {} no tiene una Orden de Venta asociada. No se pueden consumir insumos.", ordenProduccion.getIdOrdenProduccion());
            throw new BadRequestException("La OP ID " + ordenProduccion.getIdOrdenProduccion() + " no tiene OV asociada para consumir insumos.");
        }

        if (ordenVenta.getDetallesOrdenVenta() == null || ordenVenta.getDetallesOrdenVenta().isEmpty()) {
            log.warn("La Orden de Venta ID {} asociada a la OP ID {} no tiene detalles. No se pueden consumir insumos.", ordenVenta.getIdOrdenVenta(), ordenProduccion.getIdOrdenProduccion());
            return;
        }

        for (DetalleOrdenVenta detalleOV : ordenVenta.getDetallesOrdenVenta()) {
            Producto productoAProducir = detalleOV.getProducto();
            Integer cantidadAProducir = detalleOV.getCantidadProducto();

            if (productoAProducir == null) {
                log.warn("Detalle de Orden de Venta ID {} en OP ID {} no tiene producto asociado.", detalleOV.getIdDetalleOrden(), ordenProduccion.getIdOrdenProduccion());
                continue; 
            }

            log.info("Consumiendo insumos para el producto '{}' (ID: {}), cantidad a producir: {}",
                    productoAProducir.getNombreProducto(), productoAProducir.getIdProducto(), cantidadAProducir);

            List<InsumoPorProducto> bomItems = insumoPorProductoRepository.findByIdIdProducto(productoAProducir.getIdProducto()); // [cite: 175]

            if (bomItems.isEmpty()) {
                log.info("El producto ID {} ('{}') no tiene BOM definido. No se consumen insumos para este producto.", productoAProducir.getIdProducto(), productoAProducir.getNombreProducto());
                continue;
            }

            for (InsumoPorProducto bomItem : bomItems) {
                Insumo insumo = bomItem.getInsumo();
                BigDecimal cantidadRequeridaPorUnidad = bomItem.getCantidadRequerida();
                BigDecimal cantidadTotalRequerida = cantidadRequeridaPorUnidad.multiply(BigDecimal.valueOf(cantidadAProducir));

                log.info("Para producto ID {}, se requiere insumo ID {} '{}': {} {} (Total: {})",
                        productoAProducir.getIdProducto(), insumo.getIdInsumo(), insumo.getNombreInsumo(),
                        cantidadRequeridaPorUnidad, insumo.getUnidadMedidaInsumo(), cantidadTotalRequerida);

                String ubicacionConsumo = "Bodega Principal"; // Placeholder
                
                InventarioInsumo inventarioInsumo = inventarioInsumoRepository
                        .findByUbicacionInventarioAndInsumo(ubicacionConsumo, insumo)
                        .orElseThrow(() -> {
                            log.error("No se encontró registro de inventario para insumo ID {} ('{}') en ubicación '{}'. Consumo fallido para OP ID {}.",
                                    insumo.getIdInsumo(), insumo.getNombreInsumo(), ubicacionConsumo, ordenProduccion.getIdOrdenProduccion());
                            return new ResourceNotFoundException("InventarioInsumo", "insumo/ubicacion", insumo.getNombreInsumo() + "/" + ubicacionConsumo + 
                                                        ". No se puede iniciar producción para OP ID " + ordenProduccion.getIdOrdenProduccion());
                        });

                if (inventarioInsumo.getCantidadStock().compareTo(cantidadTotalRequerida) < 0) {
                    log.error("Stock insuficiente para insumo ID {} ('{}') en ubicación '{}'. Requerido: {}, Disponible: {}. Consumo fallido para OP ID {}.",
                            insumo.getIdInsumo(), insumo.getNombreInsumo(), ubicacionConsumo, cantidadTotalRequerida, inventarioInsumo.getCantidadStock(), ordenProduccion.getIdOrdenProduccion());
                    throw new BadRequestException("Stock insuficiente para " + insumo.getNombreInsumo() + " en " + ubicacionConsumo +
                                                ". Requerido: " + cantidadTotalRequerida + ", Disponible: " + inventarioInsumo.getCantidadStock() +
                                                ". No se puede procesar la OP ID " + ordenProduccion.getIdOrdenProduccion());
                }
                inventarioInsumo.setCantidadStock(inventarioInsumo.getCantidadStock().subtract(cantidadTotalRequerida)); // [cite: 100]
                inventarioInsumoRepository.save(inventarioInsumo);

                MovimientoInventarioInsumo movimiento = new MovimientoInventarioInsumo(
                    "Salida",
                    inventarioInsumo,
                    cantidadTotalRequerida,
                    "Consumo para OP #" + ordenProduccion.getIdOrdenProduccion() + 
                    ", Producto: " + productoAProducir.getReferenciaProducto() +
                    ", Detalle OV: " + detalleOV.getIdDetalleOrden()
                );
                movimientoInsumoRepository.save(movimiento); // [cite: 103]
                log.info("Movimiento de salida registrado para insumo ID {} (Cantidad: {}) desde inventario ID {} para OP ID {}",
                        insumo.getIdInsumo(), cantidadTotalRequerida, inventarioInsumo.getIdInventarioInsumo(), ordenProduccion.getIdOrdenProduccion());
            }
        }
        log.info("Consumo de insumos completado para OP ID {}.", ordenProduccion.getIdOrdenProduccion());
    }

    @Transactional
    protected void registrarEntradaProductoTerminado(OrdenProduccion ordenProduccion) {
        if (!"Terminada".equals(ordenProduccion.getEstadoProduccion())) {
            log.warn("Intento de registrar entrada de producto para OP ID {} que no está en estado 'Terminada'. Estado actual: {}", 
                     ordenProduccion.getIdOrdenProduccion(), ordenProduccion.getEstadoProduccion());
            return;
        }
    
        OrdenVenta ordenVenta = ordenProduccion.getOrdenVenta();
        if (ordenVenta == null) {
            log.error("OP ID {} no tiene Orden de Venta asociada. No se puede registrar entrada de producto.", ordenProduccion.getIdOrdenProduccion());
            throw new BadRequestException("La orden de producción no tiene una orden de venta asociada para determinar los productos a ingresar.");
        }
    
        if (ordenVenta.getDetallesOrdenVenta() == null || ordenVenta.getDetallesOrdenVenta().isEmpty()) {
            log.warn("La Orden de Venta ID {} asociada a la OP ID {} no tiene detalles. No se puede registrar entrada de producto.", ordenVenta.getIdOrdenVenta(), ordenProduccion.getIdOrdenProduccion());
            return;
        }
    
        for (DetalleOrdenVenta detalleOV : ordenVenta.getDetallesOrdenVenta()) {
            Producto productoTerminado = detalleOV.getProducto();
            Integer cantidadProducida = detalleOV.getCantidadProducto();
    
            if (productoTerminado == null) continue;
    
            log.info("Registrando entrada para producto terminado '{}' (ID: {}), cantidad: {}",
                    productoTerminado.getNombreProducto(), productoTerminado.getIdProducto(), cantidadProducida);
    
            String ubicacionEntrada = "Almacén Principal PT"; // Placeholder
    
            InventarioProducto inventarioProducto = inventarioProductoRepository
                    .findByUbicacionInventarioAndProducto(ubicacionEntrada, productoTerminado)
                    .orElseGet(() -> {
                        log.info("No existe registro de inventario para producto ID {} en '{}'. Creando nuevo registro.", productoTerminado.getIdProducto(), ubicacionEntrada);
                        InventarioProducto nuevoInventario = new InventarioProducto();
                        nuevoInventario.setProducto(productoTerminado);
                        nuevoInventario.setUbicacionInventario(ubicacionEntrada);
                        nuevoInventario.setCantidadStock(0);
                        return inventarioProductoRepository.save(nuevoInventario); // [cite: 128]
                    });
    
            inventarioProducto.setCantidadStock(inventarioProducto.getCantidadStock() + cantidadProducida); // [cite: 140, 141]
            inventarioProductoRepository.save(inventarioProducto); // [cite: 142]
    
            MovimientoInventarioProducto movimiento = new MovimientoInventarioProducto(
                "Entrada",
                inventarioProducto,
                cantidadProducida,
                "Entrada por finalización de OP #" + ordenProduccion.getIdOrdenProduccion() + 
                " (OV #" + ordenVenta.getIdOrdenVenta() + ")"
            );
            movimientoInventarioProductoRepository.save(movimiento); // [cite: 143]
            log.info("Movimiento de entrada registrado para producto ID {} (Cantidad: {}) a inventario ID {} por OP ID {}",
                    productoTerminado.getIdProducto(), cantidadProducida, inventarioProducto.getIdInventarioProducto(), ordenProduccion.getIdOrdenProduccion());
        }
        log.info("Entrada de productos terminados completada para OP ID {}.", ordenProduccion.getIdOrdenProduccion());
    }


    @Override
    public TareaProduccionResponseDTO addTareaToOrdenProduccion(Integer idOrdenProduccion, TareaProduccionCreateRequestDTO tareaRequestDTO) {
        log.info("Añadiendo tarea a orden de producción ID: {}", idOrdenProduccion);
        OrdenProduccion ordenProduccion = findOrdenProduccionEntityById(idOrdenProduccion); // [cite: 32]

        if ("Anulada".equals(ordenProduccion.getEstadoProduccion()) || "Terminada".equals(ordenProduccion.getEstadoProduccion())) {
            throw new BadRequestException("No se pueden añadir tareas a una orden de producción en estado '" + ordenProduccion.getEstadoProduccion() + "'."); // [cite: 33]
        }
        if (tareaRequestDTO.getIdOrdenProduccion() != null && !tareaRequestDTO.getIdOrdenProduccion().equals(idOrdenProduccion)) {
            throw new BadRequestException("El ID de la orden de producción en el cuerpo de la tarea no coincide con el ID en la ruta."); // [cite: 34]
        }

        TareaProduccion nuevaTarea = new TareaProduccion();
        nuevaTarea.setOrdenProduccion(ordenProduccion);
        nuevaTarea.setNombreTarea(tareaRequestDTO.getNombreTarea());
        nuevaTarea.setDuracionEstimadaTarea(tareaRequestDTO.getDuracionEstimadaTarea());
        nuevaTarea.setObservacionesTarea(tareaRequestDTO.getObservacionesTarea());
        nuevaTarea.setEstadoTarea("Pendiente");

        if (tareaRequestDTO.getIdEmpleado() != null) { // [cite: 35]
            Empleado empleado = empleadoRepository.findById(tareaRequestDTO.getIdEmpleado())
                    .orElseThrow(() -> new ResourceNotFoundException("Empleado", "id", tareaRequestDTO.getIdEmpleado()));
            nuevaTarea.setEmpleado(empleado); // [cite: 36]
        }

        if ("Pendiente".equals(ordenProduccion.getEstadoProduccion())) {
            ordenProduccion.setEstadoProduccion("En Proceso");
            if (ordenProduccion.getFechaInicioProduccion() == null) { // [cite: 37]
                ordenProduccion.setFechaInicioProduccion(LocalDate.now()); // [cite: 38]
            }
            ordenProduccionRepository.save(ordenProduccion); // [cite: 39]
        }

        TareaProduccion savedTarea = tareaProduccionRepository.save(nuevaTarea); // [cite: 39]
        log.info("Tarea ID: {} añadida a orden de producción ID: {}", savedTarea.getIdTareaProduccion(), idOrdenProduccion);
        return mapTareaToResponseDTO(savedTarea); // [cite: 40, 41]
    }

    @Override
    public TareaProduccionResponseDTO updateTareaInOrdenProduccion(Integer idOrdenProduccion, Integer idTarea, TareaProduccionUpdateRequestDTO tareaRequestDTO) {
        log.info("Actualizando tarea ID: {} en orden de producción ID: {}", idTarea, idOrdenProduccion);
        OrdenProduccion ordenProduccion = findOrdenProduccionEntityById(idOrdenProduccion); // [cite: 42]
        if ("Anulada".equals(ordenProduccion.getEstadoProduccion()) || "Terminada".equals(ordenProduccion.getEstadoProduccion())) {
            throw new BadRequestException("No se pueden actualizar tareas de una orden de producción en estado '" + ordenProduccion.getEstadoProduccion() + "'."); // [cite: 43]
        }

        TareaProduccion tareaToUpdate = tareaProduccionRepository.findByIdTareaProduccionAndOrdenProduccion_IdOrdenProduccion(idTarea, idOrdenProduccion)
                .orElseThrow(() -> new ResourceNotFoundException("TareaProduccion", "idTarea/idOrdenProduccion", idTarea + "/" + idOrdenProduccion));

        if (tareaRequestDTO.getIdEmpleado() != null) { // [cite: 44]
            Empleado empleado = empleadoRepository.findById(tareaRequestDTO.getIdEmpleado())
                    .orElseThrow(() -> new ResourceNotFoundException("Empleado", "id", tareaRequestDTO.getIdEmpleado()));
            tareaToUpdate.setEmpleado(empleado); // [cite: 45]
        }

        if (tareaRequestDTO.getNombreTarea() != null) {
            tareaToUpdate.setNombreTarea(tareaRequestDTO.getNombreTarea()); // [cite: 46]
        }
        if (tareaRequestDTO.getFechaInicioTarea() != null) {
            tareaToUpdate.setFechaInicioTarea(tareaRequestDTO.getFechaInicioTarea()); // [cite: 47]
        }
        if (tareaRequestDTO.getFechaFinTarea() != null) {
            tareaToUpdate.setFechaFinTarea(tareaRequestDTO.getFechaFinTarea()); // [cite: 48]
            if (!"Bloqueada".equals(tareaToUpdate.getEstadoTarea()) && tareaRequestDTO.getEstadoTarea() == null) { // [cite: 48]
                tareaToUpdate.setEstadoTarea("Completada"); // [cite: 49]
            }
        }
        if (tareaRequestDTO.getDuracionEstimadaTarea() != null) {
            tareaToUpdate.setDuracionEstimadaTarea(tareaRequestDTO.getDuracionEstimadaTarea()); // [cite: 50]
        }
        if (tareaRequestDTO.getDuracionRealTarea() != null) {
            tareaToUpdate.setDuracionRealTarea(tareaRequestDTO.getDuracionRealTarea()); // [cite: 51]
        }
        if (tareaRequestDTO.getEstadoTarea() != null) {
            tareaToUpdate.setEstadoTarea(tareaRequestDTO.getEstadoTarea()); // [cite: 52]
        }
        if (tareaRequestDTO.getObservacionesTarea() != null) {
            tareaToUpdate.setObservacionesTarea(tareaRequestDTO.getObservacionesTarea()); // [cite: 53]
        }

        TareaProduccion updatedTarea = tareaProduccionRepository.save(tareaToUpdate); // [cite: 53]
        log.info("Tarea ID: {} actualizada en orden de producción ID: {}", updatedTarea.getIdTareaProduccion(), idOrdenProduccion);
        return mapTareaToResponseDTO(updatedTarea); // [cite: 54, 55]
    }

    @Override
    public void removeTareaFromOrdenProduccion(Integer idOrdenProduccion, Integer idTarea) {
        log.info("Eliminando tarea ID: {} de orden de producción ID: {}", idTarea, idOrdenProduccion);
        OrdenProduccion ordenProduccion = findOrdenProduccionEntityById(idOrdenProduccion); // [cite: 56]
        if ("Anulada".equals(ordenProduccion.getEstadoProduccion()) || "Terminada".equals(ordenProduccion.getEstadoProduccion())) {
            throw new BadRequestException("No se pueden eliminar tareas de una orden de producción en estado '" + ordenProduccion.getEstadoProduccion() + "'."); // [cite: 57]
        }

        TareaProduccion tareaToRemove = tareaProduccionRepository.findByIdTareaProduccionAndOrdenProduccion_IdOrdenProduccion(idTarea, idOrdenProduccion)
                .orElseThrow(() -> new ResourceNotFoundException("TareaProduccion", "idTarea/idOrdenProduccion", idTarea + "/" + idOrdenProduccion));
        ordenProduccion.getTareasProduccion().remove(tareaToRemove); // [cite: 58]
        tareaProduccionRepository.delete(tareaToRemove);
        log.info("Tarea ID: {} eliminada de orden de producción ID: {}", idTarea, idOrdenProduccion);
    }

    @Override
    public void anularOrdenProduccion(Integer id) {
        log.info("Anulando orden de producción ID: {}", id);
        OrdenProduccion ordenProduccion = findOrdenProduccionEntityById(id); // [cite: 60]

        if ("Terminada".equals(ordenProduccion.getEstadoProduccion())) {
            throw new BadRequestException("No se puede anular una orden de producción ya terminada."); // [cite: 61]
        }
        ordenProduccion.getTareasProduccion().forEach(tarea -> { // [cite: 61]
            if ("Pendiente".equals(tarea.getEstadoTarea()) || "En Curso".equals(tarea.getEstadoTarea())) {
                tarea.setEstadoTarea("Bloqueada");
                tarea.setObservacionesTarea((tarea.getObservacionesTarea() != null ? tarea.getObservacionesTarea() : "") + " Anulada debido a anulación de OP.");
                tareaProduccionRepository.save(tarea); // [cite: 62]
            }
        });

        ordenProduccion.setEstadoProduccion("Anulada");
        ordenProduccionRepository.save(ordenProduccion);
        log.info("Orden de producción ID: {} anulada.", id);
    }

    private OrdenProduccion findOrdenProduccionEntityById(Integer id) {
        return ordenProduccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrdenProduccion", "id", id)); // [cite: 64]
    }

    private OrdenProduccionResponseDTO mapToResponseDTO(OrdenProduccion entity) {
        OrdenProduccionResponseDTO dto = new OrdenProduccionResponseDTO();
        dto.setIdOrdenProduccion(entity.getIdOrdenProduccion()); // [cite: 65]

        if (entity.getOrdenVenta() != null) { // [cite: 68]
            OrdenVenta ov = entity.getOrdenVenta();
            String clienteNombre = (ov.getCliente() != null) ? ov.getCliente().getNombreCliente() : "N/A"; // [cite: 66]
            dto.setOrdenVenta(new OrdenVentaSummaryDTO(ov.getIdOrdenVenta(), ov.getFechaPedido(), clienteNombre)); // [cite: 67]
        }

        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setFechaInicioProduccion(entity.getFechaInicioProduccion()); // [cite: 69]
        dto.setFechaFinEstimadaProduccion(entity.getFechaFinEstimadaProduccion()); // [cite: 70]
        dto.setFechaFinRealProduccion(entity.getFechaFinRealProduccion()); // [cite: 71]
        dto.setEstadoProduccion(entity.getEstadoProduccion());
        dto.setObservacionesProduccion(entity.getObservacionesProduccion());
        dto.setFechaActualizacion(entity.getFechaActualizacion());

        List<TareaProduccionResponseDTO> tareasDTO = (entity.getTareasProduccion() == null) ? // [cite: 72]
                new ArrayList<>() :
                entity.getTareasProduccion().stream()
                        .map(this::mapTareaToResponseDTO)
                        .collect(Collectors.toList());
        dto.setTareas(tareasDTO); // [cite: 73]
        return dto;
    }

    private TareaProduccionResponseDTO mapTareaToResponseDTO(TareaProduccion tarea) {
        if (tarea == null) return null;
        TareaProduccionResponseDTO dto = new TareaProduccionResponseDTO(); // [cite: 74]
        dto.setIdTareaProduccion(tarea.getIdTareaProduccion());
        if(tarea.getOrdenProduccion() != null){
            dto.setIdOrdenProduccion(tarea.getOrdenProduccion().getIdOrdenProduccion()); // [cite: 75]
        }

        if (tarea.getEmpleado() != null) { // [cite: 76]
            Empleado emp = tarea.getEmpleado();
            dto.setEmpleado(new EmpleadoSummaryDTO(emp.getIdEmpleado(), emp.getNumeroDocumento(), emp.getNombreEmpleado()));
        }

        dto.setNombreTarea(tarea.getNombreTarea());
        dto.setFechaInicioTarea(tarea.getFechaInicioTarea()); // [cite: 77]
        dto.setFechaFinTarea(tarea.getFechaFinTarea()); // [cite: 78]
        dto.setDuracionEstimadaTarea(tarea.getDuracionEstimadaTarea()); // [cite: 79]
        dto.setDuracionRealTarea(tarea.getDuracionRealTarea()); // [cite: 80]
        dto.setEstadoTarea(tarea.getEstadoTarea());
        dto.setObservacionesTarea(tarea.getObservacionesTarea());
        return dto;
    }
}