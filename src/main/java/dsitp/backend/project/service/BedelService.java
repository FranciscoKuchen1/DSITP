package dsitp.backend.project.service;

import dsitp.backend.project.domain.Bedel;
import dsitp.backend.project.domain.ReservaEsporadica;
import dsitp.backend.project.domain.ReservaPeriodica;
import dsitp.backend.project.model.BedelDTO;
import dsitp.backend.project.model.TipoTurno;
import dsitp.backend.project.repos.BedelRepository;
import dsitp.backend.project.repos.ReservaEsporadicaRepository;
import dsitp.backend.project.repos.ReservaPeriodicaRepository;
import dsitp.backend.project.util.BedelNotFoundException;
import dsitp.backend.project.util.NotFoundException;
import dsitp.backend.project.util.ReferencedWarning;
import static java.time.OffsetDateTime.now;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

@Service
@Transactional
public class BedelService {

    private final BedelRepository bedelRepository;
    private final ReservaEsporadicaRepository reservaEsporadicaRepository;
    private final ReservaPeriodicaRepository reservaPeriodicaRepository;
    private final Validator validator;

    @Autowired
    public BedelService(final BedelRepository bedelRepository,
            final ReservaEsporadicaRepository reservaEsporadicaRepository,
            final ReservaPeriodicaRepository reservaPeriodicaRepository,
            final Validator validator) {
        this.bedelRepository = bedelRepository;
        this.reservaEsporadicaRepository = reservaEsporadicaRepository;
        this.reservaPeriodicaRepository = reservaPeriodicaRepository;
        this.validator = validator;
    }

    public List<BedelDTO> findAll() {
        final List<Bedel> bedeles = bedelRepository.findByEliminadoFalse(Sort.by("id"));
        return bedeles.stream()
                .map(bedel -> toBedelDTO(bedel))
                .toList();
    }

    public BedelDTO get(final Integer id) {
        return bedelRepository.findById(id)
                .map(bedel -> toBedelDTO(bedel))
                .orElseThrow(NotFoundException::new);
    }

    public BedelDTO getByIdRegistro(final String idRegistro) {
        return bedelRepository.findByIdRegistroAndEliminadoFalse(idRegistro)
                .map(bedel -> toBedelDTO(bedel))
                .orElseThrow(NotFoundException::new);
    }

    public List<BedelDTO> getBedelesByApellido(final String apellido) {
        final List<Bedel> bedeles = bedelRepository.findByApellidoAndEliminadoFalse(apellido);
        return bedeles.stream()
                .map(bedel -> toBedelDTO(bedel))
                .toList();
    }

    public List<BedelDTO> getBedelesByTipoTurno(final Integer tipoTurno) {
        final List<Bedel> bedeles = bedelRepository.findByTipoTurnoAndEliminadoFalse(TipoTurno.fromInteger(tipoTurno));
        return bedeles.stream()
                .map(bedel -> toBedelDTO(bedel))
                .toList();
    }

    public BedelDTO getBedelByIdRegistro(final String idRegistro) {
        return bedelRepository.findByIdRegistroAndEliminadoFalse(idRegistro)
                .map(bedel -> toBedelDTO(bedel))
                .orElseThrow(NotFoundException::new);
    }

    public List<BedelDTO> findBedeles(Integer tipoTurno, String apellido) {
        if (tipoTurno != null && apellido != null) {
            return bedelRepository
                    .findByTipoTurnoAndApellidoAndEliminadoFalse(TipoTurno.fromInteger(tipoTurno), apellido).stream()
                    .map(bedel -> toBedelDTO(bedel))
                    .toList();
        } else if (tipoTurno != null) {
            return bedelRepository.findByTipoTurnoAndEliminadoFalse(TipoTurno.fromInteger(tipoTurno)).stream()
                    .map(bedel -> toBedelDTO(bedel))
                    .toList();
        } else if (apellido != null) {
            return bedelRepository.findByApellidoAndEliminadoFalse(apellido).stream()
                    .map(bedel -> toBedelDTO(bedel))
                    .toList();
        } else {
            return bedelRepository.findByEliminadoFalse().stream()
                    .map(bedel -> toBedelDTO(bedel))
                    .toList();
        }
    }

