package com.crmtech360.crmtech360_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// DTO para actualizar datos específicos del usuario, como el rol, el empleado asociado, o el estado de habilitación.
// La actualización de contraseña debería tener su propio DTO y endpoint.
// El nombre de usuario generalmente no se cambia.
@Schema(description = "DTO para la solicitud de actualización de datos específicos de una cuenta de Usuario existente. " +
        "Permite cambiar el empleado asociado, el rol del usuario y/o el estado de habilitación. " +
        "El nombre de usuario y la contraseña se gestionan por separado.")
public class UsuarioUpdateRequestDTO {

    @Schema(description = "Nuevo identificador único del Empleado al que se vinculará esta cuenta de usuario. " +
            "Si se proporciona, reemplazará la asociación existente. " +
            "Enviar nulo podría desvincular al usuario de un empleado, si la lógica de negocio lo permite.",
            example = "11",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true)
    private Integer idEmpleado;

    @Schema(description = "Nuevo rol principal que se asignará al usuario. Debe ser uno de los roles definidos en el sistema.",
            example = "Gerente",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            nullable = true)
    @Pattern(regexp = "Administrador|Gerente|Operario|Ventas", message = "Rol inválido. Valores permitidos: Administrador, Gerente, Operario, Ventas.")
    @Size(max = 50, message = "El rol del usuario no debe exceder los 50 caracteres.")
    private String rolUsuario;

    @Schema(description = "Indica si la cuenta de usuario debe estar habilitada (true) o deshabilitada (false).", // <--- NUEVA DESCRIPCIÓN
            example = "true",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, // Opcional para la actualización
            nullable = true)
    private Boolean habilitado; // <--- NUEVO CAMPO: Usar Boolean para permitir null (no modificar si no se envía)

    // Constructores
    public UsuarioUpdateRequestDTO() {
    }

    // Constructor actualizado para incluir 'habilitado'
    public UsuarioUpdateRequestDTO(Integer idEmpleado, String rolUsuario, Boolean habilitado) { // <--- MODIFICADO
        this.idEmpleado = idEmpleado;
        this.rolUsuario = rolUsuario;
        this.habilitado = habilitado; // <--- ASIGNACIÓN DEL NUEVO CAMPO
    }

    // Getters y Setters
    public Integer getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(Integer idEmpleado) { this.idEmpleado = idEmpleado; }
    public String getRolUsuario() { return rolUsuario; }
    public void setRolUsuario(String rolUsuario) { this.rolUsuario = rolUsuario; }

    // Nuevo Getter y Setter para 'habilitado'
    public Boolean getHabilitado() { // <--- NUEVO GETTER (usa Boolean, no boolean, para permitir null)
        return habilitado;
    }
    public void setHabilitado(Boolean habilitado) { // <--- NUEVO SETTER
        this.habilitado = habilitado;
    }
}