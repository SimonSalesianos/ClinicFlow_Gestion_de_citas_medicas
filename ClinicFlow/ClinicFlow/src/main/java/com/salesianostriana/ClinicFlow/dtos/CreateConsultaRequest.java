package com.salesianostriana.ClinicFlow.dtos;


public record CreateConsultaRequest(
        String observaciones,
        String diagnostico
) {}