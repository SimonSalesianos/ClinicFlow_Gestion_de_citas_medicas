package com.salesianostriana.ClinicFlow.services;

import com.salesianostriana.ClinicFlow.dtos.*;
import com.salesianostriana.ClinicFlow.models.Cita;
import com.salesianostriana.ClinicFlow.models.Consulta;
import com.salesianostriana.ClinicFlow.models.Paciente;
import com.salesianostriana.ClinicFlow.models.Profesional;
import com.salesianostriana.ClinicFlow.repositories.CitaRepository;
import com.salesianostriana.ClinicFlow.repositories.ConsultaRepository;
import com.salesianostriana.ClinicFlow.repositories.PacienteRepository;
import com.salesianostriana.ClinicFlow.repositories.ProfesionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfesionalRepository profesionalRepository;
    private final ConsultaRepository consultaRepository;

    public Page<CitaListDto> getCitas(String estado, Pageable pageable) {
        Page<Cita> page = (estado == null || estado.isBlank())
                ? citaRepository.findAll(pageable)
                : citaRepository.findByEstado(estado, pageable);

        return page.map(this::toCitaListDto);
    }

    public Page<CitaListDto> getCitasDePaciente(Long pacienteId, Pageable pageable) {
        return citaRepository.findByPacienteId(pacienteId, pageable)
                .map(this::toCitaListDto);
    }

    @Transactional
    public CitaDetailDto crearCita(CreateCitaRequest req) {

        LocalDateTime fechaHora = req.fechaHora();

        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se pueden crear citas en el pasado");
        }

        Paciente paciente = pacienteRepository.findById(req.pacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado: " + req.pacienteId()));

        Profesional profesional = profesionalRepository.findById(req.profesionalId())
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado: " + req.profesionalId()));

        if (citaRepository.existsByProfesionalIdAndFechaYhora(profesional.getId(), fechaHora)) {
            throw new RuntimeException("El profesional ya tiene una cita en esa fecha y hora");
        }

        LocalDate dia = fechaHora.toLocalDate();
        LocalDateTime inicio = dia.atStartOfDay();
        LocalDateTime fin = dia.plusDays(1).atStartOfDay();

        boolean yaTieneEseDia = citaRepository
                .findByPacienteId(paciente.getId(), Pageable.unpaged())
                .getContent()
                .stream()
                .anyMatch(c -> !c.getFechaYhora().isBefore(inicio) && c.getFechaYhora().isBefore(fin));

        if (yaTieneEseDia) {
            throw new RuntimeException("El paciente ya tiene una cita ese día");
        }

        Cita cita = Cita.builder()
                .paciente(paciente)
                .profesional(profesional)
                .fechaYhora(fechaHora)
                .estado("PROGRAMADA")
                .build();

        Cita saved = citaRepository.save(cita);
        return toCitaDetailDto(saved);
    }

    @Transactional
    public CitaDetailDto cancelarCita(Long id) {

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada: " + id));

        if ("ATENDIDA".equalsIgnoreCase(cita.getEstado())) {
            throw new RuntimeException("No se puede cancelar una cita ATENDIDA");
        }

        cita.setEstado("CANCELADA");
        Cita saved = citaRepository.save(cita);

        return toCitaDetailDto(saved);
    }

    @Transactional
    public CitaDetailDto registrarConsulta(Long idCita, CreateConsultaRequest req) {

        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada: " + idCita));

        if (!"PROGRAMADA".equalsIgnoreCase(cita.getEstado())) {
            throw new RuntimeException("Solo se puede registrar consulta si la cita está PROGRAMADA");
        }

        if (consultaRepository.findByCitaId(idCita).isPresent()) {
            throw new RuntimeException("Esa cita ya tiene una consulta registrada");
        }

        Consulta consulta = Consulta.builder()
                .observaciones(req.observaciones())
                .diagnostico(req.diagnostico())
                .fecha(LocalDate.now())
                .cita(cita) // relación
                .build();

        consultaRepository.save(consulta);

        cita.setEstado("ATENDIDA");
        citaRepository.save(cita);

        cita.setConsulta(consulta);

        return toCitaDetailDto(cita);
    }

    public List<CitaListDto> agendaDiaria(Long profesionalId, LocalDate dia) {
        LocalDateTime inicio = dia.atStartOfDay();
        LocalDateTime fin = dia.plusDays(1).atStartOfDay();

        return citaRepository.agendaDiariaProfesional(profesionalId, inicio, fin)
                .stream()
                .map(this::toCitaListDto)
                .toList();
    }


    private CitaListDto toCitaListDto(Cita c) {
        return new CitaListDto(
                c.getId(),
                c.getFechaYhora(),
                c.getEstado(),
                c.getPaciente().getNombre(),
                c.getProfesional().getNombre()
        );
    }

    private CitaDetailDto toCitaDetailDto(Cita c) {
        return new CitaDetailDto(
                c.getId(),
                c.getFechaYhora(),
                c.getEstado(),
                new PacienteSimpleDto(
                        c.getPaciente().getId(),
                        c.getPaciente().getNombre(),
                        c.getPaciente().getEmail()
                ),
                new ProfesionalSimpleDto(
                        c.getProfesional().getId(),
                        c.getProfesional().getNombre(),
                        c.getProfesional().getEspecialidad()
                ),
                (c.getConsulta() == null) ? null : new ConsultaSimpleDto(
                        c.getConsulta().getId(),
                        c.getConsulta().getObservaciones(),
                        c.getConsulta().getDiagnostico(),
                        c.getConsulta().getFecha()
                )
        );
    }
}
