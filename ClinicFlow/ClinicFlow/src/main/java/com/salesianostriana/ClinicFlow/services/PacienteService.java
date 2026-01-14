package com.salesianostriana.ClinicFlow.services;

import com.salesianostriana.ClinicFlow.dtos.CitaListDto;
import com.salesianostriana.ClinicFlow.dtos.PacienteResponseDto;
import com.salesianostriana.ClinicFlow.dtos.PacienteSimpleDto;
import com.salesianostriana.ClinicFlow.models.Cita;
import com.salesianostriana.ClinicFlow.models.Paciente;
import com.salesianostriana.ClinicFlow.repositories.CitaRepository;
import com.salesianostriana.ClinicFlow.repositories.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final CitaRepository citaRepository;

    public PacienteService(PacienteRepository pacienteRepository, CitaRepository citaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.citaRepository = citaRepository;
    }

    public List<PacienteResponseDto> findAll() {
        return pacienteRepository.findAll().stream()
                .map(this::toPacienteResponseDto)
                .toList();
    }

    public PacienteResponseDto findById(Long id) {
        Paciente p = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        return toPacienteResponseDto(p);
    }

    // GET /pacientes/{id}/citas
    public List<CitaListDto> findCitasByPacienteId(Long pacienteId) {
        // Opcional: validar que existe el paciente
        if (!pacienteRepository.existsById(pacienteId))
            throw new RuntimeException("Paciente no encontrado");

        return citaRepository.findByPacienteId(Id).stream()
                .map(this::toCitaListDto)
                .toList();
    }

    private PacienteResponseDto toPacienteResponseDto(Paciente p) {
        return new PacienteResponseDto(
                p.getId(),
                p.getNombre()
                // añade aquí más campos si tu DTO los tiene (dni, telefono, etc.)
        );
    }

    private PacienteSimpleDto toPacienteSimpleDto(Paciente p) {
        return new PacienteSimpleDto(p.getId(), p.getNombre());
    }

    private CitaListDto toCitaListDto(Cita c) {
        return new CitaListDto(
                c.getId(),
                c.getFechaYhora(),   // o getFechaYhora() si lo tienes así
                c.getEstado(),      // si estado es String
                toPacienteSimpleDto(c.getPaciente()),
                new com.salesianostriana.ClinicFlow.dtos.ProfesionalSimpleDto(
                        c.getProfesional().getId(),
                        c.getProfesional().getNombre()
                )
        );
    }
}
