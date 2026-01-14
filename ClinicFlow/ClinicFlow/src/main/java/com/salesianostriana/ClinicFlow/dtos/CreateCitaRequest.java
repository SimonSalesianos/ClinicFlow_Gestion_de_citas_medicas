package com.salesianostriana.ClinicFlow.dtos;

import java.time.LocalDateTime;

public record CreateCitaRequest(
        Long pacienteId,

        Long profesionalId,

        LocalDateTime fechaHora
) {}
