package com.salesianostriana.ClinicFlow.dtos;

import java.time.LocalDate;

public record ConsultaSimpleDto(

        Long id,
        String observaciones,
        String diagnostico,
        LocalDate fecha

) {}
