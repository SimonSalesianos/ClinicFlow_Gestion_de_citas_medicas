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

@RequiredArgsConstructor
@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfesionalRepository profesionalRepository;
    private final ConsultaRepository consultaRepository;


    @Transactional
    public Page<Cita> getAll(Pageable pageable){
        Page<Cita> citas = citaRepository.findAll(pageable);
        if(citas.isEmpty()) throw new EntityNotFoundException("No hay citas");
        return citas;
    }

    public List<Cita> getCistasPorPaciente(Long pacienteId){

        List<Cita> citasPorPaciente = citaRepository.findByPaciente_IdOrderByFechaHoraAsc(pacienteId);

        if(citasPorPaciente.isEmpty()) throw new IllegalArgumentException("No hay citas del paciente");

        return citasPorPaciente;

    }

    public List<Cita> getCistasPorEstado(Estado estado){

        List<Cita> citasPorEstado = citaRepository.findByEstado(estado);
        if(citasPorEstado.isEmpty()) throw new IllegalArgumentException("No hay citas con ese estado");
        return citasPorEstado;

    }

    public List<Cita> getCitasPorRangoFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin){
        List<Cita> citasPorRango = citaRepository.findByFechaHoraBetween(fechaInicio,fechaFin);
        if(citasPorRango.isEmpty()) throw new IllegalArgumentException("No hay citas en ese rango");
        return citasPorRango;
    }



    public Cita crearCita(CreateCitaRequest citaRequest){

        Paciente paciente = pacienteRepository.findById(citaRequest.pacienteId())
                .orElseThrow(()-> new EntityNotFoundException("No se ha encontrado al paciente con id %d".formatted(citaRequest.pacienteId())));

        Profesional profesional = profesionalRepository.findById(citaRequest.profesionalId())
                .orElseThrow(()-> new EntityNotFoundException("No se ha encontrado al profesional con id %d".formatted(citaRequest.profesionalId())));

        profesional.getCitas().forEach(cita ->{
            if(cita.getFechaHora().isEqual(citaRequest.fechaHora()))
                throw new IllegalArgumentException("El profesional ya tiene una cita en esa hora");
        });

        paciente.getCitas().forEach(cita->{
            if(cita.getFechaHora().getDayOfMonth() == citaRequest.fechaHora().getDayOfMonth())
                throw new IllegalArgumentException("Un paciente no puede tener más de una cita el mismo día");
        });

        if(citaRequest.fechaHora().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("No se pueden crear citas en el pasado");

        Cita cita = toEntity(citaRequest,paciente,profesional);

        paciente.addCita(cita);
        profesional.addCita(cita);

        return citaRepository.save(cita);

    }

    public Cita cancelarCita(Long citaId){
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(()->new EntityNotFoundException("Cita con id %d no encontrada".formatted(citaId)));

        if(cita.getEstado() == Estado.ATENDIDA || cita.getEstado() == Estado.CANCELADA)
            throw new IllegalArgumentException("Esta cita ya ha sido atendida o cancelada");

        cita.setEstado(Estado.CANCELADA);

        return citaRepository.save(cita);

    }

    public Cita registrarConsulta(Long citaId, CreateConsultaRequest consultaRequest){
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(()->new EntityNotFoundException("No se ha encontrado la cita con id %d".formatted(citaId)));

        if(cita.getEstado() != Estado.PROGRAMADA)
            throw new IllegalArgumentException("La cita ya está atendida o ha sido cancelada");

        Consulta consulta = toEntity(consultaRequest,cita);
        cita.setEstado(Estado.ATENDIDA);

        consultaRepository.save(consulta);

        return citaRepository.save(cita);

    }

    public static Consulta toEntity(CreateConsultaRequest consulta, Cita cita){
        return Consulta.builder()
                .observaciones(consulta.observaciones())
                .diagnostico(consulta.diagnostico())
                .fecha(consulta.fecha())
                .cita(cita)
                .build();
    }

    public Cita toEntity(CreateCitaRequest requestCreate, Paciente paciente,Profesional profesional){
        return Cita.builder()
                .fechaHora(requestCreate.fechaHora())
                .estado(Estado.PROGRAMADA)
                .paciente(paciente)
                .profesional(profesional)
                .build();
    }

}