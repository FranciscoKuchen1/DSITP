package dsitp.backend.project.model;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservaPeriodicaDTO {

    private Integer id;

    private Integer idCatedra;

    @Size(max = 100)
    private String nombreCatedra;

    private Integer idDocente;

    @Size(max = 100)
    private String nombreDocente;

    @Size(max = 100)
    private String apellidoDocente;

    @Size(max = 100)
    private String correoDocente;

    private Integer cantAlumnos;

    private TipoAula tipoAula;

    private TipoPeriodo tipoPeriodo;

    private Integer idPeriodo;

    private Integer idBedel;

    private List<DiaReservadoDTO> diasReservadosDTO;

}
