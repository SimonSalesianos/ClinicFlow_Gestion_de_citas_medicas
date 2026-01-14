package com.salesianostriana.ClinicFlow.dtos;

import java.time.LocalDateTime;

public record CitaDetailDto(
        Long id,
        LocalDateTime fechaHora,
        String estado,
        PacienteSimpleDto paciente,
        ProfesionalSimpleDto profesional,
        ConsultaSimpleDto consulta


) {}
