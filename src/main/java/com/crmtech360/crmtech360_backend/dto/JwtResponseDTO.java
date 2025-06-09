package com.crmtech360.crmtech360_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para la respuesta de autenticación exitosa. Contiene el token JWT y la información básica del usuario.")
public class JwtResponseDTO {

    @Schema(description = "El token de acceso JWT generado para el usuario autenticado.",
            example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c3VhcmlvQWRtaW4iLCJhdXRob3JpdGllcyI6WyJST0xFX0FETUlOSVNUUkFET1IiLCJQRVJNSVN...",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String token;

    @Schema(description = "Tipo de token. Siempre será 'Bearer' para indicar el esquema de autenticación.",
            example = "Bearer",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String type = "Bearer";

    @Schema(description = "Identificador único del usuario autenticado.",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Integer idUsuario;

    @Schema(description = "Nombre de usuario utilizado para la autenticación.",
            example = "usuarioAdmin",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String nombreUsuario;

    @Schema(description = "Rol principal del usuario autenticado en el sistema.",
            example = "ADMINISTRADOR",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String rolUsuario;

    @Schema(description = "Indica si el usuario está habilitado para acceder al sistema.", // <--- NUEVA DESCRIPCIÓN
            example = "true",
            accessMode = Schema.AccessMode.READ_ONLY)
    private boolean habilitado; // <--- NUEVO CAMPO

    // Constructores
    public JwtResponseDTO() {}

    // Constructor actualizado para incluir 'habilitado'
    public JwtResponseDTO(String token, Integer idUsuario, String nombreUsuario, String rolUsuario, boolean habilitado) { // <--- MODIFICADO
        this.token = token;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.rolUsuario = rolUsuario;
        this.habilitado = habilitado; // <--- ASIGNACIÓN DEL NUEVO CAMPO
    }

    // Getters y Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public String getRolUsuario() { return rolUsuario; }
    public void setRolUsuario(String rolUsuario) { this.rolUsuario = rolUsuario; }

    // Nuevo Getter y Setter para 'habilitado'
    public boolean isHabilitado() { // <--- NUEVO GETTER
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) { // <--- NUEVO SETTER
        this.habilitado = habilitado;
    }
}