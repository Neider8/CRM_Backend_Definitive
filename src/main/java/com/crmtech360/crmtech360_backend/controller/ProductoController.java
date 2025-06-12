package com.crmtech360.crmtech360_backend.controller;

import com.crmtech360.crmtech360_backend.dto.*;
import com.crmtech360.crmtech360_backend.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controlador para la gestión de productos terminados y su lista de materiales (BOM).
 * Permite registrar, consultar, actualizar y eliminar productos, así como administrar sus insumos asociados.
 */
@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "API para la gestión de productos terminados y su lista de materiales (BOM).")
@SecurityRequirement(name = "bearerAuth")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Crea un nuevo producto terminado.
     */
    @Operation(summary = "Crear un nuevo producto",
            description = "Registra un nuevo producto terminado. La referencia debe ser única.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe un producto con la misma referencia.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_CREAR_PRODUCTOS')")
    public ResponseEntity<ProductoResponseDTO> createProducto(@Valid @RequestBody ProductoCreateRequestDTO productoCreateRequestDTO) {
        ProductoResponseDTO createdProducto = productoService.createProducto(productoCreateRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdProducto.getIdProducto())
                .toUri();
        return ResponseEntity.created(location).body(createdProducto);
    }

    /**
     * Devuelve una lista paginada de productos terminados.
     */
    @Operation(summary = "Obtener todos los productos (paginado)",
            description = "Devuelve una lista paginada de todos los productos terminados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente.")
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ProductoResponseDTO>> getAllProductos(
            @Parameter(description = "Configuración de paginación") 
            @PageableDefault(size = 10, sort = "nombreProducto") Pageable pageable) {
        Page<ProductoResponseDTO> productos = productoService.findAllProductos(pageable);
        return ResponseEntity.ok(productos);
    }

    /**
     * Consulta un producto por su ID.
     */
    @Operation(summary = "Obtener un producto por su ID",
            description = "Devuelve los detalles de un producto específico, incluyendo su BOM si está disponible.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductoResponseDTO> getProductoById(
            @Parameter(description = "ID del producto a obtener.", required = true) @PathVariable Integer id) {
        ProductoResponseDTO producto = productoService.findProductoById(id);
        return ResponseEntity.ok(producto);
    }

    /**
     * Consulta un producto por su referencia única.
     */
    @Operation(summary = "Obtener un producto por su referencia",
            description = "Devuelve los detalles de un producto específico basado en su referencia única.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @GetMapping("/referencia/{referencia}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductoResponseDTO> getProductoByReferencia(
            @Parameter(description = "Referencia única del producto.", required = true) @PathVariable String referencia) {
        ProductoResponseDTO producto = productoService.findProductoByReferencia(referencia);
        return ResponseEntity.ok(producto);
    }

    /**
     * Actualiza los datos de un producto existente.
     */
    @Operation(summary = "Actualizar un producto existente",
            description = "Permite modificar los detalles de un producto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "La nueva referencia ya existe para otro producto.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_EDITAR_PRODUCTOS')")
    public ResponseEntity<ProductoResponseDTO> updateProducto(
            @Parameter(description = "ID del producto a actualizar.", required = true) @PathVariable Integer id,
            @Valid @RequestBody ProductoUpdateRequestDTO productoUpdateRequestDTO) {
        ProductoResponseDTO updatedProducto = productoService.updateProducto(id, productoUpdateRequestDTO);
        return ResponseEntity.ok(updatedProducto);
    }

    /**
     * Elimina un producto por su ID.
     */
    @Operation(summary = "Eliminar un producto por su ID",
            description = "Elimina un producto del sistema. Puede fallar si el producto está en uso.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente."),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "El producto no se puede eliminar (está en uso).",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('PERMISO_ELIMINAR_PRODUCTOS')")
    public ResponseEntity<Void> deleteProducto(
            @Parameter(description = "ID del producto a eliminar.", required = true) @PathVariable Integer id) {
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }

    // --- Endpoints para Lista de Materiales (BOM) ---

    /**
     * Agrega un insumo a la lista de materiales (BOM) de un producto.
     */
    @Operation(summary = "Añadir un insumo a la lista de materiales (BOM) de un producto",
            description = "Agrega un insumo con su cantidad requerida al BOM de un producto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Insumo añadido al BOM exitosamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InsumoPorProductoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto o Insumo no encontrado."),
            @ApiResponse(responseCode = "409", description = "El insumo ya forma parte del BOM de este producto.")
    })
    @PostMapping("/{idProducto}/bom")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_GESTIONAR_BOM')")
    public ResponseEntity<InsumoPorProductoResponseDTO> addInsumoToProductoBOM(
            @Parameter(description = "ID del producto al que se añade el insumo.", required = true) @PathVariable Integer idProducto,
            @Valid @RequestBody InsumoPorProductoRequestDTO bomItemDto) {
        InsumoPorProductoResponseDTO createdBomItem = productoService.addInsumoToProducto(idProducto, bomItemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBomItem);
    }

    /**
     * Devuelve la lista de materiales (BOM) de un producto.
     */
    @Operation(summary = "Obtener la lista de materiales (BOM) de un producto",
            description = "Devuelve todos los insumos y sus cantidades requeridas para fabricar un producto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "BOM del producto obtenida exitosamente.")
    })
    @GetMapping("/{idProducto}/bom")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<InsumoPorProductoResponseDTO>> getProductoBOM(
            @Parameter(description = "ID del producto para obtener su BOM.", required = true) @PathVariable Integer idProducto) {
        List<InsumoPorProductoResponseDTO> bom = productoService.getInsumosForProducto(idProducto);
        return ResponseEntity.ok(bom);
    }

    /**
     * Actualiza la cantidad de un insumo en el BOM de un producto.
     */
    @Operation(summary = "Actualizar la cantidad de un insumo en el BOM de un producto",
            description = "Modifica la cantidad requerida de un insumo específico en el BOM de un producto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cantidad del insumo en BOM actualizada.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InsumoPorProductoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto o Insumo no encontrado en el BOM.")
    })
    @PutMapping("/{idProducto}/bom/{idInsumo}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_GESTIONAR_BOM')")
    public ResponseEntity<InsumoPorProductoResponseDTO> updateInsumoInProductoBOM(
            @Parameter(description = "ID del producto.", required = true) @PathVariable Integer idProducto,
            @Parameter(description = "ID del insumo a actualizar en el BOM.", required = true) @PathVariable Integer idInsumo,
            @Valid @RequestBody InsumoPorProductoRequestDTO bomItemDto) {
        InsumoPorProductoResponseDTO updatedBomItem = productoService.updateInsumoInProducto(idProducto, idInsumo, bomItemDto);
        return ResponseEntity.ok(updatedBomItem);
    }

    /**
     * Elimina un insumo del BOM de un producto.
     */
    @Operation(summary = "Eliminar un insumo del BOM de un producto",
            description = "Quita un insumo de la lista de materiales de un producto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Insumo eliminado del BOM exitosamente."),
            @ApiResponse(responseCode = "404", description = "Producto o Insumo no encontrado en el BOM.")
    })
    @DeleteMapping("/{idProducto}/bom/{idInsumo}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE') or hasAuthority('PERMISO_GESTIONAR_BOM')")
    public ResponseEntity<Void> removeInsumoFromProductoBOM(
            @Parameter(description = "ID del producto.", required = true) @PathVariable Integer idProducto,
            @Parameter(description = "ID del insumo a eliminar del BOM.", required = true) @PathVariable Integer idInsumo) {
        productoService.removeInsumoFromProducto(idProducto, idInsumo);
        return ResponseEntity.noContent().build();
    }
}