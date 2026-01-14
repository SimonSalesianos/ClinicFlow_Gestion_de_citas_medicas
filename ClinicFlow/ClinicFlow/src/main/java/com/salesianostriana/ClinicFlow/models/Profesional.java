package com.salesianostriana.ClinicFlow.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Profesional {


    @Id
    @GeneratedValue
    private Long id;

    private String nombre;
    private String especialidad;


    @OneToMany(mappedBy = "profesional", cascade = CascadeType.ALL)
    private List<Cita> citas = new ArrayList<>();
}
