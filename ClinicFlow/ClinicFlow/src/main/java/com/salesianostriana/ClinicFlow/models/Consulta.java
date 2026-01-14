package com.salesianostriana.ClinicFlow.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Consulta {

    @Id
    @GeneratedValue
    private Long id;

    private String observaciones;
    private String diagnostico;
    private LocalDate fecha;

    @OneToOne(mappedBy = "consulta", fetch = FetchType.LAZY)
    private Cita cita;

}
