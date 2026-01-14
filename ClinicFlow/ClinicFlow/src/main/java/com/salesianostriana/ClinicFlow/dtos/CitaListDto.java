package com.salesianostriana.ClinicFlow.dtos;

import java.time.LocalDateTime;

public record CitaListDto(
        Long id,
        LocalDateTime fechaYhora,
        String estado,
        String nombrePaciente,
        String nombreProfesional
) {}
