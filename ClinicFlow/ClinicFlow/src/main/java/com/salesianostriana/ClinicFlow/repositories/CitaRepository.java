package com.salesianostriana.ClinicFlow.repositories;

import com.salesianostriana.ClinicFlow.models.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByPaciente(Long id);
    List<Cita> findByEstado(String estado);
    List<Cita> findByRandoDeFechas();
}
