package com.salesianostriana.ClinicFlow.models;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cita {


    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime fechaYhora;
    private String estado;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesional_id", nullable = false)
    private Profesional profesional;


    @OneToOne(mappedBy = "cita", fetch = FetchType.LAZY)
    private Consulta consulta;

}
