package com.salesianostriana.ClinicFlow.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Paciente {


    @Id
    @GeneratedValue
    private Long id;

    private String nombre;
    private String email;
    private LocalDate fechaNacimiento;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL)
    private List<Cita> citas = new ArrayList<>();


}
