package com.salesianostriana.ClinicFlow.repositories;

import com.salesianostriana.ClinicFlow.models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
}
