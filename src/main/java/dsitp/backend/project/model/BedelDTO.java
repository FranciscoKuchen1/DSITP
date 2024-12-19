package dsitp.backend.project.model;

import dsitp.backend.project.validation.CreateGroup;
import dsitp.backend.project.validation.PasswordMatches;
import dsitp.backend.project.validation.UsuarioIdRegistroUnique;
import dsitp.backend.project.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@PasswordMatches
@Getter
@Setter
public class BedelDTO {

    @Size(max = 20)
    @NotBlank(message = "El idRegistro es obligatorio.")
    @UsuarioIdRegistroUnique(groups = CreateGroup.class)
    private String idRegistro;

    @Size(max = 100)
    @NotBlank(message = "El nombre es obligatorio.")
    private String nombre;

    @Size(max = 100)
    @NotBlank(message = "El apellido es obligatorio.")
    private String apellido;

    @Size(max = 100)
    @ValidPassword
    @NotBlank(message = "La contrasena es obligatoria.")
    private String contrasena;

    @Size(max = 100)
    @NotBlank(message = "La confirmacion de la contrasena es obligatoria.")
    private String confirmacionContrasena;

    @NotNull(message = "El tipoTurno es obligatorio.")
    private Integer tipoTurno;

}