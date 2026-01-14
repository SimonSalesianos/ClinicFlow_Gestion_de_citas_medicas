package com.salesianostriana.ClinicFlow.services;

import com.salesianostriana.ClinicFlow.dtos.CitaListDto;
import com.salesianostriana.ClinicFlow.dtos.ConsultaResponseDto;
import com.salesianostriana.ClinicFlow.dtos.CreateConsultaRequest;
import com.salesianostriana.ClinicFlow.models.Cita;
import com.salesianostriana.ClinicFlow.models.Consulta;
import com.salesianostriana.ClinicFlow.repositories.CitaRepository;
import com.salesianostriana.ClinicFlow.repositories.ConsultaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultaService {

    private final CitaRepository citaRepository;
    private final ConsultaRepository consultaRepository;

    public ConsultaService(CitaRepository citaRepository, ConsultaRepository consultaRepository) {
        this.citaRepository = citaRepository;
        this.consultaRepository = consultaRepository;
    }

    @Transactional
    public ConsultaResponseDto createConsulta(Long citaId, CreateConsultaRequest request) {

        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        if (consultaRepository.findByCitaId(citaId).isPresent())
            throw new RuntimeException("Esta cita ya tiene una consulta");


        Consulta consulta = new Consulta();
        consulta.setDiagnostico(request.diagnostico());
        consulta.setCita(cita);
        cita.setConsulta(consulta);

        Consulta saved = consultaRepository.save(consulta);

        return toConsultaResponseDto(saved);
    }


    private ConsultaResponseDto toConsultaResponseDto(Consulta c) {
        return new ConsultaResponseDto(
                c.getId(),
                c.getCita().getId(),
                c.getDiagnostico()
        );
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

}
