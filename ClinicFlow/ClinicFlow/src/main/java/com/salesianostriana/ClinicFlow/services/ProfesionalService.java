package com.salesianostriana.ClinicFlow.services;

import com.salesianostriana.ClinicFlow.dtos.CitaListDto;
import com.salesianostriana.ClinicFlow.dtos.PacienteSimpleDto;
import com.salesianostriana.ClinicFlow.dtos.ProfesionalResponseDto;
import com.salesianostriana.ClinicFlow.dtos.ProfesionalSimpleDto;
import com.salesianostriana.ClinicFlow.models.Cita;
import com.salesianostriana.ClinicFlow.models.Profesional;
import com.salesianostriana.ClinicFlow.repositories.CitaRepository;
import com.salesianostriana.ClinicFlow.repositories.ProfesionalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProfesionalService {

    private final ProfesionalRepository profesionalRepository;
    private final CitaRepository citaRepository;

    public ProfesionalService(ProfesionalRepository profesionalRepository, CitaRepository citaRepository) {
        this.profesionalRepository = profesionalRepository;
        this.citaRepository = citaRepository;
    }

    public List<ProfesionalResponseDto> findAll() {
        return profesionalRepository.findAll().stream()
                .map(this::toProfesionalResponseDto)
                .toList();
    }

    public ProfesionalResponseDto findById(Long id) {
        Profesional p = profesionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
        return toProfesionalResponseDto(p);
    }

    // Agenda diaria (JPQL del repo)
    public List<CitaListDto> agendaDiaria(Long profesionalId, LocalDate dia) {
        if (!profesionalRepository.existsById(profesionalId))
            throw new RuntimeException("Profesional no encontrado");

        LocalDateTime inicio = dia.atStartOfDay();
        LocalDateTime fin = dia.plusDays(1).atStartOfDay();

        return citaRepository.agendaDiariaProfesional(profesionalId, inicio, fin).stream()
                .map(this::toCitaListDto)
                .toList();
    }

    private ProfesionalResponseDto toProfesionalResponseDto(Profesional p) {
        return new ProfesionalResponseDto(
                p.getId(),
                p.getNombre(),
                p.getEspecialidad()
        );
    }

    private ProfesionalSimpleDto toProfesionalSimpleDto(Profesional p) {
        return new ProfesionalSimpleDto(p.getId(), p.getNombre());
    }

    private PacienteSimpleDto toPacienteSimpleDto(com.salesianostriana.ClinicFlow.models.Paciente p) {
        return new PacienteSimpleDto(p.getId(), p.getNombre());
    }

    private CitaListDto toCitaListDto(Cita c) {
        return new CitaListDto(
                c.getId(),
                c.getFechaHora(),
                c.getEstado(),
                toPacienteSimpleDto(c.getPaciente()),
                toProfesionalSimpleDto(c.getProfesional())
        );
    }
}
