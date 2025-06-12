package com.crmtech360.crmtech360_backend.controller;

import com.crmtech360.crmtech360_backend.dto.*;
import com.crmtech360.crmtech360_backend.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controlador para la gestión de clientes y sus contactos.
 * Incluye operaciones para crear, consultar, actualizar y eliminar clientes,
 * así como para administrar los contactos asociados a cada cliente.
 */
@RestController
@RequestMapping("/api/v1/clientes")
@Tag(name = "Clientes", description = "Operaciones para la gestión de clientes y sus contactos.")
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * Registra un nuevo cliente en el sistema.
     * El número de documento debe ser único.
     */
    @Operation(summary = "Crear un nuevo cliente",
            description = "Registra un cliente. Requiere permisos de administrador, ventas o permiso específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe un cliente con ese documento.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponseDTO.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENTAS') or hasAuthority('PERMISO_CREAR_CLIENTES')")
    public ResponseEntity<ClienteResponseDTO> createCliente(@Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
        ClienteResponseDTO createdCliente = clienteService.createCliente(clienteRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCliente.getIdCliente())
                .toUri();
        return ResponseEntity.created(location).body(createdCliente);
    }

    /**
     * Devuelve una lista paginada de clientes.
     */
    @Operation(summary = "Obtener todos los clientes (paginado)",
            description = "Lista paginada de clientes. Requiere permisos de consulta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida."),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para ver clientes.")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENTAS') or hasAuthority('PERMISO_VER_CLIENTES')")
    public ResponseEntity<Page<ClienteResponseDTO>> getAllClientes(
            @Parameter(description = "Parámetros de paginación y ordenamiento")
            @PageableDefault(size = 10, sort = "nombreCliente") Pageable pageable) {
        Page<ClienteResponseDTO> clientes = clienteService.findAllClientes(pageable);
        return ResponseEntity.ok(clientes);
    }

    /**
     * Consulta los datos de un cliente por su ID.
     */
    @Operation(summary = "Obtener un cliente por su ID",
            description = "Devuelve los datos de un cliente específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado."),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado.")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENTAS') or hasAuthority('PERMISO_VER_CLIENTES')")
    public ResponseEntity<ClienteResponseDTO> getClienteById(
            @Parameter(description = "ID del cliente", required = true, example = "1") @PathVariable Integer id) {
        ClienteResponseDTO cliente = clienteService.findClienteById(id);
        return ResponseEntity.ok(cliente);
    }

    /**
     * Consulta los datos de un cliente por su número de documento.
     */
    @Operation(summary = "Obtener un cliente por su número de documento",
            description = "Busca un cliente usando su número de documento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado."),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado.")
    })
    @GetMapping("/documento/{numeroDocumento}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENTAS') or hasAuthority('PERMISO_VER_CLIENTES')")
    public ResponseEntity<ClienteResponseDTO> getClienteByNumeroDocumento(
            @Parameter(description = "Número de documento", required = true, example = "123456789") @PathVariable String numeroDocumento) {
        ClienteResponseDTO cliente = clienteService.findClienteByNumeroDocumento(numeroDocumento);
        return ResponseEntity.ok(cliente);
    }

    /**
     * Actualiza los datos de un cliente existente.
     */
    @Operation(summary = "Actualizar un cliente existente",
            description = "Modifica los datos de un cliente. Requiere permisos de edición.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente actualizado."),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado.")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENTAS') or hasAuthority('PERMISO_EDITAR_CLIENTES')")
    public ResponseEntity<ClienteResponseDTO> updateCliente(
            @Parameter(description = "ID del cliente", required = true) @PathVariable Integer id,
            @Valid @RequestBody ClienteRequestDTO clienteRequestDTO) {
        ClienteResponseDTO updatedCliente = clienteService.updateCliente(id, clienteRequestDTO);
        return ResponseEntity.ok(updatedCliente);
    }

    /**
     * Elimina un cliente por su ID.
     */
    @Operation(summary = "Eliminar un cliente por su ID",
            description = "Elimina un cliente del sistema. Esta acción es irreversible.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente eliminado."),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado.")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('PERMISO_ELIMINAR_CLIENTES')")
    public ResponseEntity<Void> deleteCliente(
            @Parameter(description = "ID del cliente", required = true) @PathVariable Integer id) {
        clienteService.deleteCliente(id);
        return ResponseEntity.noContent().build();
    }

    // --- Contactos de Cliente ---

    /**
     * Añade un contacto a un cliente existente.
     */
    @Operation(summary = "Añadir un contacto a un cliente",
            description = "Permite agregar un nuevo contacto a un cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contacto añadido."),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado.")
    })
    @PostMapping("/{idCliente}/contactos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENTAS') or hasAuthority('PERMISO_EDITAR_CLIENTES') or hasAuthority('PERMISO_CREAR_CLIENTES')")
    public ResponseEntity<ContactoClienteResponseDTO> addContactoToCliente(
            @Parameter(description = "ID del cliente", required = true) @PathVariable Integer idCliente,
            @Valid @RequestBody ContactoClienteRequestDTO contactoRequestDTO) {
        ContactoClienteResponseDTO createdContacto = clienteService.addContactoToCliente(idCliente, contactoRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{idContacto}")
                .buildAndExpand(createdContacto.getIdContacto())
                .toUri();
        return ResponseEntity.created(location).body(createdContacto);
    }

    /**
     * Obtiene todos los contactos de un cliente.
     */
    @Operation(summary = "Obtener todos los contactos de un cliente",
            description = "Devuelve la lista de contactos asociados a un cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de contactos obtenida."),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado.")
    })
    @GetMapping("/{idCliente}/contactos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'GERENTE', 'VENTAS') or hasAuthority('PERMISO_VER_CLIENTES')")
    public ResponseEntity<List<ContactoClienteResponseDTO>> getContactosByClienteId(
            @Parameter(description = "ID del cliente", required = true) @PathVariable Integer idCliente) {
        List<ContactoClienteResponseDTO> contactos = clienteService.findContactosByClienteId(idCliente);
        return ResponseEntity.ok(contactos);
    }

    /**
     * Actualiza un contacto específico de un cliente.
     */
    @Operation(summary = "Actualizar un contacto de un cliente",
            description = "Permite modificar los datos de un contacto de cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contacto actualizado."),
            @ApiResponse(responseCode = "404", description = "Cliente o contacto no encontrado.")
    })
    @PutMapping("/{idCliente}/contactos/{idContacto}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VENTAS') or hasAuthority('PERMISO_EDITAR_CLIENTES')")
    public ResponseEntity<ContactoClienteResponseDTO> updateContactoCliente(
            @Parameter(description = "ID del cliente", required = true) @PathVariable Integer idCliente,
            @Parameter(description = "ID del contacto", required = true) @PathVariable Integer idContacto,
            @Valid @RequestBody ContactoClienteRequestDTO contactoRequestDTO) {
        ContactoClienteResponseDTO updatedContacto = clienteService.updateContactoCliente(idCliente, idContacto, contactoRequestDTO);
        return ResponseEntity.ok(updatedContacto);
    }

    /**
     * Elimina un contacto de un cliente.
     */
    @Operation(summary = "Eliminar un contacto de un cliente",
            description = "Elimina un contacto asociado a un cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contacto eliminado."),
            @ApiResponse(responseCode = "404", description = "Cliente o contacto no encontrado.")
    })
    @DeleteMapping("/{idCliente}/contactos/{idContacto}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasAuthority('PERMISO_ELIMINAR_CLIENTES')")
    public ResponseEntity<Void> deleteContactoCliente(
            @Parameter(description = "ID del cliente", required = true) @PathVariable Integer idCliente,
            @Parameter(description = "ID del contacto", required = true) @PathVariable Integer idContacto) {
        clienteService.deleteContactoCliente(idCliente, idContacto);
        return ResponseEntity.noContent().build();
    }
}