    public Integer create(final BedelDTO bedelDTO) {
        final Bedel bedel = toBedelEntity(bedelDTO);
        return bedelRepository.save(bedel).getId();
    }

    public void update(final String idRegistro, final BedelDTO bedelDTO) {
        Bedel existingBedel = bedelRepository.findByIdRegistroAndEliminadoFalse(idRegistro)
                .orElseThrow(NotFoundException::new);

        existingBedel.setNombre(bedelDTO.getNombre());
        existingBedel.setApellido(bedelDTO.getApellido());
        existingBedel.setContrasena(bedelDTO.getContrasena());
        existingBedel.setLastUpdated(now());
        existingBedel.setTipoTurno(TipoTurno.fromInteger(bedelDTO.getTipoTurno()));

        bedelRepository.save(existingBedel);
    }

    public void delete(final Integer id) {
        bedelRepository.deleteById(id);
    }

    public void deleteLogico(final String idRegistro) {
        Bedel bedel = bedelRepository.findByIdRegistroAndEliminadoFalse(idRegistro)
                .orElseThrow(BedelNotFoundException::new);
        List<ReservaEsporadica> reservasEsporadicas = reservaEsporadicaRepository.findByBedel(bedel);
        for (ReservaEsporadica reserva : reservasEsporadicas) {
            reserva.setBedel(null);
            reservaEsporadicaRepository.save(reserva);
        }

        List<ReservaPeriodica> reservasPeriodicas = reservaPeriodicaRepository.findByBedel(bedel);
        for (ReservaPeriodica reserva : reservasPeriodicas) {
            reserva.setBedel(null);
            reservaPeriodicaRepository.save(reserva);
        }

        bedel.setEliminado(true);
        bedelRepository.save(bedel);
    }

    public Boolean idRegistroExists(final String idRegistro) {
        return bedelRepository.existsByIdRegistroIgnoreCaseAndEliminadoFalse(idRegistro);
    }

    public Boolean isPasswordInHistory(final String contrasena) {
        return bedelRepository.existsByContrasenaAndEliminadoFalse(contrasena);
    }

    public ReferencedWarning getReferencedWarning(final String idRegistro) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Bedel bedel = bedelRepository.findByIdRegistroAndEliminadoFalse(idRegistro)
                .orElseThrow(NotFoundException::new);
        final ReservaEsporadica bedelReservaEsporadica = reservaEsporadicaRepository.findFirstByBedel(bedel);
        if (bedelReservaEsporadica != null) {
            referencedWarning.setKey("bedel.reserva.bedel.referenced");
            referencedWarning.addParam(bedelReservaEsporadica.getId());
            return referencedWarning;
        }
        final ReservaPeriodica bedelReservaPeriodica = reservaPeriodicaRepository.findFirstByBedel(bedel);
        if (bedelReservaPeriodica != null) {
            referencedWarning.setKey("bedel.reserva.bedel.referenced");
            referencedWarning.addParam(bedelReservaPeriodica.getId());
            return referencedWarning;
        }
        return null;
    }

    public BedelDTO toBedelDTO(Bedel bedel) {
        if (bedel == null) {
            return null;
        }

        BedelDTO bedelDTO = new BedelDTO();
        bedelDTO.setIdRegistro(bedel.getIdRegistro());
        bedelDTO.setNombre(bedel.getNombre());
        bedelDTO.setApellido(bedel.getApellido());
        bedelDTO.setContrasena(bedel.getContrasena());
        bedelDTO.setConfirmacionContrasena(bedel.getContrasena()); // Suponiendo que es la misma
        bedelDTO.setTipoTurno(bedel.getTipoTurno() != null ? bedel.getTipoTurno().ordinal() : null);

        return bedelDTO;
    }

    public Bedel toBedelEntity(BedelDTO bedelDTO) {
        if (bedelDTO == null) {
            return null;
        }

        Bedel bedel = new Bedel();
        bedel.setIdRegistro(bedelDTO.getIdRegistro());
        bedel.setNombre(bedelDTO.getNombre());
        bedel.setApellido(bedelDTO.getApellido());
        bedel.setContrasena(bedelDTO.getContrasena());
        bedel.setEliminado(false);
        bedel.setTipoTurno(bedelDTO.getTipoTurno() != null ? TipoTurno.values()[bedelDTO.getTipoTurno()] : null);

        return bedel;
    }

}
