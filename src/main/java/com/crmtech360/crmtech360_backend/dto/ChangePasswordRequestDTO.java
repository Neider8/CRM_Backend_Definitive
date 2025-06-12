package com.crmtech360.crmtech360_backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Objeto de transferencia para solicitar el cambio de contraseña de un usuario.
 * Incluye la contraseña actual y la nueva contraseña.
 */
public class ChangePasswordRequestDTO {

    @Schema(
        description = "Contraseña actual del usuario. Obligatoria para el usuario, opcional si el cambio lo realiza un administrador.",
        example = "P@$$wOrdActual123"
    )
    @NotBlank(message = "La contraseña actual es obligatoria.")
    private String currentPassword;

    @Schema(
        description = "Nueva contraseña que se desea establecer. Debe tener al menos 8 caracteres.",
        example = "NuevaP@$$wOrdSegura456"
    )
    @NotBlank(message = "La nueva contraseña es obligatoria.")
    @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres.")
    private String newPassword;

    public ChangePasswordRequestDTO() {}

    public ChangePasswordRequestDTO(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}