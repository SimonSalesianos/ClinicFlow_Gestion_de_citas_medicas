package com.salesianostriana.ClinicFlow.dtos;

public record ConsultaResponseDto(
        Long id,
        Long citaId,
        String diagnostico
) {}
